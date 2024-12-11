package loki.edu.yogaclassadmin.database.repository.firestore


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import loki.edu.yogaclassadmin.model.User

val USER_COLLECTION = "users"

class FireStoreUserRepository() {
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollection = firestore.collection(USER_COLLECTION)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    suspend fun getUsersByRole(role: String): List<User> {
        return try {
            val querySnapshot = userCollection
                .whereEqualTo("role", role)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addUser(user: User): Boolean {
        return try {
            userCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    suspend fun loginUser(email: String, password: String): User? {
        return try {
            val querySnapshot = userCollection
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()

            querySnapshot.documents.firstOrNull()?.toObject(User::class.java) // Return the first User object if found
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null on exception
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            userCollection.document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getUserByEmail(email: String): User? {
        return try {
            val querySnapshot = userCollection
                .whereEqualTo("email", email)
                .get()
                .await()


            querySnapshot.documents.forEach { document ->
                Log.d("FirestoreQuery", "Document data: ${document.data}")
            }

            querySnapshot.documents.firstOrNull()?.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("FirestoreQuery", "Error fetching user by email: ${e.message}")
            null
        }
    }




    suspend fun updateUser(user: User): Boolean {
        return try {
            userCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(userId: String): Boolean {
        return try {
            userCollection.document(userId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
