package co.edu.udea.compumovil.gr08_20261.lab1.models

data class PersonalData(
    val names: String = "",
    val lastNames: String = "",
    val sex: String? = null,
    val birthDate: String = "",
    val educationLevel: String = ""
)