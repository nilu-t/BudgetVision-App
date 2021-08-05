package CodingProject.budgetvision.model;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

import CodingProject.budgetvision.R;
import CodingProject.budgetvision.controller.MainActivity;

public class ViewAllExpensesPopup extends Activity implements TextToSpeech.OnInitListener {

    /**
     * This "popup" Class allows for the user to view all the valid subcategories and expenses.
     * This Class also contains the TextToSpeech listener interface to allow the user to listen to the subcategories and expenses they added.
     */
    private UsersBudgetClass user = MainActivity.getInstance().getUser(); //users object from main activity.
    private String allExpenses;
    private TextToSpeech tts;
    private boolean isTTSInitialized;

    //empty constructor for ViewAllExpensesPopup.
    public ViewAllExpensesPopup(){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_all_expenses);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //the window will be 80% of the screen width and height.
        getWindow().setLayout((int) (width *.8),(int) (height*.8));

        //call the update all expenses method on create.
        updateAllExpenses();

        //initialize the text to speech constructor.
        tts = new TextToSpeech(this,this);

    }

    @Override
    protected void onDestroy() {

        //Close the Text to Speech Library.
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    //method for sorting all categories by expenses for the user.
    public void sortExpenses(View view){
        String sortType = getItemSelected(R.id.sortOptions);
        String categoryToSort = getItemSelected(R.id.categoryOptions);

        sortExpenses(sortType,categoryToSort);

    }

    //overloaded helper method for sorting the expenses in a specific order.
    public void sortExpenses(String sortType, String categoryToSort){
        this.user.sortAllSubcategoriesByExpense(sortType,categoryToSort);
        String sortedAscendingExpenses = this.user.getUserStatus();
        setContentsOfTextView(R.id.allExpensesText, sortedAscendingExpenses);
    }

    //method for setting the all expenses text.
    public void updateAllExpenses(){
        this.allExpenses = this.user.getUserStatus();
        setContentsOfTextView(R.id.allExpensesText, this.allExpenses);
    }

    //method for closing this activity when the "close" button is clicked.
    public void closeActivity(View view){
        this.finish();

    }

    //the interface method to communicate with the Text To Speech engine.
    public void onInit(int initStatus){
        //if the engine status is TextToSpeech.SUCCESS == 0, then the engine was initialized successfully.
        if(initStatus == TextToSpeech.SUCCESS){
            this.isTTSInitialized = true; // the tts is initialized successfully.
            tts.setLanguage(Locale.US); //set the language of the tts to US.Locale.
        }
    }

    public void speak(View view){
        if(this.isTTSInitialized) {
            this.tts.setPitch(0.9f);
            this.tts.setSpeechRate(1.0f);
            //depending on the users device the Text To Speech method may be deprecated so that is accounted for.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(this.allExpenses,TextToSpeech.QUEUE_FLUSH,null,null);
            } else {
                tts.speak(this.allExpenses, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }
    /* this accessor retrieves input chosen from some spinner (drop-down menu) */
    private String getItemSelected(int id) {
        View view = findViewById(id);
        Spinner spinner = (Spinner) view;
        String string = spinner.getSelectedItem().toString();

        return string;
    }


    /* this mutator sets the output label */
    private void setContentsOfTextView(int id, String newContents) {
        View view = findViewById(id);
        TextView textView = (TextView) view;
        textView.setText(newContents);
    }

}
