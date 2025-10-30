package com.cs407.safebite.screen

import com.cs407.safebite.component.UnifiedTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun BarcodeScanScreen (
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToResults: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }
    var showManual by remember { mutableStateOf(false) }
    var barcode by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }


    fun submitManualIfValid() {
        val valid = barcode.isNotBlank()
        inputError = !valid
        if (valid) {
            showManual = false
            onNavigateToResults()
        }
    }

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Scan Barcode",
                onNavigateBack = { onNavigateBack() },
                onNavigateToProfile = { onNavigateToProfile() },
                onNavigateToRecents = { onNavigateToRecents() },
                onNavigateToInput = { onNavigateToInput() },
                onNavigateToScan = { onNavigateToScan() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Section label
            Text(
                text = "Camera not implemented yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            Button(
                onClick = { showConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Scan!", style = MaterialTheme.typography.bodyLarge)
            }
        }

        if (showConfirm) {
            Dialog(onDismissRequest = { showConfirm = false }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 280.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Is Sample Item correct?",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .heightIn(min = 120.dp)
                                .border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Place holder for item scanned.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = {
                                    showConfirm = false
                                    onNavigateToResults()
                                }
                            ) { Text("Yes") }

                            TextButton(
                                onClick = {
                                    showConfirm = false
                                    showManual = true
                                }
                            ) { Text("No") }
                        }
                    }
                }
            }
        }

        if (showManual) {
            Dialog(onDismissRequest = { showManual = false }) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 280.dp)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Scan Again or manually input number?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Scan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clickable {
                                    showManual = false
                                    barcode = ""
                                    inputError = false
                                }
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Input",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = barcode,
                                onValueChange = { raw ->
                                    val digitsOnly = raw.filter { it.isDigit() }
                                    barcode = digitsOnly
                                    if (inputError && digitsOnly.isNotBlank()) inputError = false
                                },
                                placeholder = { Text("1234567890") },
                                singleLine = true,
                                isError = inputError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { submitManualIfValid() }
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (inputError) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Please enter a number.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { submitManualIfValid() }) {
                                Text("Submit")
                            }
                        }
                    }
                }
            }
        }
    }
}
