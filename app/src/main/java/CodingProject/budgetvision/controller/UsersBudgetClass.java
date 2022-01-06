package CodingProject.budgetvision.controller;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import CodingProject.budgetvision.view.MainActivity;

/**
 * This class is for the controls for the users and determining their budgets.
 * this class will implement the budgets for the App owner (main user).
 * The instance for this class is only created once in its lifetime via @Singleton annotation from Dagger2 open source library.
 */
@Singleton
public class UsersBudgetClass extends Application{

    /*
     * income, total balance and expenses.
     */
    private double totalIncome; //total income for the user.
    private double totalInitialIncome; //initial income for the user.
    private double totalMonthlyExpenses; //total expenses for the user.

    /*
     * daily budget.
     */
    private double dailyBudget; //daily budget for the user.

    private CategoriesClass userCategory; //Categories object userCategory, userCategory is a 'dependency' of UsersBudgetClass.
    //the transient keyword is used so when the user object is saved as a json it will NOT be serialized.
    private transient CurrencyConversionClass currencyConversionObj; //conversion object from CurrencyConversionClass, currencyConversionObj is a 'dependency' of UsersBudgetClass.

    private String currencySymbol;  //the currency symbol according to the country selected by the user.

    private String[] sortedFood; //stores all sorted food subcategories.
    private String[] sortedHousing; //stores all sorted housing subcategories.
    private String[] sortedLifestyle; //stores all sorted lifestyle subcategories.
    private String[] sortedCommute; //stores all sorted commute subcategories.
    private String[] sortedRecreation; //stores all sorted recreation subcategories.
    private String categoryName; //stores the current category name.

    private double[] sortedFoodCosts; //stores all sorted food costs subcategories.
    private double[] sortedHousingCosts; //stores all sorted housing costs subcategories.
    private double[] sortedLifestyleCosts; //stores all sorted lifestyle costs subcategories.
    private double[] sortedCommuteCosts; //stores all sorted commute costs subcategories.
    private double[] sortedRecreationCosts; //stores all sorted recreation costs subcategories.

    //TreeMaps to store the subcategories and subcategory costs. Where the key is subcategory cost and the value is the subcategories,
    TreeMap <String,Double> foodMap = new TreeMap<>(); //Tree Map for food.
    TreeMap <String,Double> housingMap = new TreeMap<>(); //Tree Map for housing.
    TreeMap <String,Double> lifestyleMap = new TreeMap<>(); //Tree Map for lifestyle.
    TreeMap <String,Double> commuteMap = new TreeMap<>(); //Tree Map for commute.
    TreeMap <String,Double> recreationMap = new TreeMap<>(); //Tree Map for recreation.

    private int NOS; //store the number of subcategories REMAINING for a specific category.
    private String immediateStatus; //the immediate status after adding an expense to a subcategory.

    private HashSet<String> newUserSet = new HashSet<>(); //hashset containing all the names of the users.
    private HashMap<String,UsersBudgetClass> usersBudgetMap = new HashMap<>(); //HashMao containing all the UserBudget objects where the key is each new user name.

    private String currentCountrySelected;
    private String dailyBudgetConverted;
    private String totalIncomeConverted;
    private String currentMonthlyExpensesConverted;
    private double currencyRate;
    private int NOCC; //number of currency conversions taken place.

    private UserBudgetComponent userComponent;
    private UserBudgetCallback userCallback;

    /**
     * default constructor; needed for using this class on the Application level within the AndroidManifest.xml.
     * userComponent is an instance of UserBudgetComponent.
     * DaggerUserBudgetComponent created by building the project after the necessary constructor injections were made.
     */
    public UsersBudgetClass(){
        this.userComponent = DaggerUserBudgetComponent.create();
    }

    /**
     * Overloaded User constructor.
     * Constructor injecting the userCategory object from CategoriesClass and currencyConversionObj from CurrencyConversionClass.
     * @param userCategory
     * @param currencyConversionObj
     */
    @Inject
    public UsersBudgetClass(CategoriesClass userCategory, CurrencyConversionClass currencyConversionObj){
        this();
        this.userCategory = userCategory; //setting user category object.
        this.currencyConversionObj = currencyConversionObj; //setting currency conversion object.
    }

    /**
     * returns the instance of UserBudgetComponent.
     * @return
     */
    public UserBudgetComponent getAppComponent(){
        return this.userComponent;
    }

    //method for increasing the user initial income by an initial amount.
    public void increaseUserInitialIncome(double amount){
        this.totalInitialIncome += amount;

    }

    //method for decreasing the user initial income by an initial amount.
    public void decreaseUserInitialIncome(double amountToDecreaseBy){
        this.totalInitialIncome -= amountToDecreaseBy;
    }


    /**
     * method for adding users category in the Categories Class.
     * @param categoryName
     */
    public void addUserCategory(String categoryName){
        //add the category to the Categories class.
        this.userCategory.addCategory(categoryName);

        this.categoryName = categoryName;
    }


    /**
     * method for adding users subcategory.
     * @param subCategoryToAdd
     * @param subCategoryCost
     */
    public void addUserSubcategory(String subCategoryToAdd, double subCategoryCost){

        this.categoriesObject().setCallback((MainActivity) this.userCallback); //set the user call back in the categories object.

        this.categoriesObject().setCurrencySymbol(getCurrencySymbol());

        //add the subcategory and the expense associated with it.
        this.userCategory.addSubCategory(this.categoryName, subCategoryToAdd,subCategoryCost);

        if(!(this.userCategory.isError())) {

            //increase the total monthly expenses due to adding the subcategory.
            this.totalMonthlyExpenses += subCategoryCost;

            this.immediateStatus = "You Have Added " + subCategoryToAdd + " for " + getCurrencySymbol() + subCategoryCost;
        }
        else{
            this.immediateStatus =  this.userCategory.getErrorMSG();
        }
    }


    /**
     * method for removing specific user subcategories.
     * @param categoryName
     * @param subcategoryToRemove
     */
    public void removeUserSubcategory(String categoryName, String subcategoryToRemove){
        double expenseToRemove = 0;

        //the index of the subcategory to remove.
        int indexOfSubcategoryToRemove = this.userCategory.getSubcategoryNameIndex(categoryName, subcategoryToRemove);

        /*
         * Find the expense to remove only if the subcategory exists for the selected category.
         */
        if(categoryName.equalsIgnoreCase("Food") && indexOfSubcategoryToRemove != -1){
            double [] allFood = this.userCategory.getFoodCostsNumerical();
            expenseToRemove = allFood[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Housing")  && indexOfSubcategoryToRemove != -1){
            double [] allHousing = this.userCategory.getHousingCostsNumerical();
            expenseToRemove = allHousing[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Recreation")  && indexOfSubcategoryToRemove != -1){
            double [] allRecreation = this.userCategory.getRecreationCostsNumerical();
            expenseToRemove = allRecreation[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Lifestyle")  && indexOfSubcategoryToRemove != -1){
            double [] allLifestyle = this.userCategory.getLifestyleCostsNumerical();
            expenseToRemove = allLifestyle[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Commute")  && indexOfSubcategoryToRemove != -1){
            double [] allCommute = this.userCategory.getCommuteCostsNumber();
            expenseToRemove = allCommute[indexOfSubcategoryToRemove];
        }
        //remove the subcategory with the category name and the subcategory to remove.
        this.userCategory.removeSubcategory(categoryName, subcategoryToRemove);

        //decrease the total monthly expenses due to removing the subcategory if and only if the subcategory is valid.
        if(!(this.userCategory.isError())) {
            this.totalMonthlyExpenses -= expenseToRemove;
            this.immediateStatus = "You Have Removed " + subcategoryToRemove + " From " + categoryName + " Category";
        }
        else{
            this.immediateStatus =  this.userCategory.getErrorMSG();
        }

    }

    /**
     * This method resets the main user by resetting all of the subcategories added.
     * However, this method does not reset the new users added.
     */
    public void resetMainUser(){
        String [] allFood = this.userCategory.getFood();
        String [] allHousing = this.userCategory.getHousing();
        String [] allCommute = this.userCategory.getCommute();
        String [] allLifestyle = this.userCategory.getLifestyle();
        String [] allRecreation = this.userCategory.getRecreation();

        for(int i = 0; i <  allFood.length; i++){
            removeUserSubcategory("Food", allFood[i]);
        }
        for(int i = 0; i <  allHousing.length; i++){
            removeUserSubcategory("Housing", allHousing[i]);
        }
        for(int i = 0; i <  allCommute.length; i++){
            removeUserSubcategory("Commute", allCommute[i]);
        }
        for(int i = 0; i <  allLifestyle.length; i++){
            removeUserSubcategory("Lifestyle", allLifestyle[i]);
        }
        for(int i = 0; i <  allRecreation.length; i++){
            removeUserSubcategory("Recreation", allRecreation[i]);
        }

    }

    /**
     * method for adding an expense to a specific subcategory in a category for the user.
     * @param categoryName
     * @param subcategoryName
     * @param additionalExpense
     */
    public void addUserAdditionalExpense(String categoryName, String subcategoryName, double additionalExpense){

        this.userCategory.addExpense(additionalExpense, subcategoryName,categoryName);

        //increase the total user income due to adding additional expense if and only if there is no error in user adding the additional expense.
        if(!(this.userCategory.isError())) {
            this.totalMonthlyExpenses += additionalExpense;
            this.immediateStatus = "You Have Added Additional Expense To " + subcategoryName + " for " + getCurrencySymbol() + additionalExpense;
        }
        else{
            this.immediateStatus =  this.userCategory.getErrorMSG();
        }
    }


    /**
     * get immediate status on what the user has added after adding subcategory.
     * @return immediateStatus
     */
    public String userImmediateStatus(){
        return this.immediateStatus;
    }


    /**
     * return a string representation of the users status for a specific category.
     * @return status
     */
    public String getUserStatus(){
        String status = "";

        status = this.userCategory.getStatus();

        return status;
    }


    /**
     * UsersBudgetClass method for returning the user daily budget amount.
     * The formula for calculating user daily budget is (monthly expenses / 30).
     * @return
     */
    public String userDailyBudget(){
        if(!(this.userCategory.isError())) {
            this.dailyBudget = (this.totalMonthlyExpenses / 30);
        }

        //rounding the daily budget to two decimal places with thousands separator.
        String dailyBudgetResult = String.format("%,.2f", this.dailyBudget);

        return dailyBudgetResult;
    }

    /**
     * method for returning the total income for the user.
     * the total income is the initial income of the user minus the monthly expenses.
     * @return
     */
    public String userTotalIncome(){
        if(!(this.userCategory.isError())) {
            this.totalIncome = ( this.totalInitialIncome - this.totalMonthlyExpenses);
        }
        //rounding the total balance to two decimal places wuth thousands seperator.
        String totalBalanceResult = String.format("%,.2f",this.totalIncome);
        return totalBalanceResult;
    }


    /**
     * Helper method to return the current CategoriesClass object.
     * @return userCategory object which is from CategoriesClass.
     */
    public CategoriesClass categoriesObject(){
        return this.userCategory;
    }


    /**
     * This method will set sorted subcategories for each and every category.
     * @param sortType
     * @param categoryToSort
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortAllSubcategoriesByExpense(String sortType, String categoryToSort) {

        if (categoryToSort.equalsIgnoreCase("Food")) {
            this.sortedFood = userCategory.getFood(); //initially sorted food is all the current food subcategory names.
            this.sortedFoodCosts = userCategory.getFoodCostsNumerical(); //initially sorted food costs is all the current food subcategory costs.
            int numFood = sortedFood.length;

            if (sortType.equalsIgnoreCase("Ascending Alphabetical Order")) {
                this.foodMap = new TreeMap<>();
                sortAlphabeticallyCategoryFromMap(this.sortedFoodCosts,this.sortedFood,this.foodMap,numFood,categoryToSort);
            }
            else if (sortType.equalsIgnoreCase("Descending Alphabetical Order")) {
                this.foodMap = new TreeMap<>(Collections.reverseOrder());
                sortAlphabeticallyCategoryFromMap(this.sortedFoodCosts, this.sortedFood, this.foodMap, numFood, categoryToSort);
            }
            else if (sortType.equalsIgnoreCase("Ascending Expenses")) {
                //TODO

            }
            else if (sortType.equalsIgnoreCase("Descending Expenses")) {
                //TODO

            }


        } else if (categoryToSort.equalsIgnoreCase("Housing")) {
            this.sortedHousing = userCategory.getHousing(); //initially sorted housing is all the current housing subcategory names.
            this.sortedHousingCosts = userCategory.getHousingCostsNumerical(); //initially sorted housing costs is all the current housing subcategory costs.
            int numHousing = sortedHousing.length;

            if (sortType.equalsIgnoreCase("Ascending Alphabetical Order")) {
                this.housingMap = new TreeMap<>();
                sortAlphabeticallyCategoryFromMap(this.sortedHousingCosts, this.sortedHousing, this.housingMap, numHousing, categoryToSort);
            }
            else if(sortType.equalsIgnoreCase("Descending Alphabetical Order")) {
                this.housingMap = new TreeMap<>(Collections.reverseOrder());
                sortAlphabeticallyCategoryFromMap(this.sortedHousingCosts, this.sortedHousing, this.housingMap, numHousing, categoryToSort);
            }
        } else if (categoryToSort.equalsIgnoreCase("Commute")) {
            this.sortedCommute = userCategory.getCommute(); //initially sorted housing is all the current housing subcategory names.
            this.sortedCommuteCosts = userCategory.getCommuteCostsNumber(); //initially sorted housing costs is all the current housing subcategory costs.
            int numCommute = sortedCommute.length;

            if (sortType.equalsIgnoreCase("Ascending Alphabetical Order")) {
                this.housingMap = new TreeMap<>();
                sortAlphabeticallyCategoryFromMap(this.sortedCommuteCosts, this.sortedCommute, this.commuteMap, numCommute, categoryToSort);
            } else if (sortType.equalsIgnoreCase("Descending Alphabetical Order")) {
                this.housingMap = new TreeMap<>(Collections.reverseOrder());
                sortAlphabeticallyCategoryFromMap(this.sortedCommuteCosts, this.sortedCommute, this.commuteMap, numCommute, categoryToSort);
            }
        }else if (categoryToSort.equalsIgnoreCase("Lifestyle")) {
            this.sortedLifestyle = userCategory.getLifestyle(); //initially sorted lifestyle is all the current lifestyle subcategory names.
            this.sortedLifestyleCosts = userCategory.getLifestyleCostsNumerical(); //initially sorted lifestyle costs is all the current lifestyle subcategory costs.
            int numLifestyles = sortedLifestyle.length;

            if (sortType.equalsIgnoreCase("Ascending Alphabetical Order")) {
                this.lifestyleMap = new TreeMap<>();
                sortAlphabeticallyCategoryFromMap(this.sortedLifestyleCosts, this.sortedLifestyle, this.lifestyleMap, numLifestyles, categoryToSort);
            }
            else if(sortType.equalsIgnoreCase("Descending Alphabetical Order")) {
                this.lifestyleMap = new TreeMap<>(Collections.reverseOrder());
                sortAlphabeticallyCategoryFromMap(this.sortedLifestyleCosts, this.sortedLifestyle, this.lifestyleMap, numLifestyles, categoryToSort);
            }

        } else if (categoryToSort.equalsIgnoreCase("Recreation")) {
            this.sortedRecreation = userCategory.getRecreation(); //initially sorted recreation is all the current recreation subcategory names.
            this.sortedRecreationCosts = userCategory.getRecreationCostsNumerical(); //initially sorted recreation costs is all the current recreation subcategory costs.
            int numRecreation = sortedRecreation.length;

            if (sortType.equalsIgnoreCase("Ascending Alphabetical Order")) {
                this.recreationMap = new TreeMap<>();
                sortAlphabeticallyCategoryFromMap(this.sortedRecreationCosts, this.sortedRecreation, this.recreationMap, numRecreation, categoryToSort);
            }
            else if(sortType.equalsIgnoreCase("Descending Alphabetical Order")) {
                this.recreationMap = new TreeMap<>(Collections.reverseOrder());
                sortAlphabeticallyCategoryFromMap(this.sortedRecreationCosts, this.sortedRecreation, this.recreationMap, numRecreation, categoryToSort);
            }

        }

    }


    /**
     * Helper method to sort the specific category array using its category map. ALPHABETICALLY.
     * Assuming the Map is Map<String,Double> for the category.
     * @param categorySubcategoriesCosts
     * @param categorySubcategory
     * @param categoryMap
     * @param numCategorySubcategories
     * @param categoryName
     */
    public void sortAlphabeticallyCategoryFromMap(double [] categorySubcategoriesCosts, String [] categorySubcategory, TreeMap categoryMap, int numCategorySubcategories, String categoryName){

        int iter = 0;

        //insert the subcategories & its costs into the category tree.
        for (int i = 0; i < numCategorySubcategories; i++) {
            categoryMap.put(categorySubcategory[i], categorySubcategoriesCosts[i]);
        }

        Map<String,Double> catMap;
        catMap = categoryMap;

        //set the category subcategories & its costs in order using the category tree.
        for (Map.Entry<String,Double> entry : catMap.entrySet()) {
            String key = entry.getKey();
            double value = entry.getValue();

            categorySubcategoriesCosts[iter] = value;
            categorySubcategory[iter] = key;

            iter++;
        }

        categoriesObject().setSubcategoryNames(categorySubcategory, categoryName);
        categoriesObject().setSubcategoryCosts(categorySubcategoriesCosts, categoryName);

    }


    /*
     * implemented binary search to find the least expensive subcategories for current category.
     */
    public void searchLeastExpensive(){
        //TODO
    }


    /*
     * implemented binary search to find the median expensive subcategories for current category.
     */
    public void searchMedianExpensive(){
        //TODO
    }


    //method for splitting income among users.
    public void splitIncomeAmongUsers(int numberOfUsers){
        //TODO in another class ? -> MultipleUsers class ?
    }


    /**
     * UsersBudgetClass Method which returns the number of subcategories REMAINING for a specific category.
     * @param categoryName
     * @return
     */
    public int getRemainingNOS(String categoryName){

        if (categoryName.equalsIgnoreCase("Food") && !(this.userCategory.isError())) {
            this.NOS = this.categoriesObject().getMAX_NOF() - this.categoriesObject().getNOF();
        } else if (categoryName.equalsIgnoreCase("Housing")  && !(this.userCategory.isError())) {
            this.NOS = this.categoriesObject().getMAX_NOH() - this.categoriesObject().getNOH();
        } else if (categoryName.equalsIgnoreCase("Lifestyle") && !(this.userCategory.isError())) {
            this.NOS = this.categoriesObject().getMAX_NOL() - this.categoriesObject().getNOL();
        } else if (categoryName.equalsIgnoreCase("Commute") && !(this.userCategory.isError())) {
            this.NOS = this.categoriesObject().getMAX_NOC() - this.categoriesObject().getNOC();
        } else if (categoryName.equalsIgnoreCase("Recreation") && !(this.userCategory.isError())) {
            this.NOS = this.categoriesObject().getMAX_NOR() - this.categoriesObject().getNOR();
        }

        return this.NOS;

    }

    /**
     * Helper method used in ViewSubcategoryPopup.java and AdditionalExpensePopup.java to update total income interface.
     * @return
     */
    public String getUserTotalIncome(){
        return userTotalIncome();
    }

    /**
     * Helper method used in ViewSubcategoryPopup.java and AdditionalExpensePopup.java to update total income interface.
     * @return
     */
    public String getUserDailyBudget(){
        return userDailyBudget();
    }

    /**
     * Helper method used in SettingsFragment.java class to get the monthly expenses.
     */
    public double getTotalMonthlyExpenses(){
        return this.totalMonthlyExpenses;
    }

    /**
     * Helper method used in SettingsFragment.java class to set the new total monthly expenses after conversion.
     *  @param convertedMonthlyExpenses
     */
    public void setTotalMonthlyExpenses(double convertedMonthlyExpenses){
        this.totalMonthlyExpenses = convertedMonthlyExpenses;
    }

    /**
     * Helper method used to set the currency symbol by the SettingsFragment.
     * @param currencySymbol
     */
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    /**
     * Helper method to get the currency symbol used in MainActivity and CategoriesClass.
     * @return the currency symbol
     */
    public String getCurrencySymbol(){
        //if the currency symbol is null then the default currency symbol is CAD -> "$"
        if(this.currencySymbol == null){
            this.currencySymbol = "$";
        }
        return this.currencySymbol;
    }

    /**
     * add the new user to the hash set. This method is used in MoneyFragment when a new user is added.
     */
    public void addNewUsers(String newUser){
        newUserSet.add(newUser); //add the user list to the hash set.

        UsersBudgetClass newUserBudget = new UsersBudgetClass(new CategoriesClass(), new CurrencyConversionClass());
        usersBudgetMap.put(newUser, newUserBudget); //add the new user to the set of all user budgets. The key is the name of the user.
    }


    /**
     * Return the hashset containing all the new users names. This method is used in MoneyFragment.
     */
    public HashSet<String> getNewUserSet(){
        return this.newUserSet;
    }

    /**
     * this method will return the UserBudgetClass object using the user name as the key, the object (value) is obtained and returned using the usersBudgetMap.
     */
    public UsersBudgetClass getUserObjectOfName(String userName){
        UsersBudgetClass userObj = usersBudgetMap.get(userName);
        return userObj;
    }

    /**
     * This method will remove the user from the parameter.
     * @param name
     */
    public void removeUser(String name) {
        newUserSet.remove(name);
    }


    /**
     * Helper method  which calculates the monthly budget.
     * @param countryDisplayName
     * @param currentMonthlyExp
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String monthlyBudgetFutureProcess(String countryDisplayName, String currentMonthlyExp){

        CompletableFuture<Void> currentMonthlyExpFuture = CompletableFuture.runAsync(() -> {
            try {
                currencyConversionObj.setCurrencySymbolAndFormat(countryDisplayName, currentMonthlyExp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        currentMonthlyExpFuture.join();


        return currencyConversionObj.toString();
    }

    /**
     * Helper method calculates the daily budget.
     * @param countryDisplayName
     * @param currentDailyBudget
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String dailyBudgetFutureProcess(String countryDisplayName, String currentDailyBudget){

        CompletableFuture<Void> currentDailyBudgetFuture = CompletableFuture.runAsync(() -> {
            try {
                currencyConversionObj.setCurrencySymbolAndFormat(countryDisplayName, currentDailyBudget);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        currentDailyBudgetFuture.join();

        return currencyConversionObj.toString();
    }


    /**
     * Helper method which calculates the total income.
     * @param countryDisplayName
     * @param currentTotalIncome
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String totalIncomeFutureProcess(String countryDisplayName, String currentTotalIncome) {

        CompletableFuture<Void> currentTotalIncomeFuture = CompletableFuture.runAsync(() -> {
            try {
                currencyConversionObj.setCurrencySymbolAndFormat(countryDisplayName, currentTotalIncome);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        currentTotalIncomeFuture.join();

        return currencyConversionObj.toString();
    }

    /**
     * Helper method for currencyChanged() in SettingsFragment.java file.
     * @return
     */
    public double[] delayAndApplyCurrencyChange(int numberOfSubcategories, double[] categoryCostArr){

        for(int i = 0; i < numberOfSubcategories; i++){
            double conversionRate = currencyConversionObj.getConversionRate();
            categoryCostArr[i] = categoryCostArr[i] * conversionRate;
        }

        return categoryCostArr;
    }


    /**
     * Helper Method executed from the "save" switch in the Settings Fragment. The currency has been changed.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void currencyChanged(String countrySelectedDisplayName) {

        //The conversion object is not saved in the JSON from Gson so the current currency rate from the user object is set to the currency object.
        currencyConversionObj.setConversionRate(currencyRate);

        currentCountrySelected = countrySelectedDisplayName;

        //get all the subcategories for each cateogry and the number of subcategories.
        double[] allFoodCosts = this.userCategory.getFoodCostsNumerical();
        int NOF = this.userCategory.getNOF();
        double[] allHousingCosts = this.userCategory.getHousingCostsNumerical();
        int NOH = this.userCategory.getNOH();
        double[] allCommuteCosts = this.userCategory.getCommuteCostsNumber();
        int NOC = this.userCategory.getNOC();
        double[] allRecreationCosts = this.userCategory.getRecreationCostsNumerical();
        int NOR = this.userCategory.getNOR();
        double[] allLifestyleCosts = this.userCategory.getLifestyleCostsNumerical();
        int NOL = this.userCategory.getNOL();

        String currentDailyBudget = String.format("%.2f",this.dailyBudget);
        String currentTotalIncome = String.format("%.2f", this.totalIncome);

        double monthlyExpenses = Double.parseDouble(String.valueOf(getTotalMonthlyExpenses()));

        //convert the monthly expenses.
        currentMonthlyExpensesConverted = monthlyBudgetFutureProcess(countrySelectedDisplayName, String.valueOf(monthlyExpenses));

        //convert the daily budget.
        dailyBudgetConverted = dailyBudgetFutureProcess(countrySelectedDisplayName, currentDailyBudget);

        //convert the total income.
        totalIncomeConverted = totalIncomeFutureProcess(countrySelectedDisplayName, currentTotalIncome);


        //background thread to convert all the costs and then using handler to update the UI.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                /*
                 * iterate through all the subcategories and format them according to their currency.
                 * Handlers are used to update the arrays after the call to the method setCurrencySymbolAndFormat which does HTML parsing from Bank Of Canada asynchronously.
                 */
                double[] latestFoodCosts = delayAndApplyCurrencyChange(NOF, allFoodCosts);
                double[] latestHousingCosts = delayAndApplyCurrencyChange(NOH, allHousingCosts);
                double[] latestLifestyleCosts = delayAndApplyCurrencyChange(NOL, allLifestyleCosts);
                double[] latestCommuteCosts = delayAndApplyCurrencyChange(NOC, allCommuteCosts);
                double[] latestRecreationCosts = delayAndApplyCurrencyChange(NOR, allRecreationCosts);

                //set the categories that are all formatted by the user-selected currency.
                userCategory.setSubcategoryCosts(latestFoodCosts, "food");
                userCategory.setSubcategoryCosts(latestHousingCosts, "housing");
                userCategory.setSubcategoryCosts(latestCommuteCosts, "commute");
                userCategory.setSubcategoryCosts(latestRecreationCosts, "recreation");
                userCategory.setSubcategoryCosts(latestLifestyleCosts, "lifestyle");

                //the current currency symbol for the currency chosen.
                String currencySymbol = currencyConversionObj.getCurrencySymbol();

                System.out.println(dailyBudgetConverted + " is dailyBudgetConverted");
                System.out.println(totalIncomeConverted + " is totalIncomeConverted");
                System.out.println(currentMonthlyExpensesConverted + " is currentMonthlyExpensesConverted");

                //set the converted monthly expenses.
                setTotalMonthlyExpenses(Double.parseDouble(currentMonthlyExpensesConverted));

                Handler handler1 = new Handler(Looper.getMainLooper());
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //set the currency symbol in the UsersBudget.java class.
                        setCurrencySymbol(currencySymbol);

                        categoriesObject().setCurrencySymbol(currencySymbol);

                        // make update on the UI for daily budget.
                        updateDailyBudgetFromActivity();
                    }
                }, 1000);

                Handler handler2 = new Handler(Looper.getMainLooper());
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //set the currency symbol in the UsersBudget.java class.
                        setCurrencySymbol(currencySymbol);

                        categoriesObject().setCurrencySymbol(currencySymbol);

                        // make update on the UI for daily budget.
                        updateTotalIncomeFromActivity();
                    }
                }, 1000);

            }
        });

        thread.start();

        this.currencyRate = currencyConversionObj.getConversionRate(); //the current currency rate is that of the currency conversion object.

        /*
         * Applying the same conversion to the new added users in the user budget hashmap.
         */
        for(UsersBudgetClass users : usersBudgetMap.values()){
            users.setCallback((MainActivity)this.userCallback);
            users.currencyChanged(countrySelectedDisplayName);
        }

        this.NOCC++; //increment the number of total conversions. Might limit the number of conversions for in-app purchases in the future.

    }

    /**
     * This method will return all the countries array.
     * @return
     */
    public String[] getAllCountries(){
        return currencyConversionObj.getAllCountries();
    }

    /**
     * This method will return the number of total currency conversion taken place over the lifetime of the app.
     * @return
     */
    public int getNumTotalCurrencyConversions(){
        return this.NOCC;
    }

    /**
     * Setting the user callback from the Main Activity. Main Activity IS-A UserBudgetCallback, so the userCallback can be passed as an argument when MainActivity is expected.
     */
    public void setCallback(MainActivity activity) {
        this.userCallback = (UserBudgetCallback) activity;
    }

    /**
     * This method will update the total income view from the Main Activity.
     */
    public void updateTotalIncomeFromActivity(){
        this.userCallback.updateTotalIncome();
    }

    /**
     * This method will update the daily budget view from the Main Activity.
     */
    public void updateDailyBudgetFromActivity(){
        this.userCallback.updateDailyBudget();
    }

    /**
     * This method will initiate the alert dialog for resetting the app view from the Main Activity.
     */
    public void clearAllWarningMessageFromActivity(){
        this.userCallback.clearAllWarningMessage();
    }

    /**
     * This method will add the subcategories to user google sheet from the Main Activity.
     * @param categoryName
     * @param subcategoryToAdd
     * @param cost
     * @throws IOException
     */
    public void addSubcategoriesToSheetFromActivity(String categoryName, String subcategoryToAdd, String cost) throws IOException {
        this.userCallback.addSubcategoriesToSheet(categoryName,subcategoryToAdd,cost);
    }

    /**
     * This method will add the animation from the Main Activity.
     * @param isAnimate
     */
    public void addAnimationFromActivity(Boolean isAnimate){
        this.userCallback.addBackgroundAnimation(isAnimate);
    }

    /**
     * This method shows the welcome back greeting view from the Main Activity.
     */
    public void welcomeBackGreetingFromActivity(String firstName, String lastName){
        this.userCallback.welcomeBackGreeting(firstName, lastName);
    }

    public String getDailyBudgetFromActivity(){
        return this.userCallback.getDailyBudget();
    }

    public String getTotalIncomeFromActivity(){
        return this.userCallback.getTotalIncome();
    }

}
