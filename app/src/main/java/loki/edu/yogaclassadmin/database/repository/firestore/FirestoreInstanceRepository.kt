package loki.edu.yogaclassadmin.database.repository.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import loki.edu.yogaclassadmin.model.YogaClassInstance


val INSTANCES_COLLECTION = "instances"

class FirestoreInstanceRepository ( ) {

    private val firestore = FirebaseFirestore.getInstance()
    private val instanceCollection = firestore.collection(INSTANCES_COLLECTION)


    suspend fun addInstance(instance: YogaClassInstance): Boolean {
        return try {
            instanceCollection.document(instance.id).set(instance).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreInstanceRepository", "Error fetching instances: ${e.message}")
            false
        }
    }

    suspend fun updateInstance(instance: YogaClassInstance, instanceId: String): Boolean {
        return try {
            val updateData = mapOf(
                "title" to instance.title,
                "description" to instance.description,
                "instance_date" to instance.instance_date,
                "instructor_id" to instance.instructor_id,
                "notes" to instance.notes,
                "class_id" to instance.class_id
            )

            instanceCollection.document(instanceId).update(updateData).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreInstanceRepository", "Error updating instance: ${e.message}")
            false
        }
    }


    suspend fun getAllInstances(): List<YogaClassInstance> {
        return try {
            val querySnapshot = instanceCollection.get().await()
            querySnapshot.toObjects(YogaClassInstance::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreInstanceRepository", "Error fetching instances: ${e.message}")
            emptyList()
        }
    }

    suspend fun getAllInstancesByClassId(classId: String): List<YogaClassInstance> {
        return try {
            val querySnapshot = instanceCollection.whereEqualTo("class_id", classId).get().await()
            querySnapshot.toObjects(YogaClassInstance::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreInstanceRepository", "Error fetching instances: ${e.message}")
            emptyList()
        }
    }

    suspend fun getInstanceById(instanceId: String): YogaClassInstance? {
        return try {
            instanceCollection.document(instanceId).get().await().toObject(YogaClassInstance::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreInstanceRepository", "Error fetching instances: ${e.message}")
            null
        }
    }



    suspend fun deleteInstance(instanceId: String): Boolean {
        return try {
            instanceCollection.document(instanceId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreInstanceRepository", "Error fetching instances: ${e.message}")
            false
        }
    }
}