package CodingProject.budgetvision.model;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.MainActivity;

public class DeleteSubcategoryPopup extends Activity {

    UsersBudgetClass user = MainActivity.getInstance().getUser();
    private String categorySelected;
    private String subcategoryChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_delete);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 80% of the screen width and height.
        getWindow().setLayout((int) (width *.8),(int) (height*.8));

        Spinner categoriesSpinner = (Spinner) findViewById(R.id.categoryOptions3);

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

    //method for creating the hints for the auto complete text view.
    public void createAutoSuggestions(String categorySelected){
        /*
         * create the auto suggestions for the auto text view based on the category selected.
         */
        if(categorySelected.equalsIgnoreCase("Food")) {
            this.categorySelected = "Food"; //the category selected is food.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions2);
            //String array adapter for the food subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.foodSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Housing")) {
            this.categorySelected = "Housing"; //the category selected is housing;.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions2);
            //String array adapter for the housing subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.housingSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Commute")) {
            this.categorySelected = "Commute"; //the category selected is Commute.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions2);
            //String array adapter for the Commute subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.commuteSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Recreation")) {
            this.categorySelected = "Recreation"; //the category selected is Recreation.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions2);
            //String array adapter for the Recreation subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.recreationSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
        else if(categorySelected.equalsIgnoreCase("Lifestyle")) {
            this.categorySelected = "Lifestyle"; //the category selected is lifestyle.
            AutoCompleteTextView editAutoSubcategory = findViewById(R.id.autoSuggestions2);
            //String array adapter for the lifestyle subcategory autocomplete text view.
            ArrayAdapter<String> autoSubcategoryAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, HomeFragment.lifestyleSubcategoryHints);
            editAutoSubcategory.setAdapter(autoSubcategoryAdapter);
        }
    }

    public void removeExpense(View view){
        //get which subcategory is chosen from the user. This subcategory is to be removed.
        AutoCompleteTextView subcategoryChoice = (AutoCompleteTextView) findViewById(R.id.autoSuggestions2);
        this.subcategoryChosen = subcategoryChoice.getText().toString();

        //remove the user expense.
        user.removeUserSubcategory(this.categorySelected, this.subcategoryChosen);

        String confirmation = this.user.userImmediateStatus();
        if(!(confirmation.equalsIgnoreCase("Error: Subcategory " + this.subcategoryChosen + " Does Not Exist For Category " + this.categorySelected))){
            //close the activity now that the expense has been added. To prevent user from spamming expenses for the same subcategory simultaneously.
            closeActivity();

            //update the total income now.
            updateTotalIncome();

            //update the daily budget now.
            updateDailyBudget();
        }
        else if((confirmation.equalsIgnoreCase("Error: Subcategory " + this.subcategoryChosen + " Does Not Exist For Category " + this.categorySelected))){
            immediateStatusRemoveExpense();
        }

    }

    //method for calculating the total income after the subcategory was deleted.
    public void updateTotalIncome(){
        String userIncome = "$ " + this.user.userTotalBalance();
        //call the update total income method in main activity since the user income text is in main activity layout.
        MainActivity.getInstance().updateTotalIncome(userIncome);
    }


    //method for calculating the daily budget after the subcategory was deleted.
    public void updateDailyBudget(){
        String dailyBudget =  "$ " + this.user.userDailyBudget();
        //call the update daily budget method in main activity since the user daily budget text is in the main activity layout.
        MainActivity.getInstance().updateDailyBudget(dailyBudget);
    }


    //controller method for computing the immediate status update after the subcategory is removed.
    public void immediateStatusRemoveExpense(){
        //set the confirmation text.
        String confirmation = this.user.userImmediateStatus();
        setContentsOfTextView(R.id.removeExpenseConfirmationTxt, confirmation);

    }

    //controller method for closing the activity.
    public void closeActivity(){
        this.finish();
    }


    //controller method for closing the activity manually via onclick for the popup.
    public void closeActivityRemoveExpManually(View view){
        this.finish();
    }

    /* this mutator sets the output label */
    private void setContentsOfTextView(int id, String newContents) {
        View view = findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }


}
