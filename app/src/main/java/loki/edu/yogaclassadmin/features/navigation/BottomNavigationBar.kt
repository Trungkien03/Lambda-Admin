package loki.edu.yogaclassadmin.features.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Mutable state to hold the current route
    val currentRoute = remember { mutableStateOf<String?>(null) }

    // Collect the back stack entry changes
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute.value = backStackEntry.destination.route
        }
    }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp,
        modifier = Modifier.graphicsLayer {
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            clip = true
        }
    ) {
        val items = listOf(
            BottomNavItem.YogaClass,
            BottomNavItem.Instance,
            BottomNavItem.Booking,
            BottomNavItem.Transaction,
            BottomNavItem.Profile

        )

        items.forEach { item ->
            val isSelected = currentRoute.value == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall,
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4CAF50), // Green color for selected icon
                    selectedTextColor = Color(0xFF4CAF50), // Green color for selected text
                    indicatorColor = Color.Unspecified,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
            )
        }
    }
}


