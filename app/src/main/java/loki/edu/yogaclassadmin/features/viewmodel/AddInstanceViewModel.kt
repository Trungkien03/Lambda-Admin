package loki.edu.yogaclassadmin.features.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreInstanceRepository
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreYogaClassRepository
import loki.edu.yogaclassadmin.database.repository.firestore.FireStoreUserRepository
import loki.edu.yogaclassadmin.model.User
import loki.edu.yogaclassadmin.model.YogaClass
import loki.edu.yogaclassadmin.model.YogaClassInstance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddInstanceViewModel : ViewModel() {
    private val fireStoreUserRepository = FireStoreUserRepository()
    private val instanceRepository = FirestoreInstanceRepository()
    private val yogaClassRepository = FirestoreYogaClassRepository()

    val yogaClassDetail = mutableStateOf<YogaClass?>(null)

    // UI State
    var instanceTitle = mutableStateOf("")
    var instanceDescription = mutableStateOf("")
    var instanceDate = mutableStateOf("")
    var instanceNotes = mutableStateOf("")
    var selectedInstructor = mutableStateOf<User?>(null)

    var showConfirmationDialog = mutableStateOf(false)
    var showDeleteDialog = mutableStateOf(false)
    var snackbarMessage = mutableStateOf<String?>(null)
    var instructors = mutableStateOf<List<User>>(emptyList())

    // Error states for validation
    var titleError = mutableStateOf(false)
    var dateError = mutableStateOf(false)
    var instructorError = mutableStateOf(false)
    var notesError = mutableStateOf(false)

    init {
        loadInstructors()
    }

    private fun loadInstructors() {
        viewModelScope.launch {
            val lecturerList = fireStoreUserRepository.getUsersByRole("instructor")
            instructors.value = lecturerList
        }
    }

    fun loadInstanceData(instance: YogaClassInstance) {
        instanceTitle.value = instance.title
        instanceDescription.value = instance.description
        instanceDate.value = instance.instance_date
        instanceNotes.value = instance.notes ?: ""
        selectedInstructor.value = instructors.value.find { it.id == instance.instructor_id }
    }

    fun validateFields(): Boolean {
        titleError.value = instanceTitle.value.isBlank()
        dateError.value = instanceDate.value.isBlank()
        instructorError.value = selectedInstructor.value == null
        notesError.value = instanceNotes.value.isBlank()

        return !titleError.value && !dateError.value && !instructorError.value && !notesError.value
    }

    private fun buildInstance(yogaClassId: String, instanceId: String? = null): YogaClassInstance {
        val generatedId = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        return YogaClassInstance(
            id = instanceId ?: generatedId,
            class_id = yogaClassId,
            title = instanceTitle.value,
            description = instanceDescription.value,
            instance_date = instanceDate.value,
            instructor_id = selectedInstructor.value?.id ?: "",
            notes = instanceNotes.value
        )
    }

    fun saveInstance(
        yogaClassId: String,
        instanceId: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (!validateFields()) return

        val instance = buildInstance(yogaClassId, instanceId)

        viewModelScope.launch {
            try {
                val result = if (instanceId != null) {
                    instanceRepository.updateInstance(instance, instanceId)
                } else {
                    instanceRepository.addInstance(instance)
                }

                if (result) {
                    snackbarMessage.value = "Instance saved successfully!"
                    onSuccess()
                } else {
                    onFailure(Exception("Failed to save instance"))
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun deleteInstance(instanceId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val result = instanceRepository.deleteInstance(instanceId)
                if (result) {
                    snackbarMessage.value = "Instance deleted successfully!"
                    onSuccess()
                } else {
                    onFailure(Exception("Failed to delete instance"))
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun getDetailYogaClass(yogaClassId: String) {
        Log.e("YogaClassId", "$yogaClassId")
        viewModelScope.launch {
            try {
                val yogaClass = yogaClassRepository.getYogaClassById(yogaClassId)
                yogaClassDetail.value = yogaClass
            } catch (e: Exception) {
                Log.e("Error", "${e.printStackTrace()}")
            }
        }
    }

    fun showConfirmation() {
        showConfirmationDialog.value = true
    }

    fun hideConfirmation() {
        showConfirmationDialog.value = false
    }

    fun showDeleteConfirmation() {
        showDeleteDialog.value = true
    }

    fun hideDeleteConfirmation() {
        showDeleteDialog.value = false
    }
}



