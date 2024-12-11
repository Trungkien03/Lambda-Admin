package loki.edu.yogaclassadmin.features.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun BackButton(navController: NavController, icon: ImageVector = Icons.Default.ArrowBack) {
    IconButton(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .background(Color.White, shape = CircleShape)
            .size(36.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
        )
    }
}
