package loki.edu.yogaclassadmin.model

data class Booking(
    val id: String = "",
    val instance_id: String = "",
    val user_id: String = "",
    val booking_date: String = "",
    val booking_time: String = "",
    val status: String = "confirmed",
    val payment_status: String = "pending",
    val total_amount: Double = 0.0,
    val notes: String = ""
)
