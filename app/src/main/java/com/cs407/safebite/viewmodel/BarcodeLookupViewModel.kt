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

data class FoodApiResponse(
    val food: FoodData?
)

data class FoodData(
    val food_name: String?,
    val brand_name: String?,
    val food_attributes: FoodAttributes?
)

data class FoodAttributes(
    val allergens: AllergenData
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
    val brandName: String,
    val foodData: FoodApiResponse
)

class BarcodeLookupViewModel : ViewModel() {

    private val _foodState: MutableStateFlow<foodState> =
        MutableStateFlow(foodState())
    val foodState = _foodState.asStateFlow()

    // In-memory list of recently looked-up items (most recent first).
    private val _recentItems: MutableStateFlow<List<RecentItem>> =
        MutableStateFlow(emptyList())
    val recentItems = _recentItems.asStateFlow()

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
                        brandName = brand,
                        foodData = body
                    )
                    _recentItems.update { current ->
                        // Prepend new item, remove older duplicate of same barcode.
                        listOf(recent) + current.filterNot { it.barcode == barcode }
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
                foodData = item.foodData,
                isLoading = false,
                error = null
            )
        }
    }

    fun removeRecentItem(item: RecentItem) {
        _recentItems.update { current ->
            current.filterNot { it.barcode == item.barcode }
        }
    }
}

interface FoodApiService {
    @GET("food/barcode/find-by-id/v2")
    suspend fun getFoodData(
        @Header("Authorization") auth: String = "Bearer ", // TODO: Insert api token
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
