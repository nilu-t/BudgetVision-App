package CodingProject.budgetvision;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import CodingProject.budgetvision.controller.*; /* import everything from model package */

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class BudgetVisionTests {

    /**
     * Testing adding the subcategories expenses, removing the subcategories expenses and verifying the expected status.
     */
    @Test
    public void test_0(){

        UsersBudgetClass Nilushanth = new UsersBudgetClass(new CategoriesClass(), new CurrencyConversionClass());

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
        String expectedStatus = "Food:\n" +
                "Groceries            $ 336.00\n" +
                "\n" +
                "\n" +
                "Housing:\n" +
                "\n" +
                "\n" +
                "Lifestyle:\n" +
                "\n" +
                "\n" +
                "Commute:\n" +
                "\n" +
                "\n" +
                "Recreation:";
        assertEquals(expectedStatus,Nilushanth.getUserStatus());
    }

     /**
      * Testing adding new expenses in user budget and getting the status.
      */
    @Test
    public void test_1(){
        UsersBudgetClass Nilushanth = new UsersBudgetClass(new CategoriesClass(), new CurrencyConversionClass());
        Nilushanth.addUserCategory("Recreation");
        Nilushanth.addUserSubcategory("Netflix Subscription", 20.48);
        Nilushanth.addUserSubcategory("TV", 120.24);
        Nilushanth.addUserSubcategory("Hulu", 49.37);
        Nilushanth.addUserSubcategory("Video games", 52.57);
        String expectedStatus = "Food:\n" +
                "\n" +
                "\n" +
                "Housing:\n" +
                "\n" +
                "\n" +
                "Lifestyle:\n" +
                "\n" +
                "\n" +
                "Commute:\n" +
                "\n" +
                "\n" +
                "Recreation:\n" +
                "Netflix Subscription $ 20.48,\n" +
                "TV                   $ 120.24,\n" +
                "Hulu                 $ 49.37,\n" +
                "Video games          $ 52.57";
        assertEquals(expectedStatus,Nilushanth.getUserStatus());

        Nilushanth.addUserSubcategory("Video games", 52.57);
        Nilushanth.addUserSubcategory("Video games", 52.57);
        Nilushanth.addUserSubcategory("Video games", 52.57);

        Nilushanth.addUserSubcategory("PS4 card", 49.37);

        String expectedStatus2 = "Food:\n" +
                "\n" +
                "\n" +
                "Housing:\n" +
                "\n" +
                "\n" +
                "Lifestyle:\n" +
                "\n" +
                "\n" +
                "Commute:\n" +
                "\n" +
                "\n" +
                "Recreation:\n" +
                "Netflix Subscription $ 20.48,\n" +
                "TV                   $ 120.24,\n" +
                "Hulu                 $ 49.37,\n" +
                "Video games          $ 52.57,\n" +
                "PS4 card             $ 49.37";
        assertEquals(expectedStatus2,Nilushanth.getUserStatus());

        Nilushanth.addUserCategory("Food");
        Nilushanth.addUserSubcategory("Groceries", 249.37);
        String expectedStatus3 = "Food:\n" +
                "Groceries            $ 249.37\n" +
                "\n" +
                "\n" +
                "Housing:\n" +
                "\n" +
                "\n" +
                "Lifestyle:\n" +
                "\n" +
                "\n" +
                "Commute:\n" +
                "\n" +
                "\n" +
                "Recreation:\n" +
                "Netflix Subscription $ 20.48,\n" +
                "TV                   $ 120.24,\n" +
                "Hulu                 $ 49.37,\n" +
                "Video games          $ 52.57,\n" +
                "PS4 card             $ 49.37";
        assertEquals(expectedStatus3,Nilushanth.getUserStatus());

        //adding duplicate subcategories.
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);
        Nilushanth.addUserSubcategory("Pet food", 50.00);

        String expectedStatus4 = "Food:\n" +
                "Groceries            $ 249.37,\n" +
                "Pet food             $ 50.00\n" +
                "\n" +
                "\n" +
                "Housing:\n" +
                "\n" +
                "\n" +
                "Lifestyle:\n" +
                "\n" +
                "\n" +
                "Commute:\n" +
                "\n" +
                "\n" +
                "Recreation:\n" +
                "Netflix Subscription $ 20.48,\n" +
                "TV                   $ 120.24,\n" +
                "Hulu                 $ 49.37,\n" +
                "Video games          $ 52.57,\n" +
                "PS4 card             $ 49.37";
        assertEquals(expectedStatus4,Nilushanth.getUserStatus());

    }


    /**
     * Testing the output of the linkedList of the recreation subcategory after applying ascending, descending, alphabetical ascending and alphabetical descending sorting.
     */
    @Test
    public void test_2(){

        //sorting recreation list ascending.
        System.out.println("------- ascending below");
        LinkedList <String> recLinkedList = new LinkedList<String>("Netflix Subscription, 20.48");
        recLinkedList.insertAtHeadAscendingSort("cheap gaming card, 12.24");
        recLinkedList.insertAtHeadAscendingSort("TV, 120.24");
        recLinkedList.insertAtHeadAscendingSort("Hulu, 49");
        recLinkedList.insertAtHeadAscendingSort("Video games, 52.57");

        String expectedSort = "cheap gaming card, 12.24\nNetflix Subscription, 20.48\nHulu, 49\nVideo games, 52.57\nTV, 120.24\n";
        assertEquals(recLinkedList.printLinkedList(),expectedSort);

        System.out.println("------- descending below");
        //sorting recreation list descending.
        recLinkedList = new LinkedList<String>("Netflix Subscription, 20.48");
        recLinkedList.insertAtHeadDescendingSort("cheap gaming card, 12.24");
        recLinkedList.insertAtHeadDescendingSort("TV, 120.24");
        recLinkedList.insertAtHeadDescendingSort("Hulu, 49");
        recLinkedList.insertAtHeadDescendingSort("Video games, 52.57");

        expectedSort = "TV, 120.24\nVideo games, 52.57\nHulu, 49\nNetflix Subscription, 20.48\ncheap gaming card, 12.24\n";
        assertEquals(recLinkedList.printLinkedList(),expectedSort);

        System.out.println("------- alphabetical descending below");
        recLinkedList = new LinkedList<String>("Netflix Subscription, 20.48");
        recLinkedList.insertAtHeadAlphaDescendingSort("cheap gaming card, 12.24");
        recLinkedList.insertAtHeadAlphaDescendingSort("TV, 120.24");
        recLinkedList.insertAtHeadAlphaDescendingSort("Hulu, 49");
        recLinkedList.insertAtHeadAlphaDescendingSort("Video games, 52.57");

        expectedSort = "Video games, 52.57\nTV, 120.24\nNetflix Subscription, 20.48\nHulu, 49\ncheap gaming card, 12.24\n";
        assertEquals(recLinkedList.printLinkedList(),expectedSort);

        System.out.println("------- alphabetical ascending below");
        recLinkedList = new LinkedList<String>("Netflix Subscription, 20.48");
        recLinkedList.insertAtHeadAlphaAscendingSort("cheap gaming card, 12.24");
        recLinkedList.insertAtHeadAlphaAscendingSort("TV, 120.24");
        recLinkedList.insertAtHeadAlphaAscendingSort("Hulu, 49");
        recLinkedList.insertAtHeadAlphaAscendingSort("Video games, 52.57");
        recLinkedList.printLinkedList();

    }

}