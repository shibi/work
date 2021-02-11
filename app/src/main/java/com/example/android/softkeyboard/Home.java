/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.softkeyboard;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.inputmethodcommon.InputMethodSettingsFragment;
import com.utils.NotificationHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays the IME preferences inside the input method setting.
 */
public class Home extends Activity {

    private static final int REQUEST_CODE = 102;
    private int buttonPressId;
    private InputMethodManager imeManager;
    private boolean isInputPickerShowing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        // We overwrite the title of the activity, as the default one is "Voice Search".
        setTitle(R.string.settings_name);

        Button btn_next = findViewById(R.id.btn_next);
        RadioButton rb_enable = findViewById(R.id.rb_enable);
        RadioButton rb_choose = findViewById(R.id.rb_choose);

        isInputPickerShowing =  false;
        imeManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        /*Intent intent = new Intent();
        intent.setComponent( new ComponentName("com.android.settings","com.android.settings.Settings$InputMethodAndLanguageSettingsActivity" ));
        startActivity(intent);*/

        boolean isKeyboardEnabled = checkKeyBoardEnabled();

        buttonPressId = (isKeyboardEnabled)?1:0;

        boolean isSelectedAsDefaultIme = checkKeyboardForDefaultInputType();
        Log.e("-------------","isSEle"+isSelectedAsDefaultIme);

        rb_enable.setSelected(true);
        //rb_choose.setActivated(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try
        {
            super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == REQUEST_CODE){
                Log.e("-----------",">"+resultCode);
                Log.e("-----------","<"+data);

                boolean isKeyboardEnabled = checkKeyBoardEnabled();
                buttonPressId = (isKeyboardEnabled)?1:0;

                if(!isKeyboardEnabled){
                    Log.e("---------------","show no");
                    NotificationHelper.createNotification(Home.this,"tiel","this message");
                }


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClick(View view){
        try
        {
            switch (view.getId())
            {
                case R.id.btn_next:

                    if(buttonPressId == 0){

                        startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), REQUEST_CODE);

                    }else {

                        showInputMethodPicker();
                    }

                    break;
            }

        }catch (Exception e){

        }
    }

    private boolean checkKeyBoardEnabled(){
        try
        {
            List<InputMethodInfo> mInputMethodProperties = imeManager.getEnabledInputMethodList();

            final int listSize = mInputMethodProperties.size();

            for (int i = 0; i < listSize; i++) {

                InputMethodInfo imi = mInputMethodProperties.get(i);

                if (imi.getId().equals(Home.this.getPackageName()+"/.SoftKeyboard")) {
                    return true;
                }
            }

            return false;

        } catch (Exception e){
            return false;
        }
    }

    private boolean checkKeyboardForDefaultInputType(){
        try
        {

            List<InputMethodInfo> mInputMethodProperties = imeManager.getEnabledInputMethodList();
            final int listSize = mInputMethodProperties.size();

            for (int i = 0; i < listSize; i++) {

                InputMethodInfo imi = mInputMethodProperties.get(i);

                if(imi.getId().equals(Home.this.getPackageName()+"/.SoftKeyboard")) {

                    if (imi.getId().equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD))) {
                        Log.e("-------------", "sdfs");
                        return true;
                    }
                }
            }

            return false;
        }catch (Exception e){
            return false;
        }
    }

    private void showInputMethodPicker() {

        if (imeManager != null) {
            imeManager.showInputMethodPicker();
            isInputPickerShowing = true;

            final View v1 = getWindow().getDecorView().getRootView();

            /*TimerTask timertask = new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                System.out.println("f>"+getWindow().getDecorView().getRootView().hasFocus());

                            }
                        });
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timertask, 500, 5000);*/


        } else {
            Toast.makeText(this, "Error",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.e("---------------","onFocis"+hasFocus);

        if(isInputPickerShowing){
            if(hasFocus){
                isInputPickerShowing = false;
                boolean isSelectedAsDefault = checkKeyboardForDefaultInputType();
                if(isSelectedAsDefault){
                    //todo
                    Log.e("---------------","go to next screen");
                }
            }
        }
    }
}
