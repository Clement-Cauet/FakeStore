package com.unilasalle.tp.navigations

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.ui.screens.HomeScreen
import com.unilasalle.tp.ui.screens.CartScreen
import com.unilasalle.tp.ui.screens.ProfileScreen
import com.unilasalle.tp.services.database.entities.User

@Composable
fun NavigationGraph(navController: NavHostController, cartItemController: CartItemController, context: Context, user: User?, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen(cartItemController) }
        composable("cart") { CartScreen(cartItemController) }
        composable("profile") { ProfileScreen(context, user) }
    }
}