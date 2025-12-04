package com.cs407.safebite.viewmodel

import android.util.Log
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import android.content.Context
import com.cs407.safebite.data.AllergenDatabase
import com.cs407.safebite.data.RecentScan
import com.cs407.safebite.data.RecentScanDao
import com.cs407.safebite.data.RecentScanDatabase

data class FoodApiResponse(
    val food: FoodData?
)

data class FoodData(
    val food_name: String?,
    val brand_name: String?,
    val food_attributes: FoodAttributes?
)

data class FoodAttributes(
    val allergens: AllergenData?,
    val preferences: PreferencesData? = null
)

data class PreferencesData(
    val preference: List<Preference>?
)

data class Preference(
    val id: Long?,
    val name: String?,
    val value: Int?  // 1 if preference applies, 0 if not
)

data class AllergenData(
    val allergen: List<Allergen>?
)

data class Allergen(
    val id: Long?,   // ID Number for Allergen
    val name: String?, // Allergen Name
    val value: Int?   // 1 if Allergen is present, 0 if not, -1 if unknown
)

data class foodState(
    val foodData: FoodApiResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// One item in the Recents list.
data class RecentItem(
    val barcode: String,
    val foodName: String,
    val brandName: String
)

class BarcodeLookupViewModel : ViewModel() {

    private val _foodState: MutableStateFlow<foodState> =
        MutableStateFlow(foodState())
    val foodState = _foodState.asStateFlow()

    // ---- RECENTS (in-memory list + DB backing) ----
    private val _recentItems: MutableStateFlow<List<RecentItem>> =
        MutableStateFlow(emptyList())
    val recentItems = _recentItems.asStateFlow()

    private var userUID: String? = null
    private var recentDao: RecentScanDao? = null

    /**
     * Must be called once we know the logged-in user's UID.
     * This sets up the DAO and loads existing recents from Room.
     */
    fun initialize(context: Context, userUID: String) {
        // Avoid re-initializing for the same user
        if (this.userUID == userUID && recentDao != null) return

        this.userUID = userUID
        this.recentDao = RecentScanDatabase
            .getDatabase(context)
            .recentScanDao()

        // Load existing recents from DB
        viewModelScope.launch {
            try {
                val dao = recentDao ?: return@launch
                val rows = dao.getAllForUser(userUID)
                _recentItems.value = rows.map { row ->
                    RecentItem(
                        barcode = row.barcode,
                        foodName = row.foodName,
                        brandName = row.brandName
                    )
                }
            } catch (e: Exception) {
                Log.e("Recents", "Failed to load recents: ${e.localizedMessage}", e)
            }
        }
    }

    fun getFoodData(barcode: String) {
        viewModelScope.launch {
            Log.d("FatSecretAPI", "Fetching food data for $barcode")
            _foodState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitInstance.foodApi.getFoodData(
                    barcode = barcode,
                )
                Log.d("FatSecretAPI", "HTTP ${response.code()}")
                val body = response.body()
                Log.d("FatSecretAPI", "Body: $body")

                _foodState.update { it.copy(isLoading = false, foodData = body) }

                // If we got valid food data, add/update this entry in Recents.
                if (body != null && body.food != null) {
                    val food = body.food
                    val name = food.food_name ?: "Unknown item"
                    val brand = food.brand_name ?: ""

                    val recent = RecentItem(
                        barcode = barcode,
                        foodName = name,
                        brandName = brand
                    )

                    // Update in-memory recents
                    _recentItems.update { current ->
                        listOf(recent) + current.filterNot { it.barcode == barcode }
                    }

                    // Also save to Room for persistence
                    val uid = userUID
                    val dao = recentDao
                    if (uid != null && dao != null) {
                        try {
                            dao.insert(
                                RecentScan(
                                    userUID = uid,
                                    barcode = barcode,
                                    foodName = name,
                                    brandName = brand
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "Recents",
                                "Failed to insert recent scan: ${e.localizedMessage}",
                                e
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "FatSecretAPI",
                    "Error fetching food data: ${e.localizedMessage}",
                    e
                )
                _foodState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to retrieve food data: ${e.message}"
                    )
                }
            } finally {
                Log.d("FatSecretAPI", "getFoodData() finished")
            }
        }
    }

    /**
     * Called from the Recents screen when a user taps a card.
     * We just push that food into foodState so ResultScreen can
     * display it without re-scanning.
     */
    fun selectRecentItem(item: RecentItem) {
        _foodState.update {
            it.copy(
                isLoading = false,
                error = null
            )
        }
    }

    fun removeRecentItem(item: RecentItem) {
        // Update UI immediately
        _recentItems.update { current ->
            current.filterNot { it.barcode == item.barcode }
        }

        // Remove from Recents DB if possible
        val uid = userUID
        val dao = recentDao
        if (uid != null && dao != null) {
            viewModelScope.launch {
                try {
                    dao.deleteByBarcode(uid, item.barcode)
                } catch (e: Exception) {
                    Log.e(
                        "Recents",
                        "Failed to delete recent scan: ${e.localizedMessage}",
                        e
                    )
                }
            }
        }
    }
}

interface FoodApiService {
    @GET("food/barcode/find-by-id/v2")
    suspend fun getFoodData(
        @Header("Authorization") auth: String = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6Ijk0Q0NGMUU4RTI4RUM3RkM4QTc2RTcyNTM4M0E4RjM0OUU3MUUzRjAiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJsTXp4Nk9LT3hfeUtkdWNsT0RxUE5KNXg0X0EifQ.eyJuYmYiOjE3NjQ4NjkzNjMsImV4cCI6MTc2NDk1NTc2MywiaXNzIjoiaHR0cHM6Ly9vYXV0aC5mYXRzZWNyZXQuY29tIiwiYXVkIjoiYmFyY29kZSIsImNsaWVudF9pZCI6ImE1MTEwODg4ZWFhNzQ3NjA5MGRiY2ZiZDViNzYwY2FhIiwic2NvcGUiOlsiYmFyY29kZSJdfQ.Djw2XhtKyc_1mLHze7NUg7S_oo3ykuWNZRo6KSN6XJ1FeYyci3tyHuwj3heG3OK5rOJ2dDxIQmrwdbWviXumb7uPujJPmHQBHRORQdYoZFH99arvXDS_NdnQXcSGUPXQuj8IgmtQWhk9-tErkaYvruq6cPXE1vZSlkAkEqvqMfq7cOfe5v2CTt1CIs0xZsOLl7hV5YwkSfdAGKDecCOAVKyACD_0Jih2dlR5-fs1emMQqw_im7XvqWY44NXCfNKPk2sRfmBFdFTxgRgzpZMQg56ml7RoQ-gCmnKtlkkmO1Q4Cphw6OmRiemSNTQE2opr6uxDChNKmsb4MTO7uPIVlSchv7zeT9Pr5wJ0nABNbc7qUQSfK5ny6jK4LT147P2IY8WAs8pvPHWtOL3RYZAew8tOqiI-W8VWpEk4Lu3fuC5zZY4GTu3TGnDHuPGFTw8UeA2lXs9WBkRg1nZ3ZesCTQN3YTZmbqqB2ikWdQN7tcNdL06EJHi-OaVx2HXupp72Zd8D2IUBYxGh2EDpJnONtMNglJLzDV2wr6GUb3-VvCMGLPrFjfdcRsuAusuu_fNjYCt21t23iUciCm5_EQJfVj12H3uLyV0Pa_Xelob3-YUn4GB6zOuua4X77JJG3pDUtd8dvLInNstEIsKsgJDrYvz7eiHqLeuKA9RQCQXMNmM",
        @Query("barcode") barcode: String,
        @Query("include_food_attributes") include: String = "true",
        @Query("format") format: String = "json",
    ): Response<FoodApiResponse>
}

object RetrofitInstance {
    private const val BASE_URL = "https://platform.fatsecret.com/rest/"
    val foodApi: FoodApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodApiService::class.java)
    }
}
