package CodingProject.budgetvision.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.UserBudgetComponent;
import CodingProject.budgetvision.controller.UsersBudgetClass;

public class LoginSuccessfulPopup extends Activity {

    private UsersBudgetClass user;
    private UserBudgetComponent userComponent;

    //empty constructor for Login Successful Popup.
    public LoginSuccessfulPopup(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_login_success);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 80% of the screen width and height.
        getWindow().setLayout((int) (width *.9),(int) (height*.8));

        this.userComponent = ((UsersBudgetClass)getApplication()).getAppComponent();
        this.user = this.userComponent.getMyMainUser();

        //the url of the spreadsheet (READ-ONLY)
        String spreadsheetUrl = this.user.getSpreadsheetUrlFromActivity();

        System.out.println("The spreadsheet url is " + spreadsheetUrl);

        //set the contents of the text view in the login successful. A clickable link to the google sheet.
        TextView spreadsheetLinkLabel = (TextView) findViewById(R.id.userSpreadsheetLink);
        spreadsheetLinkLabel.setText(spreadsheetUrl);

    }

    //controller method for closing this activity when the "close" button is clicked.
    public void closeActivity(View view){
        this.finish();
    }


}
