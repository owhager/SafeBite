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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import androidx.compose.ui.unit.dp
import com.cs407.safebite.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun BarcodeScanScreen (onNavigateToProfile: () -> Unit, onNavigateToRecents: () -> Unit, onNavigateToInput: () -> Unit) {
    val gradientTopColor = AppTheme.customColors.gradientTop
    val gradientBottomColor = AppTheme.customColors.gradientBottom

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Recents") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToRecents()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Input Your Allergens") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToInput()
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Barcode Scanner") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            gradientTopColor,
                            gradientBottomColor
                        )
                    )
                ),
            color = Color.Transparent
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
                    .padding(16.dp)
            ) {
                // App title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    Text(
                        text = "testing",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
        }
    }
}