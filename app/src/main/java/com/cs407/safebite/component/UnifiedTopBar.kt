package com.cs407.safebite.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/*
* Creates TopAppBar with back button, title, and menu button.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifiedTopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToProfile: () -> Unit,
    onNavigateToRecents: () -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToScan: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Profile") },
                    onClick = { menuOpen = false; onNavigateToProfile() }
                )
                DropdownMenuItem(
                    text = { Text("Recents") },
                    onClick = { menuOpen = false; onNavigateToRecents() }
                )
                DropdownMenuItem(
                    text = { Text("Input Allergens") },
                    onClick = { menuOpen = false; onNavigateToInput() }
                )
                DropdownMenuItem(
                    text = { Text("Scan Barcode") },
                    onClick = { menuOpen = false; onNavigateToScan() }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFE6E6E6),
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}