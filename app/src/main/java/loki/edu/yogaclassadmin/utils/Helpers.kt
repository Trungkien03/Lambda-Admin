package loki.edu.yogaclassadmin.utils

import java.text.NumberFormat
import java.util.*

class Helpers {
    companion object {
        fun formatCurrencyVN(number: Number): String {
            val localeVN = Locale("vi", "VN")
            val formatter = NumberFormat.getCurrencyInstance(localeVN)
            return formatter.format(number).replace("₫", "VNĐ")
        }
    }
}
