import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import loki.edu.yogaclassadmin.R
import loki.edu.yogaclassadmin.model.YogaClass
import loki.edu.yogaclassadmin.utils.DateUtils
import loki.edu.yogaclassadmin.utils.Helpers

@Composable
fun YogaClassItem(yogaClass: YogaClass, onClick: () -> Unit) {
    val iconColor = Color(0xFF4CAF50)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = yogaClass.image_url ?: R.drawable.yoga_logo,
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Title
            Text(
                text = yogaClass.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1
            )

            // Description
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = yogaClass.description ?: "No description available",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                maxLines = 2
            )

            // Date
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date",
                    tint = iconColor,
                    modifier = Modifier.size(16.dp) // Smaller icon size
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${DateUtils.getDayOfWeek(yogaClass.date)}, ${yogaClass.date}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 1
                )
            }

            // Time
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "Time",
                    tint = iconColor,
                    modifier = Modifier.size(16.dp) // Smaller icon size
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = yogaClass.time,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    maxLines = 1
                )
            }

            // Price with Wallet Icon
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = "Price",
                    tint = iconColor,
                    modifier = Modifier.size(16.dp) // Smaller icon size
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = Helpers.formatCurrencyVN(yogaClass.price),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }
        }
    }
}


