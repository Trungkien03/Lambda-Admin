package loki.edu.yogaclassadmin.features.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import loki.edu.yogaclassadmin.features.view.components.BackButton
import loki.edu.yogaclassadmin.features.viewmodel.AddInstanceViewModel
import loki.edu.yogaclassadmin.model.User
import loki.edu.yogaclassadmin.model.YogaClassInstance
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInstanceScreen(
    navController: NavController,
    yogaClassId: String,
    classDate: String,
    existingInstance: YogaClassInstance? = null,
    isShowClassDetail: Boolean = false,
    viewModel: AddInstanceViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val yogaClassDetail = viewModel.yogaClassDetail.value
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val minDateCalendar = Calendar.getInstance()
    minDateCalendar.time = dateFormat.parse(classDate) ?: Calendar.getInstance().time

    // Date Picker Dialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                viewModel.instanceDate.value = dateFormat.format(selectedDate.time)
            },
            minDateCalendar.get(Calendar.YEAR),
            minDateCalendar.get(Calendar.MONTH),
            minDateCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = minDateCalendar.timeInMillis
        }
    }

    LaunchedEffect(Unit) {
        if (existingInstance != null) {
            viewModel.loadInstanceData(existingInstance)
        }
        if (isShowClassDetail) {
            viewModel.getDetailYogaClass(existingInstance?.class_id ?: "")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(paddingValues)
                .imePadding()
        ) {
            BackButton(navController)
            Text(
                text = if (existingInstance != null) "Edit Instance" else "Add New Instance",
                style = MaterialTheme.typography.headlineMedium
            )

            // Title Input Field
            OutlinedTextField(
                value = viewModel.instanceTitle.value,
                onValueChange = { viewModel.instanceTitle.value = it },
                label = { Text("Title") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                isError = viewModel.titleError.value
            )
            if (viewModel.titleError.value) {
                Text("Title is required", color = MaterialTheme.colorScheme.error)
            }

            // Description Input Field
            OutlinedTextField(
                value = viewModel.instanceDescription.value,
                onValueChange = { viewModel.instanceDescription.value = it },
                label = { Text("Description") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
            )

            // Date Picker Input Field
            OutlinedTextField(
                value = viewModel.instanceDate.value,
                onValueChange = {viewModel.instanceDate.value},
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { datePickerDialog.show() },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select Date",
                    )
                },
                isError = viewModel.dateError.value,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = false
            )
            if (viewModel.dateError.value) {
                Text("Date is required", color = MaterialTheme.colorScheme.error)
            }

            InstructorDropdown(
                instructors = viewModel.instructors.value,
                selectedInstructor = viewModel.selectedInstructor.value,
                onInstructorSelected = { viewModel.selectedInstructor.value = it }
            )
            if (viewModel.instructorError.value) {
                Text("Instructor is required", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Input Field
            OutlinedTextField(
                value = viewModel.instanceNotes.value,
                onValueChange = { viewModel.instanceNotes.value = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp)),
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.showConfirmation() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (existingInstance != null) "Update Instance" else "Add Instance")
            }

            if (existingInstance != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.showDeleteConfirmation() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Instance", color = Color.White)
                }
            }
        }

        // Confirmation Dialog
        if (viewModel.showConfirmationDialog.value) {
            ConfirmationDialog(
                isEditMode = existingInstance != null,
                onConfirm = {
                    viewModel.hideConfirmation()
                    viewModel.saveInstance(
                        yogaClassId = yogaClassId,
                        instanceId = existingInstance?.id,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onFailure = {
                            // Handle save failure
                        }
                    )
                },
                onDismiss = { viewModel.hideConfirmation() }
            )
        }

        // Delete Confirmation Dialog
        if (viewModel.showDeleteDialog.value) {
            DeleteConfirmationDialog(
                onConfirm = {
                    viewModel.hideDeleteConfirmation()
                    viewModel.deleteInstance(
                        instanceId = existingInstance!!.id,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onFailure = {
                            // Handle delete failure
                        }
                    )
                },
                onDismiss = { viewModel.hideDeleteConfirmation() }
            )
        }
    }
}


@Composable
fun ConfirmationDialog(
    isEditMode: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm ${if (isEditMode) "Update" else "Addition"}") },
        text = { Text("Are you sure you want to ${if (isEditMode) "update" else "add"} this instance?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun ClassDetailItem(icon: ImageVector, label: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(0xFF4CAF50) // Green color for icon
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to delete this instance? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorDropdown(
    instructors: List<User>,
    selectedInstructor: User?,
    onInstructorSelected: (User) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selectedInstructor?.name ?: "Select Instructor",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Instructor") },
            readOnly = true,
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = false
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            instructors.forEach { instructor ->
                DropdownMenuItem(
                    text = { Text(instructor.name) },
                    onClick = {
                        onInstructorSelected(instructor)
                        expanded = false
                    }
                )
            }
        }
    }
}
