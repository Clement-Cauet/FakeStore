package com.unilasalle.tp.navigations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Bottom navigation bar composable.
 *
 * @param navController The navigation controller.
 * @param state The navigation state.
 * @param count The count of items in the cart.
 */
@Composable
fun BottomNavigationBar(navController: NavController, state: NavigationState, count: Int) {
    NavigationBar {
        state.items.forEachIndexed { index, item ->
            val badgeCount = if (item.route == "cart" && count > 0) count else null
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
                            badgeCount?.let { count ->
                                Box(
                                    modifier = Modifier
                                        .background(Color.Red, shape = CircleShape)
                                        .padding(start = 4.dp, end = 4.dp)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 10.sp,
                                        color = Color.White
                                    )
                                }
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