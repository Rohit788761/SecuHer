package com.dreamprogramming.secuher;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.dreamprogramming.secuher.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView tvname, tvemail,tvphone;
    private ActivityMainBinding binding;
    private FirebaseAuth fAuth;
    private DatabaseReference DRef;
    private Dialog dialog;
    private User user;
    String uid;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tvname = findViewById(R.id.tvName);
        tvemail = findViewById(R.id.tvEmail);
        tvphone = findViewById(R.id.tvPhone);

        fAuth = FirebaseAuth.getInstance();
        DRef = FirebaseDatabase.getInstance().getReference("Users");

        uid = fAuth.getCurrentUser().getUid().toString();

        if(uid.isEmpty())
        {
            Toast.makeText(MainActivity.this,"Uid Empty",Toast.LENGTH_SHORT).show();
        }
        else {
            getUserData();
        }
    }

    private void getUserData() {
        DRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);

                binding.tvName.setText(user.name);
                binding.tvEmail.setText(user.email);
                binding.tvPhone.setText(user.phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"Connectivity Problem",Toast.LENGTH_SHORT).show();
            }
        });
    }
}