package CodingProject.budgetvision.model;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import CodingProject.budgetvision.R;

public class LoginSuccessfulPopup extends Activity {

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
        getWindow().setLayout((int) (width *.8),(int) (height*.8));

        //the url of the spreadsheet (READ-ONLY)
        String spreadsheetUrl = MainActivity.getInstance().getSpreadsheetUrl();

        System.out.println("The spreadsheet url is " + spreadsheetUrl);


        //set the contents of the text view in the login successful. A clickable link to the google sheet.
//        TextView spreadsheetLinkLabel = (TextView) findViewById(R.id.userSpreadsheetLink);
//        spreadsheetLinkLabel.setText(spreadsheetUrl);

    }


    //controller method for closing this activity when the "close" button is clicked.
    public void closeActivity(View view){
        this.finish();
    }


    /* this accessor retrieves input entered on the text view  */
    private String getInputOfTextField(int id) {
        View view = findViewById(id);
        EditText editText = (EditText) view;
        String input = editText.getText().toString();
        return input;
    }


    /* this mutator sets the output label */
    private void setContentsOfTextView(int id, String newContents) {
        View view = findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }

}
