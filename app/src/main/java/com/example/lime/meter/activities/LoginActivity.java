package com.example.lime.meter.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.lime.meter.R;

public class LoginActivity extends AppCompatActivity {

    Button loginLoginButtton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginLoginButtton = (Button) findViewById(R.id.loginBtn);
        loginLoginButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent intent = new Intent(LoginActivity.this, ScanActivity.class);
                    startActivity(intent);
                    finish();
                }
        });
    }
}
