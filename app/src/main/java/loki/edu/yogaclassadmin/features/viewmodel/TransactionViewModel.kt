package loki.edu.yogaclassadmin.features.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.model.Transaction
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreTransactionRepository

class TransactionViewModel : ViewModel() {
    private val transactionRepository = FirestoreTransactionRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Fetch transactions from Firestore
    fun fetchTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val fetchedTransactions = transactionRepository.getAllTransactions()
                _transactions.value = fetchedTransactions
            } catch (e: Exception) {
                _transactions.value = emptyList()
                // Optionally log or handle the error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
