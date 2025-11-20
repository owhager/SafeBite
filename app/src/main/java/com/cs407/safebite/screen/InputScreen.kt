package com.cs407.safebite.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    // Get master list from ViewModel
    val masterAllergens = allergenViewModel?.master ?: emptyList()
    
    // Observe live checked state from ViewModel
    val checkedItems by remember { derivedStateOf { allergenViewModel?.checked } }
    val customAllergens by remember { derivedStateOf { allergenViewModel?.customAllergens ?: emptyList() } }
    
    // State for custom allergen input
    var customAllergenText by remember { mutableStateOf("") }
    var showAddCustom by remember { mutableStateOf(false) }

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
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Master allergens section
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
                
                // Custom allergens section
                if (customAllergens.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Custom Allergens",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    items(customAllergens, key = { it }) { allergen ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { allergenViewModel?.toggle(allergen) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checkedItems?.contains(allergen) == true,
                                onCheckedChange = { allergenViewModel?.toggle(allergen) }
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = allergen,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { allergenViewModel?.removeCustomAllergen(allergen) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            
            // Add custom allergen button or input section
            if (showAddCustom) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = customAllergenText,
                                onValueChange = { customAllergenText = it },
                                label = { Text("Custom Allergen") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        val trimmed = customAllergenText.trim()
                                        if (trimmed.isNotEmpty()) {
                                            if (allergenViewModel?.addCustomAllergen(trimmed) == true) {
                                                customAllergenText = ""
                                                showAddCustom = false
                                            }
                                        }
                                    }
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    val trimmed = customAllergenText.trim()
                                    if (trimmed.isNotEmpty()) {
                                        if (allergenViewModel?.addCustomAllergen(trimmed) == true) {
                                            customAllergenText = ""
                                            showAddCustom = false
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = {
                                    customAllergenText = ""
                                    showAddCustom = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                // Button to show add custom allergen input
                Button(
                    onClick = { showAddCustom = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Add Custom Allergen")
                }
            }

            // Footer info
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${checkedItems?.size ?: 0} allergen${if ((checkedItems?.size ?: 0) != 1) "s" else ""} selected",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}