package CodingProject.budgetvision;

import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.*;


import CodingProject.budgetvision.model.*; /* import everything from model package */

public class BudgetVisionTests {

    @Test
    public void test_01() {
        /*
         * Testing the addExpenses method in class Categories.
         */
        CategoriesClass Food1 = new CategoriesClass("Food");
        Food1.addSubCategory("Food","Groceries", 152.42);
        assertEquals(252.42, Food1.addExpense(100),0.01); //adding one expense to Food1.
        assertEquals(452.42, Food1.addExpense(200),0.01); //adding second expense to Food1.
        CategoriesClass Transportation1 = new CategoriesClass("Commute");
        Transportation1.addSubCategory("Food","Groceries", 52.38);
        assertEquals(79.61, Transportation1.addExpense(27.23),0.01); //adding one expense to Food2.
        assertEquals(99.61, Transportation1.addExpense(20),0.01); //adding second expense to Food2.

    }

    /*
    Testing the getFood, addExpenses, and getFoodCosts method in class Categories.
     */
    @Test
    public void test_02A(){
        CategoriesClass Food = new CategoriesClass("Food");
        Food.addSubCategory("Food","Groceries",253.47);
        Food.addSubCategory("Food","Candy",27.67);
        Food.addSubCategory("Food","Restaurants",161.56);
        String [] expectedNames = {"Groceries", "Candy", "Restaurants"}; //expected food subcategories names.
        String [] expectedCosts1 = {"253.47", "27.67", "161.56"}; //expected food subcategories costs.
        assertArrayEquals(expectedNames, Food.getFood());
        assertArrayEquals(expectedCosts1, Food.getFoodCosts());

        /*
         * Testing the overloaded addExpenses method in class Categories.
         */
        //adding $123.23 to groceries subCategory in Food Category.
        Food.addExpense(123.23, "Groceries","Food");
        String [] expectedCosts2 = {"376.70", "27.67", "161.56"}; //expected food subcategories costs.
        assertArrayEquals(expectedCosts2, Food.getFoodCosts());

        //adding $183.29 to candy subCategory in Food Category.
        Food.addExpense(183.29, "Candy","Food");
        String [] expectedCosts3 = {"376.70", "210.96", "161.56"}; //expected food subcategories costs.
        assertArrayEquals(expectedCosts3, Food.getFoodCosts());

        //adding $100.00 to restaurants in Food Category.
        Food.addExpense(100.00, "Restaurants","Food");
        String [] expectedCosts4 = {"376.70", "210.96", "261.56"}; //expected food subcategories costs.
        assertArrayEquals(expectedCosts4, Food.getFoodCosts());
    }

    /*
    Testing the getHousing and getHousingCosts method in class Categories.
    */
    @Test
    public void test_02B(){
        CategoriesClass Housing = new CategoriesClass("Housing");
        Housing.addSubCategory("Housing","Rent", 1900);
        Housing.addSubCategory("Housing","Internet", 50);
        Housing.addSubCategory("Housing","TV", 64.41);
        Housing.addSubCategory("Housing","Electricity", 130.32);
        String [] expectedNames = {"Rent", "Internet", "TV", "Electricity"}; //expected housing subcategories names.
        String [] expectedCosts = {"1900.00", "50.00", "64.41", "130.32"}; // expected housing subcategories costs.
        assertArrayEquals(expectedNames, Housing.getHousing());
        assertArrayEquals(expectedCosts, Housing.getHousingCosts());

    }
    /*
    Testing the getEntertainment and getEntertainmentCosts method in class Categories.
     */
    @Test
    public void test_2C(){
        CategoriesClass Recreation = new CategoriesClass("Recreation");
        Recreation.addSubCategory("Recreation","Cinema", 41.36);
        Recreation.addSubCategory("Recreation","Bowling", 20.42);
        Recreation.addSubCategory("Recreation","Gym", 50.42);
        Recreation.addSubCategory("Recreation","Sports", 60.42);
        Recreation.addSubCategory("Recreation","Vacation Costs", 50.42);
        String [] expectedNames = {"Cinema", "Bowling", "Gym", "Sports", "Vacation Costs"}; //expected entertainment subcategories names.
        String [] expectedCosts = {"41.36", "20.42", "50.42", "60.42", "50.42"}; // expected entertainment subcategories costs.
        assertArrayEquals(expectedNames,Recreation.getRecreation());
        assertArrayEquals(expectedCosts,Recreation.getRecreationCosts());
    }

    /**
    Testing the removeSubcategory and addSubcategory method in class Categories for all subcategories.
     */
    @Test
    public void test_03(){
        CategoriesClass Food = new CategoriesClass("Food");
        String[] empty = {};
        assertArrayEquals(empty, Food.getFood());
        Food.addSubCategory("Food","Groceries", 80.79); //#food subcategories : 1
        Food.addSubCategory("Food","Candies", 50) ; //#food subcategories : 2
        Food.addSubCategory("Food","Restaurants", 64.41); //#food subcategories : 3
        Food.addSubCategory("Food","Bakeries", 130.32); //#food subcategories : 4
        /*
         * Now removing entire food subcategory items.
         */
        //expected subcategory names and costs after adding them.
        String [] expectedNames1 = {"Groceries","Candies","Restaurants", "Bakeries"};
        String [] expectedCosts1 = {"80.79","50.00","64.41","130.32"};
        assertArrayEquals(expectedNames1,Food.getFood()); //#food subcategories : 4
        assertArrayEquals(expectedCosts1,Food.getFoodCosts()); //#food costs subcategories : 4

        //remove entire groceries subcategory.
        String [] expectedNames2 = {"Candies","Restaurants", "Bakeries"};
        String [] expectedCosts2 = {"50.00","64.41","130.32"};
        Food.removeSubcategory("Food", "Groceries"); //remove groceries from food category.
        assertArrayEquals(expectedNames2,Food.getFood()); //#food subcategories : 3
        assertArrayEquals(expectedCosts2,Food.getFoodCosts()); //#food costs subcategories : 3

        //remove entire candies subcategory.
        String [] expectedNames3 = {"Restaurants", "Bakeries"};
        String [] expectedCosts3 = {"64.41","130.32"};
        Food.removeSubcategory("Food","Candies"); //remove candies from food category.
        assertArrayEquals(expectedNames3,Food.getFood());//#food subcategories : 2
        assertArrayEquals(expectedCosts3,Food.getFoodCosts()); //#food costs subcategories : 2

        //remove entire restaurants subcategory.
        String [] expectedNames4 = {"Bakeries"};
        String [] expectedCosts4 = {"130.32"};
        Food.removeSubcategory("Food","Restaurants"); //remove restaurants from food category.
        assertArrayEquals(expectedNames4,Food.getFood()); //#food subcategories : 1
        assertArrayEquals(expectedCosts4,Food.getFoodCosts()); //#food costs subcategories : 1

        //remove entire bakeries subcategory.
        String [] expectedNames5 = {}; //#food subcategories : 0
        String [] expectedCosts5 = {};
        Food.removeSubcategory("Food","Bakeries"); //remove Bakeries from food category.
        assertArrayEquals(expectedNames5,Food.getFood()); //#food subcategories : 0
        assertArrayEquals(expectedCosts5,Food.getFoodCosts()); //#food subcategories : 0

        /*
         * Now adding back food subcategory items up until maximum number of food categories.
         * MAX_NOF = 10.
         */
        //add groceries subcategory.
        Food.addSubCategory("Food","Groceries", 60.49);
        String [] expectedNames6 = {"Groceries"};
        String [] expectedCosts6 = {"60.49"};
        assertArrayEquals(expectedNames6,Food.getFood()); //#food subcategories : 1
        assertArrayEquals(expectedCosts6,Food.getFoodCosts()); //#food costs subcategories : 1

        //add bakeries subcategory.
        Food.addSubCategory("Food","Bakeries", 26.57);
        String [] expectedNames7 = {"Groceries","Bakeries"};
        String [] expectedCosts7 = {"60.49", "26.57"};
        assertArrayEquals(expectedNames7,Food.getFood()); //#food subcategories : 2
        assertArrayEquals(expectedCosts7, Food.getFoodCosts()); //#food costs subcategories : 2

        //add seafood subcategory.
        Food.addSubCategory("Food","Seafood", 126.57);
        String [] expectedNames8 = {"Groceries","Bakeries","Seafood"};
        String [] expectedCosts8 = {"60.49", "26.57", "126.57"};
        assertArrayEquals(expectedNames8,Food.getFood()); //#food subcategories : 3
        assertArrayEquals(expectedCosts8, Food.getFoodCosts()); //#food costs subcategories : 3

        //add candies subcategory.
        Food.addSubCategory("Food","Candies", 56.27);
        String [] expectedNames9 = {"Groceries","Bakeries","Seafood","Candies"};
        String [] expectedCosts9 = {"60.49", "26.57", "126.57", "56.27"};
        assertArrayEquals(expectedNames9,Food.getFood()); //#food subcategories : 4
        assertArrayEquals(expectedCosts9, Food.getFoodCosts()); //#food costs subcategories : 4

        //add restaurants subcategory.
        Food.addSubCategory("Food","Restaurants", 53.25);
        String [] expectedNames10 = {"Groceries","Bakeries","Seafood","Candies","Restaurants"};
        String [] expectedCosts10 = {"60.49", "26.57", "126.57", "56.27", "53.25"};
        assertArrayEquals(expectedNames10,Food.getFood()); //#food subcategories : 5
        assertArrayEquals(expectedCosts10, Food.getFoodCosts()); //#food costs subcategories : 5

        //add pastries subcategory.
        Food.addSubCategory("Food","Pastries", 33.52);
        String [] expectedNames11 = {"Groceries","Bakeries","Seafood","Candies","Restaurants","Pastries"};
        String [] expectedCosts11 = {"60.49", "26.57", "126.57", "56.27", "53.25", "33.52"};
        assertArrayEquals(expectedNames11,Food.getFood()); //#food subcategories : 6
        assertArrayEquals(expectedCosts11, Food.getFoodCosts()); //#food costs subcategories : 6

        //add sushi subcategory.
        Food.addSubCategory("Food","Sushi", 21.24);
        String [] expectedNames12 = {"Groceries","Bakeries","Seafood","Candies","Restaurants","Pastries","Sushi"};
        String [] expectedCosts12 = {"60.49", "26.57", "126.57", "56.27", "53.25", "33.52", "21.24"};
        assertArrayEquals(expectedNames12,Food.getFood()); //#food subcategories : 7
        assertArrayEquals(expectedCosts12, Food.getFoodCosts()); //#food costs subcategories : 7

        //add cake & deserts subcategory.
        Food.addSubCategory("Food","Cake & Deserts", 32.34);
        String [] expectedNames13 = {"Groceries","Bakeries","Seafood","Candies","Restaurants","Pastries","Sushi","Cake & Deserts"};
        String [] expectedCosts13 = {"60.49", "26.57", "126.57", "56.27", "53.25", "33.52", "21.24","32.34"};
        assertArrayEquals(expectedNames13,Food.getFood()); //#food subcategories : 8
        assertArrayEquals(expectedCosts13, Food.getFoodCosts()); //#food costs subcategories : 8

        //add drinks & beverages subcategory.
        Food.addSubCategory("Food","Drinks & Beverages", 61.52);
        String [] expectedNames14 = {"Groceries","Bakeries","Seafood","Candies","Restaurants","Pastries","Sushi","Cake & Deserts","Drinks & Beverages"};
        String [] expectedCosts14 = {"60.49", "26.57", "126.57", "56.27", "53.25", "33.52", "21.24","32.34","61.52"};
        assertArrayEquals(expectedNames14,Food.getFood()); //#food subcategories : 9
        assertArrayEquals(expectedCosts14,Food.getFoodCosts()); //#food costs subcategories : 9

        //add the milk subcategory.
        Food.addSubCategory("Food","Milk", 10.24);
        String [] expectedNames15 = {"Groceries","Bakeries","Seafood","Candies","Restaurants","Pastries","Sushi","Cake & Deserts","Drinks & Beverages","Milk"};
        String [] expectedCosts15 = {"60.49", "26.57", "126.57", "56.27", "53.25", "33.52", "21.24","32.34","61.52","10.24"};
        assertArrayEquals(expectedNames15,Food.getFood()); //#food subcategories : 10
        assertArrayEquals(expectedCosts15,Food.getFoodCosts()); //#food costs subcategories : 10

        /*
         * Now getting the status of all the subcategories.
         */
        String expectedStatus15 = "Food: {Groceries $60.49, Bakeries $26.57, Seafood $126.57, Candies $56.27, Restaurants $53.25, Pastries $33.52, Sushi $21.24, Cake & Deserts $32.34, Drinks & Beverages $61.52, Milk $10.24}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus15,Food.getStatus());
        assertArrayEquals(expectedNames15,Food.getFood());

        /*
         * Adding any more food items after the limit will cause an error.
         * this.NOF > MAX_NOF -> ERROR
         */
        for(int i = 0; i < 500 ; i ++) {
            Food.addSubCategory("Food", "Meats and Poultry" + i, 51.99);

        }
        Food.addSubCategory("Food","GG",9999);
        Food.addSubCategory("Food","GGG",9999);
        String expectedError1  = "Error: Maximum Number of Food Subcategories Reached !";
        String [] expectedError2  = {"Error: Maximum Number of Food Subcategories Reached !"};
        //assertEquals(expectedError1);  //adding food subcategories when there is an error present should return the error.
        //assertArrayEquals(expectedError2,Food.addSubCategory()); //adding food subcategories when there is an error present should return the error.



    }

    /**
     * Testing adding and removing a specific transportation subcategories.
     */
    @Test
    public void test_4(){
        CategoriesClass Commute = new CategoriesClass("Commute");

        //adding transportation subcategories.
        Commute.addSubCategory("Commute","Uber",92.43);
        Commute.addSubCategory("Commute","Taxi", 45.19);
        Commute.addSubCategory("Commute","Plane Tickets",831.32);

        //expected Commute costs.
        String [] expectedCosts1 = {"92.43","45.19","831.32"};
        assertArrayEquals(expectedCosts1,Commute.getCommuteCosts());

        //removing the entire Uber subcategory.
        Commute.removeSubcategory("Commute", "Uber");
        String [] expectedNames1 = {"Taxi", "Plane Tickets"};
        String [] expectedCosts2 =  {"45.19", "831.32"};
        assertArrayEquals(expectedNames1, Commute.getCommute());
        assertArrayEquals(expectedCosts2,Commute.getCommuteCosts());

        //removing the entire Plane Tickets subcategory.
        Commute.removeSubcategory("Commute","Plane Tickets");
        String [] expectedNames2 = {"Taxi"};
        String [] expectedCosts3 = {"45.19"};
        assertArrayEquals(expectedNames2, Commute.getCommute());
        assertArrayEquals(expectedCosts3, Commute.getCommuteCosts());

        //removing the entire Taxi subcategory.
        Commute.removeSubcategory("Commute","Taxi");
        String [] expectedNames3 = {};
        String [] expectedCosts4 = {};
        assertArrayEquals(expectedNames3, Commute.getCommute());
        assertArrayEquals(expectedCosts4, Commute.getCommuteCosts());

        //removing the subcategory after all subcategories are removed results in an error.
//        Transportation.removeSubcategory("Train");
//        String expectedError1 = "Train does not exist !";
//        assertEquals(expectedError1,Transportation.getStatus());
//
//        Transportation.removeSubcategory("Subway");
//        String expectedError2 = "Subway does not exist !";
//        assertEquals(expectedError2,Transportation.getStatus());
//
//        Transportation.removeSubcategory("Car");
//        String expectedError3 = "Car does not exist !";
//        assertEquals(expectedError3,Transportation.getStatus());
//
//        Transportation.removeSubcategory("Bus");
//        String expectedError4 = "Bus does not exist !";
//        assertEquals(expectedError4,Transportation.getStatus());

        //adding the transportation subcategories again.
        //adding the uber subcategory.
        Commute.addSubCategory("Commute","Uber",92.43);
        String expectedStatus1A = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $92.43}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus1A,Commute.getStatus());

        //add another $47.27 for uber expense.
        Commute.addExpense(47.27,"Uber", "Commute");
        String expectedStatus1B = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $139.70}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus1B,Commute.getStatus());

        //add another $83.48 for uber expense.
        Commute.addExpense(83.48,"Uber", "Commute");
        String expectedStatus1C = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $223.18}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus1C,Commute.getStatus());

        Commute.addSubCategory("Commute", "Taxi", 45.19);
        String expectedStatus2 = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $223.18, Taxi $45.19}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus2,Commute.getStatus());

        Commute.addSubCategory("Commute","Plane Tickets",831.32);
        String expectedStatus3 = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $223.18, Taxi $45.19, Plane Tickets $831.32}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus3,Commute.getStatus());

        //add another $68.29 expense to taxi.
        Commute.addExpense(68.29,"Taxi", "Commute");
        String expectedStatus4 = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $223.18, Taxi $113.48, Plane Tickets $831.32}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus4,Commute.getStatus());

//        //adding a duplicate subcategory results in error message when getting the status.
//        String expectedError = "Error: Taxi Subcategory Already Created !";
//        Transportation.addSubCategory("Taxi",100.28);
//        assertEquals(expectedError,Transportation.getStatus());

        //adding a new subcategory
        Commute.addSubCategory("Commute","Subway", 34.39);
        String expectedStatus5 = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {Uber $223.18, Taxi $113.48, Plane Tickets $831.32, Subway $34.39}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus5, Commute.getStatus());

    }

    /**
     * Testing the userImmediateStatus method in class UsersBudget and the quick sort algorithm to organize subcategories by expense.
     * This method tests "Ascending" sorting by expense.
     */
    @Test
    public void test_5(){
        UsersBudgetClass Nilushanth = new UsersBudgetClass();
        Nilushanth.addUserCategory("Recreation"); //entertainment category is used to store the subcategories.
        Nilushanth.addUserSubcategory("Netflix Subscription", 20.48);
        //Nilushanth.addUserSubcategory("Netflix Subscription", 9.79);
        //assertEquals("Error: Netflix Subscription Subcategory Already Created !",Nilushanth.userImmediateStatus());

        Nilushanth.addUserSubcategory("TV", 120.24);
        Nilushanth.addUserSubcategory("Hulu", 49.37);
        Nilushanth.addUserSubcategory("Video games", 52.57);

        String expectedStatus = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, Hulu $49.37, Video games $52.57, TV $120.24}";
        Nilushanth.sortAllSubcategoriesByExpense("Ascending","Recreation"); //sorting entertainment category in ascending order.
        assertEquals(expectedStatus,Nilushanth.getUserStatus());

        Nilushanth.addUserCategory("Food"); //food category is used to store the subcategories.
        Nilushanth.addUserSubcategory("Groceries", 120.48);
        Nilushanth.addUserSubcategory("Pastries", 42.51);
        Nilushanth.addUserSubcategory("Fast food", 123.32);
        Nilushanth.addUserSubcategory("Sea food", 31.43);

        Nilushanth.sortAllSubcategoriesByExpense("Ascending","Food"); //sorting food category in ascending order.

        String expectedStatus2 = "Food: {Sea food $31.43, Pastries $42.51, Groceries $120.48, Fast food $123.32}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, Hulu $49.37, Video games $52.57, TV $120.24}";
        assertEquals(expectedStatus2,Nilushanth.getUserStatus());

        Nilushanth.addUserCategory("Lifestyle"); //food category is used to store the subcategories.
        Nilushanth.addUserSubcategory("Dentist", 1532.43);
        Nilushanth.addUserSubcategory("Gym", 50.48);
        Nilushanth.addUserSubcategory("Eye doctor", 70.51);
        Nilushanth.addUserSubcategory("Doctor", 213.34);

        Nilushanth.sortAllSubcategoriesByExpense("Ascending","Lifestyle"); //sorting all the categories again in ascending order.

        String expectedStatus3 = "Food: {Sea food $31.43, Pastries $42.51, Groceries $120.48, Fast food $123.32}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {Gym $50.48, Eye doctor $70.51, Doctor $213.34, Dentist $1532.43}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, Hulu $49.37, Video games $52.57, TV $120.24}";
        assertEquals(expectedStatus3,Nilushanth.getUserStatus());

    }

    /**
     * Testing adding new expenses in user budget and getting the status.
     */
    @Test
    public void test_6(){
        UsersBudgetClass Nilushanth = new UsersBudgetClass();
        Nilushanth.addUserCategory("Recreation");
        Nilushanth.addUserSubcategory("Netflix Subscription", 20.48);
        Nilushanth.addUserSubcategory("TV", 120.24);
        Nilushanth.addUserSubcategory("Hulu", 49.37);
        Nilushanth.addUserSubcategory("Video games", 52.57);
        String expectedStatus = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, TV $120.24, Hulu $49.37, Video games $52.57}";
        assertEquals(expectedStatus,Nilushanth.getUserStatus());

        Nilushanth.addUserSubcategory("Video games", 52.57);
        Nilushanth.addUserSubcategory("Video games", 52.57);
        Nilushanth.addUserSubcategory("Video games", 52.57);

        Nilushanth.addUserSubcategory("PS4 card", 49.37);

        String expectedStatus2 = "Food: {}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, TV $120.24, Hulu $49.37, Video games $52.57, PS4 card $49.37}";
        assertEquals(expectedStatus2,Nilushanth.getUserStatus());

        Nilushanth.addUserCategory("Food");
        Nilushanth.addUserSubcategory("Groceries", 249.37);
        String expectedStatus3 = "Food: {Groceries $249.37}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, TV $120.24, Hulu $49.37, Video games $52.57, PS4 card $49.37}";
        assertEquals(expectedStatus3,Nilushanth.getUserStatus());

        //adding duplicate subcategories.
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);

        String expectedStatus4 = "Food: {Groceries $249.37, Pet food $50.00}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {Netflix Subscription $20.48, TV $120.24, Hulu $49.37, Video games $52.57, PS4 card $49.37}";
        assertEquals(expectedStatus4,Nilushanth.getUserStatus());

    }

    /**
     * Testing adding the subcategories expenses, removing the subcategories expenses and verifying the expected status.
     */
    @Test
    public void test_7(){
        UsersBudgetClass Nilushanth = new UsersBudgetClass();
        /*
         * User Nilushanth adds groceries ($236 initial + $100) and lunch ($250 initial) to food category.
         */
        Nilushanth.addUserCategory("Food"); //Nilushanth initially adds food category.
        Nilushanth.addUserSubcategory("Groceries", 236); //Nilushanth adds groceries subcategory
        Nilushanth.addUserAdditionalExpense("Food","Groceries", 100); //Nilushanth adds $100 initial expense to the groceries subcategory.
        Nilushanth.addUserSubcategory("Lunch", 250);


        /*
         * Nilushanth decides to try and crash the app. He proceeds to remove subcategories which do not exist in food category.
         */
        Nilushanth.removeUserSubcategory("Food","pancakes");
        Nilushanth.removeUserSubcategory("Food","waffles");
        Nilushanth.removeUserSubcategory("Food","bread");
        /*

         */

        /*
         * Nilushanth decides to crash the entire app again. He proceeds to remove subcategories which do not exist in other categories.
         */
        Nilushanth.removeUserSubcategory("Commute","bus");
        Nilushanth.removeUserSubcategory("Lifestyle","gym");
        Nilushanth.removeUserSubcategory("Entertainment","tv");
        Nilushanth.removeUserSubcategory("Housing","rent");
        Nilushanth.removeUserSubcategory("Commute","car");


        /*
         * Nilushanth decides to crash the entire app again. He proceeds to add subcategories which do not exist for food category.
         */
        Nilushanth.addUserAdditionalExpense("Food","gym training", 323);
        Nilushanth.addUserAdditionalExpense("Food","yoga", 145);
        Nilushanth.addUserAdditionalExpense("Food","aaaaaah", 239023);
        

        /*
         * Nilushanth decides to stop breaking the application and decides to remove lunch subcategory entirely.
         */
        Nilushanth.removeUserSubcategory("Food","Lunch"); //Nilushanth removes lunch subcategory entirely.
        String expectedStatus = "Food: {Groceries $336.00}"+
                "\n\nHousing: {}"+
                "\n\nLifestyle: {}"+
                "\n\nCommute: {}"+
                "\n\nRecreation: {}";
        assertEquals(expectedStatus,Nilushanth.getUserStatus());
    }

}