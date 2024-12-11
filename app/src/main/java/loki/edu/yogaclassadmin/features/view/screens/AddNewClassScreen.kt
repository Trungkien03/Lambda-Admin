import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import loki.edu.yogaclassadmin.features.view.components.BackButton
import loki.edu.yogaclassadmin.features.view.components.StepBubble
import loki.edu.yogaclassadmin.features.viewmodel.AddClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewClassScreen(
    navController: NavController,
    classViewModel: AddClassViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (classViewModel.step.value == 1) "Add New Class" else "Add Class Instance",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = { BackButton(navController) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StepBubble(
                        isActive = classViewModel.step.value == 1,
                        stepNumber = "1",
                        detailStep = "Add Class Details",
                        isCompleted = classViewModel.step.value > 1
                    )

                    Divider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier
                            .height(2.dp)
                            .width(30.dp)
                    )

                    StepBubble(
                        isActive = classViewModel.step.value == 2,
                        stepNumber = "2",
                        detailStep = "Confirmation",
                        isCompleted = false // Step 3 can't be completed
                    )
                }
            }

            item {
                when (classViewModel.step.value) {
                    1 -> ClassDetailsInput(classViewModel)
                    2 -> ConfirmClassScreen(navController, classViewModel)
                }
            }
        }
    }
}
