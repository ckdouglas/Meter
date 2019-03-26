package com.example.lime.meter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.lime.meter.R;
import com.example.lime.meter.utils.AuthHelper;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView bt_send_code;
    EditText et_phone_number;
    AuthHelper authHelper = null;
    private CountryCodePicker countryCodePicker;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bt_send_code = (ImageView) findViewById(R.id.bt_send_code);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        countryCodePicker = findViewById(R.id.user_number_iso);
        countryCodePicker.registerCarrierNumberEditText(et_phone_number);

        bt_send_code.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_send_code:
                if (isValidPhoneNumber()){
                    authHelper = new AuthHelper(this,phoneNumber,this);
                    authHelper.startVerificationProcess();
                }else {
                    et_phone_number.setError("Number Cannot Be Empty");
                }
                break;
        }
    }


    private boolean isValidPhoneNumber() {
        phoneNumber = countryCodePicker.getFullNumberWithPlus();
        if (!phoneNumber.isEmpty() && countryCodePicker.isValidFullNumber())
            return true;
        return false;
    }
}
