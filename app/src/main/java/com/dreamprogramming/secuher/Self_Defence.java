package com.dreamprogramming.secuher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Self_Defence extends AppCompatActivity {

    CardView defence, knife_attack, attack, gun_defence, gun_attack, spray_defence, electric_shock;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_self_defence);

        defence = findViewById(R.id.card_defence);
        knife_attack = findViewById(R.id.knife_attack);
        attack = findViewById(R.id.attack);
        gun_attack = findViewById(R.id.gun_attack);
        gun_defence = findViewById(R.id.gun_defence);
        spray_defence = findViewById(R.id.spray_defence);
        electric_shock = findViewById(R.id.electric_shock);

        defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtu.be/SfAoGd8R-CM?si=seUPlqSdNFnJejoc"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });

        attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtube.com/shorts/HGmQcmOqLMs?si=aUQ1he-Ud92gJpOY"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });

        knife_attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtu.be/y5NU8EIFf-U?si=wDgR7YGc5jwHoQla"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });

        gun_defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtube.com/shorts/cGOjEpf0g6Q?si=Ajs72jyJWBDWjfj1"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });

        gun_attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtu.be/FksrZg7Wx0A?si=p1GgyuLnZ1dZSjDi"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });

        spray_defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtu.be/cHwdNwGV_-I?si=N9FzlbQFGRJQdQ1g"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });

        electric_shock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Self_Defence.this != null) {
                    Intent intent = new Intent(Self_Defence.this, WebView_Activity.class);
                    intent.putExtra("VIDEO_URL", "https://youtube.com/shorts/3IfHAECUvq8?si=BXcEzbKP3EGWAxPa"); // Replace VIDEO_ID
                    startActivity(intent);
                }
            }
        });
    }
}