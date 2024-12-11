import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.sqlite.SQLiteYogaClassRepository
import loki.edu.yogaclassadmin.features.view.components.ClassTypeDropdownMenu
import loki.edu.yogaclassadmin.features.viewmodel.AddClassViewModel
import loki.edu.yogaclassadmin.model.YogaClass
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Upload
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import loki.edu.yogaclassadmin.utils.FirebaseStorageUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailsInput(classViewModel: AddClassViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val repository = remember { SQLiteYogaClassRepository(context) }
    val coroutineScope = rememberCoroutineScope()


    // State to track if there’s an existing class
    var existingClassId by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadSuccess by remember { mutableStateOf(false) }
    var capacityValue by remember { mutableStateOf(classViewModel.capacity.value ?: 0) }
    var priceText by remember { mutableStateOf(classViewModel.price.value?.toString() ?: "") }
    var isPriceError by remember { mutableStateOf(false) }

    // Load data from SQLite if available
    LaunchedEffect(Unit) {
        val savedClass = repository.getFirstClass()
        savedClass?.let { saved ->
            existingClassId = saved.id
            classViewModel.title.value = saved.title
            classViewModel.description.value = saved.description ?: ""
            classViewModel.date.value = saved.date
            classViewModel.time.value = saved.time
            classViewModel.price.value = saved.price
            classViewModel.capacity.value = saved.capacity
            classViewModel.imageUrl.value = saved.image_url ?: ""
            classViewModel.selectedType.value = classViewModel.classTypes.value
                .find { it.id == saved.class_type_id }

        }
    }

    // Launcher for selecting an image from the device
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    Text(
        text = "Class Details",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary
    )

    OutlinedTextField(
        value = classViewModel.title.value,
        onValueChange = { input ->
            classViewModel.title.value = input
            classViewModel.titleError.value = input.isEmpty()
        },
        label = { Text("Class Title") },
        isError = classViewModel.titleError.value,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        singleLine = true
    )
// Description Field
    OutlinedTextField(
        value = classViewModel.description.value,
        onValueChange = { classViewModel.description.value = it },
        label = { Text("Description (Optional)") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done), // Disable Enter
        singleLine = true // Prevent multi-line input
    )


    // Date Picker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            classViewModel.date.value =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis()
    }

    OutlinedTextField(
        value = classViewModel.date.value,
        onValueChange = { },
        label = { Text("Date (YYYY-MM-DD)") },
        trailingIcon = {
            Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }, // Hiển thị picker khi nhấn vào trường
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
        ),
        enabled = false
    )


    // Time Picker
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            classViewModel.time.value = String.format("%02d:%02d", hour, minute)
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        true
    )

    OutlinedTextField(
        value = classViewModel.time.value,
        onValueChange = { },
        label = { Text("Time (HH:MM)") },
        trailingIcon = {
            Icon(Icons.Default.AccessTime, contentDescription = "Pick Time")
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { timePickerDialog.show() }, // Hiển thị picker khi nhấn vào trường
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
        ),
        enabled = false
    )


    ClassTypeDropdownMenu(
        selectedType = classViewModel.selectedType.value?.id,
        onTypeSelected = { selectedTypeId ->
            classViewModel.selectedType.value =
                classViewModel.classTypes.value.find { it.id == selectedTypeId }
        },
        classTypes = classViewModel.classTypes.value
    )

    OutlinedTextField(
        value = priceText,
        onValueChange = { input ->
            val validatedInput = input.toDoubleOrNull()
            if (validatedInput != null && validatedInput >= 0) {
                isPriceError = false
                priceText = validatedInput.toString()
                classViewModel.price.value = validatedInput
            } else {
                isPriceError = input.isNotEmpty() // Show error for invalid input
                priceText = input // Retain the raw input, even if invalid
            }
        },
        label = { Text("Price") },
        isError = isPriceError,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true, // Prevent multiline input
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )

    if (isPriceError) {
        Text(
            text = "Please enter a valid price (≥ 0)",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }

    Text(
        text = "Capacity: $capacityValue",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )

    Slider(
        value = capacityValue.toFloat(),
        onValueChange = { capacityValue = it.toInt() },
        valueRange = 0f..100f, // Adjust the range as needed
        steps = 99, // Number of discrete steps (0-100)
        onValueChangeFinished = {
            classViewModel.capacity.value = capacityValue
        },
        modifier = Modifier.fillMaxWidth()
    )


    // Image upload section
    if (!uploadSuccess && selectedImageUri == null) {
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Upload, contentDescription = "Upload Image")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Select Image")
        }
    }

    selectedImageUri?.let { uri ->
        if (!uploadSuccess && !isUploading) {
            // Trigger upload immediately after selection
            LaunchedEffect(uri) {
                isUploading = true
                FirebaseStorageUtils.uploadImage(
                    uri = uri,
                    onUploadSuccess = { downloadUrl ->
                        classViewModel.imageUrl.value = downloadUrl.toString()
                        uploadSuccess = true
                        isUploading = false
                    },
                    onUploadFailure = {
                        isUploading = false
                        Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        // Show loading indicator during upload
        if (isUploading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Uploading image...", style = MaterialTheme.typography.bodyMedium)
            }
        } else if (uploadSuccess) {
            // Show image preview and delete button after successful upload
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(classViewModel.imageUrl.value),
                    contentDescription = "Uploaded Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )

                Button(
                    onClick = {
                        FirebaseStorageUtils.deleteImage(
                            imageUrl = classViewModel.imageUrl.value,
                            onDeleteSuccess = {
                                uploadSuccess = false
                                selectedImageUri = null
                                classViewModel.imageUrl.value = ""
                            },
                            onDeleteFailure = {
                                Toast.makeText(context, "Failed to delete image: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Image")
                }
            }
        }
    }


    // Next Button
    Button(
        onClick = {
            coroutineScope.launch {
                val yogaClass = YogaClass(
                    id = existingClassId ?: UUID.randomUUID()
                        .toString(), // Use existing ID if available, otherwise generate a new one
                    title = classViewModel.title.value,
                    date = classViewModel.date.value,
                    time = classViewModel.time.value,
                    price = classViewModel.price.value ?: 0.0,
                    capacity = classViewModel.capacity.value ?: 0,
                    description = classViewModel.description.value,
                    image_url = classViewModel.imageUrl.value,
                    class_type_id = (classViewModel.selectedType.value ?: "").toString()
                )

                if (existingClassId == null) {
                    // Add new class if no existing entry
                    repository.addClass(yogaClass)
                } else {
                    // Update existing class
                    repository.updateClass(yogaClass)
                }
                classViewModel.onNext() // Proceed to the next step
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        enabled = classViewModel.isValid()
    ) {
        Text(text = "Next: Confirmation", fontSize = 16.sp)
    }

}