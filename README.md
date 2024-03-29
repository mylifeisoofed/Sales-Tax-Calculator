# <img src="https://github.com/mylifeisoofed/Sales-Tax-Calculator/assets/58831022/6ee11dff-b3d3-4ee7-af5a-d78f5379e0b6" width="50" height="50"> Sales Tax Calculator Android App  
This is a simple state sales tax calculator app I made in Android Studio written in Kotlin utilizing Jetpack Compose UI Framework.

If you ever go grocery shopping and want to figure out how much you will be paying sales taxes, simply enter the price tag, enter your zip code (or select a US State with Sales Taxes for an approximation) and the app will calculate the tax and total after applying sales tax! 


**DISCLAIMER:** This app is not guaranteed to be 100% accurate in calculating your product's total after taxes. Local jurisdictions (cities and counties) can impose and change additional state and local taxes in addition to the sales taxes, leading to varying total rates. See below for more info.

## Zip Code Option
**The Zip Code Option is the most accurate and up-to-date method to calculate your total after taxes.** It uses an API to fetch up-to-date sales taxes, including local and other additional taxes. When doing tax calculations, the zip code option will use the total tax (state tax + local tax + county tax + additional taxes) to calculate your total. Therefore, It is recommended to use the Zip code Option to accurately calculate your total after taxes.  

## State Dropdown Selection Option
**The US State Selection Option does not take into account Local and other taxes that are added to state taxes and should be used to approximate your total.**  
  
## QnA
*So then why keep both options?*  
The zip code option requires an internet connection (either through wifi or mobile data) while the US State Option does not. Therefore, if you do not have an internet connection, then the option to approximate your sales taxes is available for you. If you do have connection, it is highly recommended to use the zip code option.

*Is the sales tax applied before or after the discount?*  
It is generally applied **after** the discount. I am following this article on the topic of discounts: https://www.taxjar.com/blog/calculations/2021-12-sales-tax-discounts-coupons-promotions  
  
*What API is being used to get up-to-date sales taxes?*  
The API used to get sales tax data: https://api-ninjas.com/api/salestax  
  
*Can it calculate taxes for items with specific imposed taxes on it?*  
Neither option will calculate taxes for specific cases where an item or good has a special tax such as VAT, Excise Taxes, etc. The purpose of this app is to calculate sales taxes of general goods and merchandises that you can find in stores or online.
  
  
<img src="https://github.com/mylifeisoofed/Sales-Tax-Calculator/assets/58831022/9a42a632-a1be-40d6-b055-0457869d1561" width="400">

TODO:
- ~~Implement a selection option for users to select states that have sales taxes.~~
- ~~Maybe look for an API to hook up to grab up-to-date data for each state's sales tax.~~
- ~~Add a Discount option~~
- Find a way to use the user's current location to grab zip code
- Make UI look better(?)
- Give the app a cool name(?)
