package com.naufal.belimotor.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.get
import com.naufal.belimotor.ui.features.home.HomeScreen
import com.naufal.belimotor.ui.features.login.LoginScreen
import com.naufal.belimotor.ui.features.main_screen.BottomNavItem
import com.naufal.belimotor.ui.features.main_screen.MainScreen
import com.naufal.belimotor.ui.features.motor_detail.MotorDetailScreen
import com.naufal.belimotor.ui.features.profile.ProfileScreen
import com.naufal.belimotor.ui.features.profile.edit_profile.EditProfileScreen
import com.naufal.belimotor.ui.features.register.RegisterScreen
import com.naufal.belimotor.ui.features.splash.SplashScreen
import com.naufal.belimotor.ui.features.transaction.TransactionScreen

@Composable
fun BeliMotorGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                openLoginScreen = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                openMainScreen = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        loginGraph(navController)
        composable(Screen.Main.route) {
            //main screen called BottomNavigationGraph
            MainScreen(navController = navController)
        }
        profileGraph(navController)
        motorGraph(navController)
        transactionGraph(navController)
    }
}

@Composable
fun BottomNavigationGraph(
    bottomNavController: NavHostController,
    navController: NavHostController
) {
    NavHost(bottomNavController, startDestination = BottomNavItem.Home.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(
                openMotorDetail = {
                    navController.navigate(Screen.MotorDetail.createRoute(it))
                },
                openTransactionScreen = {
                    navController.navigate(Screen.Transaction.route)
                }
            )
        }

        composable(BottomNavItem.Profile.screen_route) {
            ProfileScreen(
                onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                openLoginScreen = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
    }
}

//LOGIN_ROUTE
fun NavGraphBuilder.loginGraph(navController: NavController) {
    navigation(startDestination = Screen.Login.route, route = NavigationRoute.LOGIN_ROUTE) {
        composable(Screen.Login.route) {
            LoginScreen(
                openMainScreen = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                openRegisterScreen = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                openLoginScreen = {
                    navController.navigateUp()
                }
            )
        }
    }
}

//PROFILE_ROUTE
fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation(
        startDestination = Screen.EditProfile.route,
        route = NavigationRoute.PROFLE_ROUTE
    ) {
        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                openProfileScreen = {
                    navController.navigateUp()
                }
            )
        }
    }
}

//MOTOR_ROUTE
fun NavGraphBuilder.motorGraph(navController: NavController) {
    navigation(
        startDestination = Screen.MotorDetail.route,
        route = NavigationRoute.MOTOR_ROUTE
    ) {
        composable(Screen.MotorDetail.route) { backStackEntry ->
            val motorId = backStackEntry.arguments?.getString("motorId")
            requireNotNull(motorId) { "motorId parameter wasn't found. Please make sure it's set!" }
            MotorDetailScreen(
                motorId = motorId,
                openHomeScreen = {
                    navController.navigateUp()
                }
            )
        }
    }
}

fun NavGraphBuilder.transactionGraph(navController: NavController) {
    navigation(
        startDestination = Screen.Transaction.route,
        route = NavigationRoute.TRANSACTION_ROUTE
    ) {
        composable(Screen.Transaction.route) { backStackEntry ->
            TransactionScreen(
                openHomeScreen = {
                    navController.navigateUp()
                },
                openMotorDetail = {
                    navController.navigate(Screen.MotorDetail.createRoute(it))
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
    val MOTOR_ROUTE = "motor_route"
    val LOGIN_ROUTE = "login_route"
    val PROFLE_ROUTE = "profile_route"
    val TRANSACTION_ROUTE = "transaction_route"
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object EditProfile : Screen("edit_profile")
    object MotorDetail : Screen("motor_detail/{motorId}") {
        fun createRoute(motorId: String) = "motor_detail/$motorId"
    }

    object Transaction : Screen("transaction")
}
