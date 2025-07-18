package com.dreamprogramming.secuher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewAccountPage extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    TextView tvLogin;
    private Button btnCreate;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private FirebaseUser user;

    private boolean isPasswordVisible = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account_page);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnCreate = findViewById(R.id.btnCreate);
        tvLogin = findViewById(R.id.tvLogin);

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");
        fAuth = FirebaseAuth.getInstance();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NewAccountPage.this, LoginPage.class);
                startActivity(i);
                finish();
            }
        });

        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etPassword.getRight() -
                            etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        // Toggle password visibility
                        if (isPasswordVisible) {
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            setDrawableRight(etPassword, R.drawable.hide_pass);
                        } else {
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            setDrawableRight(etPassword, R.drawable.show_pass);
                        }
                        etPassword.setSelection(etPassword.length());
                        isPasswordVisible = !isPasswordVisible;
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // it change to before btn place paste code
//        if(fAuth.getCurrentUser() != null)
//        {
//            startActivity(new Intent(getApplicationContext(),LogPage.class));
//            finish();
//        }

        if(TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            return;
        }

        if(TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if(TextUtils.isEmpty(phone)) {
            etPhone.setError("Mobile No is required");
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            etPassword.setError("Password is required");
            return;
        }

        if (phone.length() != 10)
        {
            etPhone.setError("Mobile number must be 10 digits");
        }

        if(password.length() < 6)
        {
            etPassword.setError("Password length is grater than 6");
            return;
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = usersRef.push().getKey();

        // Create a user object
        User user = new User(name, email, phone, password);

        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (userId != null) {
                        usersRef.child(userId).setValue(user)
                                .addOnSuccessListener(aVoid -> Toast.makeText(NewAccountPage.this, "User registered successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(NewAccountPage.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                        Intent intent = new Intent(NewAccountPage.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(NewAccountPage.this,"Error! "+task.getException().getMessage().toString().trim(),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void checkConnectivity() {
        for (int i=0; i<6; i++) {
            if (!isInternetConnected(this)) {
                Toast.makeText(this, "Please turn on your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // BroadcastReceiver to listen for connectivity changes
    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                checkConnectivity();
            }
        }
    }

    private void setDrawableRight(EditText editText, int drawableResId) {
        Drawable[] drawables = editText.getCompoundDrawables();

        Drawable leftDrawable = drawables[0];  // Left drawable
        Drawable topDrawable = drawables[1];   // Top drawable
        Drawable rightDrawable = getResources().getDrawable(drawableResId);  // Right drawable
        Drawable bottomDrawable = drawables[3];  // Bottom drawable

        editText.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
    }
}