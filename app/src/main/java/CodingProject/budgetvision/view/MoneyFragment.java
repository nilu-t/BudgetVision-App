package CodingProject.budgetvision.view;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.HashSet;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.UserBudgetComponent;
import CodingProject.budgetvision.controller.UsersBudgetClass;

public class MoneyFragment extends Fragment{
    private View myInflatedView;
    private LinearLayout layout;
    private ImageButton add;
    private EditText newUserText;

    //cards which will be used for each additional user added within the money fragment.
    private CardView foodCard;
    private CardView lifestyleCard;
    private CardView commuteCard;
    private CardView recreationCard;
    private CardView housingCard;

    private UsersBudgetClass user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate layout for the fragment.
        myInflatedView = inflater.inflate(R.layout.fragment_money, container,false);

        layout = (LinearLayout) myInflatedView.findViewById(R.id.linearLayout);

        add = (ImageButton) myInflatedView.findViewById(R.id.addUserBtn);

        UserBudgetComponent userComponent = ((UsersBudgetClass)getActivity().getApplication()).getAppComponent();
        this.user = userComponent.getMyMainUser();

        /*
         * if there are already users added and the fragment has been destroyed then the cards will disappear.
         * So, if the hashset containing all the new users is not empty then each user card will be added again on the UI.
         */
        HashSet<String> allNewUsers = this.user.getNewUserSet();
        for(String userName : allNewUsers){
            addCard(userName);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserText = (EditText) myInflatedView.findViewById(R.id.newUserText);
                String newUser = newUserText.getText().toString();

                //if the new user is not empty then it is added.
                if(! newUser.equalsIgnoreCase("")) {
                    user.addNewUsers(newUser); //add the new user in the user object.
                    System.out.println("added user (should be same as main user hash code) "+user);
                    addCard(newUser); //add the user card to the money fragment.
                }
                else if(newUser.equalsIgnoreCase("")) {
                    //make a toast, tell the user the name cannot be empty.
                    Toast.makeText(getActivity(),"The name cannot be empty.",Toast.LENGTH_SHORT).show();
                }

                //hide the virtual keyboard.
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(newUserText.getWindowToken(), 0);
            }
        });
        return myInflatedView;
    }


    public void addCard(String name) {

        //inflate the card view to make it appear on screen.
        final View view = getLayoutInflater().inflate(R.layout.user_card, null);

        //the home fragment layout made for the the money fragment specifically is inflated to be seen on the screen.
        final View viewHomeFragment = getLayoutInflater().inflate(R.layout.fragment_home_for_money, null);


        TextView newUserName = view.findViewById(R.id.name);
        newUserName.setText(name);

        ImageButton delete = view.findViewById(R.id.deleteCardNameBtn);
        Button viewBtn = view.findViewById(R.id.viewBtn);

        /*
         * The on click listeners for the card view.
         */

        //on click listener for if the delete button is clicked in the card.
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(view); //remove the cardview from the money fragment layout.
                layout.removeView(viewHomeFragment);
                user.removeUser(name);
            }
        });

        //on click listener for if the view button is clicked in the card.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),name + " card selected.", Toast.LENGTH_SHORT).show();

                //start the intnt for the view all expenses popup activity class.
                Intent intent = new Intent(getActivity(), ViewAllExpensesPopup.class);
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });

        //add the cardview to the layout.
        layout.addView(view);

        /*
         * The on click listener for the food card.
         */
        this.foodCard = viewHomeFragment.findViewById(R.id.foodCard);
        foodCard.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"food card selected.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ViewSubcategoryPopup.class);
                intent.putExtra("categoryName_extra", "Food");
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });

        /*
         * The on click listener for the housing card.
         */
        this.housingCard = viewHomeFragment.findViewById(R.id.housingCard);
        housingCard.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"housing card selected.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ViewSubcategoryPopup.class);
                intent.putExtra("categoryName_extra", "Housing");
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });

        /*
         * The on click listener for the commute card.
         */
        this.commuteCard = viewHomeFragment.findViewById(R.id.commuteCard);
        commuteCard.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"commute card selected.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ViewSubcategoryPopup.class);
                intent.putExtra("categoryName_extra", "Commute");
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });


        /*
         * The on click listener for the recreation card.
         */
        this.recreationCard = viewHomeFragment.findViewById(R.id.recreationCard);
        recreationCard.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"recreation card selected.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ViewSubcategoryPopup.class);
                intent.putExtra("categoryName_extra", "Recreation");
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });


        /*
         * The on click listener for the lifestyle card.
         */
        this.lifestyleCard = viewHomeFragment.findViewById(R.id.lifestyleCard);
        lifestyleCard.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(),"lifestyle card selected.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ViewSubcategoryPopup.class);
                intent.putExtra("categoryName_extra", "Lifestyle");
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });

        /*
         * OnClick listeners for the two image buttons part of the home fragment.
         */
        ImageButton addAdditionalExpenses = viewHomeFragment.findViewById(R.id.addBtn);
        addAdditionalExpenses.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdditionalExpensePopup.class);
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });

        ImageButton removeExpenses = viewHomeFragment.findViewById(R.id.removeBtn);
        removeExpenses.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeleteSubcategoryPopup.class);
                intent.putExtra("userName_extra",name);
                startActivity(intent);

                hideKeyboard();
            }
        });

        layout.addView(viewHomeFragment); //add the homefragment which will be below the new user card.

    }

    public void hideKeyboard(){
        //hide the virtual keyboard.
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}
