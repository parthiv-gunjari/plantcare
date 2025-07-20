package com.codewithgolap.samadhan.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codewithgolap.samadhan.R;
import com.codewithgolap.samadhan.activities.logins.ForgotPasswordActivity;
import com.codewithgolap.samadhan.activities.logins.LoginActivity;

public class SendPasswordActivity extends AppCompatActivity {

    private String emailAddress;
    private TextView userEmailIdTv;
    private Button sendToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_password);

        emailAddress = getIntent().getStringExtra("email");
        userEmailIdTv = findViewById(R.id.userEmailId);
        userEmailIdTv.setText(emailAddress);

        sendToLogin = findViewById(R.id.btn_back_to_login);
        sendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendPasswordActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    public void tryanotherEmail(View view) {
        startActivity(new Intent(SendPasswordActivity.this, ForgotPasswordActivity.class));
        finish();
    }
}