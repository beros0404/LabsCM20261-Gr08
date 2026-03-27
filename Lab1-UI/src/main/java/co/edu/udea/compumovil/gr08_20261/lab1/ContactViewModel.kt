package co.edu.udea.compumovil.gr08_20261.lab1

import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.gr08_20261.lab1.models.ContactData
import co.edu.udea.compumovil.gr08_20261.lab1.models.PersonalData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactViewModel : ViewModel() {
    private val _personalData = MutableStateFlow(PersonalData())
    val personalData: StateFlow<PersonalData> = _personalData.asStateFlow()

    private val _contactData = MutableStateFlow(ContactData())
    val contactData: StateFlow<ContactData> = _contactData.asStateFlow()

    fun updatePersonalData(data: PersonalData) {
        _personalData.value = data
    }

    fun updateContactData(data: ContactData) {
        _contactData.value = data
    }

    fun saveDataToLog() {
        val personal = _personalData.value
        val contact = _contactData.value

        val sexText = personal.sex?.let {
            if (it == "male") "Masculino" else "Femenino"
        } ?: ""

        val educationText = when (personal.educationLevel) {
            "primary" -> "Primaria"
            "secondary" -> "Secundaria"
            "university" -> "Universitaria"
            "other" -> "Otro"
            else -> ""
        }

        android.util.Log.d("ContactApp", """
            Información personal: ${personal.names} ${personal.lastNames} $sexText 
            Nació el ${personal.birthDate} $educationText
            
            Información de contacto:
            Teléfono: ${contact.phone}
            Dirección: ${contact.address}
            Email: ${contact.email}
            País: ${contact.country}
            Ciudad: ${contact.city}
        """.trimIndent())
    }
}