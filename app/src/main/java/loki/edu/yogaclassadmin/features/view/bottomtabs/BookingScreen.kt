package loki.edu.yogaclassadmin.features.view.bottomtabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import loki.edu.yogaclassadmin.features.view.components.BookingCard
import loki.edu.yogaclassadmin.model.Booking
import loki.edu.yogaclassadmin.features.viewmodel.BookingViewModel
import loki.edu.yogaclassadmin.utils.Helpers

@Composable
fun BookingScreen(bookingViewModel: BookingViewModel = viewModel()) {
    val bookings by bookingViewModel.bookings.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Bookings",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bookings) { booking ->
                BookingCard(booking = booking, onStatusChange = { newStatus ->
                    bookingViewModel.updateBookingStatus(booking.id, newStatus)
                })
            }
        }
    }
}




