import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import loki.edu.yogaclassadmin.model.YogaClassInstance

@Composable
fun YogaClassInstanceItem(
    instance: YogaClassInstance,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Display instance title
            Text(
                text = instance.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp)) // Consistent spacing between each row

            // Date Row
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF4CAF50) // Green color for icon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Date: ${instance.instance_date}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing between each row

            // Time Row
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Time Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF4CAF50) // Green color for icon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Time: ${instance.instance_date}", // Adjusted to instance time if available
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing between each row

            // Location Row
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF4CAF50) // Green color for icon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Location: ${instance.description}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // Consistent spacing between each row

            // Notes Row
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Icon(
                    imageVector = Icons.Default.Notes,
                    contentDescription = "Notes Icon",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF4CAF50) // Green color for icon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Notes: ${instance.notes ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                )
            }
        }
    }
}
