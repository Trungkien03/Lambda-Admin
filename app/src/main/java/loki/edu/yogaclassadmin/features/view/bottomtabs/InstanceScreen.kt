import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import loki.edu.yogaclassadmin.features.navigation.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceScreen(
    navController: NavController,
    instanceViewModel: InstanceViewModel = viewModel()
) {
    val instances = instanceViewModel.instances.collectAsState().value

    LaunchedEffect(Unit) {
        instanceViewModel.loadInstances()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavigationItem.AddInstanceOutSide.route)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Instance")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp) // Remove vertical padding
            ) {
                Text(
                    text = "Instances",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(paddingValues)

                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(instances) { instance ->
                        YogaClassInstanceItem(
                            instance = instance,
                            onClick = {
                                navController.navigate(
                                    NavigationItem.EditInstance.createRoute(
                                        yogaClassId = instance.class_id,
                                        classDate = instance.instance_date,
                                        instanceId = instance.id,
                                        isShowClassDetail = true
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    )
}
