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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import com.cs407.safebite.screen.LoginPage
import com.cs407.safebite.screen.ResultScreen
import com.cs407.safebite.viewmodel.AllergenViewModel
import com.cs407.safebite.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            SafeBiteTheme {
//                val vm: AllergenViewModel = viewModel()
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(
    userViewModel: UserViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val userState by userViewModel.userState.collectAsState()

    LaunchedEffect(userState.uid) {
        // if uid is not empty, meaning user has logged in, then check if they have username
        if (userState.uid.isNotEmpty()) {
            // if they dont have username bring to ask name page
//            if (userState.name.isEmpty()) {
//                navController.navigate("AskNamePage")
//            }
//            // else bring to profile
//            else {
                navController.navigate("profile")
//            }

        }
        // else uid is empty, bring to login
        else {
            navController.navigate("login") {
                popUpTo(0)
            }
        }
    }

    NavHost(
        navController = navController, startDestination = "profile"
    ) {
        composable("login") {
            LoginPage(loginButtonClick = {userState ->
                userViewModel.setUser(userState)
            })
        }
        composable("profile") {
            ProfileScreen(
//                vm = vm,
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
//                vm = vm,
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