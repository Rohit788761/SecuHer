package com.dreamprogramming.secuher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    // If user is logged in, go to HomePage
                    Intent intent = new Intent(Splash_Screen.this, HomeScreen.class);

                    startActivity(intent);
                    finish();
                } else {
                    // If user is not logged in, go to LoginPage
                    Toast.makeText(Splash_Screen.this,"Your safety is our priority, stay connected",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Splash_Screen.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);
    }
}