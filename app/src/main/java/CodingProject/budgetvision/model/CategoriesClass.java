package CodingProject.budgetvision.model;

import java.io.IOException;

import CodingProject.budgetvision.controller.MainActivity;

public class CategoriesClass{

    /**
     * This class is for creating functionality for the budget categories.
     * All the categories are assumed to be monthly categories. Thus all the expenses for this class are monthly expenses.
     * Categories will be classified as Food, Housing, Lifestyle, etc.
     * In total, so far there are 5 categories. Which are food, housing, lifestyle, commute and recreation.
     * Subcategories are contained in the Category.
     * This class also adds the error statements to the user if they do not follow conventions of the app.
     * TODO: Possible 6th category called Miscellaneous category?
     */

    private double currentExpenses; //current expenses.

    //All of the sub-categories corresponding to each category names.
    private String[] food; //stores all food subcategories.
    private String[] housing; //stores all housing subcategories.
    private String[] lifestyle; //stores all lifestyle subcategories.
    private String[] commute; //stores all commute subcategories.
    private String[] recreation; //stores all recreation subcategories.


    //All of the subcategories costs.
    private double[] foodCosts; //stores all food subcategories costs.
    private double[] housingCosts; //stores all housing subcategories costs.
    private double[] lifestyleCosts; //stores all lifestyle subcategories costs.
    private double[] commuteCosts; //stores all commute subcategories costs.
    private double[] recreationCosts; //stores all recreation subcategories costs.


    //All of the counters for each subcategories.
    private int NOF; //current number of food subcategories.
    private int NOH; //current number of housing subcategories.
    private int NOL; //current number of lifestyle subcategories.
    private int NOC; //current number of commute subcategories.
    private int NOR; //current number of recreation subcategories.


    //All of the counter for each subcategories costs.
    private int NOF_Cost; //current number of food cost subcategories.
    private int NOH_Cost; //current number of housing cost subcategories.
    private int NOL_Cost; //current number of lifestyle cost subcategories.
    private int NOC_Cost; //current number of commute cost subcategories.
    private int NOR_Cost; //current number of recreation cost subcategories.

    /*
     *  5 categories which are (food, housing, lifestyle, commute, recreation)
     *  50 maximum subcategories for food.
     *  50 maximum subcategories for housing.
     *  50 maximum subcategories for lifestyle.
     *  50 maximum subcategories for commute.
     *  50 maximum subcategories for recreation.
     */
    private final int MAX_NOF = 50; //maximum number of food subcategories.
    private final int MAX_NOH = 50; //maximum number of housing subcategories.
    private final int MAX_NOL = 50; //maximum number of lifestyle subcategories.
    private final int MAX_NOC = 50; //maximum number of commute subcategories.
    private final int MAX_NOR = 50; //maximum number of recreation subcategories.

    private String category; //current category.
    private String subCategory; //current subcategory.
    private double subCategoryCost; //current sub-category cost.

    /*
     * An error message appears when one of the following is true.
     * 1. Adding more than the maximum number of sub-categories for a specific category. (FATAL ERROR)
     * 2. removing a non-existing sub-category.
     * 3. Adding an expense to a non-existing sub-category.
     * 4. non-positive expense is added.
     */
    private String errorMSG; //current error message.
    private boolean isError; //boolean variable to check if there is any non-fatal error.
    private boolean isFatalError; //boolean variable to check for fatal error.
    private boolean isDuplicateSubcategory; //check if there is a duplicate subcategory.

    private double currencyRate = 1; //currency rate initially 1 since default currency is CAD.
    private String currencySymbol; // the currency symbol variable obtained from MainActivity.java class.

    //thread to be run in the background used for executing the google sheet commands.
    Thread doGoogleSheetInBackground;

    //user unique Id from the SettingsFragment.
    String uniqueId;

    /**
     * empty Categories constructor to intitialize all the categories names and costs.
     */
    public CategoriesClass() {
        //instantiating names related categories and sub-categories arrays to their maximum length.
        this.food = new String[MAX_NOF];
        this.housing = new String[MAX_NOH];
        this.lifestyle = new String[MAX_NOL];
        this.commute = new String[MAX_NOC];
        this.recreation = new String[MAX_NOR];

        //instantiating costs related categories and sub-categories arrays to their maximum length.
        this.foodCosts = new double[MAX_NOF];
        this.housingCosts = new double[MAX_NOH];
        this.lifestyleCosts = new double[MAX_NOL];
        this.commuteCosts = new double[MAX_NOC];
        this.recreationCosts = new double[MAX_NOR];
    }

    /**
     * Categories constructor; with a Category as parameter. This constructor will create a new categories object each time it is called.
     * @param category
     */
    public CategoriesClass(String category) {
        //calling the empty constructor to instantiate all of the arrays to their maximum desired length.
        this();
        this.category = category;

    }

    /**
     * This helper method is used by the Settings Fragment to set the user unique Id.
     * The user unique Id is the unique individual identification from the user after signing in to their google account.
     */
    public void setUserUniqueId(String uniqueId){
        this.uniqueId = uniqueId;
    }


    /**
     * method for adding category without calling the Categories constructor with String parameter.
     * @param categoryToAdd
     */
    public void addCategory(String categoryToAdd){
        this.category = categoryToAdd;
    }


    /**
     * method for adding sub-categories in an existing category.
     * the first parameter is a category name to be used to find a duplicate subcategory.
     * Since a user can click on different category cards this.category is not ideal to use to compare for duplicate categories.
     * However, this.category is ideal to use immediately after the addCategory("category") constructor is called.
     * @param categoryName
     * @param subCategoryToAdd
     * @param subCategoryCost
     */
    public void addSubCategory(String categoryName, String subCategoryToAdd, double subCategoryCost) {
        this.currencySymbol = MainActivity.getInstance().getUser().getCurrencySymbol();

        //reset the error before the subcategory is added. Calling this method will not reset any fatal errors.
        resetError();

        this.subCategory = subCategoryToAdd.trim(); //remove the trailing and leading whitespaces if-any from the user inputted subcategory to be added.
        this.subCategoryCost = subCategoryCost * this.currencyRate;
        String[] tempNames; //temp array stores all names.
        double[] tempCosts; //temp array stores all costs.

        //if there is no fatal error (exceeding maximum subcategories for category), check for duplicates.
        if(!isFatalError) {
            this.isDuplicateSubcategory = isDuplicate(categoryName, this.subCategory);
        }

        String userUniqueId = uniqueId;

        //only post data if they are signed in to google in the application. Thus, the users unique id cannot be null.
        if(userUniqueId != null) {
            //thread to run in the background.
            doGoogleSheetInBackground = new Thread(new Runnable() {
                @Override
                public void run() {
                    String cost = String.format("%.2f", subCategoryCost);

                    //add the subcategories and cost to the BudgetVision user google sheet if and only if there are no duplicate subcategories or errors detected.
                    if(!isError() && !isDuplicateSubcategory) {
                        try {
                            MainActivity.getInstance().addSubcategoriesToSheet(categoryName, subCategoryToAdd, cost);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            doGoogleSheetInBackground.start();
        }

        /*
        For example, Category is Food and sub-category could be Groceries.
        Another example, Category is Housing and sub-category could be Rent.
        Another example, Category is Lifestyle and sub-category could be Gym.
        etc for all Categories and sub-categories...
         */
        if (this.category.equalsIgnoreCase("Food") && !isDuplicateSubcategory && !isFatalError) {
            tempNames = this.food; //temp array stores all the current values of food names.
            tempCosts = this.foodCosts;  //temp array stores all the current values of food costs.

            /*
             * IMPORTANT: In the case where the food category names have different lengths from removing its subcategories.
             * load all the previous food category names and add the new subcategory to the food category.
             * Also, load all the previous food costs names and add the new subcategory cost to the food category.
             */
            if (this.NOF < MAX_NOF) {
                this.food = new String[MAX_NOF];
                for (int i = 0; i < this.NOF; i++) {
                    //load all of the food names.
                    this.food[i] = tempNames[i];
                }
                this.food[this.NOF] = this.subCategory;
            } else if (this.NOF >= MAX_NOF) {
                addError("Error: Maximum Number of Food Subcategories " + this.MAX_NOF + "Reached !\nClear Food Subcategories To Add Again"); // add the error message for exceeding the maximum number of food sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            if (this.NOF_Cost < MAX_NOF) {
                this.foodCosts = new double[MAX_NOF];
                for (int i = 0; i < this.NOF_Cost; i++) {
                    //load all of the food costs.
                    this.foodCosts[i] = tempCosts[i];
                }
                this.foodCosts[this.NOF_Cost] = this.subCategoryCost;
            } else if (this.NOF_Cost >= MAX_NOF) {
                addError("Error: Maximum Number of Food Subcategories " + this.MAX_NOF + "Reached !\nClear Food Subcategories To Add Again"); // add the error message for exceeding the maximum number of food sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            this.NOF++; //increment the total number of food subcategories.
            this.NOF_Cost++; //increment the total number of food subcategories costs.
        }

        else if (this.category.equalsIgnoreCase("Housing") && !isDuplicateSubcategory && !isFatalError) {
            tempNames = this.housing;  //temp array stores all the current values of housing names.
            tempCosts = this.housingCosts; //temp array stores all the current values of housing costs.
            /*
             * In the case where the housing category names have different lengths from removing its subcategories.
             * load all the previous housing category names and add the new subcategory to the housing category.
             */
            if (this.NOH < MAX_NOH) {
                this.housing = new String[MAX_NOH];
                for (int i = 0; i < this.NOH; i++) {
                    this.housing[i] = tempNames[i];
                }
                this.housing[this.NOH] = this.subCategory;
            } else if (this.NOH >= MAX_NOH) {
                addError("Error: Maximum Number of Housing Subcategories " + this.MAX_NOH + "Reached !\nClear Housing Subcategories To Add Again"); //add the error message for exceeding the maximum number of housing sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            if (this.NOH_Cost < MAX_NOF) {
                this.housingCosts = new double[MAX_NOH];
                for (int i = 0; i < this.NOH_Cost; i++) {
                    //load all of the food costs.
                    this.housingCosts[i] = tempCosts[i];
                }
                this.housingCosts[this.NOH_Cost] = this.subCategoryCost;
            } else if (this.NOH_Cost >= MAX_NOH) {
                addError("Error: Maximum Number of Housing Subcategories " + this.MAX_NOH + "Reached !\nClear Housing Subcategories To Add Again"); // add the error message for exceeding the maximum number of food sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            this.NOH++; //increment the total number of housing subcategories.
            this.NOH_Cost++; //increment the total number of housing subcategories costs.
        }
        else if (this.category.equalsIgnoreCase("Lifestyle") && !isDuplicateSubcategory && !isFatalError) {
            tempNames = this.lifestyle; //temp array stores all the current values of lifestyle names.
            tempCosts = this.lifestyleCosts;  //temp array stores all the current values of lifestyle costs.
            /*
             * In the case where the lifestyle category names have different lengths from removing its subcategories.
             * load all the previous lifestyle category names and add the new subcategory to the lifestyle category.
             */
            if (this.NOL < MAX_NOL) {
                this.lifestyle = new String[MAX_NOL];
                for (int i = 0; i < this.NOL; i++) {
                    this.lifestyle[i] = tempNames[i];
                }
                this.lifestyle[this.NOL] = this.subCategory;
            } else if (this.NOL >= MAX_NOL) {
                addError("Error: Maximum Number of Lifestyle Subcategories " + this.MAX_NOL + "Reached !\nClear Lifestyle Subcategories To Add Again"); //add the error message for exceeding the maximum number of lifestyle sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            if (this.NOL_Cost < MAX_NOF) {
                this.lifestyleCosts = new double[MAX_NOL];
                for (int i = 0; i < this.NOL_Cost; i++) {
                    //load all of the food costs.
                    this.lifestyleCosts[i] = tempCosts[i];
                }
                this.lifestyleCosts[this.NOL_Cost] = this.subCategoryCost;
            } else if (this.NOL_Cost >= MAX_NOL) {
                addError("Error: Maximum Number of Lifestyle Subcategories " + this.MAX_NOL + "Reached !\nClear Lifestyle Subcategories To Add Again"); // add the error message for exceeding the maximum number of food sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            this.NOL++; //increment the total number of lifestyle subcategories.
            this.NOL_Cost++; //increment the total number of lifestyle subcategories costs.
        }

        else if (this.category.equalsIgnoreCase("Commute") && !isDuplicateSubcategory && !isFatalError) {
            tempNames = this.commute; //temp array stores all the current values of commute names.
            tempCosts = this.commuteCosts;  //temp array stores all the current values of commute costs.
            /*
             * In the case where the commute category names have different lengths from removing its subcategories.
             * load all the previous commute category names and add the new subcategory to the commute category.
             */
            if (this.NOC < MAX_NOC) {
                this.commute = new String[MAX_NOC];
                for (int i = 0; i < this.NOC; i++) {
                    this.commute[i] = tempNames[i];
                }
                this.commute[this.NOC] = this.subCategory;
            } else if (this.NOC >= MAX_NOC) {
                addError("Error: Maximum Number of Commute Subcategories " + this.MAX_NOC + "Reached !\nClear Commute Subcategories To Add Again"); //add the error message for exceeding the maximum number of commute subcategories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            if (this.NOC_Cost < MAX_NOC) {
                this.commuteCosts = new double[MAX_NOC];
                for (int i = 0; i < this.NOC_Cost; i++) {
                    //load all of the food costs.
                    this.commuteCosts[i] = tempCosts[i];
                }
                this.commuteCosts[this.NOC_Cost] = this.subCategoryCost;
            } else if (this.NOC_Cost >= MAX_NOC) {
                addError("Error: Maximum Number of Commute Subcategories " + this.MAX_NOC + "Reached !\nClear Commute Subcategories To Add Again"); // add the error message for exceeding the maximum number of food sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            this.NOC++; //increment the total number of commute subcategories.
            this.NOC_Cost++; //increment the total number of commute subcategories costs.
        }

        else if (this.category.equalsIgnoreCase("Recreation") && !isDuplicateSubcategory && !isFatalError) {
            tempNames = this.recreation;  //temp array stores all the current values of recreation names.
            tempCosts = this.recreationCosts;  //temp array stores all the current values of recreation costs.
            /*
             * In the case where the recreation category names have different lengths from removing its subcategories.
             * load all the previous recreation category names and add the new subcategory to the recreation category.
             */
            if (this.NOR < MAX_NOR) {
                this.recreation = new String[MAX_NOR];
                for (int i = 0; i < this.NOR; i++) {
                    this.recreation[i] = tempNames[i];
                }
                this.recreation[this.NOR] = this.subCategory;
            } else if (this.NOR >= MAX_NOR) {
                addError("Error: Maximum Number of Recreation Subcategories " + this.MAX_NOR + "Reached !\nClear Recreation Subcategories To Add Again"); //add the error message for exceeding the maximum number of recreation subcategories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            if (this.NOR_Cost < MAX_NOR) {
                this.recreationCosts = new double[MAX_NOR];
                for (int i = 0; i < this.NOR_Cost; i++) {
                    //load all of the food costs.
                    this.recreationCosts[i] = tempCosts[i];
                }
                this.recreationCosts[this.NOR_Cost] = this.subCategoryCost;
            } else if (this.NOR_Cost >= MAX_NOR) {
                addError("Error: Maximum Number of Recreation Subcategories " + this.MAX_NOR + "Reached !\nClear Recreation Subcategories To Add Again"); // add the error message for exceeding the maximum number of food sub-categories.
                this.isFatalError = true; //fatal error for adding more then or equal to maximum number of subcategories in category.
            }
            this.NOR++; //increment the total number of recreation subcategories.
            this.NOR_Cost++; //increment the total number of recreation subcategories costs.
        }
        /*
         * If the subcategory is a duplicate add an error.
         */
        if (this.isDuplicateSubcategory) {
            addError("Error: " + this.subCategory + " Subcategory Already Created !");
        }
    }

    /**
     * method for adding another expense for the subcategory.
     * @param expenseToAdd
     * @return
     */
    public double addExpense(double expenseToAdd) {

        if (this.category.equalsIgnoreCase("Food")) {
            this.foodCosts[this.NOF - 1] += expenseToAdd;
            this.currentExpenses = this.foodCosts[this.NOF - 1];
        } else if (this.category.equalsIgnoreCase("Housing")) {
            this.housingCosts[this.NOH - 1] += expenseToAdd;
            this.currentExpenses = this.housingCosts[this.NOH - 1];
        } else if (this.category.equalsIgnoreCase("LifeStyle")) {
            this.lifestyleCosts[this.NOL - 1] += expenseToAdd;
            this.currentExpenses = this.lifestyleCosts[this.NOL - 1];
        } else if (this.category.equalsIgnoreCase("Commute")) {
            this.commuteCosts[this.NOC - 1] += expenseToAdd;
            this.currentExpenses = this.commuteCosts[this.NOC - 1];
        } else if (this.category.equalsIgnoreCase("Recreation")) {
            this.recreationCosts[this.NOR - 1] += expenseToAdd;
            this.currentExpenses = this.recreationCosts[this.NOR - 1];
        }

        //format as string object the current expenses to two decimal places.
        String result = String.format("%.2f", this.currentExpenses);

        //determine the double representation value of the current expenses.
        this.currentExpenses = Double.valueOf(result);

        return this.currentExpenses;
    }


    /**
     * overloaded method for adding expenses to a specific subCategory.
     * @param expenseToAdd
     * @param subCategoryName
     * @param categoryName
     * @return
     */
    public double addExpense(double expenseToAdd, String subCategoryName, String categoryName) {

        //reset the error before additional expenses are added.
        resetError();

        int index = getSubcategoryNameIndex(categoryName, subCategoryName);

        //if and only if the subcategory is found for the category add an expense otherwise add an error.
        if(index != -1) {
           if (categoryName.equalsIgnoreCase("Food")) {
               foodCosts[index] += expenseToAdd;
               this.currentExpenses += foodCosts[index];
           } else if (categoryName.equalsIgnoreCase("Housing")) {
               housingCosts[index] += expenseToAdd;
               this.currentExpenses += housingCosts[index];
           } else if (categoryName.equalsIgnoreCase("LifeStyle")) {
               lifestyleCosts[index] += expenseToAdd;
               this.currentExpenses += lifestyleCosts[index];
           } else if (categoryName.equalsIgnoreCase("Commute")) {
               commuteCosts[index] += expenseToAdd;
               this.currentExpenses += commuteCosts[index];
           } else if (categoryName.equalsIgnoreCase("Recreation")) {
               recreationCosts[index] += expenseToAdd;
               this.currentExpenses += recreationCosts[index];
           }
           //format as string object the current expenses to two decimal places.
           String result = String.format("%.2f", this.currentExpenses);

           //determine the double representation value of the current expenses.
           this.currentExpenses = Double.valueOf(result);
        }
        //add an error message if the user trys to add an expense to a non-existing subcategory for a specific category.
        else if (index == -1){
            addError("Error: Subcategory " + subCategoryName + " Does Not Exist For Category " + categoryName );
        }
        return this.currentExpenses;
    }


    /**
     * @param categoryName
     * @param subcategory
     * method for removing both the subcategory and removing the expense associated with the subcategory
     * To remove the subcategory the subcategory name and the subcategory expense have to be removed.
     * IMPORTANT NOTE: Expense is to be removed first and after subcategory name is removed.
     * Because if the subcategory name is first removed then the expense associated will not be found.
     */
    public void removeSubcategory(String categoryName, String subcategory) {
        //reset the error before subcategory is removed.
        resetError();

        removeSubcategoryExpense(categoryName, subcategory); //remove the subcategory expense first.
        removeSubcategoryName(categoryName, subcategory); //remove the subcategory name second.

    }

    /**
     * method for getting status.
     * The status will return all the current subcategories for each category.
     * MONOSPACE FONT must be used to display the status correctly in its textview within the app.
     * the returned status has subcategories formatted evenly with 21 spaces. Since, the length of each subcategory is capped at 20 spaces. Appending a dollar sign is also another character.
     * @return
     */
    public String getStatus() {
        //get the currency symbol from the user object in Main Activity.
        this.currencySymbol = MainActivity.getInstance().getUser().getCurrencySymbol();

        String status = "";
        status = "Food:";
        String[] allFood = getFood();
        String[] allFoodCosts = getFoodCosts();
        for (int i = 0; i < this.NOF ; i++) {
            status+="\n";
            status +=  String.format("%-21s" , allFood[i]) +  this.currencySymbol + allFoodCosts[i];
            System.out.println(status);

            if (i < this.NOF - 1) {
                status += ",";
            }
        }

        status += "\n\n\n";
        status +=  "Housing:";
        String[] allHousing = getHousing();
        String[] allHousingCosts = getHousingCosts();
        for (int i = 0; i < this.NOH; i++) {
            status+="\n";
            status +=  String.format("%-21s" , allHousing[i]) +  this.currencySymbol + allHousingCosts[i];

            if (i < this.NOH - 1) {
                status += ",";
            }
        }

        status += "\n\n\n";
        status +=  "Lifestyle:";
        String[] allLifestyle = getLifestyle();
        String[] allLifestyleCosts = getLifestyleCosts();
        for (int i = 0; i < this.NOL; i++) {
            status+="\n";
            status +=  String.format("%-21s" , allLifestyle[i]) +  this.currencySymbol + allLifestyleCosts[i];

            if (i < this.NOL - 1) {
                status += ",";
            }
        }

        status += "\n\n\n";
        status += "Commute:";
        String[] allCommute = getCommute();
        String[] allCommuteCosts = getCommuteCosts();
        for (int i = 0; i < this.NOC; i++) {
            status+="\n";
            status +=  String.format("%-21s" , allCommute[i]) +  this.currencySymbol + allCommuteCosts[i];

            if (i < this.NOC - 1) {
                status += ",";
            }
        }

        status += "\n\n\n";
        status +=  "Recreation:";
        String[] allRecreation = getRecreation();
        String[] allRecreationCosts = getRecreationCosts();
        for (int i = 0; i < this.NOR; i++) {
            status+="\n";
            status +=  String.format("%-21s" , allRecreation[i]) + this.currencySymbol + allRecreationCosts[i];

            if (i < this.NOR - 1) {
                status += ",";
            }
        }

        return status;
    }


    /**
     * method for returning the food subcategory names.
     * @return result
     */
    public String[] getFood() {
        String[] result = getSubcategoryNames("Food");

        return result;
    }


    /**
     * method for returning the String representation of food subcategory costs.
     * @return result
     */
    public String[] getFoodCosts() {
        String[] result = getSubcategoryCosts("Food");

        return result;
    }


    /**
     * method for returning the number value representation of food subcategory costs.
     * @return result
     */
    public double[] getFoodCostsNumber(){
        double[] result = getSubcategoryCostsNumber("Food");
        return result;
    }


    /**
     * method for returning the housing subcategory names.
     * @return result
     */
    public String[] getHousing() {
        String[] result = getSubcategoryNames("Housing");

        return result;
    }


    /**
     * method for returning the String representation of housing subcategory costs.
     * @return result
     */
    public String[] getHousingCosts() {
        String[] result = getSubcategoryCosts("Housing");

        return result;
    }


    /**
     * method for returning the number value representation of housing subcategory costs.
     * @return result
     */
    public double[] getHousingCostsNumber(){
        double[] result = getSubcategoryCostsNumber("Housing");
        return result;
    }


    /**
     * method for returning the lifestyle subcategory names.
     * @return result
     */
    public String[] getLifestyle() {
        String[] result = getSubcategoryNames("Lifestyle");

        return result;
    }


    /**
     * method for returning the String representation of lifestyle subcategory costs.
     * @return result
     */
    public String[] getLifestyleCosts() {

        String[] result = getSubcategoryCosts("Lifestyle");

        return result;
    }


    /**
     * method for returning the number value representation of lifestyle subcategory costs.
     * @return result
     */
    public double[] getLifestyleCostsNumber(){
        double[] result = getSubcategoryCostsNumber("Lifestyle");
        return result;
    }


    /**
     * method for returning the commute subcategory names in array.
     * @return result
     */
    public String[] getCommute() {
        String[] result = getSubcategoryNames("Commute");

        return result;
    }


    /**
     * method for returning the String representation of commute subcategory costs.
     * @return result
     */
    public String[] getCommuteCosts() {
        String[] result = getSubcategoryCosts("Commute");

        return result;
    }


    /**
     * method for returning the number value representation of commute subcategory costs.
     * @return result
     */
    public double[] getCommuteCostsNumber(){
        double[] result = getSubcategoryCostsNumber("Commute");
        return result;
    }


    /**
     * method for returning the recreation subcategory names in array.
     * @return result
     */
    public String[] getRecreation() {
        String[] result = getSubcategoryNames("Recreation");

        return result;
    }


    /**
     * method for returning the String representation of recreation subcategory costs.
     * @return result
     */
    public String[] getRecreationCosts() {
        String[] result  = getSubcategoryCosts("Recreation");

        return result;
    }


    /**
     * method for returning the number value representation of recreation subcategory costs.
     * @return result
     */
    public double[] getRecreationCostsNumber(){
        double[] result = getSubcategoryCostsNumber("Recreation");
        return result;
    }

    //method for a recurring transaction.
    public void recurringTransaction() {
        //TODO : This method is for creating a recurringTransaction expense.

    }


    /**
     *
     *
    Helper methods.
     *
     *
     */

    /**
     * helper method for finding the index of a subcategory.
     * @param categoryName
     * @param subcategoryName
     * @return
     */
    public int getSubcategoryNameIndex(String categoryName, String subcategoryName) {
        int index = -1; //index is initially -1; if index is -1 then the index is not found for the subcategory name.

        if (categoryName.equalsIgnoreCase("Food")) {
            for (int i = 0; i < this.NOF; i++) {
                if (this.food[i].equalsIgnoreCase(subcategoryName)) {
                    index = i;
                }
            }
        } else if (categoryName.equalsIgnoreCase("Housing")) {
            for (int i = 0; i < this.NOH; i++) {
                if (this.housing[i].equalsIgnoreCase(subcategoryName)) {
                    index = i;
                }
            }
        } else if (categoryName.equalsIgnoreCase("Lifestyle")) {
            for (int i = 0; i < this.NOL; i++) {
                if (this.housing[i].equalsIgnoreCase(subcategoryName)) {
                    index = i;
                }
            }
        } else if (categoryName.equalsIgnoreCase("Commute")) {
            for (int i = 0; i < this.NOC; i++) {
                if (this.commute[i].equalsIgnoreCase(subcategoryName)) {
                    index = i;
                }
            }
        } else if (categoryName.equalsIgnoreCase("Recreation")) {
            for (int i = 0; i < this.NOR; i++) {
                if (this.recreation[i].equalsIgnoreCase(subcategoryName)) {
                    index = i;
                }
            }
        }
        return index;
    }


    /**
     * helper method for returning an array of subcategory name values without null elements.
     * @param categoryName
     * @return
     */
    public String[] getSubcategoryNames(String categoryName) {
        String[] temp = null;

        //temporary count variable.
        int count = 0;

        if (categoryName.equalsIgnoreCase("Food")) {
            temp = new String[this.NOF];
            /*
             * the subcategory array may contain null values if this.NOF != MAX_NOF.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOF; i++) {
                temp[count] = this.food[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Housing")) {
            temp = new String[this.NOH];
            /*
             * the subcategory array may contain null values if this.NOH != MAX_NOH.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOH; i++) {
                temp[count] = this.housing[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Commute")) {
            temp = new String[this.NOC];
            /*
             * the subcategory array may contain null values if this.NOC != MAX_NOC.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOC; i++) {
                temp[count] = this.commute[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Recreation")) {
            temp = new String[this.NOR];
            /*
             * the subcategory array may contain null values if this.NOR != MAX_NOR.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOR; i++) {
                temp[count] = this.recreation[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Lifestyle")) {
            temp = new String[this.NOL];
            /*
             * the subcategory array may contain null values if this.NOL != MAX_NOL.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOL; i++) {
                temp[count] = this.lifestyle[i];
                count++; //increment the counter.
            }
        }

        //new result array the size of the count.
        String[] result = new String[count]; //the array to return containing no null-values.
        for (int i = 0; i < count; i++) {
            result[i] = temp[i];
        }

        return result;
    }


    /**
     * helper method for returning an array of subcategory costs values as a String representation.
     * @param categoryName
     * @return
     */
    public String[] getSubcategoryCosts(String categoryName) {
        double[] temp = null; //temporary array to store sub-category cost values.
        String tempNum = ""; //temporary number to be stored as a String object.
        //temporary count variable
        int count = 0;

        if (categoryName.equalsIgnoreCase("Food")) {
            temp = new double[this.NOF_Cost];
            /*
             * the sub-category array may contain null values if this.NOF_Cost != MAX_NOF.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOF_Cost; i++) {
                temp[count] = this.foodCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Housing")) {
            temp = new double[this.NOH_Cost];
            /*
             * the sub-category array may contain null values if this.NOH_Cost != MAX_NOH.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOH_Cost; i++) {
                temp[count] = this.housingCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Commute")) {
            temp = new double[this.NOC_Cost];
            /*
             * the sub-category array may contain null values if this.NOC_Cost != MAX_NOC.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOC_Cost; i++) {
                temp[count] = this.commuteCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Lifestyle")) {
            temp = new double[this.NOL_Cost];
            /*
             * the sub-category array may contain null values if this.NOL_Cost != MAX_NOL.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOL_Cost; i++) {
                temp[count] = this.lifestyleCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Recreation")) {
            temp = new double[this.NOR_Cost];
            /*
             * the sub-category array may contain null values if this.NOR_Cost != MAX_NOR.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOR_Cost; i++) {
                temp[count] = this.recreationCosts[i];
                count++; //increment the counter.
            }
        }
        //new result array the size of the count.
        String[] result = new String[count]; //the array to return containing no null-values.
        for (int i = 0; i < count; i++) {
            tempNum = String.format("%.2f", temp[i]);
            result[i] = tempNum;
        }

        return result;
    }

    /**
     * helper method for returning an array of subcategory costs values as a double representation.
     * @param categoryName
     * @return
     */
    public double[] getSubcategoryCostsNumber(String categoryName) {
        double[] temp = null; //temporary array to store sub-category cost values.
        double tempNum = 0; //temporary number to be stored as a double data type.
        //temporary count variable
        int count = 0;

        if (categoryName.equalsIgnoreCase("Food")) {
            temp = new double[this.NOF_Cost];
            /*
             * the sub-category array may contain null values if this.NOF_Cost != MAX_NOF.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOF_Cost; i++) {
                temp[count] = this.foodCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Housing")) {
            temp = new double[this.NOH_Cost];
            /*
             * the sub-category array may contain null values if this.NOH_Cost != MAX_NOH.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOH_Cost; i++) {
                temp[count] = this.housingCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Recreation")) {
            temp = new double[this.NOR_Cost];
            /*
             * the sub-category array may contain null values if this.NOR_Cost != MAX_NOR.
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOR_Cost; i++) {
                temp[count] = this.recreationCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Lifestyle")) {
            temp = new double[this.NOL_Cost];
            /*
             * the sub-category array may contain null values if this.NOL_Cost != MAX_NOF
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOL_Cost; i++) {
                temp[count] = this.lifestyleCosts[i];
                count++; //increment the counter.
            }
        } else if (categoryName.equalsIgnoreCase("Commute")) {
            temp = new double[this.NOC_Cost];
            /*
             * the sub-category array may contain null values if this.NOC_Cost != MAX_NOC
             * so, another counter is used to index non-null values.
             */
            for (int i = 0; i < this.NOC_Cost; i++) {
                temp[count] = this.commuteCosts[i];
                count++; //increment the counter.
            }
        }
        //new result array the size of the count.
        double[] result = new double[count]; //the array to return containing no null-values.
        for (int i = 0; i < count; i++) {
            //round each expense item to two decimal places.
            tempNum = Math.round(temp[i]*100.0)/100.0;
            result[i] = tempNum;
        }

        return result;
    }


    /**
     * helper method for removing a subcategory name.
     * @param categoryName
     * @param subcategory
     */
    public void removeSubcategoryName(String categoryName, String subcategory) {
        int index = getSubcategoryNameIndex(categoryName, subcategory); //index where the subcategory is found.

        String[] temp = null; // this temp array will store the subcategories after the desired subcategory is removed.
        String[] allNames = null; //this allNames array will store all the subcategory names for a specific category.
        int count = 0;

        if (categoryName.equalsIgnoreCase("Food") && index != -1) {
            temp = new String[this.NOF - 1]; //new result array with minus 1 length of number of subcategory .
            allNames = getFood(); //get all the food names.
            for (int i = 0; i < this.NOF; i++) {
                if (!(allNames[i].equalsIgnoreCase(allNames[index]))) {
                    temp[count] = allNames[i];
                    count++;
                }
            }
            this.NOF--; //decrement this.NOF counter by 1 since a food subcategory was removed.
            this.food = temp; //now the food array is the temp array.

        } else if (categoryName.equalsIgnoreCase("Housing") && index != -1) {
            temp = new String[this.NOH - 1]; //new result array with minus 1 length of number of subcategory .
            allNames = getHousing(); //get all the housing names.
            for (int i = 0; i < this.NOH; i++) {
                if (!(allNames[i].equalsIgnoreCase(allNames[index]))) {
                    temp[count] = allNames[i];
                    count++;
                }
            }
            this.NOH--; //decrement this.NOH counter by 1 since a housing subcategory was removed.
            this.housing = temp; //now the housing array is the temp array.
        } else if (categoryName.equalsIgnoreCase("Lifestyle") && index != -1) {
            temp = new String[this.NOL - 1]; //new result array with minus 1 length of number of subcategory .
            allNames = getLifestyle(); //get all the lifestyle names.
            for (int i = 0; i < this.NOL; i++) {
                if (!(allNames[i].equalsIgnoreCase(allNames[index]))) {
                    temp[count] = allNames[i];
                    count++;
                }
            }
            this.NOL--; //decrement this.NOL counter by 1 since a lifestyle subcategory was removed.
            this.lifestyle = temp; //now the lifestyle array is the temp array.
        } else if (categoryName.equalsIgnoreCase("Commute") && index != -1) {
            temp = new String[this.NOC - 1]; //new result array with minus 1 length of number of subcategory .
            allNames = getCommute(); //get all Commute names.
            for (int i = 0; i < this.NOC; i++) {
                if (!(allNames[i]).equalsIgnoreCase(allNames[index])) {
                    temp[count] = allNames[i];
                    count++;
                }
            }
            this.NOC--; //decrement this.NOC counter by 1 since a Commute subcategory was removed.
            this.commute = temp; //now the Commute array is the temp array.
        } else if (categoryName.equalsIgnoreCase("recreation") && index != -1) {
            temp = new String[this.NOR - 1]; //new result array with minus 1 length of number of subcategory .
            allNames = getRecreation(); //get all recreation names.
            for (int i = 0; i < this.NOR; i++) {
                if (!(allNames[i].equalsIgnoreCase(allNames[index]))) {
                    temp[count] = allNames[i];
                    count++;
                }
            }
            this.NOR--; //decrement this.NOR counter by 1 since an recreation subcategory was removed.
            this.recreation = temp; //now the recreation array is the temp array.
        }
        //Add the error if the subcategory was never found.
        else if (index == -1) {
            addError("Error: Subcategory " + subcategory + " Does Not Exist For Category " + categoryName );
        }

    }


    /**
     * helper method for removing an expense for a specific subcategories in a Category.
     * The purpose of this is to remove an expense to add a different expense for a specific subcategories in a Category.
     * @param categoryName
     * @param subcategory
     */
    public void removeSubcategoryExpense(String categoryName, String subcategory) {
        double[] temp = null;
        double[] allCosts = null;
        int count = 0;
        int index = getSubcategoryNameIndex(categoryName, subcategory);  //index of where the subcategory is found in its category.

        if (categoryName.equalsIgnoreCase("Food") && index != -1) {
            allCosts = this.foodCosts;
            temp = new double[this.NOF_Cost - 1]; // the temp size is one less since a subcategory for food is being removed.

            for (int i = 0; i < this.NOF_Cost; i++) {
                if (i != index) {
                    temp[count] = allCosts[i];
                    count++;
                }
            }
            this.foodCosts = temp;
            this.NOF_Cost--;
        } else if (categoryName.equalsIgnoreCase("Lifestyle") && index != -1) {
            allCosts = this.lifestyleCosts;
            temp = new double[this.NOL_Cost - 1];  // the temp size is one less since a subcategory for lifestyle is being removed.

            for (int i = 0; i < this.NOL_Cost; i++) {
                if (i != index) {
                    temp[count] = allCosts[i];
                    count++;
                }
            }
            this.lifestyleCosts = temp;
            this.NOL_Cost--;
        } else if (categoryName.equalsIgnoreCase("Recreation") && index != -1) {
            allCosts = this.recreationCosts;

            temp = new double[this.NOR_Cost - 1];  // the temp size is one less since a subcategory for recreation is being removed.

            for (int i = 0; i < NOR_Cost; i++) {
                if (i != index) {
                    temp[count] = allCosts[i];
                    count++;
                }
            }
            this.recreationCosts = temp;
            this.NOR_Cost--;
        } else if (categoryName.equalsIgnoreCase("Commute") && index != -1) {
            allCosts = this.commuteCosts;

            temp = new double[this.NOC_Cost - 1];  // the temp size is one less since a subcategory for commute is being removed.

            for (int i = 0; i < this.NOC_Cost; i++) {
                if (i != index) {
                    temp[count] = allCosts[i];
                    count++;
                }
            }
            this.commuteCosts = temp;
            this.NOC_Cost--;
        } else if (categoryName.equalsIgnoreCase("Housing") && index != -1) {
            allCosts = this.housingCosts;

            temp = new double[this.NOH_Cost - 1]; // the temp size is one less since a subcategory for housing is being removed.

            for (int i = 0; i < NOH_Cost; i++) {
                if (i != index) {
                    temp[count] = allCosts[i];
                    count++;
                }
            }
            this.housingCosts = temp;
            this.NOH_Cost--;
        }
        //Add the error if the subcategory was never found.
        else if (index == -1) {
            addError("Error: Subcategory " + subcategory + " Does Not Exist For Category " + categoryName );
        }

    }


    /**
     * Helper method for determining if an already existing (duplicate) subcategories name was tried to be added.
     * @param categoryName
     * @param subcategory
     * @return isDuplicate
     */
    public boolean isDuplicate(String categoryName, String subcategory) {
        boolean isDuplicate = false;
        if (categoryName.equalsIgnoreCase("Food")) {
            String[] allFood = getFood();
            for (int i = 0; i < this.NOF; i++) {
                isDuplicate = isDuplicate || (allFood[i].equalsIgnoreCase(subcategory));
            }
        } else if (categoryName.equalsIgnoreCase("Housing")) {
            String[] allHousing = getHousing();
            for (int i = 0; i < this.NOH; i++) {
                isDuplicate = isDuplicate || (allHousing[i].equalsIgnoreCase(subcategory));
            }
        } else if (categoryName.equalsIgnoreCase("Commute")) {
            String[] allCommute = getCommute();
            for (int i = 0; i < this.NOC; i++) {
                isDuplicate = isDuplicate || (allCommute[i].equalsIgnoreCase(subcategory));
            }
        } else if (categoryName.equalsIgnoreCase("Lifestyle")) {
            String[] allLifestyle = getLifestyle();
            for (int i = 0; i < this.NOL; i++) {
                isDuplicate = isDuplicate || (allLifestyle[i].equalsIgnoreCase(subcategory));
            }
        } else if (categoryName.equalsIgnoreCase("Recreation")) {
            String[] allRecreation = getRecreation();
            for (int i = 0; i < this.NOR; i++) {
                isDuplicate = isDuplicate || (allRecreation[i].equalsIgnoreCase(subcategory));
            }
        }
        return isDuplicate;
    }


    /**
     * helper method to add error.
     * @param errorMessage
     */
    public void addError(String errorMessage) {
        this.isError = true; //there is now a valid error.
        this.errorMSG = errorMessage; //there is now an error message.
    }


    /**
     * helper method to reset error.
     */
    public void resetError() {
        this.isError = false; // no error.
        this.errorMSG = "";  // no error message.

    }


    /**
     * helper method used in Class UsersBudget. Return the current subcategory name.
     * @return this.subCategory
     */
    public String getCurrentSubcategoryName(){
        return this.subCategory;

    }


    /**
     * /helper method used in Class UsersBudget. Return the current subcategory cost as Double data type.
     * @return this.subCategoryCost
     */
    public double getCurrentSubcategoryCostsNumber(){
        return this.subCategoryCost;

    }


    /**
     * helper method used in Class UsersBudget. Return if there is currently an error.
     * @return this.isError
     */
    public boolean isError(){
        return this.isError;

    }


    /**
     * helper method used in Class UsersBudget. Returns the error message.
     * @return this.errorMSG
     */
    public String getErrorMSG(){
        return this.errorMSG;

    }

    /**
     * Helper method used in Class UsersBudget. Sets all sorted subcategory names. The input parameter is the sorted subcategory names.
     * @param subcategoryNames
     * @param categoryName
     */
    public void setSubcategoryNames(String[] subcategoryNames, String categoryName){
        if(categoryName.equalsIgnoreCase("Food")){
            this.food = subcategoryNames;
        }
        else if(categoryName.equalsIgnoreCase("Housing")){
            this.housing = subcategoryNames;
        }
        else if(categoryName.equalsIgnoreCase("Commute")){
            this.commute = subcategoryNames;
        }
        else if(categoryName.equalsIgnoreCase("Recreation")){
            this.recreation = subcategoryNames;
        }
        else if(categoryName.equalsIgnoreCase("Lifestyle")){
            this.lifestyle = subcategoryNames;
        }
    }

    /**
     * Helper method used in Class UsersBudget. Sets all sorted subcategory costs. The input parameter is the sorted subcategory costs.
     * @param subcategoryCosts
     * @param categoryName
     */
    public void setSubcategoryCosts(double[] subcategoryCosts, String categoryName){
        if(categoryName.equalsIgnoreCase("Food")){
            this.foodCosts = subcategoryCosts;
        }
        else if(categoryName.equalsIgnoreCase("Housing")){
            this.housingCosts = subcategoryCosts;
        }
        else if(categoryName.equalsIgnoreCase("Commute")){
            this.commuteCosts = subcategoryCosts;
        }
        else if(categoryName.equalsIgnoreCase("Recreation")){
            this.recreationCosts = subcategoryCosts;
        }
        else if(categoryName.equalsIgnoreCase("Lifestyle")){
            this.lifestyleCosts = subcategoryCosts;
        }
    }

    /**
     * Helper method used in Class UsersBudget to get number of food subcategories.
     */
    public int getNOF(){
        return this.NOF;
    }

    /**
     * Helper method used in Class UsersBudget to get number of housing subcategories.
     */
    public int getNOH(){
        return this.NOH;
    }

    /**
     * Helper method used in Class UsersBudget to get number of lifestyle subcategories.
     */
    public int getNOL(){
        return this.NOL;
    }

    /**
     * Helper method used in Class UsersBudget to get number of commute subcategories.
     */
    public int getNOC(){
        return this.NOC;
    }

    /**
     * Helper methods used in Class UsersBudget to get number of recreation subcategories.
     */
    public int getNOR(){
        return this.NOR;
    }


    /**
     * Helper method used in Class UsersBudget to get maximum number of food subcategories.
     */
    public int getMAX_NOF(){
        return this.MAX_NOF;
    }

    /**
     * Helper method used in Class UsersBudget to get maximum number of housing subcategories.
     */
    public int getMAX_NOH(){
        return this.MAX_NOH;
    }

    /**
     * Helper method used in Class UsersBudget to get maximum number of lifestyle subcategories.
     */
    public int getMAX_NOL(){
        return this.MAX_NOL;
    }

    /**
     * Helper method used in Class UsersBudget to get maximum number of commute subcategories.
     */
    public int getMAX_NOC(){
        return this.MAX_NOC;
    }

    /**
     * Helper methods used in Class UsersBudget to get maximum number of recreation subcategories.
     */
    public int getMAX_NOR(){
        return this.MAX_NOR;
    }

    /**
     * Helper method for returning the current category name.
     * @return
     */
    public String getCategory(){
        return this.category;
    }

    /**
     * This method sets the category from the given category parameter.
     * @param category
     */
    public void setCategory(String category){
        this.category = category;
    }

}