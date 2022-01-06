package CodingProject.budgetvision.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.UserBudgetComponent;
import CodingProject.budgetvision.controller.UsersBudgetClass;

public class AdditionalExpensePopup extends Activity {

    UsersBudgetClass user;

    private String categorySelected;
    private String subcategoryChosen;
    private String userName;

    /**
     * The OnCreate method of this AdditionalExpensePopup activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_add);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 80% of the screen width and height.
        getWindow().setLayout((int) (width *.8),(int) (height*.8));

        //get the user name from the intent.
        Intent i = getIntent();
        userName = i.getStringExtra("userName_extra");

        //main user object
        UserBudgetComponent userComponent = ((UsersBudgetClass)getApplication()).getAppComponent();
        this.user = userComponent.getMyMainUser();

        /*
         * if the user object is not the main user object, then retrieve the other user object.
         * Before the userName is checked to see if it does not equal "#Default" it is first checked if the userName is not null.
         */
        if(this.userName != null && ! this.userName.equalsIgnoreCase("#Default")){
            this.user = this.user.getUserObjectOfName(this.userName);
        }

        Spinner categoriesSpinner = (Spinner) findViewById(R.id.categoryOptions2);

        /*
         * Setting an item listener inside OnCreate for the categories spinner for getting which category is selected.
         * The category selected is used to determine the subcategories hints for the user.
         */
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                //get the selected item from the category options.
                String categorySelected = categoriesSpinner.getSelectedItem().toString();

                // a toast to show which category was selected
                Toast.makeText(getApplicationContext(), categorySelected + " selected" ,Toast.LENGTH_SHORT).show();

                //call the createAutoSuggestions method to create the subcategory hints for the category selected.
                createAutoSuggestions(categorySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //when nothing is selected set the item selected to "-choose category-".
                categoriesSpinner.setSelection(0);

            }
        });

    }


    /**
     * method for creating the hints for the auto complete text view.
     * @param categorySelected
     */
    public void createAutoSuggestions(String categorySelected){
        /*
         * create the auto suggestions for the auto text view based on the category selected.
         */
        if(categorySelected.equalsIgnoreCase("Food")) {
            this.categorySelected = "Food"; //the category selected is food.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions);
            //String array adapter for the food subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.foodSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Housing")) {
            this.categorySelected = "Housing"; //the category selected is housing;.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions);
            //String array adapter for the housing subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.housingSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Commute")) {
            this.categorySelected = "Commute"; //the category selected is commute.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions);
            //String array adapter for the commute subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.commuteSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Recreation")) {
            this.categorySelected = "Recreation"; //the category selected is recreation.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions);
            //String array adapter for the recreation subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.recreationSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Lifestyle")) {
            this.categorySelected = "Lifestyle"; //the category selected is lifestyle.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions);
            //String array adapter for the lifestyle subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.lifestyleSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
    }


    /**
     * controller method for adding an expense to an existing category and subcategory.
     * this method is called OnClick() when the add expense button is clicked.
     * @param view
     */
    public void addExpense(View view)  {
        //get which subcategory is chosen from the user.
        AutoCompleteTextView subcategoryChoice = (AutoCompleteTextView) findViewById(R.id.autoSuggestions);
        this.subcategoryChosen = subcategoryChoice.getText().toString();

        //get the amount the user wants to add.
        EditText expenseChoice = (EditText) findViewById(R.id.expToIncrTxt);
        String expense = expenseChoice.getText().toString();

        //add the user expense.
        try {
            if( !this.subcategoryChosen.trim().equalsIgnoreCase("") && !expense.trim().equalsIgnoreCase( "")){
                user.addUserAdditionalExpense(this.categorySelected, this.subcategoryChosen, Double.parseDouble(expense));
                immediateStatus();

            }
            else if (this.subcategoryChosen.trim().equalsIgnoreCase("") || expense.trim().equalsIgnoreCase("")){
                String confirmation = "Error: No inputs For Either Subcategory Or Additional Expense Amount.";
                Toast.makeText(this,confirmation,Toast.LENGTH_SHORT).show();
            }
        }
        catch(NumberFormatException e){
            /*
             * the user entered multiple points instead of one optional decimal point.
             * set the multiple points error text.
             */
            String confirmation = "Error: Multiple decimal points. Invalid Additional Expense Amount.";
            Toast.makeText(this,confirmation,Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * method for calculating the total income
     */
    public void updateTotalIncome(){
        //call the update total income method in Main activity.
        this.user.updateTotalIncomeFromActivity();
    }


    /**
     * method for calculating the daily budget
     */
    public void updateDailyBudget(){
        //call the update daily budget method in Main activity.
        this.user.updateDailyBudgetFromActivity();
    }


    //controller method for computing the immediate status update.
    public void immediateStatus(){
        //set the confirmation text.
        String confirmation = this.user.userImmediateStatus();
        setContentsOfTextView(R.id.addExpenseConfirmationText, confirmation);

        //update the total income now.
        updateTotalIncome();

        //update the daily budget now.
        updateDailyBudget();

    }

    //controller method for closing the activity manually via onclick on the popup image button.
    public void closeActivityAddExpManually(View view){
        this.finish();
    }


    /* this mutator sets the output label */
    private void setContentsOfTextView(int id, String newContents) {
        View view = findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }

}
