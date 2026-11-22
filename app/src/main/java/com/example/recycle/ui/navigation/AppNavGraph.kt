package com.example.recycle.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recycle.core.data.model.Profile
import com.example.recycle.feature.auth.AuthViewModel
import com.example.recycle.feature.auth.LoginScreen
import com.example.recycle.feature.auth.RegisterScreen
import com.example.recycle.feature.auth.SessionState
import com.example.recycle.feature.auth.SplashScreen
import com.example.recycle.feature.droppoint.DropPointDetailScreen
import com.example.recycle.feature.droppoint.DropPointFormScreen
import com.example.recycle.feature.droppoint.DropPointListScreen
import com.example.recycle.ui.components.ScreenWithStatusBar


// Pemilik: Bersama
// NavHost: Splash, Auth, Main, dan layar detail/form.
@Composable
fun AppNavGraph(
    authViewModel: AuthViewModel = viewModel()
){
    val navController = rememberNavController()
    val sessionState by authViewModel.sessionState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val isPengelola = (sessionState as? SessionState.LoggedIn)?.role == Profile.ROLE_PENGELOLA

    NavHost(
        navController = navController,
        startDestination = Routes.splash,
    ){
        composable(route = Routes.splash){
           SplashScreen(
               sessionState = sessionState,
               onSplashFinished = { isLoggedIn ->
                   navController.navigate(if (isLoggedIn) Routes.main else Routes.login){
                       popUpTo(Routes.splash){inclusive = true}
                   }
               }
           )
        }

        composable(route = Routes.login) {
            LaunchedEffect(sessionState) {
                if (sessionState is SessionState.LoggedIn) navController.goToMain()
            }
            ScreenWithStatusBar {
                LoginScreen(
                    authState = authState,
                    onLogin = authViewModel::login,
                    onRegisterClick = {
                        authViewModel.resetAuthState()
                        navController.navigate(Routes.register)
                    }
                )
            }
        }

        composable(route = Routes.register) {
            LaunchedEffect(sessionState) {
                if (sessionState is SessionState.LoggedIn) navController.goToMain()
            }
            ScreenWithStatusBar {
                RegisterScreen(
                    authState = authState,
                    onRegister = authViewModel::register,
                    onLoginClick = {
                        authViewModel.resetAuthState()
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(route = Routes.main) {
            val session = sessionState
            LaunchedEffect(session) {
                if (session is SessionState.LoggedOut) {
                    navController.navigate(Routes.login) {
                        popUpTo(Routes.main) { inclusive = true }
                    }
                }
            }
            if (session is SessionState.LoggedIn) {
                ScreenWithStatusBar {
                    MainScreen(
                        session = session,
                        onLogout = authViewModel::logout,
                        onOpenDropPoints = { navController.navigate(Routes.dropPoints) }
                    )
                }
            }
        }

        composable(route = Routes.dropPoints){
            RedirectWhenLoggedOut(sessionState, navController)
            DropPointListScreen(
                isPengelola = isPengelola,
                onDropPointClick = { dropPoint ->
                    dropPoint.id?.let { navController.navigate(Routes.dropPointDetail(it)) }
                },
                onNavigateToAdd = {navController.navigate((Routes.dropPointForm))
                }
            )
        }

        composable(
            route = Routes.dropPointDetail,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ){
            RedirectWhenLoggedOut(sessionState, navController)
            val dropPointId = it.arguments?.getString("id").orEmpty()
            ScreenWithStatusBar {
                DropPointDetailScreen(
                    dropPointId = dropPointId,
                    isPengelola = isPengelola,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Routes.dropPointEdit(id))
                    }
                )
            }
        }

        composable(
            route = "drop_points/form?id={id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ){ backStackEntry ->
            RedirectWhenLoggedOut(sessionState, navController)
            val editId = backStackEntry.arguments?.getString("id")
            ScreenWithStatusBar {
                DropPointFormScreen(
                    dropPointId = editId,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }
        }
    }
}

/** Jika pengguna logout, kembali ke Login dan kosongkan back stack. */
@Composable
private fun RedirectWhenLoggedOut(sessionState: SessionState, navController: NavHostController) {
    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.LoggedOut) {
            navController.navigate(Routes.login) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
}

/** Ke Main dan hapus Login/Register dari back stack agar tombol Back tidak kembali ke sana. */
private fun NavHostController.goToMain() {
    navigate(Routes.main) {
        popUpTo(graph.id) { inclusive = true }
    }
}
