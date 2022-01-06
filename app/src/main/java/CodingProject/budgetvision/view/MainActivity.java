package CodingProject.budgetvision.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.IOException;


import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.UserBudgetCallback;
import CodingProject.budgetvision.controller.UserBudgetComponent;
import CodingProject.budgetvision.controller.UsersBudgetClass;

public class MainActivity extends AppCompatActivity implements UserBudgetCallback {

    private HomeFragment homeFragment = new HomeFragment(); //the home fragment to be used.
    private MoneyFragment moneyFragment = new MoneyFragment(); // the money fragment to be used.
    private SettingsFragment settingsFragment = new SettingsFragment(); //the settings fragment to be used.

    private UsersBudgetClass mainUser; //creating a User Budget object from the UsersBudget Class.
    private String incomeSave;
    private String dailyBudgetSave;

    private static final String prefsName = "budget_vision_app_saver";

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

        //retrieving the component from the Application class.
        UserBudgetComponent userComponent = ((UsersBudgetClass) getApplication()).getAppComponent();
        mainUser = userComponent.getMyMainUser();

        mainUser.setCallback(this);

        /*
         * Creating the budgetVision bottom navigation view object. (menu on the bottom of the screen).
         */
        BottomNavigationView budgetVisionBottomNav = findViewById(R.id.budgetVision_navigation);
        budgetVisionBottomNav.setOnNavigationItemSelectedListener(navListener);

        /*
         * On create of main activity the the home fragment is added. Home fragment contains all the user interaction elements.
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        //SharedPreferences prefs = getSharedPreferences(prefsName, MODE_PRIVATE); //get the shared preferences associated with the file name from 'prefsName
        //Gson gson = new Gson(); //gson Object from the gson library now used to retrieve the previous JSON representation of the main user object,
        //String jsonMainUser = prefs.getString("mainUserSaved", ""); //the main user object JSON representation is retrieved from the shared preferences.
        //UsersBudgetClass restoredMainUser = gson.fromJson(jsonMainUser, UsersBudgetClass.class); //the main user is restored from the gson.

        //if the restored main user is not null then the new main user is the restored main user from its JSON representation.
//        if (restoredMainUser != null) {
//            this.mainUser = restoredMainUser;
//        }

        updateViews();

    }

    /**
     * Right before the app is closed the onSaveInstanceState method is executed.
     * In this method all the variables needed to be saved are put in the shared preferences.
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        SharedPreferences.Editor editor = getSharedPreferences(prefsName, MODE_PRIVATE).edit();
//        Gson gson = new Gson(); //gson object from gson library used to convert the main user into its JSON string representation.
//        String jsonMainUser = gson.toJson(mainUser); // the main user object JSON representation.
//        editor.putString("mainUserSaved", jsonMainUser); //save the main user JSON representation in the default shared preferences.
//
//        editor.commit(); //commit the changes.

        super.onSaveInstanceState(savedInstanceState);

    }

    /**
     * executing the background with list of gradients on the main constraint layout of the application.
     */
    @Override
    public void addBackgroundAnimation(Boolean isBkgAnimated) {

        ConstraintLayout mainLayout = findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) mainLayout.getBackground();

        if (isBkgAnimated) {
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        } else {
            animationDrawable.stop();
        }
    }

    /**
     * On navigation item selected listener for the bottom navigation view.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
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

    //helper method used in SettingsFragment to show a welcome back greeting in the main activity layout.
    @Override
    public void welcomeBackGreeting(String firstName, String lastName) {
        TextView greetingLabel = (TextView) findViewById(R.id.greetingLabel);
        setContentsOfTextView(R.id.greetingLabel, "Welcome back, " + firstName + " " + lastName);
        greetingLabel.setVisibility(View.VISIBLE); // set the visibility of the greeting label to visible now that the user signed in.

    }

    /**
     * Method called from OnCreate which updates the current views of daily budget and total income in MainActivity.
     */
    public void updateViews() {
        updateTotalIncome();
        updateDailyBudget();
    }


    /**
     * This method is used in ViewSubcategoryPopup.java and MainActivity.java .
     * helper method for the setting the total income text.
     */
    @Override
    public void updateTotalIncome() {
        String currencySymbol = mainUser.getCurrencySymbol(); // the currency symbol.
        this.incomeSave = currencySymbol + " " + this.mainUser.getUserTotalIncome();
        setContentsOfTextView(R.id.incomeValueText, this.incomeSave);
    }


    /**
     * This method is used in the ViewSubcategoryPopup.java and MainActivity.java .
     * helper method for setting the daily budget text.
     */
    @Override
    public void updateDailyBudget() {
        String currencySymbol = mainUser.getCurrencySymbol(); // the currency symbol.
        this.dailyBudgetSave = currencySymbol + " " + this.mainUser.getUserDailyBudget();
        setContentsOfTextView(R.id.dailyBudgetText, this.dailyBudgetSave);
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
    @Override
    public void addSubcategoriesToSheet(String categoryName, String subCategoryToAdd, String cost) throws IOException {
        settingsFragment.addNewSubcategoriesToSheet(categoryName, subCategoryToAdd, cost);
    }

    /**
     * this mutator sets the output label
     *
     * @param id
     * @param newContents
     */
    @Override
    public void setContentsOfTextView(int id, String newContents) {
        View view = (TextView) findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }

    /**
     * This method will create a new categories object, all other previous attributes associated with the category will be gone.
     * An alert dialog is displayed to give a warning for the user before clearing all the subcategories.
     */
    @Override
    public void clearAllWarningMessage() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("BudgetVision App Alert");
        alertDialog.setMessage("WARNING:\nBudgetVision App will reset all expenses added by you.\nUsers added in the 'Manage Money Owed' section will not be affected.\nAre you sure you want to proceed?");
        //alert dialog button to remove all the subcategories
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == AlertDialog.BUTTON_POSITIVE) {
                            mainUser.resetMainUser();

                            updateDailyBudget();
                            updateTotalIncome();
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

    /**
     * method for initially updating both the total income and daily budget in the view.
     */
    @Override
    public void updateInitialValues() {
        updateTotalIncome();
        updateDailyBudget();
    }

    /**
     * Helper method used in Settings Fragment. Returns the current text of the daily budget.
     */
    @Override
    public String getDailyBudget() {
        return ((TextView) (findViewById(R.id.dailyBudgetText))).getText().toString();
    }

    /**
     * Helper method used in Settings Fragment. Returns the current text of the total income.
     */
    @Override
    public String getTotalIncome() {
        return ((TextView) (findViewById(R.id.incomeValueText))).getText().toString();
    }
}