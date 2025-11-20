package com.cs407.safebite.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.viewmodel.AllergenViewModel
import com.cs407.safebite.viewmodel.BarcodeLookupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    allergenViewModel: AllergenViewModel?,
    barcodeModel: BarcodeLookupViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
    onLogout: () -> Unit
) {
    // Observe the latest food data from the shared barcode view model
    val foodState by barcodeModel.foodState.collectAsStateWithLifecycle()

    val food = foodState.foodData?.food
    val productName = food?.food_name ?: "Unknown item"
    val brandName = food?.brand_name ?: ""

    // All allergens returned by the API that are marked as "present"
    val apiAllergens = food
        ?.food_attributes
        ?.allergens
        ?.allergen
        ?.filter { it.value == 1 && !it.name.isNullOrBlank() }
        ?.map { it.name!!.trim() }
        ?: emptyList()

    // User-selected allergens from our local DB (InputScreen)
    val userAllergens = allergenViewModel?.checked ?: emptyList()

    data class DisplayAllergen(
        val name: String,
        val isUserAllergen: Boolean
    )

    // Mark which API allergens match the user's allergen list (case-insensitive / contains)
    val displayAllergens = apiAllergens.map { apiName ->
        val lower = apiName.lowercase()
        val matchesUser = userAllergens.any { user ->
            val u = user.lowercase()
            lower.contains(u) || u.contains(lower)
        }
        DisplayAllergen(apiName, matchesUser)
    }

    val containsUserAllergen = displayAllergens.any { it.isUserAllergen }

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Results",
                onNavigateBack = onNavigateBack,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToRecents = onNavigateToRecents,
                onNavigateToInput = onNavigateToInput,
                onNavigateToScan = onNavigateToScan,
                onLogout = onLogout
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Danger banner if this item appears unsafe for the current user
            if (containsUserAllergen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp)
                        .height(40.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(20.dp)
                        )
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.error,
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Contains one of your allergens",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Product card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(vertical = 14.dp, horizontal = 18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = productName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (brandName.isNotBlank()) {
                        Text(
                            text = brandName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Allergens section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.onSurface,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Allergens",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    when {
                        foodState.isLoading -> {
                            Text(
                                text = "Loading allergen information…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        foodState.error != null -> {
                            Text(
                                text = foodState.error
                                    ?: "Failed to load allergen information.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        apiAllergens.isEmpty() -> {
                            Text(
                                text = "No allergen information available for this item.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        else -> {
                            displayAllergens.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    // Small pill indicating whether this allergen matches the user
                                    val pillColor =
                                        if (item.isUserAllergen)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.primary

                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                pillColor,
                                                RoundedCornerShape(4.dp)
                                            )
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color =
                                            if (item.isUserAllergen)
                                                MaterialTheme.colorScheme.error
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other Potential Allergens section (allergens with value == 0)
            val otherAllergens = food
                ?.food_attributes
                ?.allergens
                ?.allergen
                ?.filter { it.value == 0 && !it.name.isNullOrBlank() }
                ?.map { it.name!!.trim() }
                ?: emptyList()

            if (otherAllergens.isNotEmpty() || foodState.isLoading || foodState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onSurface,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Other Potential Allergens",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        when {
                            foodState.isLoading -> {
                                Text(
                                    text = "Loading allergen information…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            foodState.error != null -> {
                                Text(
                                    text = foodState.error
                                        ?: "Failed to load allergen information.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            otherAllergens.isEmpty() -> {
                                Text(
                                    text = "No other potential allergens listed for this item.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            else -> {
                                otherAllergens.forEach { allergen ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "• $allergen",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Food Attributes section (preferences)
            val preferences = food
                ?.food_attributes
                ?.preferences
                ?.preference
                ?.filter { it.value == 1 && !it.name.isNullOrBlank() }
                ?.map { it.name!!.trim() }
                ?: emptyList()

            if (preferences.isNotEmpty() || foodState.isLoading || foodState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.onSurface,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Food Attributes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        when {
                            foodState.isLoading -> {
                                Text(
                                    text = "Loading food attributes…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            foodState.error != null -> {
                                Text(
                                    text = foodState.error
                                        ?: "Failed to load food attributes.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            preferences.isEmpty() -> {
                                Text(
                                    text = "No food attributes available for this item.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            else -> {
                                preferences.forEach { preference ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "• $preference",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Scan another item")
            }
        }
    }
}
