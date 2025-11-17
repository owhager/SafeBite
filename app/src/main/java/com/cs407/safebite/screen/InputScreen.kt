package com.cs407.safebite.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.data.UserState
import com.cs407.safebite.viewmodel.AllergenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    allergenViewModel: AllergenViewModel?,
    userState: UserState,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
) {
    // Hard-coded master list — must match AllergenViewModel.master
    val masterAllergens = listOf(
        "Peanut", "Milk", "Egg", "Fish", "Gluten",
        "Lactose", "Nuts", "Sesame", "Shellfish", "Soy"
    )

    // Observe live checked state from ViewModel
    val checkedItems by remember { derivedStateOf { allergenViewModel?.checked } }

    // In both ProfileScreen and InputScreen
    if (userState.uid.isEmpty()) {
        // Show loading or message
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Text("Loading user data...", Modifier.padding(16.dp))
        }
        return
    }

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Update Allergens",
                onNavigateBack = onNavigateBack,
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToRecents = onNavigateToRecents,
                onNavigateToInput = onNavigateToInput,
                onNavigateToScan = onNavigateToScan,
                onLogout = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Select Your Allergens",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(masterAllergens, key = { it }) { allergen ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { allergenViewModel?.toggle(allergen) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedItems?.contains(allergen) == true ,
                            onCheckedChange = { allergenViewModel?.toggle(allergen) }
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = allergen,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Optional footer info
            Spacer(Modifier.height(16.dp))
            Text(
                text = "${checkedItems?.size} allergen${if (checkedItems?.size != 1) "s" else ""} selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}