package com.adonis_development_design12.mdrrmo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class Intro extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        // Initialize the ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // Delay the activity change by 2 seconds
        new Handler().postDelayed(() -> {
            // Start the new activity
            Intent intent = new Intent(Intro.this, Create_Login.class);
            startActivity(intent);
            finish();
        }, 500); // 2-second delay
    }
}
