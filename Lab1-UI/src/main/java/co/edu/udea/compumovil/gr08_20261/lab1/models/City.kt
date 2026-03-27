package co.edu.udea.compumovil.gr08_20261.lab1.models

data class MunicipalityResponse(
    val status: String,
    val message: String,
    val data: List<Municipality>
)

data class Municipality(
    val id: Int,
    val code: String,
    val name: String,
    val department: String
)

data class ColombianCity(
    val id: Int,
    val nombre: String,
    val departamento: String,
    val codigo: String = ""
)

fun Municipality.toColombianCity(): ColombianCity {
    return ColombianCity(
        id = this.id,
        nombre = this.name,
        departamento = this.department,
        codigo = this.code
    )
}