package com.cs407.safebite.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.viewmodel.AllergenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
//    vm: AllergenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
) {
//    val allergens = vm.allergens
//    val checkedMap = vm.checkedMap

    var showAddDialog by remember { mutableStateOf(false) }
    var newAllergen by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

//    fun addAllergenIfValid() {
//        val candidate = newAllergen.trim()
//        when {
//            candidate.isEmpty() -> error = "Allergen name can’t be empty."
//            allergens.any { it.equals(candidate, ignoreCase = true) } ->
//                error = "\"$candidate\" is already in the list."
//            else -> {
//                vm.addAllergen(candidate)
//                checkedMap[candidate] = true
//                newAllergen = ""
//                error = null
//                showAddDialog = false
//            }
//        }
//    }

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Update Allergens",
                onNavigateBack = { onNavigateBack() },
                onNavigateToProfile = { onNavigateToProfile() },
                onNavigateToRecents = { onNavigateToRecents() },
                onNavigateToInput = { onNavigateToInput() },
                onNavigateToScan = { onNavigateToScan() },
                onLogout = {}
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                "Your allergens",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

//            LazyColumn(
//                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                contentPadding = PaddingValues(bottom = 8.dp)
//            ) {
//                items(allergens, key = { it.lowercase() }) { item ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Checkbox(
//                            checked = checkedMap[item] == true,
//                            onCheckedChange = { checked -> vm.setChecked(item, checked) }
//                        )
//                        Spacer(Modifier.width(8.dp))
//                        Text(
//                            item,
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = MaterialTheme.colorScheme.onBackground
//                        )
//                    }
//                }
//            }

            Column {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        "Can't find your allergen? Click here",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (showAddDialog) {
//                AlertDialog(
//                    onDismissRequest = { showAddDialog = false },
//                    title = { Text("Add a New Allergen") },
//                    text = {
//                        Column {
//                            OutlinedTextField(
//                                value = newAllergen,
//                                onValueChange = {
//                                    newAllergen = it
//                                    if (error != null) error = null
//                                },
//                                singleLine = true,
//                                placeholder = { Text("e.g., Wheat") },
//                                isError = error != null,
//                                supportingText = { if (error != null) Text(error!!) },
//                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
//                                keyboardActions = KeyboardActions(onDone = { addAllergenIfValid() }),
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                    },
//                    confirmButton = {
//                        TextButton(onClick = { addAllergenIfValid() }) { Text("Add") }
//                    },
//                    dismissButton = {
//                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
//                    }
                //)
            }
        }
    }
}
