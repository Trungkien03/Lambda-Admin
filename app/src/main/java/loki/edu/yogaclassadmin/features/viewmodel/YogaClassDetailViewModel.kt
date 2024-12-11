import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreClassTypeRepository
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreInstanceRepository
import loki.edu.yogaclassadmin.model.YogaClass
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreYogaClassRepository
import loki.edu.yogaclassadmin.model.ClassType
import loki.edu.yogaclassadmin.model.YogaClassInstance

class YogaClassDetailViewModel : ViewModel() {
    private val repository = FirestoreYogaClassRepository()
    private val instanceRepository = FirestoreInstanceRepository()
    private val classTypesRepository = FirestoreClassTypeRepository()

    // Yoga detail
    private var _yogaClass = MutableStateFlow<YogaClass?>(null)
    var yogaClass = _yogaClass.asStateFlow()
    private val _yogaType = MutableStateFlow<ClassType?>(null)
    val yogaType = _yogaType.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val classTypes = mutableStateOf<List<ClassType>>(emptyList())

    // Yoga Instances
    private val _yogaClassInstances = MutableStateFlow<List<YogaClassInstance>>(emptyList())
    val yogaClassInstances = _yogaClassInstances.asStateFlow()
    private val _isLoadingGetInstance = MutableStateFlow(false)

    init {
        getAllClassTypes()
    }

    // Load detail yoga class
    fun loadYogaClass(yogaClassId: String) {
        Log.e("YogaClassDetailViewModel", "Loading yoga class with ID: $yogaClassId")
        viewModelScope.launch {
            _isLoading.value = true
            val yogaClass = repository.getYogaClassById(yogaClassId)
            _yogaClass.value = yogaClass
            _yogaType.value = repository.getClassTypeById(yogaClass?.class_type_id.toString())
            _isLoading.value = false
        }
    }

    // Get all instances belonging to yoga class
    fun loadYogaClassInstances(yogaClassId: String) {
        viewModelScope.launch {
            _isLoadingGetInstance.value = true
            _yogaClassInstances.value = instanceRepository.getAllInstancesByClassId(yogaClassId)
            _isLoadingGetInstance.value = false
        }
    }

    // Update the class in Firestore
    fun updateYogaClass(updatedClass: YogaClass, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val isSuccess = repository.updateClass(updatedClass)
                if (isSuccess) {
                    onSuccess()
                } else {
                    onFailure(Exception("Update failed"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete the yoga class and all its instances
    fun deleteClass(yogaClassId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Delete all instances associated with the class
                val instances = instanceRepository.getAllInstancesByClassId(yogaClassId)
                instances.forEach { instance ->
                    instanceRepository.deleteInstance(instance.id)
                }

                // Delete the class itself
                val isClassDeleted = repository.deleteClass(yogaClassId)
                if (isClassDeleted) {
                    onSuccess()
                } else {
                    onFailure(Exception("Failed to delete the class"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get a specific instance by ID
    fun getInstanceById(instanceId: String, onResult: (YogaClassInstance?) -> Unit) {
        viewModelScope.launch {
            try {
                val instance = instanceRepository.getInstanceById(instanceId)
                onResult(instance)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null)
            }
        }
    }

    // Load all class types
    fun getAllClassTypes() {
        viewModelScope.launch {
            val classTypeList = classTypesRepository.getAllClassTypes()
            classTypes.value = classTypeList
            Log.e("classTypeList", "${classTypes.value}")
        }
    }
}
