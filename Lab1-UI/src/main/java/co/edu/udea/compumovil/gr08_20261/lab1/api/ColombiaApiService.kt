package co.edu.udea.compumovil.gr08_20261.lab1.api

import co.edu.udea.compumovil.gr08_20261.lab1.models.MunicipalityResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ColombiaApiService {
    @GET("municipalities")
    suspend fun getAllMunicipalities(): MunicipalityResponse

    @GET("municipalities")
    suspend fun getMunicipalitiesByDepartment(
        @Query("department") department: String
    ): MunicipalityResponse

    @GET("municipalities")
    suspend fun searchMunicipalities(
        @Query("search") search: String
    ): MunicipalityResponse
}