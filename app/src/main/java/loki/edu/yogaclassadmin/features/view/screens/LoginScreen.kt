package loki.edu.yogaclassadmin.features.view.screens

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.R
import loki.edu.yogaclassadmin.features.navigation.Screen
import loki.edu.yogaclassadmin.utils.GoogleAuthUiClient


@Composable
fun LoginScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()
    val email = viewModel.email
    val password = viewModel.password
    val passwordVisible = viewModel.passwordVisible
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage.value
    val focusManager = LocalFocusManager.current
    val googleAuthUiClient = GoogleAuthUiClient(context, Identity.getSignInClient(context))
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.setIsLoading(true)
                viewModel.viewModelScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )

                    viewModel.onSignInResult(signInResult) { isSuccessful ->
                        viewModel.setIsLoading(false)
                        if (isSuccessful) {
                            viewModel.checkUserInDatabaseAfterGoogleSignIn(signInResult.data?.email ?: "") { userExists ->
                                if (userExists) {
                                    viewModel.currentUser.value?.let { user ->
                                        if (user.role == "admin") {
                                            appStateViewModel.setUserLoggedIn(user)
                                            navController.navigate(Screen.HOME.name) {
                                                popUpTo(Screen.LOGIN.name) { inclusive = true }
                                            }
                                        } else {
                                            appStateViewModel.logout()
                                            viewModel.setErrorMessage("Access Denied. Admin access only.")
                                        }
                                    }
                                } else {
                                    viewModel.setErrorMessage("Cannot find user in database")
                                }
                            }
                        }
                    }
                }
            } else {
                viewModel.setErrorMessage("SignIn cancelled or failed with resultCode: ${result.resultCode}")
                Log.e("GoogleSignIn", "SignIn cancelled or failed with resultCode: ${result.resultCode}")
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { focusManager.clearFocus() }
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.yoga_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 10.dp)
                )

                // Title
                Text(text = "Lambda Admin", fontSize = 28.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(24.dp))

                // Email TextField
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(
                                imageVector = if (passwordVisible.value) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible.value) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        viewModel.loginUser { user ->
                            if (user != null) {
                                if (user.role == "admin") {
                                    appStateViewModel.setUserLoggedIn(user)
                                    navController.navigate(Screen.HOME.name) {
                                        popUpTo(Screen.LOGIN.name) { inclusive = true }
                                    }
                                } else {
                                    viewModel.setErrorMessage("Access Denied. Admin access only.")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(text = "Login", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider with "OR"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                    Text(text = "OR", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                    Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Google Sign-In Button
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sign in with Google", color = Color.Black, fontSize = 16.sp)
                    }
                }
            }
        }

        // Display loading indicator if isLoading is true
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable(enabled = false) { }
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }

    if (errorMessage.trim().isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.setErrorMessage("") },
            title = { Text(text = "Login Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.setErrorMessage("") }
                ) {
                    Text("OK", color = Color(0xFF4CAF50))
                }
            }
        )
    }
}




