package loki.edu.yogaclassadmin.features.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreClassTypeRepository
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreYogaClassRepository
import loki.edu.yogaclassadmin.model.ClassType
import loki.edu.yogaclassadmin.model.YogaClass
import java.text.SimpleDateFormat
import java.util.*

class AddClassViewModel : ViewModel() {
    private val classTypesRepository = FirestoreClassTypeRepository()
    private val classRepository = FirestoreYogaClassRepository()

    var step = mutableStateOf(1)

    // Class Details
    var title = mutableStateOf("")
    var date = mutableStateOf("")
    var time = mutableStateOf("")
    var selectedType = mutableStateOf<ClassType?>(null)
    var price = mutableStateOf<Double?>(0.0) // Default price to 0.0
    var imageUrl = mutableStateOf("")
    var capacity = mutableStateOf<Int?>(1) // Default capacity to 1
    var description = mutableStateOf("")

    // Error states
    var titleError = mutableStateOf(false)
    var dateError = mutableStateOf(false)
    var timeError = mutableStateOf(false)
    var priceError = mutableStateOf(false)
    var capacityError = mutableStateOf(false)

    val classTypes = mutableStateOf<List<ClassType>>(emptyList())

    init {
        getAllClassTypes()
    }

    // Add the class to Firestore
    fun insertClassToFirestore(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            val id = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(Date())

            val classData = YogaClass(
                id = id,
                title = title.value,
                date = date.value,
                time = time.value,
                class_type_id = selectedType.value?.id ?: "",
                price = price.value ?: 0.0,
                image_url = imageUrl.value,
                capacity = capacity.value ?: 0,
                description = description.value
            )

            classRepository.addClass(classData, onSuccess, onFailure)
        }
    }

    // Validates all fields and returns a boolean
    fun isValid(): Boolean {
        return validateFieldsAndMarkErrors()
    }

    // Validates all fields, marks errors, and returns a boolean
    private fun validateFieldsAndMarkErrors(): Boolean {
        titleError.value = title.value.length < 6 || !title.value.matches(Regex("^[a-zA-Z0-9 _-]*$"))
        dateError.value = date.value.isEmpty()
        timeError.value = time.value.isEmpty()
        priceError.value = price.value == null || price.value!! <= 0.0
        capacityError.value = capacity.value == null || capacity.value!! <= 0
        return !titleError.value && !dateError.value && !timeError.value && !priceError.value && !capacityError.value
    }

    // Moves to the next step, if valid
    fun onNext() {
        if (validateFieldsAndMarkErrors()) {
            if (step.value == 1) {
                step.value = 2 // Move to Step 2
            } else if (step.value == 2) {
                step.value = 3 // Move to Step 3 (Confirmation)
            }
        }
    }

    // Fetch all class types from Firestore
    fun getAllClassTypes() {
        viewModelScope.launch {
            try {
                val classTypeList = classTypesRepository.getAllClassTypes()
                classTypes.value = classTypeList
                Log.d("AddClassViewModel", "Fetched class types: $classTypeList")
            } catch (e: Exception) {
                Log.e("AddClassViewModel", "Error fetching class types: ${e.message}")
            }
        }
    }
}
