package loki.edu.yogaclassadmin.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreBookingRepository
import loki.edu.yogaclassadmin.model.Booking

class BookingViewModel : ViewModel() {
    private val repository = FirestoreBookingRepository()
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> get() = _bookings

    init {
        fetchBookings()
    }

    private fun fetchBookings() {
        viewModelScope.launch {
            _bookings.value = repository.getAllBookings()
        }
    }

    fun updateBookingStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            val booking = _bookings.value.find { it.id == bookingId }
            booking?.let {
                val updatedBooking = it.copy(status = newStatus)
                val success = repository.updateBooking(updatedBooking, updatedBooking.id)
                if (success) {
                    fetchBookings()
                }
            }
        }
    }
}
