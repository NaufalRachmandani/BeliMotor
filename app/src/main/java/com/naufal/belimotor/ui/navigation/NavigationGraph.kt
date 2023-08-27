package com.naufal.belimotor.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.get
import com.naufal.belimotor.ui.home.HomeScreen
import com.naufal.belimotor.ui.login.LoginScreen
import com.naufal.belimotor.ui.main_screen.BottomNavItem
import com.naufal.belimotor.ui.main_screen.MainScreen
import com.naufal.belimotor.ui.profile.ProfileScreen
import com.naufal.belimotor.ui.register.RegisterScreen
import com.naufal.belimotor.ui.splash.SplashScreen

@Composable
fun BeliMotorGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.SPLASH
    ) {
        composable(NavigationRoute.SPLASH) {
            SplashScreen(
                openLoginScreen = {
                    navController.navigate(NavigationRoute.LOGIN_ROUTE)
                },
                openMainScreen = {
                    navController.navigate(NavigationRoute.MAIN)
                }
            )
        }
        loginGraph(navController)
        composable(NavigationRoute.MAIN) {
            //main screen called BottomNavigationGraph
            MainScreen()
        }
    }
}

@Composable
fun BottomNavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen()
        }
        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen()
        }
    }
}

//LOGIN_ROUTE
fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = NavigationRoute.LOGIN, route = NavigationRoute.LOGIN_ROUTE) {
        composable(NavigationRoute.LOGIN) {
            LoginScreen(
                openMainScreen = {
                    navController.navigate(NavigationRoute.MAIN)
                },
                openRegisterScreen = {
                    navController.navigate(NavigationRoute.REGISTER)
                }
            )
        }

        composable(NavigationRoute.REGISTER) {
            RegisterScreen(
                openLoginScreen = {
                    navController.navigateUp()
                }
            )
        }
    }
}

fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class], content).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}

object NavigationRoute {
    val SPLASH = "splash"
    val LOGIN = "login"
    val REGISTER = "register"
    val MAIN = "main"


    val LOGIN_ROUTE = "login_route"
}
