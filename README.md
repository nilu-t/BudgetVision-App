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

- The user has the option to apply currency conversion to one of the top 25 most traded currencies in the world which is done from using the Bank of Canada website exchange rates. 

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

### Examples of use: Initial states of the app.
The app when initially opened for the first time. The "card" elements can be swiped as shown below.
<table>
  <tr>
    <td>default app (part one)</td>
    <td>default app (part two)</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_state1.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_state2.png" width=270 height=480></td>
  </tr>
</table>

<br>
<br>

The app when initially clicking the food, housing, commute, lifestyle and recreation cards as shown below. 
<table>
  <tr>
    <td>commute card popup</td>
    <td>food card popup</td>
    <td>housing card popup</td>
    <td>lifestyle card popup</td>
    <td>recreation card popup</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_commute.png" width=300 height=400></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_food.png" width=300 height=400></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_housing.png" width=300 height=400></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_lifestyle.png" width=300 height=400></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/initial_recreation.png" width=300 height=400></td>
  </tr>
</table>

<br>
<br>

The app when clicking the "home button", "money button", and "settings button" in the bottom navigation view.
<table>
  <tr>
    <td>clicked bottom navigation "home"</td>
    <td>clicked bottom navigation "money"</td>
    <td>clicked bottom navigation "settings"</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/clicked_home_bottomnav_view.png" width=270   height=480</td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/clicked_money_bottomnav_view.png" width=270 
height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/clicked_settings_bottomnav_view.png" width=270 height=480></td>
  </tr>
</table>

<br>
<br>

The app when adding/removing income, adding a new expense to an existing subcategory, and removing an existing subcategory expense.
<table>
  <tr>
    <td>Add or remove income</td>
    <td>Add expense to existing subcategory</td>
    <td>Remove existing subcategory expense"</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/add_or_remove_income.png" width=270 height=480</td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/additional_expense.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/remove_expense.png" width=270 height=480></td>
  </tr>
</table>


### Examples of use: Using BudgetVision.

The app when using google sign-in.
<table>
  <tr>
    <td>BudgetVision google sign-in permissions</td>
    <td>BudgetVision spreadsheet url displayed</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/google_sign_in_budgetvision.png" width=270   height=480</td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/login_successful_budgetvision.png" width=270 
height=480></td>
  </tr>
</table>

<br>
<br>

The app after adding a food expense.
<table>
  <tr>
    <td>Adding food expense</td>
    <td>Daily budget & total income after food expense</td>
    <td>View all expenses after food expense</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/add_food_expense.png" width=270 height=480</td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/after_food_expense.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/view_all_expenses_after_food_expense.png" width=270 height=480></td>
  </tr>
</table>

<br>
<br>

The app after converting currency from default CAD to EUROPEAN EURO. (after adding multiple expenses).
<table>
  <tr>
    <td>View all expenses after adding multiple expenses.</td>
    <td>Enable currency conversion in settings to EUROPEAN EURO</td>
    <td>Daily budget & total income after conversion</td>
    <td>View all expenses after currency conversion.</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/multiple_expenses.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/settings_currency_conversion.png" width=270 height=480</td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/after_multiple_expense_conversion.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/after_currency_conversion.png" width=270 height=480></td>
  </tr>
</table>

<br>
<br>

The app after adding additional users to keep track of and moniter other individual expenses. An example of converting to EUROPEAN EURO is also shown below.
<table>
  <tr>
    <td> Adding two additional new users nilu & thiru</td>
    <td> Added multiple expenses for user nilu</td>
    <td> View all expenses after conversion to euro</td>
  </tr>
  <tr>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/money_fragment.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/nilu_multiple_expenses.png" width=270 height=480></td>
    <td><img src="https://github.com/nilu-t/BudgetVision-App/blob/main/app/src/main/java/ScreenshotsOfUse/nilu_multiple_expenses_euro.png" width=270 height=480></td>
  </tr>
<table>


