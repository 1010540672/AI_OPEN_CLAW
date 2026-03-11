package com.example.authapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.authapp.data.repository.AuthRepository
import com.example.authapp.ui.login.LoginPresenter
import com.example.authapp.ui.login.LoginScreen
import com.example.authapp.ui.login.LoginUiState
import com.example.authapp.ui.register.RegisterPresenter
import com.example.authapp.ui.register.RegisterScreen
import com.example.authapp.ui.register.RegisterUiState
import com.example.authapp.ui.success.SuccessScreen
import com.example.authapp.ui.theme.AuthAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Success : Screen("success/{username}") {
        fun createRoute(username: String) = "success/$username"
    }
}

@Composable
fun AuthApp() {
    val navController = rememberNavController()
    val repository = remember { AuthRepository() }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 登录界面
        composable(Screen.Login.route) {
            val viewModel: LoginPresenter = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    // 登录成功，跳转到成功界面
                    // 这里假设 username 是最后输入的，实际应该从 result 中获取
                    navController.navigate(Screen.Success.createRoute("User"))
                }
            }

            LoginScreen(
                uiState = uiState,
                onLoginClick = { username, password ->
                    viewModel.login(username, password)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onClearErrors = {
                    viewModel.clearErrors()
                }
            )
        }

        // 注册界面
        composable(Screen.Register.route) {
            val viewModel: RegisterPresenter = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    // 注册成功，返回登录界面
                    navController.popBackStack()
                }
            }

            RegisterScreen(
                uiState = uiState,
                onRegisterClick = { username, password, confirmPassword ->
                    viewModel.register(username, password, confirmPassword)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onClearErrors = {
                    viewModel.clearErrors()
                }
            )
        }

        // 成功界面
        composable(Screen.Success.route) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "User"

            SuccessScreen(
                username = username,
                onLogoutClick = {
                    // 退出登录，返回登录界面
                    navController.popBackStack(Screen.Login.route, inclusive = true)
                    navController.navigate(Screen.Login.route)
                }
            )
        }
    }
}
