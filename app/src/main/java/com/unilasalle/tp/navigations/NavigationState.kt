package com.unilasalle.tp.navigations

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable

/**
 * Remember the navigation state.
 *
 * @return The navigation state.
 */
@Composable
fun rememberNavigationState(): NavigationState {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    val bottomNavigationItems = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            route = "home"
        ),
        BottomNavigationItem(
            title = "Cart",
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart,
            route = "cart"
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            route = "profile"
        )
    )

    return NavigationState(
        selectedItemIndex = selectedItemIndex,
        onItemSelected = { selectedItemIndex = it },
        items = bottomNavigationItems
    )
}

data class NavigationState(
    val selectedItemIndex: Int,
    val onItemSelected: (Int) -> Unit,
    val items: List<BottomNavigationItem>
)