package loki.edu.yogaclassadmin.features.view.screens

import YogaClassInstanceItem
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import loki.edu.yogaclassadmin.features.navigation.NavigationItem
import loki.edu.yogaclassadmin.features.view.components.BackButton
import loki.edu.yogaclassadmin.model.ClassType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import loki.edu.yogaclassadmin.utils.FirebaseStorageUtils

enum class DialogState {
    NONE, SHOW_MESSAGE, SHOW_DELETE_CONFIRMATION
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateClassScreen(
    navController: NavController, viewModel: YogaClassDetailViewModel
) {
    val yogaClass by viewModel.yogaClass.collectAsState()
    val yogaClassInstances by viewModel.yogaClassInstances.collectAsState()
    val availableClassTypes by viewModel.classTypes
    var showDialog by remember { mutableStateOf(false) }
    var dialogState by remember { mutableStateOf(DialogState.NONE) }
    var dialogMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    var updatedClass by remember { mutableStateOf(yogaClass) }
    val calendar = Calendar.getInstance()

    // Image upload states
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccess by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            isUploading = true
            FirebaseStorageUtils.uploadImage(uri, onUploadSuccess = { downloadUrl ->
                updatedClass = updatedClass?.copy(image_url = downloadUrl.toString())
                isUploading = false
                uploadSuccess = true
            }, onUploadFailure = {
                isUploading = false
                uploadSuccess = false
            })
        }
    }

    // Date and Time Pickers
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            updatedClass = updatedClass?.copy(date = dateFormat.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context, { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updatedClass = updatedClass?.copy(time = timeFormat.format(calendar.time))
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
    )

    LaunchedEffect(Unit) {
        viewModel.loadYogaClass(yogaClass?.id ?: "")
        viewModel.loadYogaClassInstances(yogaClass?.id ?: "")
    }

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .imePadding()
    ) {
        item {
            BackButton(navController)
            Text(text = "Update Class Details", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            OutlinedTextField(
                value = updatedClass?.title ?: "",
                onValueChange = { updatedClass = updatedClass?.copy(title = it) },
                label = { Text("Class Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            DropdownMenu(classTypeOptions = availableClassTypes,
                selectedClassType = updatedClass?.class_type_id,
                onClassTypeSelected = { selectedType ->
                    updatedClass = updatedClass?.copy(class_type_id = selectedType.id)
                })
        }

        item {
            OutlinedTextField(value = updatedClass?.date ?: "",
                onValueChange = {},
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { datePickerDialog.show() }, // Proper chaining of Modifier properties
                readOnly = true, // Disables manual text input
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date"
                    )
                },
                shape = RoundedCornerShape(12.dp), // Defines a consistent border radius
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                ),
                enabled = false
            )
        }




        item {
            OutlinedTextField(value = updatedClass?.time ?: "",
                onValueChange = {},
                label = { Text("Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timePickerDialog.show() }
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { timePickerDialog.show() },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Select Time",
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                ),
                enabled = false
            )
        }

        item {
            OutlinedTextField(value = updatedClass?.capacity?.toString() ?: "",
                onValueChange = {
                    updatedClass = updatedClass?.copy(capacity = it.toIntOrNull() ?: 0)
                },
                label = { Text("Capacity") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = updatedClass?.description ?: "",
                onValueChange = { updatedClass = updatedClass?.copy(description = it) },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Image Upload Section with Delete Option
        item {
            if (updatedClass?.image_url.isNullOrEmpty()) {
                Button(
                    onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isUploading) "Uploading Image..." else "Select Image")
                }
            } else {
                updatedClass?.image_url?.let { imageUrl ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Class Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                FirebaseStorageUtils.deleteImage(imageUrl = imageUrl,
                                    onDeleteSuccess = {
                                        updatedClass = updatedClass?.copy(image_url = null)
                                        uploadSuccess = false
                                    },
                                    onDeleteFailure = { exception ->
                                        dialogMessage =
                                            "Failed to delete image: ${exception.message}"
                                        dialogState = DialogState.SHOW_MESSAGE
                                        showDialog = true
                                    })
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Delete Image", color = Color.White)
                        }
                    }
                }
            }
        }

        // Add Instance Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Instances",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
                IconButton(onClick = {
                    navController.navigate(
                        NavigationItem.AddInstance.createRoute(
                            yogaClassId = yogaClass?.id ?: "", classDate = yogaClass?.date ?: ""
                        )
                    )
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Instance")
                }
            }
        }

        if (yogaClassInstances.isEmpty()) {
            item {
                Text(
                    text = "No instances available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(yogaClassInstances.size) { index ->
                val instance = yogaClassInstances[index]
                YogaClassInstanceItem(instance = instance, onClick = {
                    navController.navigate(
                        NavigationItem.EditInstance.createRoute(
                            yogaClassId = yogaClass?.id ?: "",
                            classDate = yogaClass?.date ?: "",
                            instanceId = instance.id,
                            isShowClassDetail = false
                        )
                    )
                })
            }
        }

        // Save Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        viewModel.updateYogaClass(updatedClass!!, onSuccess = {
                            dialogMessage = "Class updated successfully!"
                            dialogState = DialogState.SHOW_MESSAGE
                            showDialog = true
                        }, onFailure = { exception ->
                            dialogMessage = "Failed to update class: ${exception.message}"
                            dialogState = DialogState.SHOW_MESSAGE
                            showDialog = true
                        })
                    }, modifier = Modifier.weight(1f)
                ) {
                    Text("Save Changes")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        dialogState = DialogState.SHOW_DELETE_CONFIRMATION
                        showDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Class", color = Color.White)
                }
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
    classTypeOptions: List<ClassType>,
    selectedClassType: String?,
    onClassTypeSelected: (ClassType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { expanded = !expanded } // Mở menu khi nhấn vào toàn bộ field
    ) {
        OutlinedTextField(value = classTypeOptions.find { it.id == selectedClassType }?.name
            ?: "Select Class Type",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Class Type") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = false
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            classTypeOptions.forEach { classType ->
                DropdownMenuItem(text = { Text(classType.name) }, onClick = {
                    onClassTypeSelected(classType)
                    expanded = false
                })
            }
        }
    }
}

