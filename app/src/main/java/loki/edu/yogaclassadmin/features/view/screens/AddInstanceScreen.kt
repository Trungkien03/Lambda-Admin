package loki.edu.yogaclassadmin.features.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import loki.edu.yogaclassadmin.features.view.components.BackButton
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.DatePickerDialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.draw.clip
import loki.edu.yogaclassadmin.features.viewmodel.AddInstanceOutSideViewModel
import loki.edu.yogaclassadmin.model.YogaClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInstanceScreen(
    navController: NavController,
    viewModel: AddInstanceOutSideViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayCalendar = Calendar.getInstance()

    LaunchedEffect(Unit) {
        viewModel.loadClasses()
    }

    var selectedClassId by remember { mutableStateOf<String?>(null) }

    // Date Picker Dialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                // Validate if the selected date is in the past
                if (selectedDate.before(todayCalendar)) {
                    viewModel.dateError.value = true
                } else {
                    viewModel.dateError.value = false
                    viewModel.instanceDate.value = dateFormat.format(selectedDate.time)
                }
            },
            todayCalendar.get(Calendar.YEAR),
            todayCalendar.get(Calendar.MONTH),
            todayCalendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = todayCalendar.timeInMillis // Restrict past dates
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
                text = "Add New Instance",
                style = MaterialTheme.typography.headlineMedium
            )

            // Class Dropdown Field
            ClassDropdownMenu(
                label = "Select Class",
                classes = viewModel.listClasses.value,
                selectedClassId = selectedClassId,
                onClassSelected = { selectedClassId = it }
            )

            if (selectedClassId == null) {
                Text("Please select a class", color = MaterialTheme.colorScheme.error)
            }

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
                onValueChange = {},
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
                Text("Date cannot be in the past", color = MaterialTheme.colorScheme.error)
            }

            // Instructor Dropdown Menu
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
                onClick = {
                    if (selectedClassId != null) {
                        viewModel.showConfirmation()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Instance")
            }
        }

        // Confirmation Dialog
        if (viewModel.showConfirmationDialog.value) {
            ConfirmationDialog(
                isEditMode = false,
                onConfirm = {
                    viewModel.hideConfirmation()
                    viewModel.saveInstance(
                        yogaClassId = selectedClassId?: "",
                        instanceId = null,
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDropdownMenu(
    label: String,
    classes: List<YogaClass>,
    selectedClassId: String?,
    onClassSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedClassName = classes.find { it.id == selectedClassId }?.title ?: label

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selectedClassName,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
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
            classes.forEach { yogaClass ->
                DropdownMenuItem(
                    text = { Text(yogaClass.title) },
                    onClick = {
                        onClassSelected(yogaClass.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

