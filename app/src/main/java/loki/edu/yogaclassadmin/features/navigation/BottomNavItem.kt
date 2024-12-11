import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Event // Add this import for the booking icon
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomTab {
    YOGA_CLASS,
    INSTANCE,
    PROFILE,
    BOOKING,
    TRANSACTION
}

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object YogaClass : BottomNavItem("Yoga Class", Icons.Filled.Home, "yoga_class")
    object Instance : BottomNavItem("Instance", Icons.Filled.Class, BottomTab.INSTANCE.name)
    object Profile : BottomNavItem("Profile", Icons.Filled.Person, BottomTab.PROFILE.name)
    object Booking : BottomNavItem("Booking", Icons.Filled.Event, BottomTab.BOOKING.name)
    object Transaction: BottomNavItem("Transaction", Icons.Filled.Event, BottomTab.TRANSACTION.name)
}

fun getBottomNavItem(tab: BottomTab): BottomNavItem {
    return when (tab) {
        BottomTab.YOGA_CLASS -> BottomNavItem.YogaClass
        BottomTab.INSTANCE -> BottomNavItem.Instance
        BottomTab.PROFILE -> BottomNavItem.Profile
        BottomTab.BOOKING -> BottomNavItem.Booking
        BottomTab.TRANSACTION -> BottomNavItem.Transaction
    }
}
