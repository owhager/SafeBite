package com.cs407.safebite.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.safebite.component.UnifiedTopBar
import com.cs407.safebite.data.UserState
import com.cs407.safebite.viewmodel.AllergenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    allergenViewModel: AllergenViewModel?,
    userState: UserState,
    onNavigateBack: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddMoreAllergens: () -> Unit = {},
    onNavigateToScan: () -> Unit,
    onLogout: () -> Unit,
    onDelete: () -> Unit
) {
    val checkedItems = allergenViewModel?.checked ?: emptyList()

    // In both ProfileScreen and InputScreen
    if (userState.uid.isEmpty() || allergenViewModel == null) {
        // Show loading or message
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
            Text("Loading...", Modifier.padding(16.dp))
        }
        return
    }

    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = "Profile",
                onNavigateBack = { onNavigateBack() },
                onNavigateToProfile = { onNavigateToProfile() },
                onNavigateToRecents = { onNavigateToRecents() },
                onNavigateToInput = { onNavigateToInput() },
                onNavigateToScan = { onNavigateToScan() },
                onLogout = { onLogout() },
                onDelete = { onDelete() }
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
                text = "Hello, " + userState.name,
                fontSize = 30.sp,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = "Saved User Allergens",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            // Show only checked allergens
            if (checkedItems.isEmpty()) {
                Text(
                    text = "No allergens selected yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                checkedItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            onClick = { allergenViewModel.toggle(item) }  // or remove(item)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove allergen",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onAddMoreAllergens() },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(Modifier.width(8.dp))
                TextButton(
                    onClick = { onAddMoreAllergens() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Add more allergens")
                }


            }

            Button(
                onClick = { onNavigateToScan() },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Scan item!", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
