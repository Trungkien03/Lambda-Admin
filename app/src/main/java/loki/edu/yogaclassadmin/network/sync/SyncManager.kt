package loki.edu.yogaclassadmin.network.sync

import android.content.Context
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreYogaClassRepository
import loki.edu.yogaclassadmin.database.repository.sqlite.SQLiteYogaClassRepository
import loki.edu.yogaclassadmin.network.NetworkUtils

class SyncManager(
    context: Context,
    private val firestoreRepository: FirestoreYogaClassRepository,
    private val sqliteRepository: SQLiteYogaClassRepository
) {

    private val isNetworkAvailable = NetworkUtils.isNetworkAvailable(context)

    fun syncClasses() {
        if (isNetworkAvailable) {
            val offlineClasses = sqliteRepository.getClasses()
            offlineClasses.forEach { yogaClass ->
                firestoreRepository.addClass(yogaClass,
                    onSuccess = { sqliteRepository.deleteClass(yogaClass.id) },  // Clear local data after successful sync
                    onFailure = { /* Handle sync failure */ }
                )
            }
        }
    }
}
