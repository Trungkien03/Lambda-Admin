package loki.edu.yogaclassadmin.features.navigation

import AddNewClassScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import loki.edu.yogaclassadmin.features.view.screens.LoginScreen
import loki.edu.yogaclassadmin.features.view.screens.MainScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import loki.edu.yogaclassadmin.features.view.screens.AddEditInstanceScreen
import loki.edu.yogaclassadmin.features.view.screens.AddInstanceScreen
import loki.edu.yogaclassadmin.features.view.screens.UpdateClassScreen
import loki.edu.yogaclassadmin.features.view.screens.YogaClassDetailScreen
import loki.edu.yogaclassadmin.model.YogaClassInstance

enum class Screen {
    HOME,
    LOGIN,
    ADD_NEW_CLASS,
    UPDATE_CLASS,
    ADD_INSTANCE_OUTSIDE,
}

sealed class NavigationItem(val route: String) {
    object Home : NavigationItem(Screen.HOME.name)
    object Login : NavigationItem(Screen.LOGIN.name)
    object AddNewClass : NavigationItem(Screen.ADD_NEW_CLASS.name)
    object Detail : NavigationItem("detail/{yogaClassId}") {
        fun createRoute(yogaClassId: String) = "detail/$yogaClassId"
    }
    object UpdateClass : NavigationItem(Screen.UPDATE_CLASS.name)
    object AddInstance : NavigationItem("add_instance/{yogaClassId}/{classDate}") {
        fun createRoute(yogaClassId: String, classDate: String) =
            "add_instance/$yogaClassId/$classDate"
    }
    object EditInstance : NavigationItem("edit_instance/{yogaClassId}/{classDate}/{instanceId}/{isShowClassDetail}") {
        fun createRoute(yogaClassId: String, classDate: String, instanceId: String, isShowClassDetail: Boolean) =
            "edit_instance/$yogaClassId/$classDate/$instanceId/$isShowClassDetail"
    }
    object AddInstanceOutSide: NavigationItem(Screen.ADD_INSTANCE_OUTSIDE.name)

}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = NavigationItem.Login.route,
    appStateViewModel: AppStateViewModel = viewModel(),
    loginViewModel: LoginViewModel = viewModel(),
    detailViewModel: YogaClassDetailViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoading by appStateViewModel.isLoadingCheckingAuth.collectAsState()

    LaunchedEffect(currentUser) {
        appStateViewModel.setLoadingCheckingAuth(true)

        if (currentUser != null) {
            loginViewModel.checkUserInDatabaseAfterGoogleSignIn(currentUser.email ?: "") { userExists ->
                if (userExists) {
                    loginViewModel.currentUser.value?.let { user ->
                        appStateViewModel.setUserLoggedIn(user)
                    }
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Login.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(NavigationItem.Login.route) {
                        popUpTo(NavigationItem.Home.route) { inclusive = true }
                    }
                }
                appStateViewModel.setLoadingCheckingAuth(false)
            }
        } else {
            navController.navigate(NavigationItem.Login.route) {
                popUpTo(NavigationItem.Home.route) { inclusive = true }
            }
            appStateViewModel.setLoadingCheckingAuth(false)
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = startDestination
        ) {
            composable(NavigationItem.Login.route) {
                LoginScreen(navController, appStateViewModel)
            }

            composable(NavigationItem.Home.route) {
                MainScreen(navController, appStateViewModel)
            }

            composable(NavigationItem.AddNewClass.route) {
                AddNewClassScreen(navController)
            }

            composable(
                route = NavigationItem.Detail.route,
                arguments = listOf(navArgument("yogaClassId") { type = NavType.StringType })
            ) { backStackEntry ->
                val yogaClassId = backStackEntry.arguments?.getString("yogaClassId")
                if (yogaClassId != null) {
                    YogaClassDetailScreen(
                        navController = navController,
                        yogaClassId = yogaClassId,
                        detailViewModel
                    )
                }
            }

            composable(NavigationItem.UpdateClass.route) {
                UpdateClassScreen(
                    navController,
                    detailViewModel
                )
            }

            composable(NavigationItem.AddInstanceOutSide.route) {
                AddInstanceScreen(navController)
            }

            // Route for Adding an Instance
            composable(
                route = NavigationItem.AddInstance.route,
                arguments = listOf(
                    navArgument("yogaClassId") { type = NavType.StringType },
                    navArgument("classDate") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val yogaClassId = backStackEntry.arguments?.getString("yogaClassId")
                val classDate = backStackEntry.arguments?.getString("classDate")

                if (yogaClassId != null && classDate != null) {
                    AddEditInstanceScreen(
                        navController = navController,
                        yogaClassId = yogaClassId,
                        classDate = classDate,
                        existingInstance = null
                    )
                }
            }

            // Route for Editing an Instance
            composable(
                route = NavigationItem.EditInstance.route,
                arguments = listOf(
                    navArgument("yogaClassId") { type = NavType.StringType },
                    navArgument("classDate") { type = NavType.StringType },
                    navArgument("instanceId") { type = NavType.StringType },
                    navArgument("isShowClassDetail") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val yogaClassId = backStackEntry.arguments?.getString("yogaClassId")
                val classDate = backStackEntry.arguments?.getString("classDate")
                val instanceId = backStackEntry.arguments?.getString("instanceId")
                val isShowClassDetail = backStackEntry.arguments?.getBoolean("isShowClassDetail")

                if (yogaClassId != null && classDate != null && instanceId != null && isShowClassDetail != null) {
                    // Fetch the existing instance for edit mode
                    val existingInstance = remember { mutableStateOf<YogaClassInstance?>(null) }
                    val isInstanceLoaded = remember { mutableStateOf(false) }

                    LaunchedEffect(instanceId) {
                        detailViewModel.getInstanceById(instanceId) { instance ->
                            existingInstance.value = instance
                            isInstanceLoaded.value = true
                        }
                    }

                    if (isInstanceLoaded.value && existingInstance.value != null) {
                        AddEditInstanceScreen(
                            navController = navController,
                            yogaClassId = yogaClassId,
                            classDate = classDate,
                            existingInstance = existingInstance.value,
                            isShowClassDetail = isShowClassDetail,
                        )
                    } else if (isInstanceLoaded.value && existingInstance.value == null) {
                        Text("Instance not found or failed to load.")
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
