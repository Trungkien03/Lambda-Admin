package loki.edu.yogaclassadmin.features.view.screens

import YogaClassInstanceItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import loki.edu.yogaclassadmin.features.navigation.NavigationItem
import loki.edu.yogaclassadmin.features.view.components.BackButton
import loki.edu.yogaclassadmin.model.ClassType
import loki.edu.yogaclassadmin.model.YogaClass
import loki.edu.yogaclassadmin.model.YogaClassInstance
import loki.edu.yogaclassadmin.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YogaClassDetailScreen(
    navController: NavController,
    yogaClassId: String,
    viewModel: YogaClassDetailViewModel
) {
    val yogaClass by viewModel.yogaClass.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val yogaClassInstances by viewModel.yogaClassInstances.collectAsState()
    val yogaClassType by viewModel.yogaType.collectAsState()

    LaunchedEffect(yogaClassId) {
        viewModel.loadYogaClass(yogaClassId)
        viewModel.loadYogaClassInstances(yogaClassId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            yogaClass != null -> {
                YogaClassDetailContent(
                    navController = navController,
                    yogaClass = yogaClass!!,
                    yogaClassInstances = yogaClassInstances.orEmpty(),
                    yogaClassType = yogaClassType
                )
            }
            else -> {
                Text(
                    text = "Error loading class details",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun YogaClassDetailContent(
    navController: NavController,
    yogaClass: YogaClass,
    yogaClassInstances: List<YogaClassInstance>,
    yogaClassType: ClassType?
) {
    val iconColor = Color(0xFF4CAF50) // Green shade for icons
    val headerBackground = Color(0xFF2E7D32) // Darker green for header gradient


    val dateWithDayOfWeek = try {
        val dayOfWeek = DateUtils.getDayOfWeek(yogaClass.date)
        "${yogaClass.date} ($dayOfWeek)"
    } catch (e: Exception) {
        yogaClass.date
    }

    // Add vertical scrolling
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Enable vertical scrolling
    ) {
        Box(modifier = Modifier.height(250.dp)) {
            AsyncImage(
                model = yogaClass.image_url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                BackButton(navController = navController)
                IconButton(
                    onClick = { navController.navigate(NavigationItem.UpdateClass.route) },
                    modifier = Modifier
                        .background(Color.White, shape = CircleShape)
                        .size(36.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Black
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, headerBackground.copy(alpha = 0.7f))
                        )
                    )
            ) {
                Text(
                    text = yogaClass.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Yoga Class Type Section
            if (yogaClassType != null) {
                SectionHeader(icon = Icons.Default.Info, title = "Yoga Class Type", iconColor = iconColor)

                // Description
                Text(
                    text = yogaClassType.description ?: "No description available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Benefits
                if (!yogaClassType.benefits.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Benefits:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        yogaClassType.benefits.forEach { benefit ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = benefit,
                                    tint = iconColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = benefit,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Capacity and Type Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, contentDescription = "Capacity", tint = iconColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${yogaClass.capacity} Capacity",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Type", tint = iconColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = yogaClassType?.name ?: "Unknown Type",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date and Time Information
            DetailRow(icon = Icons.Default.Event, label = "Date:", value = dateWithDayOfWeek, iconColor = iconColor)
            Spacer(modifier = Modifier.height(8.dp))
            DetailRow(icon = Icons.Default.Schedule, label = "Time:", value = yogaClass.time, iconColor = iconColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            SectionHeader(icon = Icons.Default.Description, title = "Description", iconColor = iconColor)
            Text(
                text = yogaClass.description ?: "No description available",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Instances
            SectionHeader(icon = Icons.Default.EventNote, title = "Instances", iconColor = iconColor)
            if (yogaClassInstances.isEmpty()) {
                Text(
                    text = "No sessions available for this class.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                    yogaClassInstances.forEach { item ->
                        YogaClassInstanceItem(instance = item)
                    }

            }
        }
    }
}



@Composable
fun DetailRow(icon: ImageVector, label: String, value: String, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String, iconColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = title, tint = iconColor)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black
        )
    }
}
