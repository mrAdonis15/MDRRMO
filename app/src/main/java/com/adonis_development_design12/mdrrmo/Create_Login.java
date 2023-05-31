package com.adonis_development_design12.mdrrmo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Create_Login extends AppCompatActivity {

    private Button createAccountButton;
    private Button loginButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Show the system navigation panel
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_create_login);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Check if the login process has already been completed
        if (isLoginCompleted()) {
            navigateToNextActivity();
            finish(); // Optional: Close the Create_Login activity so it doesn't appear in the back stack
        }

        // Retrieve the buttons
        createAccountButton = findViewById(R.id.button);
        loginButton = findViewById(R.id.button2);

        // Set a click listener for the create account button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignupActivity
                Intent intent = new Intent(Create_Login.this, signup.class);
                startActivity(intent);
                finish();
            }
        });

        // Set a click listener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the login activity
                Intent intent = new Intent(Create_Login.this, login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isLoginCompleted() {
        return sharedPreferences.getBoolean("loginCompleted", false);
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(Create_Login.this, Front_Face.class);
        startActivity(intent);
    }
}
