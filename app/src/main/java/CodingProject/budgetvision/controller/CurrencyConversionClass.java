package CodingProject.budgetvision.controller;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import java.util.Currency;
import java.util.Locale;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

@Singleton
public class CurrencyConversionClass {

    /**
     * HTTP call is made through OkHttpClient and then JSoup is used to HTML parse conversion rates from Bank Of Canada.
     * This class will take the daily budget, total income etc. and convert from CAD to desired currency.
     * By default the currency of BudgetVision will be CAD (Canadian Dollars).
     * This class will use previous web scraping to get the conversion rates for CAD to another currency. Also, currencies are formatted.
     */

    private final OkHttpClient client;
    private double afterConversionValue;
    private String countryName;
    private double conversionRate;
    private String[] top25TradedIsoCountries;
    private boolean currencyNotAvailable; //boolean instance variable to store True or False depending on if the currency from Bank of Canada is available.
    private String currencySymbol;

    private HashMap<String, String> countriesMap;  //hashmap for constant lookup types where the key is the country display name.

    private boolean isCurrencySameCountry; //boolean variable to store if the currency has been changed.

    /**
     * Constructor for the currency conversion class. When an instance of Currency Conversion Class is created then the map of all ISO countries will be intiialized.
     * @Inject is used for Dagger to instantiate this currency conversion class as an object.
     */
    @Inject
    public CurrencyConversionClass() {
        initCountriesMap();
        client = new OkHttpClient();
    }

    /**
     * method for initializing the countries map.
     */
    public void initCountriesMap() {
        //String array with all two letter ISO 3166 countries of the 26 currencies.
        this.top25TradedIsoCountries = new String[26];

        //initialize the countriesMap.
        countriesMap = new HashMap<>();

        //Key is the currency. Value is the 2 letter ISO 3166 code.
        countriesMap.put("Australian dollar", "AU");
        countriesMap.put("Brazilian real", "BR");
        countriesMap.put("Chinese renminbi", "CN");
        countriesMap.put("European euro", "EU");
        countriesMap.put("Hong Kong dollar", "HK");
        countriesMap.put("Indian rupee", "IN");
        countriesMap.put("Indonesian rupiah", "ID");
        countriesMap.put("Japanese yen", "JP");
        countriesMap.put("Malaysian ringgit", "MY");
        countriesMap.put("New Zealand dollar", "NZ");
        countriesMap.put("Mexican peso", "MX");
        countriesMap.put("Norwegian krone", "NO");
        countriesMap.put("Russian ruble", "RU");
        countriesMap.put("Peruvian new sol", "PE");
        countriesMap.put("Vietnamese dong", "VN");
        countriesMap.put("United States", "US");
        countriesMap.put("Turkish lira", "TR");
        countriesMap.put("UK pound sterling", "GB");
        countriesMap.put("Thai baht", "TH");
        countriesMap.put("Swiss franc", "CH");
        countriesMap.put("Taiwanese dollar", "TW");
        countriesMap.put("Swedish krona", "SE");
        countriesMap.put("South Korean won", "KR");
        countriesMap.put("Saudi riyal", "SA");
        countriesMap.put("Singapore dollar", "SG");
        countriesMap.put("South African rand", "ZA");

        int iter = 0;
        for(String currency: countriesMap.keySet()){
            this.top25TradedIsoCountries[iter++] = currency;
        }

    }

    /**
     * Method used in the SettingsFragment.java file to get the array of all ISO 3166 countries display names without the language such as "(English)".
     *
     * @return this.allIsoCountries
     */
    public String[] getAllCountries() {
        return  this.top25TradedIsoCountries;
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
    public void setCurrencySymbolAndFormat(String countryName, String amount) throws InterruptedException {

        /*
         * a try-catch to catch a Null pointer exception in getting the conversion rate for a country which is not possible by locale.
         * unsupported currency rates are for countries which do not have a currency rate. Such as Antartica.
         */
        //get the country ISO from the country ISO hashmap.
        String countryISO = countriesMap.get(countryName);

        //if the country is canada since it is default currency it was not added to the countriesMap. So, if it is canada, country ISO will be CA.
        if(countryName.equalsIgnoreCase("Canada")){
            countryISO = "CA";
            countryName = "Canada";
        }

        //if the currency is not changed no need to convert from CAD to the same currency again. Otherwise, if it has been changed then convert the currency.
        this.isCurrencySameCountry = countryName.equals(this.countryName);
        this.countryName = countryName; // change the instance variable value of country name to the current one.

        amount = amount.replaceAll("[^\\d.]+", "").trim();

        double finalAmount = Double.parseDouble(amount);

        //if the currency is not available then the conversion rate is 1.
        if(currencyNotAvailable){
            this.conversionRate = 1;

        }

        // if the currency has been changed then execute the conversions.
        if (!this.isCurrencySameCountry) {
            String finalCountryName = countryName;
            String finalCountryISO = countryISO;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    convertCurrency(finalCountryISO, finalCountryName, finalAmount);
                }
            });
            /*
             * start the thread.
             */
            thread.start();

            //sleep 3 second to allow thread to finish.
            TimeUnit.SECONDS.sleep(3);

            /*
             * wait for the thread to finish. The thread finishes after the asynchronous call to Bank Of Canada is completed.
             */
            thread.join();

        } else if (isCurrencySameCountry) {
            //since the currency is not changed no need to retrieve the currency and re-parsing all of the HTML from Bank Of Canada again.
            this.afterConversionValue = finalAmount * conversionRate;
        }

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
    public void convertCurrency(String countryISO, String countryDisplayName, double amount) {

        if(countryISO.equals("CA")){
            resetCurrencyConversionBackToCad(amount);
        }
        else {

            //creating a locale object in english with the country 3166 ISO code retrieved from the hashmap.
            Locale locale = new Locale("en", countryISO);
            String currencyCode = Currency.getInstance(locale).getCurrencyCode();
            Currency currencyType = Currency.getInstance(currencyCode);

            //HTTP client request to Bank Of Canada.
            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://www.bankofcanada.ca/rates/exchange/daily-exchange-rates-lookup/?series%5B%5D=FX" + currencyType + "CAD&lookupPage=lookup_daily_exchange_rates_2017.php&startRange=2011-11-04&rangeType=range&rangeValue=1.w&dFrom=&dTo=&submit_button=Submit")
                    .build();

            //HTTP call to Bank Of Canada. Asynchronous call  needed because SettingsFragment.java class updates user interfaces.
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                    //if the HTTP call response is successful then the data-reactid="57" value which corresponds to the currency conversion rate can be extracted.
                    if (response.isSuccessful()) {
                            String responseHTML = response.body().string();
                            //The JSoup document parses the response HTML from the HTTP request.
                            Document conversionDoc = Jsoup.parse(responseHTML);

                            String conversionStr;
                            try {
                                conversionStr = conversionDoc.getElementsByClass("bocss-table__tr").text();
                                conversionStr = conversionStr.substring(conversionStr.indexOf("Average"), conversionStr.indexOf(" High"));
                                conversionStr = conversionStr.substring(conversionStr.lastIndexOf("[") + 1, conversionStr.lastIndexOf(" "));
                                conversionStr = conversionStr.replaceAll(",", ""); //replace all commas.

                                //the conversion rate to be applied based on currency.
                                conversionRate = Double.parseDouble(conversionStr);
                                afterConversionValue = conversionRate * amount;
                                currencySymbol = currencyType.getSymbol();

                                currencyNotAvailable = false;
                            }
                            catch(Exception e){
                                currencyNotAvailable = true;
                            }

                        //set the country name to the parameters country name.
                        countryName = countryDisplayName;
                    }

                }// end of onResponse method.

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }// end of onFaliure method.

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
     */
    public void resetCurrencyConversionBackToCad(double amount) {
        this.currencySymbol = "$";
        if(!currencyNotAvailable) {
            this.conversionRate = Math.pow(this.conversionRate, -1); //the conversion rate is the reciprocial of the current conversion rate.
            this.afterConversionValue = this.conversionRate * amount;
        }
        else {
            this.afterConversionValue = amount;
        }
    }

    /**
     * The toString() method of the CurrencyConversionClass.java file.
     *
     * @return this.formattedCurrencyValue
     **/
    @Override
    public String toString() {
        return this.afterConversionValue + "";
    }


    /**
     * Set the current conversion rate to the parameter conversion rate.
     * @param currencyRate
     */
    public void setConversionRate(double currencyRate) {
        this.conversionRate = currencyRate;
    }

}

