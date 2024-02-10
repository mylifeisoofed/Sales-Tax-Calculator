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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.salestaxcalculator.ui.theme.SalesTaxCalculatorTheme
import java.lang.NumberFormatException

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
                    SalesTax() // the sales tax stuff
                    Credits()
                }

                SmallTopAppBar() // has to be outside of surface idk why
                // learned about the use of scaffolding. Might try to utilize it.
            }
        }
    }
}

//@Preview(showBackground = true)
@Composable
fun SalesTax() {
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
fun SmallTopAppBar() {
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

@Composable
fun Price(modifier: Modifier = Modifier) { // Text Input for Price
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
                Log.i(TAG, text)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        StateOption(text)
        //Total(text)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun StateOption(text: String = "0.00"){ // State Option
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*
            Text(
                "State",
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
            )*/
            //Spacer(modifier = Modifier.width(68.dp))
            /*
            Text(
                "WA 6.5%",
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
            )
            */
            /*

            OutlinedTextField(value = state,
                onValueChange = {state = it},
                label = { Text("State")},
                modifier = Modifier,
                )
                */

             // This is the new dropdown menu State selection Field.
            // TODO: get an api to do this for me
            val taxRates = mapOf(
                "No Sales Taxes" to "0.0",
                "Washington 6.5%" to "6.5",
                "Arizona 5.6%" to "5.6",
                "California 6%" to "6.0",
                "Arkansas 6.5%" to "6.5",
                )

            val stateOptions = taxRates.keys.toList()
            var expanded by remember { mutableStateOf(false)}
            var selectedStateOption by remember { mutableStateOf(stateOptions[0]) }
            var selectedTaxRates by remember { mutableStateOf(taxRates[selectedStateOption])}
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
                    modifier = Modifier.menuAnchor(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
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
            Spacer(modifier = Modifier.height(100.dp))
            Total(text, selectedTaxRates.toString())
        }
    }
}


//@Preview(showBackground = false)
@Composable
fun Total(text: String = "0.00", tax: String = "0.0") { // Sales Tax Calculation
    val input = text
    var total = 0.00
    var tax = tax.toDouble()
    Surface (
        modifier = Modifier.fillMaxWidth()
    ){
        if (input.isEmpty() || !isDecimal(input)) // checking input.
        {
            total = 0.00
        }
        else
        {
            tax = (input.toDouble() * (tax / 100))
            total = input.toDouble() + tax
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


                Text(text = "$%.2f".format(tax),
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

