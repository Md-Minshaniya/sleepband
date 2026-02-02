package com.example.sleepband

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

import com.example.sleepband.ui.device.DeviceConnectScreen
import com.example.sleepband.ui.history.HistoryScreen
import com.example.sleepband.ui.insights.InsightsScreen
import com.example.sleepband.ui.navigation.Routes
import com.example.sleepband.ui.onboarding.*
import com.example.sleepband.ui.session.SessionScreen
import com.example.sleepband.ui.theme.SleepBandTheme
import com.example.sleepband.viewmodel.SessionViewModel
import com.example.sleepband.ui.onboarding.LoginChoiceScreen
import com.example.sleepband.ui.onboarding.LoginScreen
import com.example.sleepband.ui.onboarding.EmailSignUpScreen
import com.example.sleepband.ui.onboarding.AccountCreatedScreen
import com.example.sleepband.ui.settings.SettingsScreen
import com.example.sleepband.ui.settings.SleepBandSettingsScreen



import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepBandTheme {
                PermissionRequester {
                    RootNavHost()
                }
            }
        }
    }
}

@Composable
fun PermissionRequester(content: @Composable () -> Unit) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }

    content()
}

@Composable
fun RootNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinue = { navController.navigate(Routes.LOGIN_CHOICE) }
            )
        }

        composable(Routes.LOGIN_CHOICE) {
            LoginChoiceScreen(
                onGoogleClick = { navController.navigate(Routes.GOOGLE_ACCOUNT) },
                onEmailSignUpClick = { navController.navigate(Routes.EMAIL_SIGNUP) },
                        // temporary
                onLoginClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.EMAIL_SIGNUP) {
            EmailSignUpScreen(
                onCreateAccount = { navController.navigate(Routes.ACCOUNT_CREATED) }
            )
        }

        composable(Routes.ACCOUNT_CREATED) {
            AccountCreatedScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) }
            )
        }


        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.DEVICE_CONNECT) }
            )
        }



        composable(Routes.GOOGLE_ACCOUNT) {
            GoogleAccountScreen(
                onNext = { navController.navigate(Routes.DEVICE_CONNECT) }
            )
        }

        composable(Routes.DEVICE_CONNECT) {
            DeviceConnectScreen(
                onNext = { navController.navigate(Routes.SUCCESS) }
            )
        }

        composable(Routes.SUCCESS) {
            SuccessScreen(
                onStart = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainTabsScreen()
        }
    }
}

@Composable
fun MainTabsScreen() {
    val navController = rememberNavController()
    val sessionViewModel: SessionViewModel = hiltViewModel()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(
                navController = navController,
                startDestination = "session"
            ) {

                composable("session") {
                    SessionScreen(
                        viewModel = sessionViewModel,
                        onNavigateToHistory = { navController.navigate("history") },
                        onNavigateToInsights = { navController.navigate("insights") }
                    )
                }
                composable("insights") {
                    InsightsScreen(viewModel = sessionViewModel)
                }
                composable("history") {
                    HistoryScreen()
                }
                composable("settings") {
                    SettingsScreen(
                        onBandClick = { navController.navigate("band_settings") }
                    )
                }

                composable("band_settings") {
                    SleepBandSettingsScreen()
                }

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route

    val items = listOf(
        NavigationItem("Home", "session", Icons.Default.Home),
        NavigationItem("Insights", "insights", Icons.Default.Insights),
        NavigationItem("History", "history", Icons.Default.History),
        NavigationItem("Settings", "settings", Icons.Default.Settings)

    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)
