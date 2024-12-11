package loki.edu.yogaclassadmin.database.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import loki.edu.yogaclassadmin.model.Transaction

val TRANSACTION_COLLECTION = "transactions"

class FirestoreTransactionRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val transactionCollection = firestore.collection(TRANSACTION_COLLECTION)

    // Fetch all transactions
    suspend fun getAllTransactions(): List<Transaction> {
        return try {
            val querySnapshot = transactionCollection.get().await()
            querySnapshot.toObjects(Transaction::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreTransactionRepo", "Error fetching transactions: ${e.message}")
            emptyList()
        }
    }

    // Add a transaction
    suspend fun addTransaction(transaction: Transaction) {
        try {
            transactionCollection.document(transaction.id).set(transaction).await()
        } catch (e: Exception) {
            Log.e("FirestoreTransactionRepo", "Error adding transaction: ${e.message}")
        }
    }

    // Get a specific transaction by ID
    suspend fun getTransactionById(transactionId: String): Transaction? {
        return try {
            val documentSnapshot = transactionCollection.document(transactionId).get().await()
            documentSnapshot.toObject(Transaction::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreTransactionRepo", "Error fetching transaction: ${e.message}")
            null
        }
    }

    // Update a transaction
    suspend fun updateTransaction(transactionId: String, updates: Map<String, Any>) {
        try {
            transactionCollection.document(transactionId).update(updates).await()
        } catch (e: Exception) {
            Log.e("FirestoreTransactionRepo", "Error updating transaction: ${e.message}")
        }
    }

    // Delete a transaction
    suspend fun deleteTransaction(transactionId: String) {
        try {
            transactionCollection.document(transactionId).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreTransactionRepo", "Error deleting transaction: ${e.message}")
        }
    }
}
