import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreInstanceRepository
import loki.edu.yogaclassadmin.model.YogaClassInstance

class InstanceViewModel : ViewModel() {

    private val _instances = MutableStateFlow<List<YogaClassInstance>>(emptyList())
    val instances: StateFlow<List<YogaClassInstance>> = _instances


    private val classInstanceRepository = FirestoreInstanceRepository()

    init {
        loadInstances()
    }

    // Function to load instances from a data source
     fun loadInstances() {
        viewModelScope.launch {
           val allInstances =  classInstanceRepository.getAllInstances()
            _instances.value = allInstances
        }
    }
}
