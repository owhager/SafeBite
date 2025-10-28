package com.cs407.safebite.screen

import com.cs407.safebite.R
import com.cs407.safebite.component.UnifiedTopBar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.safebite.ui.theme.AppTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun BarcodeScanScreen (
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToResults: () -> Unit
) {
    val gradientTopColor = AppTheme.customColors.gradientTop
    val gradientBottomColor = AppTheme.customColors.gradientBottom

    var showConfirm by remember { mutableStateOf(false) } // pop up confirmation dialog if true
    var showManual by remember { mutableStateOf(false) } // confirm scan again dialog if true
    var barcode by remember { mutableStateOf("") } // manual user barcode input
    var inputError by remember { mutableStateOf(false) } // error message for manual input


    /*
    * Checks if custom input typed by user is valid before adding to allergen list.
    */
    fun submitManualIfValid() {
        val valid = barcode.isNotBlank()
        inputError = !valid
        if (valid) {
            showManual = false
            onNavigateToResults()
        }
    }

    Scaffold(
        // back button, screen title, and menu displayed.
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
        containerColor = Color.Transparent
    ){
            inner ->
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Section label
                Text(
                    text = "Camera not implemented yet",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                // Button to navigate to scan screen
                Button(
                    onClick = { showConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Scan!", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        // Dialog 1 to confirm item
        if (showConfirm) {
            Dialog(onDismissRequest = { showConfirm = false }) {
                Surface(
                    color = Color(0xFFE0E0E0),            // light gray
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 280.dp)
                            .padding(16.dp)
                    ) {
                        // Title
                        Text(
                            text = "Is Sample Item correct?",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )

                        Spacer(Modifier.height(16.dp))

                        // item that will be displayed in the dialog
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .heightIn(min = 120.dp)
                                .border(
                                    width = 3.dp,
                                    color = Color.Black,
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Place holder for item scanned.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Black
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Bottom row: Yes (left) and No (right)
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
                                onClick = { showManual = true }
                            ) { Text("No") }
                        }
                    }
                }
            }
        }

        // Dialog 2 to scan again or manual barcode input
        if (showManual) {
            Dialog(onDismissRequest = { showManual = false }) {
                Surface(
                    color = Color(0xFFE0E0E0),
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
                            color = Color.Black
                        )

                        Spacer(Modifier.height(12.dp))

                        // "Scan" action (left)
                        Text(
                            text = "Scan",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // just close this dialog (we're already on scan screen)
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
                                color = Color.Black
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = 2.dp,
                                        color = Color.Black,
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = barcode,
                                    onValueChange = { raw ->
                                        // keep digits only
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
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(0.dp), // box already has pill outline
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        disabledBorderColor = Color.Transparent,
                                        errorBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }

                        // Print error message if input is invalid
                        if (inputError) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Please enter a number.",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
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