## Sales Tax Calculator Android App

This is a simple state sales tax calculator app I made in Android Studio written in Kotlin utilizing Jetpack Compose.

If you ever go grocery shopping and want to figure out how much you will be paying sales taxes, simply enter the price tag, enter your postal code (or select a US State with Sales Taxes) and the app will calculate the tax and total after applying sales tax! 

The API used to get sales tax data: https://api-ninjas.com/api/salestax

DISCLAIMER: This app is not guaranteed to be 100% accurate in calculating your product's total after taxes. Local jurisdictions (cities and counties) can impose and change additional state and local taxes in addition to the sales taxes, leading to varying total rates. 

The US State Selection Option does not take into account Local and other taxes that are added to state taxes. 

The Postal Code Option is the most accurate and up-to-date method to calculate your total after taxes. It uses an API to fetch up-to-date sales taxes, including local and other additional taxes. When doing tax calculations, the postal code option will use the total tax (state tax + local tax + county tax + additional taxes) to calculate your total. Therefore, It is recommended to use the Postal/Zip code Option to accurately calculate your total after taxes.  

Neither option will calculate taxes for cases other than the three taxes listed above such as VAT, Excise Taxes, etc.

<img src="https://github.com/mylifeisoofed/Sales-Tax-Calculator/assets/58831022/ac4be57b-790d-4d69-b241-ce59ed9e864a" width="400">

TODO:
- ~~Implement a selection option for users to select states that have sales taxes.~~
- ~~Maybe look for an API to hook up to grab up-to-date data for each state's sales tax.~~
- Make UI look better(?)
- Give the app a cool name(?)
