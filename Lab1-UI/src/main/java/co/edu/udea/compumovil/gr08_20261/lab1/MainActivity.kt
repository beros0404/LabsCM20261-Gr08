package co.edu.udea.compumovil.gr08_20261.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.edu.udea.compumovil.gr08_20261.lab1.ui.theme.Labs20261Gr08Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Labs20261Gr08Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Crear el ViewModel aquí
                    val contactViewModel: ContactViewModel = viewModel()

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "personal_data"
                    ) {
                        composable("personal_data") {
                            PersonalDataScreen(
                                onNext = { navController.navigate("contact_data") },
                                viewModel = contactViewModel
                            )
                        }
                        composable("contact_data") {
                            ContactDataScreen(
                                onSave = {
                                    navController.popBackStack()
                                },
                                viewModel = contactViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}