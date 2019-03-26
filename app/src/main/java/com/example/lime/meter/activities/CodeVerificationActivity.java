package com.example.lime.meter.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.example.lime.meter.R;
import com.example.lime.meter.utils.AuthHelper;

public class CodeVerificationActivity extends AppCompatActivity {

    private static final String TAG = CodeVerificationActivity.class.getSimpleName();
    ProgressDialog progressDialog;
    AuthHelper authHelper = null;
    String mCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        progressDialog =new ProgressDialog(this);
        final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.verification_code_entry);
        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
                    mCode = str.toString();
                    verifyCode();
                }
            });
        }
    }


    private void verifyCode(){
        Log.d(TAG,"verifyCode");
        authHelper = new AuthHelper(this);
        authHelper.verifyCodeSent(mCode);
    }
}
