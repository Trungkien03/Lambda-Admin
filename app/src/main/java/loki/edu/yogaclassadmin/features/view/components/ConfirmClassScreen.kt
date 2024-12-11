import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import loki.edu.yogaclassadmin.database.repository.sqlite.SQLiteYogaClassRepository
import loki.edu.yogaclassadmin.features.viewmodel.AddClassViewModel

@Composable
fun ConfirmClassScreen(navController: NavController, classViewModel: AddClassViewModel) {
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val showMessageDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }
    val appGreenColor = Color(0xFF4CAF50)
    val context = LocalContext.current
    val repository = remember { SQLiteYogaClassRepository(context) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Please confirm your details:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        DetailRow(icon = Icons.Default.Title, label = "Class Title", value = classViewModel.title.value, iconColor = appGreenColor)

        // Format date to include day of the week
        val dateWithDayOfWeek = remember(classViewModel.date.value) {
            formatDateWithDayOfWeek(classViewModel.date.value)
        }
        DetailRow(icon = Icons.Default.CalendarToday, label = "Date", value = dateWithDayOfWeek, iconColor = appGreenColor)

        DetailRow(icon = Icons.Default.AccessTime, label = "Time", value = classViewModel.time.value, iconColor = appGreenColor)
        DetailRow(icon = Icons.Default.Info, label = "Class Type", value = classViewModel.selectedType.value?.name ?: "Not selected", iconColor = appGreenColor)
        DetailRow(icon = Icons.Default.AttachMoney, label = "Price", value = classViewModel.price.value?.toString() ?: "Not provided", iconColor = appGreenColor)
        DetailRow(icon = Icons.Default.People, label = "Capacity", value = classViewModel.capacity.value?.toString() ?: "Not provided", iconColor = appGreenColor)
        DetailRow(icon = Icons.Default.Description, label = "Description", value = classViewModel.description.value.ifEmpty { "No description" }, iconColor = appGreenColor)

        if (classViewModel.imageUrl.value.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(classViewModel.imageUrl.value),
                contentDescription = "Class Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = 12.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        } else {
            DetailRow(icon = Icons.Default.Image, label = "Image URL", value = "No image provided", iconColor = appGreenColor)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { classViewModel.step.value = 1 },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Edit", color = Color.Black)
            }
            Button(
                onClick = { showConfirmationDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = appGreenColor)
            ) {
                Text("Confirm", color = Color.White)
            }
        }

        if (showConfirmationDialog.value) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmationDialog.value = false
                    classViewModel.insertClassToFirestore(
                        onSuccess = {
                            dialogMessage.value = "Class added successfully!"
                            showMessageDialog.value = true
                        },
                        onFailure = {
                            dialogMessage.value = "Failed to add class: ${it.message}"
                            showMessageDialog.value = true
                        }
                    )
                },
                onDismiss = { showConfirmationDialog.value = false }
            )
        }

        if (showMessageDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showMessageDialog.value = false
                    repository.deleteAllClasses()
                    if (dialogMessage.value == "Class added successfully!") {
                        navController.popBackStack()
                    }
                },
                title = { Text("Result") },
                text = { Text(dialogMessage.value) },
                confirmButton = {
                    TextButton(onClick = {
                        showMessageDialog.value = false
                        repository.deleteAllClasses()
                        if (dialogMessage.value == "Class added successfully!") {
                            navController.popBackStack()
                        }
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String, iconColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp) // Smaller icon size
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$label: ",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

// Helper function to format date with day of the week
fun formatDateWithDayOfWeek(dateString: String): String {
    return try {
        val inputFormatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val outputFormatter = java.text.SimpleDateFormat("EEEE, MMM dd, yyyy", java.util.Locale.getDefault())
        val date = inputFormatter.parse(dateString)
        outputFormatter.format(date ?: "")
    } catch (e: Exception) {
        dateString // Return the original date string in case of error
    }
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Submission") },
        text = { Text("Are you sure you want to add this class to Firestore?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes", color = Color(0xFF4CAF50))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No", color = Color.Gray)
            }
        }
    )
}
