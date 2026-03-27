package co.edu.udea.compumovil.gr08_20261.lab1

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import co.edu.udea.compumovil.gr08_20261.lab1.models.PersonalData
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    onNext: () -> Unit,
    viewModel: ContactViewModel
) {
    val context = LocalContext.current
    val personalDataState = viewModel.personalData.collectAsState()
    var personalData by remember { mutableStateOf(personalDataState.value) }

    val educationLevels = listOf("Primaria", "Secundaria", "Universitaria", "Otro")
    var expanded by remember { mutableStateOf(false) }
    var selectedEducation by remember { mutableStateOf("") }

    var namesError by remember { mutableStateOf(false) }
    var lastNamesError by remember { mutableStateOf(false) }
    var birthDateError by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Datos Personales",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = personalData.names,
            onValueChange = {
                personalData = personalData.copy(names = it)
                namesError = it.isBlank()
            },
            label = { Text("Nombres *") },
            isError = namesError,
            supportingText = { if (namesError) Text("Campo obligatorio") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = personalData.lastNames,
            onValueChange = {
                personalData = personalData.copy(lastNames = it)
                lastNamesError = it.isBlank()
            },
            label = { Text("Apellidos *") },
            isError = lastNamesError,
            supportingText = { if (lastNamesError) Text("Campo obligatorio") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sexo",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = personalData.sex == "male",
                    onClick = { personalData = personalData.copy(sex = "male") }
                )
                Text("Hombre")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = personalData.sex == "female",
                    onClick = { personalData = personalData.copy(sex = "female") }
                )
                Text("Mujer")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = personalData.birthDate,
            onValueChange = { },
            label = { Text("Fecha de Nacimiento *") },
            readOnly = true,
            isError = birthDateError,
            supportingText = { if (birthDateError) Text("Campo obligatorio") },
            trailingIcon = {
                Button(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                personalData = personalData.copy(
                                    birthDate = "$dayOfMonth/${month + 1}/$year"
                                )
                                birthDateError = false
                            },
                            currentYear, currentMonth, currentDay
                        ).show()
                    }
                ) {
                    Text("Seleccionar Fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedEducation,
                onValueChange = { },
                readOnly = true,
                label = { Text("Grado de Escolaridad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                educationLevels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level) },
                        onClick = {
                            selectedEducation = level
                            personalData = personalData.copy(
                                educationLevel = when(level) {
                                    "Primaria" -> "primary"
                                    "Secundaria" -> "secondary"
                                    "Universitaria" -> "university"
                                    else -> "other"
                                }
                            )
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                var isValid = true
                if (personalData.names.isBlank()) {
                    namesError = true
                    isValid = false
                }
                if (personalData.lastNames.isBlank()) {
                    lastNamesError = true
                    isValid = false
                }
                if (personalData.birthDate.isBlank()) {
                    birthDateError = true
                    isValid = false
                }

                if (isValid) {
                    viewModel.updatePersonalData(personalData)
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Siguiente")
        }
    }
}