package co.edu.udea.compumovil.gr08_20261.lab1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.udea.compumovil.gr08_20261.lab1.api.RetrofitClient
import co.edu.udea.compumovil.gr08_20261.lab1.models.ColombianCity
import co.edu.udea.compumovil.gr08_20261.lab1.models.ContactData
import co.edu.udea.compumovil.gr08_20261.lab1.models.PersonalData
import co.edu.udea.compumovil.gr08_20261.lab1.models.toColombianCity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactViewModel : ViewModel() {
    private val _personalData = MutableStateFlow(PersonalData())
    val personalData: StateFlow<PersonalData> = _personalData.asStateFlow()

    private val _contactData = MutableStateFlow(ContactData())
    val contactData: StateFlow<ContactData> = _contactData.asStateFlow()

    private val _allCities = MutableStateFlow<List<ColombianCity>>(emptyList())
    val allCities: StateFlow<List<ColombianCity>> = _allCities.asStateFlow()

    private val _filteredCities = MutableStateFlow<List<ColombianCity>>(emptyList())
    val filteredCities: StateFlow<List<ColombianCity>> = _filteredCities.asStateFlow()

    private val _isLoadingCities = MutableStateFlow(false)
    val isLoadingCities: StateFlow<Boolean> = _isLoadingCities.asStateFlow()

    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    init {
        loadAllCities()
    }

    fun updatePersonalData(data: PersonalData) {
        _personalData.value = data
    }

    fun updateContactData(data: ContactData) {
        _contactData.value = data
    }

    fun loadAllCities() {
        viewModelScope.launch {
            _isLoadingCities.value = true
            _apiError.value = null
            try {
                val response = RetrofitClient.instance.getAllMunicipalities()

                if (response.status == "OK") {
                    val cities = response.data.map { it.toColombianCity() }
                    _allCities.value = cities
                    _filteredCities.value = cities
                } else {
                    _apiError.value = response.message
                    _allCities.value = getDefaultCities()
                    _filteredCities.value = getDefaultCities()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiError.value = "Error de conexión: ${e.message}"
                _allCities.value = getDefaultCities()
                _filteredCities.value = getDefaultCities()
            } finally {
                _isLoadingCities.value = false
            }
        }
    }

    fun searchCities(searchText: String) {
        if (searchText.isEmpty()) {
            _filteredCities.value = _allCities.value
        } else {
            _filteredCities.value = _allCities.value.filter { city ->
                city.nombre.contains(searchText, ignoreCase = true) ||
                        city.departamento.contains(searchText, ignoreCase = true)
            }
        }
    }

    private fun getDefaultCities(): List<ColombianCity> {
        return listOf(
            ColombianCity(1, "Bogotá", "Cundinamarca"),
            ColombianCity(2, "Medellín", "Antioquia"),
            ColombianCity(3, "Cali", "Valle del Cauca"),
            ColombianCity(4, "Barranquilla", "Atlántico"),
            ColombianCity(5, "Cartagena", "Bolívar"),
            ColombianCity(6, "Cúcuta", "Norte de Santander"),
            ColombianCity(7, "Bucaramanga", "Santander"),
            ColombianCity(8, "Pereira", "Risaralda"),
            ColombianCity(9, "Santa Marta", "Magdalena"),
            ColombianCity(10, "Manizales", "Caldas"),
            ColombianCity(11, "Villavicencio", "Meta"),
            ColombianCity(12, "Neiva", "Huila"),
            ColombianCity(13, "Sincelejo", "Sucre"),
            ColombianCity(14, "Valledupar", "Cesar"),
            ColombianCity(15, "Riohacha", "La Guajira"),
            ColombianCity(16, "Pasto", "Nariño"),
            ColombianCity(17, "Ibagué", "Tolima"),
            ColombianCity(18, "Armenia", "Quindío"),
            ColombianCity(19, "Popayán", "Cauca"),
            ColombianCity(20, "Montería", "Córdoba"),
            ColombianCity(21, "Tunja", "Boyacá"),
            ColombianCity(22, "Florencia", "Caquetá"),
            ColombianCity(23, "Yopal", "Casanare"),
            ColombianCity(24, "Quibdó", "Chocó"),
            ColombianCity(25, "Mocoa", "Putumayo"),
            ColombianCity(26, "Puerto Carreño", "Vichada"),
            ColombianCity(27, "Arauca", "Arauca"),
            ColombianCity(28, "San Andrés", "San Andrés y Providencia"),
            ColombianCity(29, "Leticia", "Amazonas"),
            ColombianCity(30, "Inírida", "Guainía")
        )
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