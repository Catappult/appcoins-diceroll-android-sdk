# AppCoins Diceroll Android sample

   - [💻 About](#-about)
   - [⚙️ Design/Features](#️-designfeatures)
   - [🚀 How to run it](#-how-to-run-it)
   - [🛠 Tech Notes](#-tech-notes)
   - [Limitations](#limitations)

## 💻 About

This sample app is used to show the integration via [AppCoins Billing SDK](https://docs.catappult.io/docs/native-android-billing-sdk) of the AppCoins Billing System.

> This sample app is still under development and some features might be imcomplete for now.

## ⚙️ Design/Features

- Simple roll of the dice game with statistics for rolls to avoid having an oversimplified case and allow for some navigation and state managment
- Number of rolls available are limited to 3 maximum, requiring a payment if it reaches 0. Resetting to max if payment was completed.
- If the roll is guess is correct, it resets to the maximum just like a payment would.

## 🚀 How to run it

Dev and Prod variants are available which can be chosen via BuildVariants and run on Android Studio.
To correctly test the Application, update the [CATAPPULT_PUBLIC_KEY](https://github.com/Catappult/appcoins-diceroll-android-sdk/blob/master/gradle.properties#L28-L29) with the Sample key on [SDK Documentation](https://docs.catappult.io/docs/native-android-billing-sdk#faq).

## 🛠 Tech Notes

This sample app attempts to use modern android development techniques as a validation gateway, with the following relevant takeways.
* Uses `Version Catalog` for the libraries management, a simple TOML file
* Proposes a Modularisation strategy
* Uses `Convention Plugins` up all modules and simplfy boilerplate in module creation and dependency management.
* Uses `Jetpack Compose` for the entire UI, with Single Activity
* Uses various `Jetpack` libraries for navigation, splash, accompanist, etc
* Uses `Kotlin Courotines` for the entire app
* Uses `Kotlin Result` for improved and forced error handling
* Uses `Retrofit` for network calls, with a custom adapter
* Uses `Hilt` for dependency injection

## Limitations

Since this app is still under development, some features may not be fully developed.
