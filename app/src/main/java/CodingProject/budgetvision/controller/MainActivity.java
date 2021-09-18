package CodingProject.budgetvision.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.IOException;
import java.lang.ref.WeakReference;


import CodingProject.budgetvision.R;
import CodingProject.budgetvision.model.CategoriesClass;
import CodingProject.budgetvision.model.CurrencyConversionClass;
import CodingProject.budgetvision.model.HomeFragment;
import CodingProject.budgetvision.model.MoneyFragment;
import CodingProject.budgetvision.model.SettingsFragment;
import CodingProject.budgetvision.model.UsersBudgetClass;

public class MainActivity extends AppCompatActivity {


    private UsersBudgetClass user = new UsersBudgetClass(); //creating a User Budget object from the UsersBudget Class.

    public static WeakReference <MainActivity> weakActivity;

    private HomeFragment homeFragment = new HomeFragment(); //the home fragment to be used.
    private MoneyFragment moneyFragment = new MoneyFragment(); // the money fragment to be used.
    private SettingsFragment settingsFragment = new SettingsFragment(); //the settings fragment to be used.
    //SharedPreferences sharedPreferences;

    private String incomeLoad;
    private String dailyBudgetLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 100% of the screen width and height.
        getWindow().setLayout((int) (width), (int) (height));

        /**
         * Creating the budgetVision bottom navigation view object. (menu on the bottom of the screen).
         */
        BottomNavigationView budgetVisionBottomNav = findViewById(R.id.budgetVision_navigation);
        budgetVisionBottomNav.setOnNavigationItemSelectedListener(navListener);

        /**
         * executing the background with list of gradients on the main constraint layout of the application.
         */
//        //commented out for now for saving my battery when running android emulator ._.
//        ConstraintLayout mainLayout = findViewById(R.id.layout);
//        AnimationDrawable animationDrawable = (AnimationDrawable) mainLayout.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();

        /*
         * On create of main activity the the home fragment is added. Home fragment contains all the user interaction elements.
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        //instance is this main activity.
        weakActivity = new WeakReference<>(MainActivity.this);

        //update the daily budget and total income initially to zero dollars on initial create.
        updateInitialValues();

 //       loadData();
//      updateViews();

    }

    //TODO: saving the variables is not working after the application is restarted. Fix later.
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState){
//        super.onSaveInstanceState(savedInstanceState);
//
//        TextView incomeTextView = (TextView) findViewById(R.id.incomeValueText); //The income textview.
//        TextView dailyBudgetTextView = (TextView) findViewById(R.id.dailyBudgetText); //the daily budget textview.
//
//        savedInstanceState.putString("incomeLoad", incomeTextView.getText().toString());
//        savedInstanceState.putString("dailyBudgetLoad", dailyBudgetTextView.getText().toString());
//
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState){
//        super.onRestoreInstanceState(savedInstanceState);
//
//        System.out.println("SAVED INSTANCE STATE NOt NULL");
//        TextView incomeTextView = (TextView) findViewById(R.id.incomeValueText); //The income textview.
//        TextView dailyBudgetTextView = (TextView) findViewById(R.id.dailyBudgetText); //the daily budget textview.
//
//        String savedIncome = savedInstanceState.getString("incomeLoad");
//        incomeTextView.setText(savedIncome);
//
//        String savedDailyBudget = savedInstanceState.getString("dailyBudgetLoad");
//        dailyBudgetTextView.setText(savedDailyBudget);
//    }

    /**
     * On navigation item selected listener for the bottom navigation view.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.navigation_home:
                        //remove the money fragment. Its view will be removed from fragment_full_container if it was added.
                        getSupportFragmentManager().beginTransaction().remove(moneyFragment).commit();

                        //remove the settings fragment. Its view will be removed from fragment_full_container if it was added.
                        getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();

                        //re-attach the home fragment. Which was created in MainActivity OnCreate() method.
                        getSupportFragmentManager().beginTransaction().attach(homeFragment).commit();
                        break;

                    case R.id.navigation_money:
                        //detach the home fragment. The home fragment view is destroyed but its contents will be kept.
                        getSupportFragmentManager().beginTransaction().detach(homeFragment).commit();

                        //remove the settings fragment. Its view will be removed from fragment_full_container if it was added.
                        getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();

                        //add the money fragment to the fragment_full_container. Any fragment previously in the full container is destroyed.
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_full_container, moneyFragment).commit();
                    break;

                    case R.id.navigation_settings:
                        //detach the home fragment. The home fragment view is destroyed but its contents will be kept.
                        getSupportFragmentManager().beginTransaction().detach(homeFragment).commit();

                        //remove the money fragment. Its view will be removed from fragment_full_container if it was added.
                        getSupportFragmentManager().beginTransaction().remove(moneyFragment).commit();

                        //add the settings fragment to the fragment_full_container. Any fragment previously in the full container is destroyed.
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_full_container, settingsFragment).commit();
                        break;

                    default:
                    break;
                }
                return true;
            }
        };


    //helper method to return the current user used in all popup classes.
    public UsersBudgetClass getUser(){
        return this.user;
    }

    //helper method used in SettingsFragment to show a welcome back greeting in the main activity layout.
    public void welcomeBackGreeting(String firstName, String lastName){
        TextView greetingLabel = (TextView) findViewById(R.id.greetingLabel);
        setContentsOfTextView(R.id.greetingLabel, "Welcome back, " + firstName + " " + lastName);
        greetingLabel.setVisibility(View.VISIBLE); // set the visibility of the greeting label to visible now that the user signed in.

    }


    /**
     * method to return the instance of this main fragment. Used in the popup activities, the fragments and other classes.
     * The instance of the MainActivity is needed to call a specific method in the MainActivity.
     * Instance of MainActivity eliminates the need for using static methods since MainActivity
     * @return
     */
    public static MainActivity getInstance() {
        return weakActivity.get();
    }

//TODO: shared preferences not working, fix later.

//    public void saveData(){
//        TextView incomeTextView = (TextView) findViewById(R.id.incomeValueText); //The income textview.
//        TextView dailyBudgetTextView = (TextView) findViewById(R.id.dailyBudgetText); //the daily budget textview.
//
//        sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(incomeLoad,incomeTextView.getText().toString()).commit();
//        editor.putString(dailyBudgetLoad,dailyBudgetTextView.getText().toString()).commit();
//    }
//
//    public void loadData(){
//        sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
//        incomeLoad = sharedPreferences.getString(incomeLoad, "");
//        dailyBudgetLoad = sharedPreferences.getString(dailyBudgetLoad, "");
//
//    }
//    public void updateViews(){
//        setContentsOfTextView(R.id.incomeValueText, incomeLoad);
//        setContentsOfTextView(R.id.dailyBudgetText, dailyBudgetLoad);
//    }


    /**
     * This method is used in ViewSubcategoryPopup.java and MainActivity.java .
     * helper method for the setting the total income text.
     */
    public void updateTotalIncome(){
        String currencySymbol = user.getCurrencySymbol(); // the currency symbol.

        setContentsOfTextView(R.id.incomeValueText, currencySymbol + " " + this.user.getUserTotalIncome());
        //saveData();
    }


    /**
     * This method is used in the ViewSubcategoryPopup.java and MainActivity.java .
     * helper method for setting the daily budget text.
     */
    public void updateDailyBudget(){
        String currencySymbol = user.getCurrencySymbol(); // the currency symbol.

        setContentsOfTextView(R.id.dailyBudgetText, currencySymbol  + " " +  this.user.getUserDailyBudget());
        //saveData();
    }

    /**
     * This method is used in the Categories Class.
     * This method takes the VALID category name, subcategory name and cost associated with the subcategory.
     * The parameters are added to the users google spreadsheet from Settings Fragment.
     *
     * @param categoryName
     * @param subCategoryToAdd
     * @param cost
     * @throws IOException
     */
    public void addSubcategoriesToSheet(String categoryName, String subCategoryToAdd, String cost) throws IOException {
        settingsFragment.addSubcategoriesToSheet(categoryName,subCategoryToAdd,cost);
    }

    /**
     * This method is used in the LoginSuccessfulPopup Class.
     * This method takes the VALID spreadsheet url from the SettingsFragment.
     * @return
     */
    public String getSpreadsheetUrl(){
        return settingsFragment.getSpreadsheetUrl();
    }

    /**
     * this mutator sets the output label
     * @param id
     * @param newContents
     */
    private void setContentsOfTextView(int id, String newContents) {
        View view = (TextView) findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }

    /**
     * This method will create a new categories object, all other previous attributes associated with the category will be gone.
     * An alert dialog is displayed to give a warning for the user before clearing all the subcategories.
     */
    public void clearAllWarningMessage(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("BudgetVision Alert");
        alertDialog.setMessage("WARNING:\nAll subcategories will be cleared.\nAre you sure you want to proceed?");
        //alert dialog button to remove all the subcategories
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == AlertDialog.BUTTON_POSITIVE){
                            user = new UsersBudgetClass();
                            updateInitialValues();
                        }
                        dialog.dismiss();
                    }
                });
        //alert dialog button to cancel and go back,
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void clearAll(){
        user = new UsersBudgetClass();
        updateInitialValues();
    }
    /**
     * method for initially setting the text input values to $0. Also, executes after the method clearAll().
     */
    public void updateInitialValues(){
        setContentsOfTextView(R.id.incomeValueText, "$0.00");
        setContentsOfTextView(R.id.dailyBudgetText, "$0.00");
    }

    /**
     * Helper method used in Settings Fragment. Returns the current text of the daily budget.
     */
    public String getDailyBudget(){
        return ((TextView)(findViewById(R.id.dailyBudgetText))).getText().toString();
    }

    /**
     * Helper method used in Settings Fragment. Returns the current text of the total income.
     */
    public String getTotalIncome(){
        return ((TextView)(findViewById(R.id.incomeValueText))).getText().toString();
    }

}