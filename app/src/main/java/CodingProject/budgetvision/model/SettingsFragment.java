package CodingProject.budgetvision.model;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.MainActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    //Google sign in client object.
    GoogleSignInClient mGoogleSignInClient;

    //Google credential object. Used to access the google account spreadsheet.
    GoogleCredentials credentials;

    Sheets sheetService;

    Spreadsheet userSpreadsheet;

    boolean stateOfSaveSwitch = false;

    View myInflatedView; //the inflated view which stores all the contents from this Settings Fragment.

    private String userUniqueId; //the unique id of the user. If the unique id is null then they have not signed into google via BudgetVision application.
    private String refreshToken; //the refresh token used to authenticate the BudgetVision user to google spreadsheet.
    private String accessToken;
    private String spreadsheetUrl; //the url of the BudgetVision user google spreadsheet.

    private int tempRowCounter = 1; //stores the row number used to update subcategories and costs in the google sheet.
    private static final String APPLICATION_NAME = "BudgetVision Transactions";

    //currency conversion object from CurrencyConversionClass.java file.
    CurrencyConversionClass currencyConversionObj = new CurrencyConversionClass();

    CategoriesClass categoriesObject;
    Spinner currencySpinner;
    Switch saveCurrencySwitch;

    int currencyCounter;
    int defaultCurrencyPosition;

    String dailyBudgetConverted;
    String totalIncomeConverted;
    String currentMonthlyExpensesConverted;

    ArrayAdapter<String> allCountriesAdapter;

    /**
     * OnCreate method executes when the fragment is first created.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        categoriesObject = MainActivity.getInstance().getUser().categoriesObject();

        //inflate the contents inside the fragment.
        this.myInflatedView = inflater.inflate(R.layout.fragment_settings,container,false);

        SignInButton signInButton = (SignInButton) this.myInflatedView.findViewById(R.id.sign_in_button);
        signInButton.setVisibility(View.VISIBLE);
        signInButton.setEnabled(true);

        //setting the array adapter containing all ISO 3166 countries in the array adapter.
        String [] allCountries = currencyConversionObj.getAllCountries();

        Spinner allCountriesSpinner = (Spinner)(this.myInflatedView.findViewById(R.id.currencySpinner));
        allCountriesAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_dropdown_item,allCountries);

        allCountriesAdapter.remove((String)allCountriesSpinner.getSelectedItem());

        //default spinner item is Canada since the default currency is CAD.
        allCountriesSpinner.setAdapter(allCountriesAdapter);

        //defaultCurrencyPosition = allCountriesAdapter.getPosition("(Canada)");
        //allCountriesSpinner.setSelection(defaultCurrencyPosition);

        signInButton.setOnClickListener(this);

        /*
         * The input stream of where the client_secrets.json file is stored.
         * The input stream is obtained from the res folder of type raw where client_secrets.json file is stored.
         */
        InputStream inputStream = getResources().openRawResource(R.raw.client_secrets);
        GoogleClientSecrets clientSecrets = null;
        try {
            clientSecrets = GoogleClientSecrets.load(new JacksonFactory(), new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();
        //testing out the client secret and ID.
        System.out.println("From reading the file, printed by me client ID " + clientId);
        System.out.println("From reading the file, printed by me client secret " + clientSecret);


        /*
         * Configure sign-in to request the user's ID, email address, and basic
         * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
         * Requested the scope for full google drive access to access users spreadsheet services.
         * Requesting the authorization code using the server client Id to later enable google API.
         */
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/spreadsheets"))
                .requestServerAuthCode(clientId)
                .requestEmail()
                .build();

        //view onclick listener for the clear all button.
        ( (Button) this.myInflatedView.findViewById(R.id.clearAllButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        Switch saveBkgAnimationSwitch = ((Switch)(this.myInflatedView.findViewById(R.id.saveSwitchBkg)));

        saveBkgAnimationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MainActivity.getInstance().addBackgroundAnimation(true);
                }
                else{
                    MainActivity.getInstance().addBackgroundAnimation(false);
                }
            }
        });

        currencySpinner = ((Spinner)(this.myInflatedView.findViewById(R.id.currencySpinner)));

        saveCurrencySwitch = ((Switch)(this.myInflatedView.findViewById(R.id.saveSwitch)));

        //only execute the save switch to change currencies when the country is unique.
        //listener for the save switch.
        saveCurrencySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked && currencyCounter % 2 == 0) {
                    currencyChanged(); //change the currency to the currency selected.
                    currencyCounter++;

                    //prevent clicking of the switch again to avoid spamming/abuse.
                    disableSaveCurrencySwitch();

                }
                if(isChecked){
                    //prevent clicking of the spinner since the only option is to go back to CAD.
                    currencySpinner.setEnabled(false);
                    currencySpinner.setClickable(false);
                }
                if(! isChecked) {
                    //allow clicking of the spinner there is option to choose new currency.
                    currencySpinner.setEnabled(true);
                    currencySpinner.setClickable(true);

                    resetSpinnerToCanadaPosition(); //reset the spinner default position to Canada.

                    currencyChanged(); //change the currency back to CAD.

                    /*setting the array adapter containing all ISO 3166 countries in the array adapter.
                     *This is to include Canada which was removed initially since default currency is CAD.
                     */

                    String [] allCountries = currencyConversionObj.getAllCountries();
                    List<String> allCountriesList = new ArrayList<String>(Arrays.asList(allCountries));

                    //prevent clicking of the switch again to avoid spamming.
                    disableSaveCurrencySwitch();

                    //remove option for Canada once again since the default currency is CAD again.
                    allCountriesList.remove("Canada");
                    allCountries = allCountriesList.toArray(new String[0]);

                    allCountriesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,allCountries);
                    currencySpinner.setAdapter(allCountriesAdapter);

                    currencyCounter++;


                }

            }
        });


        // Build a GoogleSignInClient with the options specified by gso.
        //the context is the activity associated with the fragment which is called from getActivity(). This is a context since Activity extends Context.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(),gso);

        return myInflatedView;
    }


    /**
     * When the google sign in button is clicked call the signIn() method.
     * @param v
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.downloadSheetBtn:
                downloadGoogleSheet();
                break;
        }
    }


    //before the activity is displayed assignment is done.
    ActivityResultLauncher <Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                            handleSignInResult(task);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }


    /**
     * Method for handling the users google sign-in for the end user.
     * This method also handles the budgetvision user client secrets to give them access to google spreadsheet scopes.
     * @param completedTask
     * @throws IOException
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) throws IOException {
        /*
         * The input stream of where the client_secrets.json file is stored.
         * The input stream is obtained from the res folder of type raw where client_secrets.json file is stored.
         */
        InputStream inputStream = getResources().openRawResource(R.raw.client_secrets);
        GoogleClientSecrets clientSecrets = null;
        try {
            clientSecrets = GoogleClientSecrets.load(new JacksonFactory(), new InputStreamReader(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();
        System.out.println("From reading the file, printed by me client ID " + clientId);
        System.out.println("From reading the file, printed by me client secret " + clientSecret);

        if(refreshToken != null) {
            /*
             * Refresh the token with the refresh token instead of creating a 1 hour access token.
             */
            credentials.refreshIfExpired();
            System.out.println("refresh token not null, (printed by me):" + refreshToken);

            /*
             * Start a new activity which tells the user that they are signed in to google successfully.
             */
            Intent intent = new Intent(getActivity(), LoginSuccessfulPopup.class);
            startActivity(intent);

            //set the enabled option of the sign in button to false since the user has been signed in.
//            SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//            signInButton.setEnabled(false);

        }
        /*
         * If there is a valid refresh token then use the refresh token from now on to authorize the budgetvision user to google spreadsheet api.
         */
        if(refreshToken == null){
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                /*
                 * Start the process for getting the authorization code.
                 * Then, using the authorization code to get an access token which can be used to use the google API for spreadsheet.
                 */
                String authCode = account.getServerAuthCode(); //the authorization code.

                OkHttpClient client = new OkHttpClient();

                /*
                 * the request body which contains the data for the request to get an Oauth2 v4 token.
                 *
                 */
                RequestBody requestBody = new FormBody.Builder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", clientId)
                        .add("client_secret", clientSecret)
                        .add("redirect_uri", "")
                        .add("code", authCode)
                        .add("access_type","offline")
                        .build();

                //create the okhttp3 request for the URI for an Oauth2 v4 token.
                final okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://www.googleapis.com/oauth2/v4/token")
                        .post(requestBody)
                        .build();

                /*
                 * Asynchronous HTTP request is used to prevent network exception error since the call is being executed on the main thread.
                 * Get the callback of the request when the response is fetched OR an error was executed.
                 * create a JSON object from the fetched request response to obtain the access token.
                 * The access token is then used to create the GoogleCredential.
                 */
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                        try {
                            //create a JSON object from the json string containing the object of the Oauth 2 v4 token request.
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            //human readable JSON String, use for debugging.
                            final String message = jsonObject.toString(5);

                            System.out.println("Message " + message); //print out the message.

                            /*
                             * Retrieve the initial access token from the jsonObject.
                             */
                            accessToken = jsonObject.get("access_token").toString();
                            System.out.println("Printed By Me Access token is " + accessToken);

                            /*
                             * IMPORTANT NOTE : the refresh token is stored and this refresh token will be used to authenticate the user to the google spreadsheet api.
                             * The refresh token does not expire whereas the access token has a span of 3600 seconds which is 1 hour.
                             * Once the access token expires the refresh token will be used to authenticate the budgetvision user to the google spreadsheet.
                             */
                            refreshToken = jsonObject.get("refresh_token").toString();
                            System.out.println("Printed By Me Refresh token is " + refreshToken);

                            //  Google credential is now depreciated. This was how credential was accessed before depreciation.
                            //  GoogleCredential credential = new GoogleCredential.Builder()
                            //                                    .setJsonFactory(new JacksonFactory())
                            //                                    .setTransport(new NetHttpTransport())
                            //                                    .setClientSecrets(serverClientId, clientSecret)
                            //                                    .build();
                            //  credential.setAccessToken(accessToken); //set the access token to the credential.
                            //  credential.setRefreshToken(refreshToken); //set the refresh token to the credential.

                            //OAuth2 Credentials representing a user's identity and consent
                            credentials =
                                    UserCredentials.newBuilder()
                                            .setClientId(clientId)
                                            .setClientSecret(clientSecret)
                                            .setRefreshToken(refreshToken)
                                            .build();
                            credentials.refreshIfExpired();

                            /*
                             * Created a new spreadsheet in the users google account called "New BudgetVision Transactions".
                             */
                            Spreadsheet spreadsheetInitial = new Spreadsheet()
                                    .setProperties(new SpreadsheetProperties()
                                            .setTitle("New BudgetVision Transactions"));

                            /*
                             * Created a HttpRequestInitializer with the OAuth2 credentials to be able to later access the sheets service.
                             */
                            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

                            /*
                             * Created a new sheets service which is used to create the spreadsheet.
                             */
                            sheetService = new Sheets.Builder(
                                    new com.google.api.client.http.javanet.NetHttpTransport(),
                                    JacksonFactory.getDefaultInstance(), requestInitializer)
                                    .setApplicationName(APPLICATION_NAME)
                                    .build();

                            /*
                             * Created and initialise the user spreadsheet.
                             */
                            userSpreadsheet = sheetService.spreadsheets().create(spreadsheetInitial)
                                    .execute();

                            /*
                             * Call method to initialize the spreadsheet with all the categories and cost headings.
                             */
                            initialiseTheSheet();

                            /*
                             * Store the url of the BudgetVision user sheet.
                             * this url will be the pdf link to the BudgetVision user sheet.
                             */
                            //spreadsheetUrl = userSheet.getSpreadsheetUrl();
                            spreadsheetUrl = "https://docs.google.com/spreadsheets/d/"+userSpreadsheet.getSpreadsheetId()+
                                    "/export"+
                                    "?format=pdf&"+
                                    "size=0&"+
                                    "fzr=true&"+
                                    "portrait=false&"+
                                    "fitw=true&"+
                                    "gridlines=false&"+
                                    "printtitle=true&"+
                                    "sheetnames=true&"+
                                    "pagenum=CENTER&"+
                                    "attachment=true&"+
                                    "access_token="+accessToken;

                            /*
                             * Start a new activity which tells the user that they are signed in to google successfully.
                             */
                            Intent intent = new Intent(getActivity(), LoginSuccessfulPopup.class);
                            startActivity(intent);

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        System.out.println("FAILURE DETECTED FROM onFaliure(): " + call);
                    }
                });

                // Signed in successfully, show authenticated UI.
                if (account != null) {

                    //user first name
                    String personGivenName = account.getGivenName();

                    //user last name
                    String personFamilyName = account.getFamilyName();

                    //greet the user in the main activity -> activity main layout.
                    MainActivity.getInstance().welcomeBackGreeting(personGivenName,personFamilyName);

                    //user unique id.
                    this.userUniqueId = account.getId();

                    //set the unique id in the Categories class.
                    CategoriesClass categoriesObject = MainActivity.getInstance().getUser().categoriesObject();
                    categoriesObject.setUserUniqueId(this.userUniqueId);

                    //set the enabled option of the sign in button to false since the user has been signed in.
                    SignInButton signInButton = (SignInButton) myInflatedView.findViewById(R.id.sign_in_button);
                    signInButton.setEnabled(false);
                }


            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.

                e.printStackTrace();
            }
        }

    }


    /**
     * This method is used by SettingsFragment class.
     * This method initialises the budgetvision users google sheet.
     */
    public void initialiseTheSheet(){

        ValueRange categoryBody = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList("Food", "Housing", "Commute", "Lifestyle", "Recreation", "Costs")));
        try {
            AppendValuesResponse appendCategories = sheetService.spreadsheets().values()
                    .append(userSpreadsheet.getSpreadsheetId(), "A:F", categoryBody)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method is used by the Categories Class.
     * This helper method adds the subcategories to the budgetvision users google sheet in their google account.
     * @param categoryName
     * @param subcategoryName
     * @param cost
     * @throws IOException
     */
    public void addSubcategoriesToSheet(String categoryName, String subcategoryName, String cost) throws IOException {
        //increment the temporary row counter.
        this.tempRowCounter += 1;

        //subcategory value names used in the spreadsheet as headers for each budget category.
        ValueRange subcategoryBody = new ValueRange()
                .setValues(Arrays.asList(Arrays.asList(subcategoryName)));

        //category value names used in the spreadsheet as headers for each budget category.
        ValueRange costBody = new ValueRange()
                .setValues((Arrays.asList(Arrays.asList(cost))));

        /*
         * Add the categories to the user sheet.
         */
        String rangeSubcategory = "";
        String rangeCosts = "F:F" + tempRowCounter;

        if(categoryName.equalsIgnoreCase("Food")) {
            rangeSubcategory = "A:A" + tempRowCounter;
        }
        else if(categoryName.equalsIgnoreCase("Housing")) {
            rangeSubcategory = "B:B" + tempRowCounter;
        }
        else if(categoryName.equalsIgnoreCase("Commute")) {
            rangeSubcategory = "C:C" + tempRowCounter;
        }
        else if(categoryName.equalsIgnoreCase("Lifestyle")) {
            rangeSubcategory = "D:D" + tempRowCounter;
        }
        else if(categoryName.equalsIgnoreCase("Recreation")) {
            rangeSubcategory = "E:E" + tempRowCounter;
        }

        //update the subcategories cells.
        UpdateValuesResponse updateSubcategories = sheetService.spreadsheets().values()
                .update(userSpreadsheet.getSpreadsheetId(),rangeSubcategory,subcategoryBody)
                .setValueInputOption("RAW")
                .setIncludeValuesInResponse(true)
                .execute();

        //update the costs cells.
        UpdateValuesResponse updateCosts = sheetService.spreadsheets().values()
                .update(userSpreadsheet.getSpreadsheetId(),rangeCosts,costBody)
                .setValueInputOption("RAW")
                .setIncludeValuesInResponse(true)
                .execute();

    }

    /**
     * This method is used by the LoginSuccessfulPopup Class.
     * @return BugdgetVision user spreadsheet url.
     */
    public String getSpreadsheetUrl() {
        return this.spreadsheetUrl;
    }

    /**
     * This method is used for downloading the users BudgetVision google sheet.
     * This method is called by the MainActivity Class.
     */
    public void downloadGoogleSheet(){
        /*
         * if the spreadsheet url is null this method does nothing.
         * If the spreadsheet url is valid then the pdf of spreadsheet is dowloaded.
         */
        if(spreadsheetUrl != null) {
            //request with download manager the URI of the spreadsheet url.
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(spreadsheetUrl));

            request.setTitle("BudgetVision User Spreadsheet");
            request.setDescription("Downloading File Please Wait...");

            //set the destination of the download to the public external storage directory.
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "BudgetVision User Spreadsheet");

            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

            //make the toast to tell the user downloading started.
            Toast.makeText(getActivity(), "Downloading started", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is executed via OnClick.
     */
    public void clearAll(){
        MainActivity.getInstance().clearAllWarningMessage();
    }

    /**
     * Helper Method executed from the "save" switch in the Settings Fragment. The currency has been changed.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void currencyChanged() {
        String countryDisplayName = "English " + ((Spinner) this.myInflatedView.findViewById(R.id.currencySpinner)).getSelectedItem().toString();

        //get all the subcategories for each cateogry and the number of subcategories.
        double[] allFoodCosts = this.categoriesObject.getFoodCostsNumber();
        int NOF = this.categoriesObject.getNOF();
        double[] allHousingCosts = this.categoriesObject.getHousingCostsNumber();
        int NOH = this.categoriesObject.getNOH();
        double[] allCommuteCosts = this.categoriesObject.getCommuteCostsNumber();
        int NOC = this.categoriesObject.getNOC();
        double[] allRecreationCosts = this.categoriesObject.getRecreationCostsNumber();
        int NOR = this.categoriesObject.getNOR();
        double[] allLifestyleCosts = this.categoriesObject.getLifestyleCostsNumber();
        int NOL = this.categoriesObject.getNOL();

        String currentDailyBudget = MainActivity.getInstance().getDailyBudget();
        String currentTotalIncome = MainActivity.getInstance().getTotalIncome();

        double monthlyExpenses = Double.parseDouble(String.valueOf(MainActivity.getInstance().getUser().getTotalMonthlyExpenses()));

        if( ! countryDisplayName.equalsIgnoreCase("English (Canada)")) {
            //convert the monthly expenses.
            currentMonthlyExpensesConverted = monthlyBudgetFutureProcess(countryDisplayName, String.valueOf(monthlyExpenses));

            //convert the daily budget.
            dailyBudgetConverted = dailyBudgetFutureProcess(countryDisplayName, currentDailyBudget);

            //convert the total income.
            totalIncomeConverted = totalIncomeFutureProcess(countryDisplayName, currentTotalIncome);
        }
        else if (countryDisplayName.equalsIgnoreCase("English (Canada)")){
            try {
                currencyConversionObj.setCurrencySymbolAndFormat(countryDisplayName, String.valueOf(monthlyExpenses));
                currentMonthlyExpensesConverted = currencyConversionObj.toString();
                currencyConversionObj.setCurrencySymbolAndFormat(countryDisplayName, currentDailyBudget);
                dailyBudgetConverted = currencyConversionObj.toString();
                currencyConversionObj.setCurrencySymbolAndFormat(countryDisplayName, currentTotalIncome);
                totalIncomeConverted = currencyConversionObj.toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if( ! (dailyBudgetConverted.equalsIgnoreCase("currency not supported") || totalIncomeConverted.equalsIgnoreCase("currency not supported") || currentMonthlyExpensesConverted.equalsIgnoreCase("currency not supported")) ) {
            //background thread to convert all the costs and then using handler to update the UI.
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                     /*
                         * iterate through all the subcategories and format them according to their currency.
                         * Handlers are used to update the arrays after the call to the method setCurrencySymbolAndFormat which does HTML parsing from yahoo finance asynchronously.
                         */
                        double[] latestFoodCosts = delayAndApplyCurrencyChange(NOF, allFoodCosts);
                        double[] latestHousingCosts = delayAndApplyCurrencyChange(NOH, allHousingCosts);
                        double[] latestLifestyleCosts = delayAndApplyCurrencyChange(NOL, allLifestyleCosts);
                        double[] latestCommuteCosts = delayAndApplyCurrencyChange(NOC, allCommuteCosts);
                        double[] latestRecreationCosts = delayAndApplyCurrencyChange(NOR, allRecreationCosts);

                        //set the categories that are all formatted by the user-selected currency.
                        categoriesObject.setSubcategoryCosts(latestFoodCosts, "food");
                        categoriesObject.setSubcategoryCosts(latestHousingCosts, "housing");
                        categoriesObject.setSubcategoryCosts(latestCommuteCosts, "commute");
                        categoriesObject.setSubcategoryCosts(latestRecreationCosts, "recreation");
                        categoriesObject.setSubcategoryCosts(latestLifestyleCosts, "lifestyle");

                        //the current currency symbol for the currency chosen.
                        String currencySymbol = currencyConversionObj.getCurrencySymbol();

                        System.out.println(dailyBudgetConverted + " is dailyBudgetConverted");
                        System.out.println(totalIncomeConverted + " is totalIncomeConverted");
                        System.out.println(currentMonthlyExpensesConverted + " is currentMonthlyExpensesConverted");

                        //set the converted monthly expenses.
                        MainActivity.getInstance().getUser().setTotalMonthlyExpenses(Double.parseDouble(currentMonthlyExpensesConverted));

                        Handler handler1 = new Handler(Looper.getMainLooper());
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //set the currency symbol in the UsersBudget.java class.
                                MainActivity.getInstance().getUser().setCurrencySymbol(currencySymbol);

                                //formatting the currency value to 2 decimal places.
                                String formattedCurrencyValue = " " + dailyBudgetConverted;

                                // make update on the UI for daily budget.
                                MainActivity.getInstance().updateDailyBudget();
                            }
                        }, 500);

                        Handler handler2 = new Handler(Looper.getMainLooper());
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //set the currency symbol in the UsersBudget.java class.
                                MainActivity.getInstance().getUser().setCurrencySymbol(currencySymbol);

                                //formatting the currency value to 2 decimal places.
                                String formattedCurrencyValue = " " + totalIncomeConverted;

                                // make update on the UI for daily budget.
                                MainActivity.getInstance().updateTotalIncome();
                            }
                        }, 500);

                }
            });

        thread.start();

        } //end of if-statement.
        /*
         * otherwise the currency is not supported. A toast is made to tell the user that the currency is currently not supported.
         * Also, currency switch will then be on the switched off position.
         */
        else {
            Toast.makeText(getActivity(), "This country currency is currently not supported.", Toast.LENGTH_LONG).show();
            saveCurrencySwitch.setOnCheckedChangeListener(null);
            saveCurrencySwitch.setChecked(false);
            saveCurrencySwitch.setOnCheckedChangeListener(null);

            //prevent clicking of the currency switch again.
            saveCurrencySwitch.setEnabled(false);
            saveCurrencySwitch.setClickable(false);

        }

    }


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

        // Block and get the result of the Future
        currentMonthlyExpensesConverted = currencyConversionObj.toString();

        return currentMonthlyExpensesConverted;
    }

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

        // Block and get the result of the Future
        dailyBudgetConverted = currencyConversionObj.toString();

        return dailyBudgetConverted;
    }


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

        // Block and get the result of the Future
        totalIncomeConverted = currencyConversionObj.toString();
        return totalIncomeConverted;
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
     * This method disables the clicking of the save currency switch.
     */
    public void disableSaveCurrencySwitch(){
        saveCurrencySwitch.setEnabled(false);
        saveCurrencySwitch.setClickable(false);
    }

    public void resetSpinnerToCanadaPosition(){
        String [] allCountries = currencyConversionObj.getAllCountries();
        List<String> allCountriesList = new ArrayList<String>(Arrays.asList(allCountries));

        allCountriesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,allCountries);

        //set the selection of the currency spinner to CAD.
        defaultCurrencyPosition = allCountriesAdapter.getPosition("(Canada)");
        currencySpinner.setSelection(defaultCurrencyPosition);
    }


}
