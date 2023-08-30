package com.naufal.belimotor.ui.features.main_screen

import com.naufal.belimotor.R

sealed class BottomNavItem(var title: String, var icon: Int, var screen_route: String) {

    object Home : BottomNavItem("Home", R.drawable.baseline_home_24, "home")
    object Profile : BottomNavItem("Profile", R.drawable.baseline_person_24, "profile")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Profile,
)