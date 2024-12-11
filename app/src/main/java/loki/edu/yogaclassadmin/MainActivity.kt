package loki.edu.yogaclassadmin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import loki.edu.yogaclassadmin.database.repository.firestore.FirestoreBookingRepository
import loki.edu.yogaclassadmin.features.navigation.AppNavHost
import loki.edu.yogaclassadmin.features.theme.YogaClassAdminTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

        setContent {
            YogaClassAdminTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YogaClassAdminTheme() {
        val navController = rememberNavController()
        AppNavHost(navController = navController)
    }
}
