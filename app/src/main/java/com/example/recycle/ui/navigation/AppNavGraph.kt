package com.example.recycle.ui.navigation

import android.window.SplashScreen
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recycle.feature.auth.LoginScreen
import com.example.recycle.feature.auth.SplashScreen
import com.example.recycle.ui.components.ScreenWithStatusBar


// Pemilik: Bersama
// NavHost: Splash, Auth, Main, dan layar detail/form.
@Composable
fun AppNavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.splash,
    ){
        composable(route = Routes.splash){
           SplashScreen(
               onSplashFinished = {
                   navController.navigate(Routes.login){
                       popUpTo(Routes.splash){inclusive = true}
                   }
               }
           )
        }

        composable(route = Routes.login) {
           ScreenWithStatusBar {
               LoginScreen()
           }
        }
    }
}