package com.example.android.softkeyboard;


import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.utils.NotificationHelper;
import java.util.List;

public class SetUpActivity extends Activity {

    //to identify request
    private static final int REQUEST_CODE = 102;

    //to check which action to perform on each time next button is clicked.
    private int buttonPressId;

    //declare input method manager
    private InputMethodManager imeManager;

    //flag to check keyboard selection dialog close event
    private boolean isInputPickerShowing;

    //button for to go through the steps of keyboard setup
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_set_up);
            //set title
            setTitle(R.string.settings_name);

            //flag set false initially
            isInputPickerShowing = false;

            //initialize button
            btn_next = findViewById(R.id.btn_next);

            //initialize input method manager
            imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        try {

            super.onResume();

            //check keyboard enabled from settings screens (First step)
            boolean isKeyboardEnabled = checkKeyBoardEnabled();

            //check if keyboard is enabled from settings screen
            if (!isKeyboardEnabled) {

                //if not enabled, show the label enable keyboard
                setButtonTextAndPressId(getResources().getString(R.string.step1_btn_text), 0);

            } else {

                //check whether keyboard is selected as default (Second step)
                boolean isSelectedAsDefaultKeyboard = checkKeyboardForDefaultInputType();

                //if both steps completed , show message that keyboard is active
                if (!isSelectedAsDefaultKeyboard) {

                    //if keyboard is not default , show the label choose keyboard
                    setButtonTextAndPressId(getResources().getString(R.string.step2_btn_text), 1);

                } else {

                    //if both steps completed , show keyboard active
                    setButtonTextAndPressId(getResources().getString(R.string.setupFinish_btn_text), 2);
                    Toast.makeText(SetUpActivity.this, "Keyboard is Active", Toast.LENGTH_LONG).show();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to handle layout item clicks
     * @param view
     * */
    public void onClick(View view){
        try
        {
            //get the clicked item id
            switch (view.getId())
            {
                //check for next button
                case R.id.btn_next:

                    //check the buttonPressID variable to find where to go
                    //0 - first step
                    //1 - second step
                    //2 - setup finish
                    switch (buttonPressId)
                    {
                        case 0:
                            //open settings screen language and input option
                            startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), REQUEST_CODE);
                            break;
                        case 1:
                            //if keyboard is enabled , then show the keyboard picker dialogbox
                            showInputMethodPicker();
                            break;
                        case 2:
                            //nothing to do...
                            //show error message
                            Toast.makeText(this, "Keyboard is active", Toast.LENGTH_LONG).show();
                            break;
                    }


                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to check action status
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try
        {

            super.onActivityResult(requestCode, resultCode, data);

            //check request code matching
            if(requestCode == REQUEST_CODE){

                //check whether this keyboard is enabled
                boolean isKeyboardEnabled = checkKeyBoardEnabled();
                //set the button press id to determine which action to perform.
                if(isKeyboardEnabled){
                    //keyboard enabled , next step is to choose the keyboard
                    //set the button press id = 1;
                    setButtonTextAndPressId("CHOOSE KEYBOARD",1);
                }else {
                    //keyboard is not enabled. show the first step
                    //set the button press id = 0;
                    setButtonTextAndPressId("ENABLE KEYBOARD",0);

                    //show notification that keyboard is not enabled.
                    showSetupNotification();
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check whether keyboard is enabled from settings
     * */
    private boolean checkKeyBoardEnabled(){
        try
        {
            //get the list of active keyboards
            List<InputMethodInfo> mInputMethodProperties = imeManager.getEnabledInputMethodList();

            //get list size
            final int listSize = mInputMethodProperties.size();

            //loop through list and check for the softkeyboard id
            for (int i = 0; i < listSize; i++) {
                //get the info object
                InputMethodInfo imi = mInputMethodProperties.get(i);
                //check for id in enabled keyboard list and return true if found.
                if (imi.getId().equals(SetUpActivity.this.getPackageName()+"/.SoftKeyboard")) {
                    return true;
                }
            }
            //return false
            return false;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * to check whether current keyboard is selected as default input type
     * */
    private boolean checkKeyboardForDefaultInputType(){
        try
        {
            //get the list of active keyboards
            List<InputMethodInfo> mInputMethodProperties = imeManager.getEnabledInputMethodList();
            //get size
            final int listSize = mInputMethodProperties.size();
            //loop through the list and check for our keyboard id
            for (int i = 0; i < listSize; i++) {

                //get info object of each item in list
                InputMethodInfo imi = mInputMethodProperties.get(i);
                //check for id
                if(imi.getId().equals(SetUpActivity.this.getPackageName()+"/.SoftKeyboard")) {
                    //check whether this keyboard is set as default inputtype
                    if (imi.getId().equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD))) {
                        return true;
                    }
                }
            }
            //return false
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * to show input keyboard chooser
     * */
    private void showInputMethodPicker() {

        try {
            //check object initialized before proceeding
            if (imeManager != null) {
                //call method to show input picker dialog box
                imeManager.showInputMethodPicker();
                //set the flag true to denote dialog box showing.
                //flag is used to check dialog box close in onWindowFocusChanged() method
                isInputPickerShowing = true;

            } else {
                //show error message
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * override window focus changed method to check whether the inputmethod picker dialog box
     * @param hasFocus
     * */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        try {
            super.onWindowFocusChanged(hasFocus);
            //check whether keyboard selection dialog showing
            if (isInputPickerShowing) {
                //if root window gained focus, it means user closed keyboard picker dialog
                if (hasFocus) {
                    //set the flag false , since dialog box closed
                    isInputPickerShowing = false;
                    //check whether this keyboard is selected as default input keyboard
                    boolean isSelectedAsDefault = checkKeyboardForDefaultInputType();
                    //if the flag true, then keyboard is active
                    if (isSelectedAsDefault) {
                        //show keyboard active label
                        setButtonTextAndPressId(getResources().getString(R.string.setupFinish_btn_text),2);
                        //show message
                        Toast.makeText(SetUpActivity.this, "Keyboard is active", Toast.LENGTH_LONG).show();

                        //cancel notifications ,if there any
                        NotificationHelper.cancelNotification();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to set button label and press id
     * @param buttonLabel
     * @param pressId
     * */
    private void setButtonTextAndPressId(String buttonLabel, int pressId){
        try{
            //set the label and press id
            btn_next.setText(buttonLabel);
            buttonPressId = pressId;

            //update step icon image according to buttonpress id
            switch (buttonPressId){
                case 0:
                    //for 0 , show both icons (steps) not completed
                    showUpdatedImage(R.id.iv_circle1,false);
                    showUpdatedImage(R.id.iv_circle2,false);
                    break;
                case 1:
                    //for 1 , show first step completed and second not completed
                    showUpdatedImage(R.id.iv_circle1,true);
                    showUpdatedImage(R.id.iv_circle2,false);
                    break;
                case 2:
                    //for 2 , show both icons as completed
                    showUpdatedImage(R.id.iv_circle1,true);
                    showUpdatedImage(R.id.iv_circle2,true);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * To update the image view source to denote step completion
     * */
    private void showUpdatedImage(int iv_id, boolean isComplete){
        try
        {
            //set the image source on the specified image view
            ((ImageView)findViewById(iv_id)).setImageResource((isComplete)?R.drawable.ic_comment:R.drawable.circle_blue);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();

            //check whether keyboard is selected as default (Second step)
            boolean isSelectedAsDefaultKeyboard = checkKeyboardForDefaultInputType();
            //check whether final step is complete,
            //if not,...show notification
            if(!isSelectedAsDefaultKeyboard){
                //show notification about setup status
                showSetupNotification();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * show notification that keyboard setup is incomplete
     * */
    private void showSetupNotification(){
        try
        {
            //show notification that keyboard is not enabled.
            NotificationHelper.createNotification(SetUpActivity.this,"SoftKeyboard","Keyboard is not enabled");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}