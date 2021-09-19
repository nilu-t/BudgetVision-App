package CodingProject.budgetvision.model;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.MainActivity;

public class AddIncomePopup extends Activity implements View.OnClickListener{

    private double userInitialIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_add_income);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 80% of the screen width and height.
        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        //create an onclick listener for the add income button.
        Button addIncomeButton = ((Button) findViewById(R.id.addIncomeBtn));
        addIncomeButton.setOnClickListener(this);

        //create an onclick listener for the remove income button.
        Button removeIncomeButton = ((Button) findViewById(R.id.removeIncomeBtn));
        removeIncomeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //if the income button is clicked.
            case R.id.addIncomeBtn:
                EditText userIncomeTxt1 = ((EditText)(findViewById(R.id.addIncomeTxt)));

                userInitialIncome = Double.parseDouble(userIncomeTxt1.getText().toString());
                setInitialIncreaseIncome(); //update the initial income of the user object.
                updateTotalIncome(); //update the total income of the user object.
                closeActivity(); //close the activity now the user has added their initial income.
                break;

            case R.id.removeIncomeBtn:
                EditText userIncomeTxt2 = ((EditText)(findViewById(R.id.removeIncomeTxt)));
                userInitialIncome = Double.parseDouble(userIncomeTxt2.getText().toString());
                setInitialDecreaseIncome(); //update the initial income of the user object.
                updateTotalIncome(); //update the total income of the user object.
                closeActivity(); //close the activity now the user has added their initial income.
        }
    }

    public void setInitialIncreaseIncome(){
        MainActivity.getInstance().getUser().increaseUserInitialIncome(userInitialIncome);
    }

    public void setInitialDecreaseIncome(){
        MainActivity.getInstance().getUser().decreaseUserInitialIncome(userInitialIncome);
    }

    public void updateTotalIncome(){
        MainActivity.getInstance().updateTotalIncome();
    }

    //method for closing the activity from OnClick.
    public void closeActivity(View view){
        this.finish();
    }

    //method for closing the activity without OnClick.
    public void closeActivity(){
        this.finish();
    }

}
