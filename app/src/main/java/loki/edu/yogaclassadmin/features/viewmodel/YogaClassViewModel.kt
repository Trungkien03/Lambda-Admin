package loki.edu.yogaclassadmin.features.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreYogaClassRepository
import loki.edu.yogaclassadmin.model.YogaClass

class YogaClassViewModel : ViewModel() {
    private val repository = FirestoreYogaClassRepository()

    private val _yogaClasses = MutableStateFlow<List<YogaClass>>(emptyList())
    val yogaClasses = _yogaClasses.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchYogaClasses()
    }

    // Hàm để tải tất cả YogaClass từ Firestore
     fun fetchYogaClasses() {
        _isLoading.value = true
        repository.getClasses(
            onSuccess = { classes ->
                _yogaClasses.value = classes
                _isLoading.value = false
            },
            onFailure = {
                _yogaClasses.value = emptyList() // Nếu có lỗi, trả về danh sách rỗng
                _isLoading.value = false
            }
        )
    }
}
