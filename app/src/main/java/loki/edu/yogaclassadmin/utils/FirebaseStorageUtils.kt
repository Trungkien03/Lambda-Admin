package loki.edu.yogaclassadmin.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object FirebaseStorageUtils {

    /**
     * Upload an image to Firebase Storage.
     * @param uri The URI of the image to upload.
     * @param onUploadSuccess Callback invoked with the download URL upon successful upload.
     * @param onUploadFailure Callback invoked with the exception upon failure.
     */
    fun uploadImage(
        uri: Uri,
        onUploadSuccess: (Uri) -> Unit,
        onUploadFailure: (Exception) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef =
            storage.reference.child("class_images/${UUID.randomUUID()}.jpg") // Unique file name
        val uploadTask = storageRef.putFile(uri)

        // Upload listener
        uploadTask.addOnSuccessListener {
            // Retrieve download URL after successful upload
            storageRef.downloadUrl.addOnSuccessListener(onUploadSuccess)
        }.addOnFailureListener(onUploadFailure)
    }

    /**
     * Delete an image from Firebase Storage.
     * @param imageUrl The URL of the image to delete.
     * @param onDeleteSuccess Callback invoked upon successful deletion.
     * @param onDeleteFailure Callback invoked with the exception upon failure.
     */
    fun deleteImage(
        imageUrl: String,
        onDeleteSuccess: () -> Unit,
        onDeleteFailure: (Exception) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl(imageUrl)

        storageRef.delete()
            .addOnSuccessListener { onDeleteSuccess() }
            .addOnFailureListener(onDeleteFailure)
    }
}
