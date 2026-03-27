package co.edu.udea.compumovil.gr08_20261.lab1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.util.Patterns
import co.edu.udea.compumovil.gr08_20261.lab1.models.ContactData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDataScreen(
    onSave: () -> Unit,
    viewModel: ContactViewModel
) {
    val contactDataState = viewModel.contactData.collectAsState()
    var contactData by remember { mutableStateOf(contactDataState.value) }

    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var countryError by remember { mutableStateOf(false) }

    val latinCountries = listOf(
        "Argentina", "Bolivia", "Brasil", "Chile", "Colombia",
        "Costa Rica", "Cuba", "Ecuador", "El Salvador", "Guatemala",
        "Honduras", "México", "Nicaragua", "Panamá", "Paraguay",
        "Perú", "República Dominicana", "Uruguay", "Venezuela"
    )

    val colombianCities = listOf(
        "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena",
        "Cúcuta", "Bucaramanga", "Pereira", "Santa Marta", "Manizales"
    )

    var countryExpanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }
    var countryText by remember { mutableStateOf(contactData.country) }
    var cityText by remember { mutableStateOf(contactData.city) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Datos de Contacto",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = contactData.phone,
            onValueChange = {
                contactData = contactData.copy(phone = it)
                phoneError = it.isBlank()
            },
            label = { Text("Teléfono *") },
            isError = phoneError,
            supportingText = { if (phoneError) Text("Campo obligatorio") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contactData.address,
            onValueChange = { contactData = contactData.copy(address = it) },
            label = { Text("Dirección") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contactData.email,
            onValueChange = {
                contactData = contactData.copy(email = it)
                emailError = it.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(it).matches()
            },
            label = { Text("Correo Electrónico *") },
            isError = emailError,
            supportingText = {
                if (emailError) {
                    if (contactData.email.isBlank()) Text("Campo obligatorio")
                    else Text("Correo electrónico inválido")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = countryExpanded,
            onExpandedChange = { countryExpanded = !countryExpanded }
        ) {
            OutlinedTextField(
                value = countryText,
                onValueChange = {
                    countryText = it
                    contactData = contactData.copy(country = it)
                    countryError = it.isBlank()
                },
                label = { Text("País *") },
                isError = countryError,
                supportingText = { if (countryError) Text("Campo obligatorio") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = countryExpanded,
                onDismissRequest = { countryExpanded = false }
            ) {
                latinCountries.filter { it.contains(countryText, ignoreCase = true) }
                    .forEach { country ->
                        DropdownMenuItem(
                            text = { Text(country) },
                            onClick = {
                                countryText = country
                                contactData = contactData.copy(country = country)
                                countryError = false
                                countryExpanded = false
                            }
                        )
                    }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = cityExpanded,
            onExpandedChange = { cityExpanded = !cityExpanded }
        ) {
            OutlinedTextField(
                value = cityText,
                onValueChange = {
                    cityText = it
                    contactData = contactData.copy(city = it)
                },
                label = { Text("Ciudad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = cityExpanded,
                onDismissRequest = { cityExpanded = false }
            ) {
                colombianCities.filter { it.contains(cityText, ignoreCase = true) }
                    .forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                cityText = city
                                contactData = contactData.copy(city = city)
                                cityExpanded = false
                            }
                        )
                    }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                var isValid = true

                if (contactData.phone.isBlank()) {
                    phoneError = true
                    isValid = false
                }

                if (contactData.email.isBlank()) {
                    emailError = true
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(contactData.email).matches()) {
                    emailError = true
                    isValid = false
                }

                if (contactData.country.isBlank()) {
                    countryError = true
                    isValid = false
                }

                if (isValid) {
                    viewModel.updateContactData(contactData)
                    viewModel.saveDataToLog()
                    onSave()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}