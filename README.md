# BudgetVision 

## Copyright & License 
© 2021 Nilushanth Thiruchelvam, BudgetVision.

## Table of contents
- [General info](#general-info)
- [Technologies used](#technologies-used)
- [Features of BudgetVision](#features-of-budgetvision)
- [Testing procedures](#testing-procedures)
- [Examples of use](#examples-of-use)

## General info
- This application takes into the account of users income and monthly expenses to create a daily budget and summary of expenses for them. 

- The summary of expenses can be read out loud via text to speech.

- The user can sign in to BudgetVision to export the summary of expenses in to their google account google sheet. 

- No connecting to bank account. No sharing of private data. 


## Technologies used
- BudgetVision is an Android application created in Java using Android Studio IDE implemented with the dependency injection design pattern using Dagger2 open source library and MCV framework.

- XML (Extensible Markup Language) is used to create the user interface elements.

## Features of BudgetVision
- 5 total categories; Food, Housing, Commute, Lifestyle and Recreation. For each category a restriction of 50 subcategories are placed in which the user cannot exceed. The reason for a maximum of 50 subcategories is due to the fact there are already 5 total categories. Thus, 5 * 50 = 250 subcategories in total. Moreover, restricting the total amount of subcategories allows the user to carefully consider their subcategories and their budget plan accordingly. 

- Calculates the users daily budget based on income and monthly expenses.

- A summary table of all expenses formatted in unsorted order. With an option to sort the expenses in ascending alphabetical order, descending alphabetical order, low to high expenses, high to low expenses. Summary table can be read out loud via Text To Speech. 

- Google Sheets API is used to export a google sheet with detailed monthly user transactions within the app with just a click of a button. 

## Testing procedures
- JUnit is used for unit testing BudgetVision. In the folder "CodingProject.budgetvision" contains the JUnit test file called "BudgetVisionTests". 

- The tests are created to find potential errors to prevent bugs and prevent user from crashing the application.

## Examples of use

