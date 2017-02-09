package com.app.gsmademo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText mobileNoET;
    private CheckBox termCB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mobileNoET = (EditText) findViewById(R.id.et_mobile_no);
        termCB = (CheckBox)findViewById(R.id.cb_term_condition);
        termCB.setChecked(true);


        ((TextView)findViewById(R.id.tv_verify)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()) {
                    InputMethodManager inputMethodManager = ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE));
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    MobileAuthenticationVolley.getInstance().requestDiscoveryApi(MainActivity.this, "91" + mobileNoET.getText().toString().trim(), new MobileAuthenticationVolley.mobileCallback() {
                        @Override
                        public void result(boolean isMobileValidate, String response) {
                            Toast.makeText(MainActivity.this, isMobileValidate + "", Toast.LENGTH_SHORT).show();
                        }
                    });
                  /* MobileAuthentication.getInstance().requestDiscoveryApi(MainActivity.this, "91" + mobileNoET.getText().toString().trim(), new MobileAuthentication.mobileCallback() {

                        @Override
                        public void result(boolean isMobileValidate, String response) {
                            Toast.makeText(MainActivity.this, isMobileValidate + "", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }
        });
    }

    /**
     * check validity of mobile number and agreement Terms & conditions for mobile connect
     * @return
     */
    private boolean validation() {

        if (mobileNoET.getText().toString().length() == 0) {
            Toast.makeText(MainActivity.this, "Please enter mobile number", Toast.LENGTH_LONG).show();
            return false;
        } else if (mobileNoET.getText().toString().length() < 10) {
            Toast.makeText(MainActivity.this, "Please enter at least 10 digit mobile number", Toast.LENGTH_LONG).show();
            return false;
        }else if(!termCB.isChecked())
        {
            Toast.makeText(MainActivity.this, "Please agree to mobile connect Terms & Conditions", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }
}
