package CodingProject.budgetvision.view;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.UserBudgetComponent;
import CodingProject.budgetvision.controller.UsersBudgetClass;

public class HomeFragment extends Fragment implements View.OnClickListener {

     /*
    TODO : saving and loading data to the app of user status.
    DONE : Create custom suggestions to hint what types of subcategories the user can input.
     */

    String category = ""; //stores the current category.

    private View myInflatedView;

    /*
     * Array of subcategory names used for adding subcategories for their respective category.
     * All of the arrays are public static final declaration as they are non-modified names.
     * Thus, they the 5 String array hints are accessible for the entire class.
     */
    public static final String [] foodSubcategoryHints = {
            "Beverages","Bakery", "Breakfast", "Candy", "Dinner", "Deserts",
            "Fast Food", "Food delivery", "Groceries","Lunch", "Pastries", "Pet food", "Restaurant",
            "School lunch", "Snacks", "Work lunch"
    };

    public static final String [] housingSubcategoryHints= {
            "Alarm system", "Association fees", "Cable", "Cleaning service", "Electric", "Furniture & décor", "Home maintenance",
            "Home phone", "Home repairs", "Hydro", "Internet", "Kitchen items", "Garden costs", "Mobile phone(s)",
            "Mortgage",  "Natural gas", "Pool costs", "Rent", "Real estate taxes", "Trash service", "Tools costs"
    };

    public static final String [] commuteSubcategoryHints = {
            "Air Travel", "Bus", "Car inspection", "Car Maintenance", "Car payment", "Car repairs", "Car Registration Fees", "Flight",
            "Gas", "Hobby transportation", "Lyft", "Metro","Motorcycle Insurance", "Oil Changes", "Parking Fees","Property taxes",
            "Roadside assistance", "Sporting events", "Subway", "Taxi",  "Toll Fees", "Tires", "Train", "Uber",
            "Vitamins & supplements"
    };

    public static final String [] lifestyleSubcategoryHints= {
            "Bathing & Hygiene", "Child Care",  "Clothing", "College/University", "Cosmetics", "Dermatologist", "Dental care", "Doctor", "Dry Cleaning", "First aid supplies",
            "Gym Membership", "Hair products", "Heat/gas", "Health Insurance", "Hotel", "Household supplies",  "Home security costs", "Laundry",
            "Life insurance", "Medical insurance", "Pet insurance", "Prescriptions", "Makeup costs", "Optometrist",
            "Salon/ barber", "Spa & massage", "Sports", "Veterinary care", "Vision insurance"

    };

    public static final String [] recreationSubcategoryHints= {
            "Arts", "Bowling", "Cinema", "Concerts", "Gaming costs", "Music Streaming", "Movie Rentals", "Movie Tickets", "Newspaper & Magazines"
            , "Season Tickets","TV subscription", "Vacation Costs", "Video Streaming"
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate layout for the fragment.
        this.myInflatedView = inflater.inflate(R.layout.fragment_home, container,false);

        /*
         * Adding OnClick listeners for all 5 category cards.
         */
        //set an onclick listener for the food card.
        CardView foodCard = (CardView) this.myInflatedView.findViewById(R.id.foodCard);
        foodCard.setOnClickListener(this);

        //set an onclick listener for the housing card.
        CardView housingCard = (CardView) this.myInflatedView.findViewById(R.id.housingCard);
        housingCard.setOnClickListener(this);

        //set an onclick listener for the commute card.
        CardView commuteCard = (CardView) this.myInflatedView.findViewById(R.id.commuteCard);
        commuteCard.setOnClickListener(this);

        //set an onclick listener for the recreation card.
        CardView recreationCard = (CardView) this.myInflatedView.findViewById(R.id.recreationCard);
        recreationCard.setOnClickListener(this);

        //set an onclick listener for the commute card.
        CardView lifestyleCard = (CardView) this.myInflatedView.findViewById(R.id.lifestyleCard);
        lifestyleCard.setOnClickListener(this);

        //set an onclick listener for the income card
        CardView incomeCard = (CardView) this.myInflatedView.findViewById(R.id.incomeCard);
        incomeCard.setOnClickListener(this);

        /*
         * Adding an OnClick listener for the all expenses button.
         */
        Button allExpenses = (Button) this.myInflatedView.findViewById(R.id.allExpensesBtn);
        allExpenses.setOnClickListener(this);

        /*
         * Adding OnClick listeners for the add and remove category image buttons.
         */
        ImageButton addButton = (ImageButton) this.myInflatedView.findViewById(R.id.addBtn);
        addButton.setOnClickListener(this);

        ImageButton removeButton = (ImageButton) this.myInflatedView.findViewById(R.id.removeBtn);
        removeButton.setOnClickListener(this);

        //return the view for the fragment for the main activity.
        return this.myInflatedView;
    }


    //onclick method from superclass to execute when an object with onClickListener is clicked.
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        UserBudgetComponent userComponent = ((UsersBudgetClass)getActivity().getApplication()).getAppComponent();
        UsersBudgetClass user = userComponent.getMyMainUser();

        switch (v.getId()) {
            /*
             * All the cases for cards clicked.
             */
            case R.id.foodCard:
                computeFoodCard(user);
                break;

            case R.id.housingCard:
                computeHousingCard(user);
                break;
            case R.id.commuteCard:
                computeCommuteCard(user);
                break;
            case R.id.recreationCard:
                computeRecreationCard(user);
                break;
            case R.id.lifestyleCard:
                computeLifestyleCard(user);
                break;
            case R.id.incomeCard:
                computeIncomeCard();
                break;

            /*
             * The case for view all expenses button clicked.
             */
            case R.id.allExpensesBtn:
                popupAllExpenses();
                break;
            /*
             * The case for add and delete expenses image buttons clicked.
             */
            case R.id.addBtn:
                popUpWindowAdd();
                break;

            case R.id.removeBtn:
                popUpWindowRemove();
                break;
        }
    }

    //method to create the popup windows.
    /*
     * This method will start another activity in the AddSubcategoryPopup class. Executes OnClick.
     */
    public void popUpWindowAdd(){
        Intent intent = new Intent(getActivity(), AdditionalExpensePopup.class);
        intent.putExtra("userName_extra","#Default");
        startActivity(intent);
    }

    /*
     * This method will start another activity in the DeleteSubcategoryPopup class. Executes OnClick.
     */
    public void popUpWindowRemove(){
        Intent intent = new Intent(getActivity(), DeleteSubcategoryPopup.class);
        intent.putExtra("userName_extra","#Default");
        startActivity(intent);
    }

    /*
     * This method will start another activity in their ViewSubcategoryPopup class.
     */
    public void popUpSubcategory() {
        Intent intent = new Intent(getActivity(), ViewSubcategoryPopup.class);
        intent.putExtra("categoryName_extra",this.category);
        intent.putExtra("userName_extra","#Default");
        startActivity(intent);
    }

    /*
     * This method will start another activity in the AddIncomePopup class.
     */
    public void popUpAddIncome(){
        Intent intent = new Intent(getActivity(), AddOrRemoveIncomePopup.class);
        startActivity(intent);
    }


    /*
     * This method will start another activity in the ViewAllExpensesPopup class. Executes OnClick.
     */
    public void popupAllExpenses(){
        Intent intent = new Intent(getActivity(), ViewAllExpensesPopup.class);
        startActivity(intent);
    }

    //controller method when food card is clicked.
    public void computeFoodCard(UsersBudgetClass user){

        this.category = "food";
        setCategory(user);

        //call the popUpSubcategory method.
        popUpSubcategory();
    }

    //controller method when housing card is clicked.
    public void computeHousingCard(UsersBudgetClass user){

        this.category = "housing";
        setCategory(user);

        //call the popUpSubcategory method.
        popUpSubcategory();

    }

    //controller method when commute card is clicked.
    public void computeCommuteCard(UsersBudgetClass user){

        this.category = "commute";
        setCategory(user);

        //call the popUpSubcategory method.
        popUpSubcategory();

    }

    //controller method when recreation card is clicked.
    public void computeRecreationCard(UsersBudgetClass user){

        this.category = "recreation";
        setCategory(user);

        //call the popUpSubcategory method.
        popUpSubcategory();
    }

    //controller method when lifestyle card is clicked.
    public void computeLifestyleCard(UsersBudgetClass user){
        this.category = "lifestyle";
        setCategory(user);

        //call the popUpSubcategory method.
        popUpSubcategory();

    }

    //controller method when compute income card is clicked.
    public void computeIncomeCard(){
        popUpAddIncome();

    }

    public void setCategory(UsersBudgetClass user){
        user.categoriesObject().setCategory(this.category);
    }

    /**
     * This method returns the category of the card selected.
     * @return
     */
    public String getCategory(){
        return this.category;
    }


}
