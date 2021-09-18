package CodingProject.budgetvision.model;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import java.util.Currency;
import java.util.Locale;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class CurrencyConversionClass {

    /**
     * HTTP call is made through OkHttpClient and then JSoup is used to HTML parse conversion rates from yahoo finance.
     * This class will take the daily budget, total income etc. and convert from CAD to desired currency.
     * By default the currency of BudgetVision will be CAD (Canadian Dollars).
     * This class will use previous web scraping to get the conversion rates for CAD to another currency. Also, currencies are formatted.
     */

    private final OkHttpClient client = new OkHttpClient();
    private String formattedCurrencyValue; //the currency symbol when the conversion rate is applied.
    private String countryName;
    private double conversionRate;
    private String[] allIsoCountries;

    private String currencySymbol;

    private int numTimesCAD;

    //hashmap for constant lookup types where the key is the country display name.
    private HashMap<String, String> countriesMap;

    private boolean isCurrencySameCountry; //boolean variable to store if the currency has been changed.

    //constructor for the currency conversion class. When an instance of Currency Conversion Class is created then the map of all ISO countries will be intiialized.
    public CurrencyConversionClass() {
        initCountriesMap();
    }

    /**
     * method for initializing the countries map.
     */
    public void initCountriesMap() {
        //String array with all two letter ISO 3166 countries.
        this.allIsoCountries = Locale.getISOCountries();

        //initialize the countriesMap.
        countriesMap = new HashMap<>();

        //iterate through all the ISO 3166 countries and put the 2 letter ISO 3166 into the hash map.
        for (int i = 0; i < allIsoCountries.length; i++) {
            String countryISO = allIsoCountries[i];
            Locale locale = new Locale("en", countryISO);
            String countryDisplayName = locale.getDisplayName();
            String country3166CodeISO = locale.getCountry();
            countriesMap.put(countryDisplayName, country3166CodeISO);
        }

    }

    /**
     * Method used in the SettingsFragment.java file to get the array of all ISO 3166 countries display names without the language such as "(English)".
     *
     * @return this.allIsoCountries
     */
    public String[] getAllCountries() {
        String[] allCountriesNames = new String[this.allIsoCountries.length];
        for (int i = 0; i < this.allIsoCountries.length; i++) {
            Locale locale = new Locale("en", this.allIsoCountries[i]);
            allCountriesNames[i] = locale.getDisplayName().replaceAll("(English)", "").trim();
        }
        return allCountriesNames;
    }

    /**
     * method used in CurrencyConversionClass.java to set the format of currency symbol to a cost.
     * Converts a cost amount to the desired currency format given the country name.
     * Since the locale uses language "en" (english) the names of the countries must be in the format "English + countryName
     * For example, for India the parameters countryName will be " English (India)".
     *
     * @param countryName
     * @return
     */
    public String setCurrencySymbolAndFormat(String countryName, String amount) throws InterruptedException {

        /*
         * a try-catch to catch a Null pointer exception in getting the conversion rate for a country which is not possible by locale.
         * unsupported currency rates are for countries which do not have a currency rate. Such as Antartica.
         */
        try {
            //get the country ISO from the country ISO hashmap.
            String countryISO = countriesMap.get(countryName);


            //creating a locale object in english with the country 3166 ISO code retrieved from the hashmap.
            Locale locale = new Locale("en", countryISO);

            String currencyCode = Currency.getInstance(locale).getCurrencyCode();

            Currency currencyType = Currency.getInstance(currencyCode);

            this.currencySymbol = currencyType.getSymbol();

            //if the currency is not changed no need to convert from CAD to the same currency again. Otherwise, if it has been changed then convert the currency.
            this.isCurrencySameCountry = countryName.equals(this.countryName);
            this.countryName = countryName; // change the instance variable value of country name to the current one.

            amount = amount.replaceAll("[^\\d.]+", "").trim();

            double finalAmount = Double.parseDouble(amount);

            if (currencyCode.equalsIgnoreCase("CAD")) {
                numTimesCAD++;
            } else {
                numTimesCAD = 0;
            }

            // if the currency has not been changed then execute the conversions.
            if (!this.isCurrencySameCountry) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        //NumberFormat countryCurrency = NumberFormat.getCurrencyInstance(locale);
                        convertCurrency(currencyCode, countryName, finalAmount);
                    }
                });
                /*
                 * start the thread.
                 */
                thread.start();

                //sleep 1 second to allow thread to finish.
                TimeUnit.SECONDS.sleep(2);

                /*
                 * wait for the thread to finish. The thread finishes after the asynchronous call to yahoo finance is completed.
                 */
                thread.join();

            } else if (isCurrencySameCountry) {
                //since the currency is not changed no need to retrieve the currency and re-parsing all of the HTML from yahoo finance again.
                this.formattedCurrencyValue = String.valueOf(finalAmount * conversionRate);
            }
        }
        catch(NullPointerException e){
            this.formattedCurrencyValue = "currency not supported";
        }
        return this.formattedCurrencyValue;
    }


    /**
     * method which returns the conversion rate.
     */
    public double getConversionRate(){
        return this.conversionRate;
    }


    /**
     * method for converting from CAD to another currency dollars.
     *
     * @return
     */
    public void convertCurrency(String currencyType, String countryDisplayName, double amount) {

        System.out.println(countryDisplayName + " countryDisplayName");
        if(currencyType.equals("CAD")){
            resetCurrencyConversionBackToCad(amount);
        }
        else {
            //HTTP client request to yahoo finance.
            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://ca.finance.yahoo.com/quote/CAD" + currencyType + "%3DX?p=CAD" + currencyType + "%3DX")
                    .build();

            //HTTP call to yahoo finance. Asynchronous call  needed because SettingsFragment.java class does update user interfaces.
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                    //if the HTTP call response is successful then the data-reactid="57" value which corresponds to the currency conversion rate can be extracted.
                    if (response.isSuccessful()) {
                        String responseHTML = response.body().string();
                        //System.out.println(responseHTML);
                        //The JSoup document parses the response HTML from the HTTP request.
                        Document conversionDoc = Jsoup.parse(responseHTML);
                        //System.out.println("\n\n\n\n" + conversionDoc);

                        String conversionStr = conversionDoc.getElementsByAttributeValueStarting("data-reactid", "\"57\"").first().text();

                        //replace any commas in the conversion string with a empty string. Also, remove any leading or trailing white spaces.
                        conversionStr = conversionStr.replaceAll(",", "").trim();
                        System.out.println("conversionStr " + conversionStr);
                        //the conversion rate to be applied based on currency.
                        conversionRate = Double.parseDouble(conversionStr);
                        System.out.println("conversionRate " + conversionRate);
                        System.out.println("yahoo finance call was executed successfully");

                        formattedCurrencyValue = String.valueOf(conversionRate * amount);
                        System.out.println("formatted currency value: " + formattedCurrencyValue);
                    }
                    //set the country name to the parameters country name.
                    countryName = countryDisplayName;

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("FAILURE DETECTED FROM onFaliure(): " + call);
                }

            });
        }
    }

    /**
     * This method is used by other classes to get the currency symbol of the current currency.
     */
    public String getCurrencySymbol(){
        return this.currencySymbol;
    }

    /**
     * This method will reset the currency conversion to get back to the conversion rate to convert to CAD. This is done by taking the reciprocal of the current conversion rate.
     * This method will also set the conversion symbol to "$" to represent CAD currency.
     * If the current conversion rate is 0 this method will do nothing.
     */
    public void resetCurrencyConversionBackToCad(double amount) {
        this.currencySymbol = "$";
        if(this.conversionRate != 0.0 && numTimesCAD == 1) {
            this.conversionRate = Math.pow(this.conversionRate, -1); //the conversion rate is the reciprocial of the current conversion rate.
        }
        this.formattedCurrencyValue = String.valueOf(this.conversionRate * amount);
    }

    /**
     * The toString() method of the CurrencyConversionClass.java file.
     *
     * @return this.formattedCurrencyValue
     **/
    @Override
    public String toString() {
        return this.formattedCurrencyValue;
    }


}

