package com.example.salestaxcalculator

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.salestaxcalculator.ui.theme.SalesTaxCalculatorTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.NumberFormatException

var url = "https://api.api-ninjas.com/"
private val apikey = "" // api key goes here.

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SalesTaxCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { SmallTopAppBar() },
                        content = { innerPadding ->
                            Column(
                                modifier = Modifier.padding(innerPadding),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(50.dp))
                                SalesTax() // the sales tax stuff
                                //Credits()
                            }
                        }
                    )
                    Credits()
                }
            }
        }
    }

}

// This is the API Call to get sales taxes.
// Check the Retrofit API Documentations
fun getTaxes(zipcode: String, callback: (String) -> Unit) {
    val api = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(url)
        .build()
        .create(MyApi::class.java)

    val retrofitData = api.getSalesTaxes(zipcode, apikey)

    retrofitData.enqueue(object : Callback<List<SalesTaxesList>?> {
        override fun onResponse(
            call: Call<List<SalesTaxesList>?>,
            response: Response<List<SalesTaxesList>?>
        ) {
            val responseBody = response.body()

            if (response.isSuccessful && responseBody != null) // if we connected.
            {
                val myStringBuilder = StringBuilder()
                for (salesTaxList in responseBody) {
                    myStringBuilder.append(salesTaxList.zip_code)
                    myStringBuilder.append("\n")
                    Log.i("APICALL", "Call Success Zip Code ${salesTaxList.total_rate}")
                    callback(salesTaxList.total_rate) // "Returning" the total Tax Rate
                    return
                }
            }
            else // API Call connected but we did not get a response.
            {
                Log.i("APICALL", "Unsuccessful response. Status code: ${response.code()}")
            }
        }

        // if we failed to connect
        override fun onFailure(call: Call<List<SalesTaxesList>?>, t: Throwable) {
            Log.i("APICALL", "Call failed")
        }
    })
}


//@Preview(showBackground = true)
@Composable
fun SalesTax() { // Just a wrapper function to call Price()
    SalesTaxCalculatorTheme {
        Price()
    }
}


@Composable
fun Credits() {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Made by Brian!",
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

//@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar() { // A small app bar that will be placed at the top of the app.
    TopAppBar(
        title = {
            Text(
                "Sales Tax Calculator",
                maxLines = 1
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun Price(modifier: Modifier = Modifier) { // Text Input for Price and will call StateOption
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        var text by remember { mutableStateOf("")} // user inputs price
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Price") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = null){
                Log.i("PRICE", text)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(100.dp))
        StateOption(text)
        //Total(text)
        Log.i("PRICE", text)
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun StateOption(price: String = "0.00"){ // State Option and Sales Tax Calculations.
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {

            // Warning, very loooooong list. Should probably put this in another Kotlin file later.
            val taxRates = mapOf(
                "No Sales Taxes" to "0.0",
                "Arizona 5.6%" to ".056",
                "Arkansas 6.5%" to ".065",
                "California 6%" to ".060",
                "Colorado 6%" to "0.06",
                "Connecticut 6.35%" to "0.0635",
                "Florida 6%" to "0.06",
                "Georgia 4%" to "0.04",
                "Hawaii 4%" to "0.04",
                "Idaho 4%" to "0.04",
                "Illinois 6.25%" to "0.0625",
                "Indiana 7%" to "0.07",
                "Iowa 6%" to "0.06",
                "Kansas 6.5%" to "0.065",
                "Kentucky 6%" to "0.06",
                "Louisiana 4.45%" to "0.0445",
                "Maine 5.5%" to "0.055",
                "Maryland 6%" to "0.06",
                "Massachusetts 5.6%" to "0.056",
                "Michigan 6%" to "0.06",
                "Minnesota 6.88%" to "0.0688",
                "Mississippi 7%" to "0.07",
                "Missouri 4.23%" to "0.0423",
                "Nebraska 5.5%" to "0.055",
                "Nevada 4.6%" to "0.046",
                "New Jersey 6.63%" to "0.0663",
                "New Mexico 5.13%" to "0.0513",
                "New York 4%" to "0.04",
                "North Carolina 4.75%" to "0.0475",
                "North Dakota 5%" to "0.05",
                "Ohio 5.75%" to "0.0575",
                "Oklahoma 4.5%" to "0.045",
                "Pennsylvania 6%" to "0.06",
                "Rhode Island 7%" to "0.07",
                "South Carolina 6%" to "0.06",
                "South Dakota 4.5%" to "0.045",
                "Tennessee 7%" to "0.07",
                "Texas 6.25%" to "0.065",
                "Utah 4.7%" to "0.047",
                "Vermont 6%" to "0.06",
                "Virgina 4.3%" to "0.043",
                "Washington 6.5%" to "0.065",
                "West Virginia 6%" to "0.06",
                "Wisconsin 5%" to "0.05",
                "Wyoming 4%" to "0.04",
                )

            val stateOptions = taxRates.keys.toList()
            var expanded by remember { mutableStateOf(false)}
            var selectedStateOption by remember { mutableStateOf(stateOptions[0]) }
            var selectedTaxRates by remember { mutableStateOf(taxRates[selectedStateOption])}

            var zipcodeOption by remember { mutableStateOf(true)}
            var zipcode by remember { mutableStateOf("")}
            var tax by remember { mutableStateOf("0.0")}
            var donePressed = true;
            val keyboardController = LocalSoftwareKeyboardController.current


            Row( // Zip Code UI
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ){
                RadioButton(selected = zipcodeOption,
                    onClick = { zipcodeOption = true },
                    modifier = Modifier.padding(vertical = 10.dp),
                )

                OutlinedTextField(
                    value = zipcode,
                    onValueChange = { zipcode = it },
                    label = { Text("Zipcode (Press Done when Finished!)",
                        maxLines = 1,
                        fontSize = 12.sp
                    ) },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { // when user taps done
                        Log.i("ZIPINPUT", zipcode)
                        getTaxes(zipcode) {resultTax ->
                            tax = resultTax
                            Log.i("TAX RETRIEVED", tax)
                            donePressed = true
                        }
                        keyboardController?.hide()
                    },
                        onNext = { // when the user taps the other outlined text field
                            Log.i("ZIPINPUT", zipcode)
                            getTaxes(zipcode) {resultTax ->
                                tax = resultTax
                                Log.i("TAX RETRIEVED", tax)
                                donePressed = true
                            }
                            keyboardController?.hide()
                        }) {

                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(end = 32.dp)
                )
            }

            Row( // US State UI
                modifier = Modifier,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ){
                RadioButton(selected = !zipcodeOption,
                    onClick = { zipcodeOption = false },
                    modifier = Modifier.padding(vertical = 10.dp),
                    )

                ExposedDropdownMenuBox(expanded = expanded,
                    onExpandedChange = { expanded = it},

                    ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedStateOption,
                        onValueChange = {},
                        label = {Text("State")},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.fillMaxWidth().padding(end = 32.dp).menuAnchor(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),

                    )

                    ExposedDropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        stateOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(text = selectionOption) },
                                onClick = {
                                    selectedStateOption = selectionOption
                                    selectedTaxRates = taxRates[selectionOption] ?: "0.0"
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))

            if (zipcodeOption and donePressed)
            {
                Total(price, tax)
                donePressed = false
            }
            else
            {
                Total(price, selectedTaxRates.toString())
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun Total(text: String = "0.00", tax: String = "0.0") { // Sales Tax Calculation
    val input = text
    var total = 0.00
    //var tax = tax.toDouble()
    var taxTotal = 0.0

    Log.i("TAX CURRENTLY", tax)


    Surface (
        modifier = Modifier.fillMaxWidth()
    ){
        if (input.isEmpty() || !isDecimal(input)) // checking input.
        {
            total = 0.00
        }
        else
        {
            taxTotal = (input.toDouble() * (tax.toDouble()))
            total = input.toDouble() + taxTotal
            Log.i("TOTAL AFTER TAX", total.toString())
        }

        Column (modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly){
            Row(
                horizontalArrangement = Arrangement.Start
            ) {
                Text("Tax",
                    textAlign = TextAlign.Start,
                    fontSize = 32.sp,
                )

                Spacer(modifier = Modifier.width(96.dp))


                Text(text = "$%.2f".format(taxTotal),
                    textAlign = TextAlign.Start,
                    fontSize = 32.sp,
                )
            }

            Row(horizontalArrangement = Arrangement.Start) {

                Text("Total",
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                )

                Spacer(modifier = Modifier.width(75.dp))

                Text(text = "$%.2f".format(total),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                )
            }
        }

    }
}

fun isDecimal(text: String): Boolean { // have to create my own decimal check via exception handling.
    try{
        text.toDouble()
    }
    catch(e: NumberFormatException){
        return false
    }
    return true
}

