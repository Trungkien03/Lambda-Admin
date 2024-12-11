import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import loki.edu.yogaclassadmin.features.navigation.NavigationItem

@Composable
fun ProfileScreen(
    navController: NavController,
    appState: AppStateViewModel
) {
    val user = appState.user.collectAsState().value
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5) // Background color from reference image
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                // Profile Image or Initials
                user?.let {
                    if (it.profile_image.isNotEmpty()) {
                        AsyncImage(
                            model = it.profile_image,
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD8D8D8)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD8D8D8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = it.name.firstOrNull()?.toString() ?: "",
                                fontSize = 36.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Row with Icon
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = it.email,
                        iconTint = Color(0xFF4CAF50)
                    )

                    // Role Row with Icon
                    ProfileInfoRow(
                        icon = Icons.Default.Work,
                        label = "Role: ${it.role}",
                        iconTint = Color(0xFF4CAF50)
                    )

                    // Specialization Row with Icon (if available)
                    it.specialization?.let { specialization ->
                        ProfileInfoRow(
                            icon = Icons.Default.Person,
                            label = "Specialization: $specialization",
                            iconTint = Color(0xFF4CAF50)
                        )
                    }
                } ?: run {
                    Text(
                        text = "User not logged in",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign Out Button
        Button(
            onClick = { showLogoutDialog = true }, // Show dialog on click
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text(
                text = "Sign Out",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Confirm Logout") },
            text = { Text(text = "Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    appState.logout()
                    navController.navigate(NavigationItem.Login.route) {
                        popUpTo(NavigationItem.Home.route) { inclusive = true }
                    }
                }) {
                    Text("Yes", color = Color(0xFF4CAF50))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No", color = Color(0xFF4CAF50))
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, iconTint: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF333333))
        )
    }
}
