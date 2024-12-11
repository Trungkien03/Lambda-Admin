package loki.edu.yogaclassadmin.database.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import loki.edu.yogaclassadmin.model.ClassType


val CLASS_TYPE_COLLECTION = "class_types"

class FirestoreClassTypeRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val instanceCollection = firestore.collection(CLASS_TYPE_COLLECTION)


    suspend fun getAllClassTypes(): List<ClassType> {
        return try {
            val querySnapshot = instanceCollection.get().await()
            querySnapshot.toObjects(ClassType::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreClassTypeRepository", "Error fetching classTypes: ${e.message}")
            emptyList()
        }
    }
}