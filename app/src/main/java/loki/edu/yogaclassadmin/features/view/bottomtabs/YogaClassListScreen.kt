import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import loki.edu.yogaclassadmin.features.navigation.NavigationItem
import loki.edu.yogaclassadmin.features.viewmodel.YogaClassViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun YogaClassListScreen(
    navController: NavController,
    viewModel: YogaClassViewModel = viewModel()
) {
    val yogaClasses = viewModel.yogaClasses.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchFieldFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchYogaClasses()
    }

    // Filtered classes based on search query
    val filteredClasses = yogaClasses.filter { yogaClass ->
        yogaClass.title.contains(searchQuery, ignoreCase = true) ||
                yogaClass.date.contains(searchQuery, ignoreCase = true) ||
                yogaClass.time.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF4CAF50), // Green color to match your theme
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add New Class")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .clickable {
                    keyboardController?.hide()
                    isSearchFieldFocused = false
                }
                .padding(horizontal = 10.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yoga Class (${yogaClasses.size})", // Total count displayed here
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(
                    onClick = { viewModel.fetchYogaClasses() } // Refresh button to re-fetch classes
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Refresh Classes",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isSearchFieldFocused = focusState.isFocused
                    },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        isSearchFieldFocused = false
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray
                )
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredClasses.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No classes available",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredClasses.size) { index ->
                        YogaClassItem(
                            yogaClass = filteredClasses[index],
                            onClick = { navController.navigate(NavigationItem.Detail.createRoute(filteredClasses[index].id)) }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Class") },
            text = { Text("Do you want to add a new yoga class?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate(NavigationItem.AddNewClass.route)
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}


@Composable
fun DashboardView() {
    // Card data
    val items = listOf(
        Triple("Yoga Class", "23", Color(0xFF4CAF50)), // Green
        Triple("Users", "12", Color(0xFFF44336)),         // Red
        Triple("Transactions", "35", Color(0xFFFFC107)),        // Yellow
        Triple("Bookings", "None", Color(0xFF2196F3))      // Blue
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.take(2).forEach { (title, count, color) ->
                DashboardCard(title, count, color, Modifier.weight(1f).padding(end = 8.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.drop(2).forEach { (title, count, color) ->
                DashboardCard(title, count, color, Modifier.weight(1f).padding(end = 8.dp))
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
        }
    }
}

