package com.cs407.safebite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.safebite.screen.BarcodeScanScreen
import com.cs407.safebite.screen.InputScreen
import com.cs407.safebite.screen.ProfileScreen
import com.cs407.safebite.screen.RecentsScreen
import com.cs407.safebite.ui.theme.SafeBiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeBiteTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = "home"
    ) {
        composable("home") {
            ProfileScreen(
                onNavigateToHome = { navController.navigate(route = "home")}
            )
        }
        composable("barcode_scan") {
            BarcodeScanScreen(
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToRecents = { navController.navigate("recents") },
                onNavigateToInput = { navController.navigate("input_allergies") }
            )
        }
        composable("recents") {
            RecentsScreen(
                onNavigateToHome = { navController.navigate(route = "home")}
            )
        }
        composable("input_allergies") {
            InputScreen(
                onNavigateToHome = { navController.navigate(route = "home")}
            )
        }
    }
}