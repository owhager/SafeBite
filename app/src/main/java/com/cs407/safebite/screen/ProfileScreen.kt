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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.viewmodel.AllergenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: AllergenViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddMoreAllergens: () -> Unit = {},
    onNavigateToScan: () -> Unit
) {
    val gradientTopColor = AppTheme.customColors.gradientTop
    val gradientBottomColor = AppTheme.customColors.gradientBottom
    val checkedItems = vm.checkedItems()

    Scaffold(
        // back button, screen title, and menu displayed.
        topBar = {
            UnifiedTopBar(
                title = "User allergens",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Section label
                Text(
                    text = "Saved User allergens",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )

                // Saved allergens
                checkedItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = { checked ->
                                vm.setChecked(item, checked) // if false, it disappears from this screen
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(item, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(Modifier.weight(1f))

                // Check box to navigate to input screen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onAddMoreAllergens() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = { onAddMoreAllergens() }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Add more allergens",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Button to navigate to scan screen
                Button(
                    onClick = { onNavigateToScan() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Scan item!", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
