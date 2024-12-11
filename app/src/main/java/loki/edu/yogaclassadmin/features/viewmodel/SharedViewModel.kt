package loki.edu.yogaclassadmin.features.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import loki.edu.yogaclassadmin.model.User

class SharedViewModel : ViewModel() {
    val globalState = mutableStateOf<User?>(null)

    fun updateGlobalState(newValue: User) {
        globalState.value = newValue
    }
}