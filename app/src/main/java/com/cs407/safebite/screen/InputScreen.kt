package com.cs407.safebite.screen

import com.cs407.safebite.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cs407.safebite.ui.theme.AppTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.viewmodel.AllergenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    vm: AllergenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
) {
    val gradientTopColor = AppTheme.customColors.gradientTop
    val gradientBottomColor = AppTheme.customColors.gradientBottom

    // 1) List & per-item checked state
    val allergens = vm.allergens
    val checkedMap = vm.checkedMap

    // 2) dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var newAllergen by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    /*
    * Checks if custom input typed by user is valid before adding to allergen list.
    */
    fun addAllergenIfValid() {
        val candidate = newAllergen.trim()
        when {
            candidate.isEmpty() -> error = "Allergen name can’t be empty."
            allergens.any { it.equals(candidate, ignoreCase = true) } ->
                error = "\"$candidate\" is already in the list."
            else -> {
                vm.addAllergen(candidate)
                checkedMap[candidate] = true
                newAllergen = ""
                error = null
                showAddDialog = false
            }
        }
    }

    Scaffold(

        topBar = {
            UnifiedTopBar(
                title = "Input Allergens",
                onNavigateBack = { onNavigateBack() },
                onNavigateToProfile = { onNavigateToProfile() },
                onNavigateToRecents = { onNavigateToRecents() },
                onNavigateToInput = { onNavigateToInput() },
                onNavigateToScan = { onNavigateToScan() }
            )
        },
        containerColor = Color.Transparent
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientTopColor, gradientBottomColor)
                    )
                )
                .padding(inner)
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {

                Text(
                    "Your allergens",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // List of allergens
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(allergens, key = { it.lowercase() }) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checkedMap[item] == true,
                                onCheckedChange = { checked -> vm.setChecked(item, checked) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(item, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                Column {
                    // A check box to add more allergens to the list
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clickable { showAddDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { showAddDialog = true }) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Can't find your allergen? Click here",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    // Button to navigate to scan screen
                    Button(
                        onClick = { onNavigateToScan() },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Scan item!", style = MaterialTheme.typography.bodyLarge)
                    }


                }


                // Simple AlertDialog for adding
                if (showAddDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        title = { Text("Can't find your allergen? Click here!") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = newAllergen,
                                    onValueChange = {
                                        newAllergen = it
                                        if (error != null) error = null
                                    },
                                    singleLine = true,
                                    placeholder = { Text("Type an allergen (e.g., Wheat)") },
                                    isError = error != null,
                                    supportingText = { if (error != null) Text(error!!) },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { addAllergenIfValid() }),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { addAllergenIfValid() }) { Text("Add") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }
        }
    }
}