import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.model.User

class AppStateViewModel : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoadingCheckingAuth = MutableStateFlow<Boolean>(false)
    val isLoadingCheckingAuth: StateFlow<Boolean> = _isLoadingCheckingAuth.asStateFlow()

    fun setLoadingCheckingAuth(isLoading: Boolean) {
        _isLoadingCheckingAuth.value = isLoading
    }

    fun setUserLoggedIn(user: User) {
        _user.value = user
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut() // Sign out from Firebase
        viewModelScope.launch {
            _user.value = null // Clear the user state
        }
    }

    fun clearUser() {
        _user.value = null
    }
}
