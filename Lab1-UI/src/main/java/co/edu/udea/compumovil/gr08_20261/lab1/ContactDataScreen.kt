package co.edu.udea.compumovil.gr08_20261.lab1

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.util.Patterns
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val filteredCities = viewModel.filteredCities.collectAsState()
    val isLoadingCities = viewModel.isLoadingCities.collectAsState()
    val apiError = viewModel.apiError.collectAsState()

    val latinCountries = listOf(
        "Argentina", "Bolivia", "Brasil", "Chile", "Colombia",
        "Costa Rica", "Cuba", "Ecuador", "El Salvador", "Guatemala",
        "Honduras", "México", "Nicaragua", "Panamá", "Paraguay",
        "Perú", "República Dominicana", "Uruguay", "Venezuela"
    )

    var countryExpanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }
    var countryText by remember { mutableStateOf(contactData.country) }
    var cityText by remember { mutableStateOf(contactData.city) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(cityText) {
        if (cityText.isNotEmpty()) {
            delay(500)
            viewModel.searchCities(cityText)
            cityExpanded = true
        } else {
            viewModel.searchCities("")
            cityExpanded = false
        }
    }

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
                    countryExpanded = true
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

        Box {
            Column {
                OutlinedTextField(
                    value = cityText,
                    onValueChange = {
                        cityText = it
                        contactData = contactData.copy(city = it)
                    },
                    label = { Text("Ciudad (Autocomplete con API)") },
                    trailingIcon = {
                        if (isLoadingCities.value) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    },
                    supportingText = {
                        apiError.value?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused && cityText.isNotEmpty()) {
                                cityExpanded = true
                            }
                        }
                )
            }

            if (cityExpanded && filteredCities.value.isNotEmpty() && cityText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp)
                        .heightIn(max = 250.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredCities.value) { city ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = city.nombre,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = city.departamento,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (city.codigo.isNotEmpty()) {
                                            Text(
                                                text = "Código: ${city.codigo}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    cityText = city.nombre
                                    contactData = contactData.copy(city = city.nombre)
                                    cityExpanded = false
                                    keyboardController?.hide()
                                }
                            )
                            Divider()
                        }
                    }
                }
            } else if (cityExpanded && cityText.isNotEmpty() && !isLoadingCities.value && filteredCities.value.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 70.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron ciudades con: $cityText",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (filteredCities.value.isNotEmpty() && cityText.isNotEmpty()) {
            Text(
                text = "Se encontraron ${filteredCities.value.size} ciudades",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

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