package CodingProject.budgetvision.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

public class UsersBudgetClass {

    /*
     * This class is for the controls for the users and determining their budgets.
     * this class will implement the budgets for the App owner (user) .
     *TODO : using the users budget data export a spreadsheet or excel file to their email account.
     */


    /*
     * income, total balance and expenses.
     */
    private double totalBalance; //total balance of a user.
    private double totalIncome; //total income for the user.
    private double totalInitialIncome; //initial income for the user.
    private double totalMonthlyExpenses; //total expenses for the user.

    /*
     * daily budget, monthly budget, projection etc.
     */
    private double dailyBudget; //daily budget for the user.

    private CategoriesClass userCategory; //Categories object userCategory.

    private String sortedFood[]; //stores all sorted food subcategories.
    private String sortedHousing[]; //stores all sorted housing subcategories.
    private String sortedLifestyle[]; //stores all sorted lifestyle subcategories.
    private String sortedCommute[]; //stores all sorted commute subcategories.
    private String sortedRecreation[]; //stores all sorted recreation subcategories.
    private String categoryName; //stores the current category name.

    private double sortedFoodCosts[]; //stores all sorted food costs subcategories.
    private double sortedHousingCosts[]; //stores all sorted housing costs subcategories.
    private double sortedLifestyleCosts[]; //stores all sorted lifestyle costs subcategories.
    private double sortedCommuteCosts[]; //stores all sorted commute costs subcategories.
    private double sortedRecreationCosts[]; //stores all sorted recreation costs subcategories.

    //TreeMaps to store the subcategories and subcategory costs. Where the key is subcategory cost and the value is the subcategories,
    TreeMap <String,Double> foodMap = new TreeMap<>(); //Tree Map for food.
    TreeMap <String,Double> housingMap = new TreeMap<>(); //Tree Map for housing.
    TreeMap <String,Double> lifestyleMap = new TreeMap<>(); //Tree Map for lifestyle.
    TreeMap <String,Double> commuteMap = new TreeMap<>(); //Tree Map for commute.
    TreeMap <String,Double> recreationMap = new TreeMap<>(); //Tree Map for recreation.

    private int NOS; //store the number of subcategories REMAINING for a specific category.

    //empty User constructor.
    public UsersBudgetClass(){
        this.userCategory = new CategoriesClass(); //creating a user category object with the empty default constructor.
    }


    //method for increasing the user initial income by an initial amount.
    public void increaseUserInitialIncome(double amount){
        this.totalInitialIncome += amount;

    }


    //method for removing the user initial income.
    public void removeUserInitialIncome(){
        this.totalInitialIncome = 0;
    }


    //method for decreasing the user initial income by an initial amount.
    public void decreaseUserInitialIncome(double amount){
        this.totalInitialIncome -= amount;
    }


    //method for increasing user income.
    public void increaseUserIncome(double amountToIncreaseBy){
        this.totalIncome += amountToIncreaseBy;

    }


    //method for decreasing user income.
    public void decreaseUserIncome(double amountToDecreaseBy){
        this.totalIncome -= amountToDecreaseBy;
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

        //add the subcategory and the expense associated with it.
        this.userCategory.addSubCategory(this.categoryName, subCategoryToAdd,subCategoryCost);

        if(!(this.userCategory.isError())) {
            //increase the total monthly expenses due to adding the subcategory.
            this.totalMonthlyExpenses += subCategoryCost;

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
            double [] allFood = this.userCategory.getFoodCostsNumber();
            expenseToRemove = allFood[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Housing")  && indexOfSubcategoryToRemove != -1){
            double [] allHousing = this.userCategory.getHousingCostsNumber();
            expenseToRemove = allHousing[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Recreation")  && indexOfSubcategoryToRemove != -1){
            double [] allRecreation = this.userCategory.getRecreationCostsNumber();
            expenseToRemove = allRecreation[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Lifestyle")  && indexOfSubcategoryToRemove != -1){
            double [] allLifestyle = this.userCategory.getLifestyleCostsNumber();
            expenseToRemove = allLifestyle[indexOfSubcategoryToRemove];
        }
        else if(categoryName.equalsIgnoreCase("Commute")  && indexOfSubcategoryToRemove != -1){
            double [] allCommute = this.userCategory.getCommuteCostsNumber();
            expenseToRemove = allCommute[indexOfSubcategoryToRemove];
        }

        //decrease the total monthly expenses due to removing the subcategory if and only if the subcategory is valid.
        if(!(this.userCategory.isError())) {
            this.totalMonthlyExpenses -= expenseToRemove;
        }

        //remove the subcategory with the category name and the subcategory to remove.
        this.userCategory.removeSubcategory(categoryName, subcategoryToRemove);
    }


    /**
     * /method for adding an expense to a specific subcategory in a category for the user.
     * @param categoryName
     * @param subcategoryName
     * @param additionalExpense
     */
    public void addUserAdditionalExpense(String categoryName, String subcategoryName, double additionalExpense){
        this.userCategory.addExpense(additionalExpense, subcategoryName,categoryName);

        //increase the total user income due to adding additional expense if and only if there is no error in user adding the additional expense.
        if(!(this.userCategory.isError())) {
            this.totalMonthlyExpenses += additionalExpense;
        }

    }


    /**
     * get immediate status on what the user has added after adding subcategory.
     * @return
     */
    public String userImmediateStatus(){
        String immediateStatus = "";

        if(this.userCategory.isError()){
            immediateStatus = this.userCategory.getErrorMSG();
        }
        else if (!(this.userCategory.isError())) {
            String subcategoryName = this.userCategory.getCurrentSubcategoryName();
            String subcategoryExpense = String.format("%.2f", this.userCategory.getCurrentSubcategoryCostsNumber());

            immediateStatus = "You Have Added " + subcategoryName + " for $" + subcategoryExpense;
        }

        return immediateStatus;
    }


    /**
     * return a string representation of the users status for a specific category.
     * @return
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


    //method for returning the total balance for the user.
    public String userTotalBalance(){
        if(!(this.userCategory.isError())) {
            this.totalBalance = ((this.totalIncome + this.totalInitialIncome) - this.totalMonthlyExpenses);
        }
        //rounding the total balance to two decimal places wuth thousands seperator.
        String totalBalanceResult = String.format("%,.2f",this.totalBalance);

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
    public void sortAllSubcategoriesByExpense(String sortType, String categoryToSort) {

        if (categoryToSort.equalsIgnoreCase("Food")) {
            this.sortedFood = userCategory.getFood(); //initially sorted food is all the current food subcategory names.
            this.sortedFoodCosts = userCategory.getFoodCostsNumber(); //initially sorted food costs is all the current food subcategory costs.
            int numFood = sortedFood.length;

            if (sortType.equalsIgnoreCase("Ascending Alphabetical Order")) {
                this.foodMap = new TreeMap<>();
                sortAlphabeticallyCategoryFromMap(this.sortedFoodCosts,this.sortedFood,this.foodMap,numFood,categoryToSort);
            }
            else if (sortType.equalsIgnoreCase("Descending Alphabetical Order")) {
                this.foodMap = new TreeMap<>(Collections.reverseOrder());
                sortAlphabeticallyCategoryFromMap(this.sortedFoodCosts, this.sortedFood, this.foodMap, numFood, categoryToSort);
            }

        } else if (categoryToSort.equalsIgnoreCase("Housing")) {
            this.sortedHousing = userCategory.getHousing(); //initially sorted housing is all the current housing subcategory names.
            this.sortedHousingCosts = userCategory.getHousingCostsNumber(); //initially sorted housing costs is all the current housing subcategory costs.
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
            this.sortedLifestyleCosts = userCategory.getLifestyleCostsNumber(); //initially sorted lifestyle costs is all the current lifestyle subcategory costs.
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
            this.sortedRecreationCosts = userCategory.getRecreationCostsNumber(); //initially sorted recreation costs is all the current recreation subcategory costs.
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

        categoriesObject().setSortedSubcategoryNames(categorySubcategory, categoryName);
        categoriesObject().setSortedSubcategoryCosts(categorySubcategoriesCosts, categoryName);

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

        if(!(this.userCategory.isError())) {
            if (categoryName.equalsIgnoreCase("Food")) {
                this.NOS = this.categoriesObject().getMAX_NOF() - this.categoriesObject().getNOF();
            } else if (categoryName.equalsIgnoreCase("Housing")) {
                this.NOS = this.categoriesObject().getMAX_NOH() - this.categoriesObject().getNOH();
            } else if (categoryName.equalsIgnoreCase("Lifestyle")) {
                this.NOS = this.categoriesObject().getMAX_NOL() - this.categoriesObject().getNOL();
            } else if (categoryName.equalsIgnoreCase("Commute")) {
                this.NOS = this.categoriesObject().getMAX_NOC() - this.categoriesObject().getNOC();
            } else if (categoryName.equalsIgnoreCase("Recreation")) {
                this.NOS = this.categoriesObject().getMAX_NOR() - this.categoriesObject().getNOR();
            }
        }
        return this.NOS;

    }


}
