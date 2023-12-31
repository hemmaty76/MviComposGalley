package com.sisapp.compose.presention.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sisapp.compose.presention.screen.galley.Gallery

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.GALLERY.name) {
        composable(Screens.GALLERY.name) {
            Gallery(navController)
        }
    }
}