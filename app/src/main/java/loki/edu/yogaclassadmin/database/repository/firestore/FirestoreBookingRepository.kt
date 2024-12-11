package loki.edu.yogaclassadmin.database.repository.firestore


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import loki.edu.yogaclassadmin.model.Booking


val BOOKING_COLLECTION = "bookings"

class FirestoreBookingRepository() {
    private val firestore = FirebaseFirestore.getInstance()
    private val bookingCollection = firestore.collection(BOOKING_COLLECTION)

    suspend fun getAllBookings(): List<Booking> {
        return try {
            val querySnapshot = bookingCollection.get().await()
            querySnapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateBooking(booking: Booking, bookingId: String): Boolean {
        return try {
            val updateData = mapOf(
                "booking_date" to booking.booking_date,
                "booking_time" to booking.booking_time,
                "instance_id" to booking.instance_id,
                "notes" to booking.notes,
                "payment_status" to booking.payment_status,
                "status" to booking.status,
                "total_amount" to booking.total_amount,
                "user_id" to booking.user_id
            )

            bookingCollection.document(bookingId).update(updateData).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreBookingRepository", "Error updating booking: ${e.message}")
            false
        }
    }

}
