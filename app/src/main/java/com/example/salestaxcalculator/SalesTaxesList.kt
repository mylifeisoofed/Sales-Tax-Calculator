package com.example.salestaxcalculator

// This Data Class holds the parameters that the API will return
data class SalesTaxesList(
    val zip_code : String,
    val total_rate : String,
    val state_rate : String,
    val city_rate : String,
    val country_rate : String,
    val additional_rate : String,
)

