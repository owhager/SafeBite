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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.safebite.screen.ResultScreen
import com.cs407.safebite.viewmodel.AllergenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeBiteTheme {
                val vm: AllergenViewModel = viewModel()
                AppNavigation(vm)
            }
        }
    }
}

@Composable
fun AppNavigation(vm: AllergenViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = "profile"
    ) {
        composable("profile") {
            ProfileScreen(
                vm = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecents = { navController.navigate("recents") },
                onNavigateToInput   = { navController.navigate("input_allergies") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToScan    = { navController.navigate("barcode_scan") },
                onAddMoreAllergens = { navController.navigate("input_allergies")},
            )
        }
        composable("barcode_scan") {
            BarcodeScanScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecents = { navController.navigate("recents") },
                onNavigateToInput   = { navController.navigate("input_allergies") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToScan    = { navController.navigate("barcode_scan") },
                onNavigateToResults = { navController.navigate("results") }
            )
        }
        composable("recents") {
            RecentsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecents = { navController.navigate("recents") },
                onNavigateToInput = { navController.navigate("input_allergies") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToScan = { navController.navigate("barcode_scan") },
            )
        }
        composable("input_allergies") {
            InputScreen(
                vm = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecents = { navController.navigate("recents") },
                onNavigateToInput   = { navController.navigate("input_allergies") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToScan    = { navController.navigate("barcode_scan") }
            )
        }
        composable("results") {
            ResultScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecents = { navController.navigate("recents") },
                onNavigateToInput = { navController.navigate("input_allergies") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToScan = { navController.navigate("barcode_scan") }
            )
        }
    }
}