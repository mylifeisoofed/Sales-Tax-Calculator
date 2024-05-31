package com.riceroll.salestaxcalculator

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.salestaxcalculator.ui.theme.SalesTaxCalculatorTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.NumberFormatException
import com.example.salestaxcalculator.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean


var url = "https://api.api-ninjas.com/"


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
                        bottomBar = {
                                    BottomAppBar(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        BannerAd()
                                    }
                        },
                        content = { innerPadding ->
                            Column(
                                modifier = Modifier.padding(innerPadding),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                //Spacer(modifier = Modifier.height(5.dp))
                                SalesTax() // the sales tax stuff
                                Credits()
                            }
                        }
                    )
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
            text = "Made by Brian Le!",
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
            Row(modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "App Icon Image",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    "Sales Tax Calculator",
                    maxLines = 1
                )
            }

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
        var discount by remember { mutableStateOf("")} // for discount
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
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = discount,
            onValueChange = { discount = it },
            modifier = Modifier,
            label = { Text("Discount (Enter the %)") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = null){
                Log.i("DISCOUNT", text)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(10.dp))
        StateOption(text, discount)
        //Total(text)
        Log.i("PRICE", text)
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun StateOption(price: String = "0.00", discount: String = "0.00"){ // State Option and Sales Tax Calculations.
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {

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
                    keyboardActions = KeyboardActions(onDone = { // when user taps done, call the tax api
                        Log.i("ZIPINPUT", zipcode)
                        if (zipcode.length == 5) {
                            getTaxes(zipcode) {resultTax ->
                                tax = resultTax
                                Log.i("TAX RETRIEVED", tax)
                                donePressed = true
                            }
                            Log.i("ZIPINPUT", "VALID ZIP")
                        }
                        else
                            Log.i("ZIPINPUT", "INVALID ZIP")

                        keyboardController?.hide()
                    },

                        /*onNext = { // when the user taps the other outlined text field
                            Log.i("ZIPINPUT", zipcode)
                            getTaxes(zipcode) {resultTax ->
                                tax = resultTax
                                Log.i("TAX RETRIEVED", tax)
                                donePressed = true
                            }
                            keyboardController?.hide()
                        }*/) {

                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 32.dp)
                )
            }

            Row( // US State dropdown list
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 32.dp)
                            .menuAnchor(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),

                    )

                    ExposedDropdownMenu(expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .width(500.dp)
                                .height(1000.dp)
                            ){
                            items(stateOptions) { selectionOption ->
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
            }

            Spacer(modifier = Modifier.height(100.dp))

            if (zipcodeOption and donePressed)
            {
                Total(price, tax, discount)
                donePressed = false
            }
            else
            {
                Total(price, selectedTaxRates.toString(), discount)
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun Total(text: String = "0.00", tax: String = "0.0", discount: String = "0.0") { // Sales Tax Calculation
    val input = text
    var total = 0.00
    var taxDouble = tax.toDouble() * 100
    var taxTotal = 0.0
    var discount = discount
    var saved = 0.0

    Log.i("TAX CURRENTLY", tax)


    Surface (
        modifier = Modifier.fillMaxWidth()
    ){
        if (discount.isEmpty() || !isDecimal(discount))
            discount = "0.0"

        if (input.isEmpty() || !isDecimal(input)) // checking input.
            total = 0.00


        else
        {
            saved = (input.toDouble() * (discount.toDouble() / 100)) // discount savings
            //total = (input.toDouble() - (input.toDouble() * (discount.toDouble() / 100)))
            total = (input.toDouble() - saved)
            taxTotal = total * (tax.toDouble())
            total += taxTotal
            Log.i("TOTAL AFTER TAX", total.toString())
        }


        Column (modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly){
            Row(horizontalArrangement = Arrangement.Start) {
                Text("Saved",
                    textAlign = TextAlign.Start,
                    fontSize = 32.sp)

                Spacer(modifier = Modifier.width(60.dp))

                Text("$%.2f".format(saved),
                    textAlign = TextAlign.Start,
                    fontSize = 32.sp)
            }
            Row(horizontalArrangement = Arrangement.Start
            ){

                Text("Tax Rate",
                    textAlign = TextAlign.Start,
                    fontSize = 32.sp)

                Spacer(modifier = Modifier.width(23.dp))

                Text(text = "%.2f".format(taxDouble) + "%",
                    fontSize = 32.sp)
            }
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

@Preview(showBackground = true)
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
        AdView(it).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-4168857671507421/6525729259"
            loadAd(AdRequest.Builder().build())
        }
    })
}




