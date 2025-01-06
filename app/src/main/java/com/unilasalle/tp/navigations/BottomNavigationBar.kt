package com.unilasalle.tp.navigations

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController, state: NavigationState) {
    NavigationBar {
        state.items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = state.selectedItemIndex == index,
                onClick = {
                    state.onItemSelected(index)
                    navController.navigate(item.route)
                },
                label = { Text(item.title) },
                icon = {
                    BadgedBox(
                        badge = {
                            item.badgeCount?.let { count ->
                                Text(text = item.badgeCount.toString())
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (state.selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}