package com.unilasalle.tp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unilasalle.tp.navigations.BottomNavigationBar
import com.unilasalle.tp.navigations.NavigationGraph
import com.unilasalle.tp.navigations.rememberNavigationState
import com.unilasalle.tp.services.database.AppDatabase
import com.unilasalle.tp.services.database.DatabaseProvider
import com.unilasalle.tp.ui.theme.TPTheme
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import com.unilasalle.tp.viewmodels.UsersViewModel
import com.unilasalle.tp.viewmodels.UsersViewModelFactory

class MainActivity : ComponentActivity(), DatabaseProvider {

    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val navigationState = rememberNavigationState()
            val cartViewModel: CartViewModel = viewModel(factory = CartViewModelFactory(this, database.cartController(), database.cartItemController()))
            val usersViewModel: UsersViewModel = viewModel(factory = UsersViewModelFactory(database.usersController()))

            val userId = intent.getStringExtra("userId") ?: ""
            usersViewModel.fetchUserById(userId)
            val user by usersViewModel.user.collectAsState()


            TPTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            BottomNavigationBar(
                                navController = navController,
                                state = navigationState,
                                count = cartViewModel.totalItemCount.collectAsState().value
                            )
                        }
                    ) { innerPadding ->
                        NavigationGraph (
                            navController = navController,
                            cartController = database.cartController(),
                            cartItemController = database.cartItemController(),
                            context = this,
                            user = user,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}