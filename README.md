# AppCoins Diceroll Android sample

   - [💻 About](#-about)
   - [⚙️ Design/Features](#️-designfeatures)
   - [🚀 How to run it](#-how-to-run-it)
   - [🛠 Tech Notes](#-tech-notes)
   - [Limitations](#limitations)

## 💻 About

This sample app is used to show the possible integrations with the AppCoins Billing System. 
It directly compares different integrations and simplifies the integration in a modern application.

> This sample app is still under development and some features might be imcomplete for now.

## ⚙️ Design/Features

- Simple roll of the dice game with statistics for rolls to avoid having an oversimplified case and allow for some navigation and state managment
- Number of rolls available are limited to 3 maximum, requiring a payment if it reaches 0. Resetting to max if payment was completed.
- If the roll is guess is correct, it resets to the maximum just like a payment would.

## 🚀 How to run it

Even though a possible future dev variant is available, the app can only be ran in a release variant opening in Android Studio.
All the configuration is for the catappult connection is already setup.

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
