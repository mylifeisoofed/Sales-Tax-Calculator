package com.example.salestaxcalculator

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

// Check out the Retrofit API Documentations for more info.
interface MyApi {
    //@Headers("X-Api-Key: " + "0/xIel8AZsKs9dkoSCe8rQ==x6SdeC0TTzy1eHWU")
    @GET("/v1/salestax")
    fun getSalesTaxes(
        @Query("zip_code") zipCode : String, // zip code parameter being passed in.
        @Header("X-Api-Key") apiKey: String // Giving the API Key
    ): Call<List<SalesTaxesList>>
}

// Other Parameters are State and City.