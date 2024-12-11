package loki.edu.yogaclassadmin.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        /**
         * Returns the day of the week for a given date string.
         * @param dateString A string representing a date in the format "yyyy-MM-dd".
         * @param dateFormat Format of the input date string (default is "yyyy-MM-dd").
         * @return Day of the week (e.g., "Monday", "Tuesday").
         */
        fun getDayOfWeek(dateString: String, dateFormat: String = "yyyy-MM-dd"): String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val date: Date = formatter.parse(dateString) ?: return "Invalid Date"
            val calendar = Calendar.getInstance()
            calendar.time = date
            return SimpleDateFormat("EEEE", Locale.getDefault()).format(date) // Full name of the day
        }
    }
}
