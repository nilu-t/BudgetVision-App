package CodingProject.budgetvision.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.UserBudgetComponent;
import CodingProject.budgetvision.controller.UsersBudgetClass;

public class ViewSubcategoryPopup extends Activity {
    private UsersBudgetClass mainUser; //main user object.
    private UsersBudgetClass user; //user object for a specific added user from the money fragment.

    private String category; //stores the category name.
    private String userName; //stores the user name of the added user.

    /**
     * The OnCreate method used in the ViewSubcategoryPopup class.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_subcategory);

        Intent i = getIntent();
        category = i.getStringExtra("categoryName_extra");
        userName = i.getStringExtra("userName_extra");

        //main user object.
        UserBudgetComponent userComponent = ((UsersBudgetClass) getApplication()).getAppComponent();
        this.mainUser = userComponent.getMyMainUser();

        //checking to see if the userName is not null and does not equal #Default, then the userObject associated with the userName is retrieved.
        if(userName != null && ! userName.equalsIgnoreCase("#Default")) {
            this.user = this.mainUser.getUserObjectOfName(userName);
        }
        else{
            this.user = this.mainUser; //the current user object is the main user since there is no new user added or retrieved.
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 80% of the screen width and height.
        getWindow().setLayout((int) (width *.8),(int) (height*.8));

        if(this.category.equalsIgnoreCase("Food")) {
            setContentsOfTextView(R.id.categoryAddLabel,"Food");

            this.user.addUserCategory(this.category); //add the user food category.

            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSubcategory);
            //String array adapter for the food subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.foodSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(this.category.equalsIgnoreCase("Housing")) {
            setContentsOfTextView(R.id.categoryAddLabel,"Housing");

            this.user.addUserCategory(this.category); //add the user housing category.

            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSubcategory);
            //String array adapter for the housing subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.housingSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(this.category.equalsIgnoreCase("Commute")) {
            setContentsOfTextView(R.id.categoryAddLabel,"Commute");

            this.user.addUserCategory(this.category); //add the user commute category.

            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSubcategory);
            //String array adapter for the commute subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.commuteSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(this.category.equalsIgnoreCase("Recreation")) {
            setContentsOfTextView(R.id.categoryAddLabel,"Recreation");

            this.user.addUserCategory(this.category); //add the user recreation category.

            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSubcategory);
            //String array adapter for the recreation subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.recreationSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(this.category.equalsIgnoreCase("Lifestyle")) {
            setContentsOfTextView(R.id.categoryAddLabel,"Lifestyle");

            this.user.addUserCategory(this.category); //add the user lifestyle category.

            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSubcategory);
            //String array adapter for the lifestyle subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.lifestyleSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }

        //update the number of subcategories for the current category.
        updateNumSubcategories(this.category);
    }


    /**
     * Executed via OnClick of "ADD SUBCATEGORY" button.
     * method for updating the total income on "ViewSubcategoryPopup" user interface.
     */
    public void updateTotalIncome(){
        //update the total income using the user callback in updateTotalIncomeFromActivity method.
        this.mainUser.updateTotalIncomeFromActivity();
    }


    /**
     * Executed via OnClick of "ADD SUBCATEGORY" button.
     * method for updating the daily budget on "ViewSubcategoryPopup" user interface.
     */
    public void updateDailyBudget(){
        //update the daily budget using the user callback in updateDailyBudgetFromActivity method.
        this.mainUser.updateDailyBudgetFromActivity();
    }


    /**
     * controller method for computing the immediate status update. Executed via OnClick().
     * @param view
     */
    public void immediateStatus(View view){
        //get the subcategory input from the user.
        AutoCompleteTextView subcategoryChoice = (AutoCompleteTextView) findViewById(R.id.autoSubcategory);
        String subcategory = subcategoryChoice.getText().toString();

        //get the expense input from the user.
        EditText expenseChoice = (EditText) findViewById(R.id.monthlyExpenseText);
        String expense = expenseChoice.getText().toString();

        try {
            if( !subcategory.trim().equalsIgnoreCase("") && !expense.trim().equalsIgnoreCase( "")){
                this.user.addUserSubcategory(subcategory, Double.parseDouble(expense)); //add the user subcategory and its expense associated.

                //update the number of subcategories for the current category. Since a valid subcategory was added
                updateNumSubcategories(this.category);

                String confirmation = this.user.userImmediateStatus();
                setContentsOfTextView(R.id.ConfirmationText, confirmation);

                //update the total income now.
                updateTotalIncome();

                //update the daily budget now.
                updateDailyBudget();
            }
            else if (subcategory.trim().equalsIgnoreCase("") || expense.trim().equalsIgnoreCase("")){
                String confirmation = "Error: No inputs For Either Subcategory Or Monthly Expense.";
                setContentsOfTextView(R.id.ConfirmationText, confirmation);
            }
        }
        catch(NumberFormatException e){
            /*
             * the user entered multiple points instead of one optional decimal point.
             * set the multiple points error text.
             */
            String confirmation = "Error: Multiple decimal points. Invalid Monthly Expense.";
            setContentsOfTextView(R.id.ConfirmationText, confirmation);
        }

    }


    //controller method for closing this activity when the "close" button is clicked.
    public void closeActivity(View view){
        this.finish();
    }


    /* this mutator sets the output label */
    private void setContentsOfTextView(int id, String newContents) {
        View view = findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }


    /**
     * Updates the number of subcategories left for a specific category after adding an item.
     * @param categoryName
     */
    public void updateNumSubcategories(String categoryName){
        int NOS = this.user.getRemainingNOS(categoryName); //store the number of subcategories for the current category.
        TextView maxNOS = (TextView) findViewById(R.id.maxSubcategoriesLabel);

        String updateNumSubcategories;

        if(categoryName.equalsIgnoreCase("Food")){
            updateNumSubcategories = "Maximum Food Subcategories Left : " + NOS;
            maxNOS.setText(updateNumSubcategories);
        }
        else if (categoryName.equalsIgnoreCase("Housing")){
            updateNumSubcategories = "Maximum Housing Subcategories Left : " + NOS;
            maxNOS.setText(updateNumSubcategories);
        }
        else if (categoryName.equalsIgnoreCase("Lifestyle")){
            updateNumSubcategories = "Maximum Lifestyle Subcategories Left : " + NOS;
            maxNOS.setText(updateNumSubcategories);
        }
        else if (categoryName.equalsIgnoreCase("Commute")){
            updateNumSubcategories = "Maximum Commute Subcategories Left : " + NOS;
            maxNOS.setText(updateNumSubcategories);
        }
        else if (categoryName.equalsIgnoreCase("Recreation")) {
            updateNumSubcategories = "Maximum Recreation Subcategories Left : " + NOS;
            maxNOS.setText(updateNumSubcategories);
        }


    }


}
