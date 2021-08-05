package CodingProject.budgetvision.model;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyConversion {

    /*
     * This class will take the daily budget, total income etc. and convert from CAD to desired currency.
     * By default the currency will be CAD.
     * This class will format the currencies.
     * This class will use previous web scraping to get the conversion rates for CAD to another currency.
     *
     */

    CategoriesClass category;

    CategoriesClass subcategory;
    String USD; //USA dollar.


    /*
     * method for converting from CAD to USA dollars
     */
    public void cadToUsd(double CAD, double USD){
        this.USD = NumberFormat.getCurrencyInstance().format(Locale.US);
    }

}
