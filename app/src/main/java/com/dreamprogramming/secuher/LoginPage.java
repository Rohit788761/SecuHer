package com.dreamprogramming.secuher;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPage extends AppCompatActivity {

    EditText etEmail,etPassword;
    TextView tvCreateAccount,tvForgotPass;
    String email,password;
    Button btnLogin;
    private FirebaseAuth fAuth;
    private FirebaseDatabase database;
    private boolean isPasswordVisible = false;
    private ConnectivityReceiver connectivityReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPass = findViewById(R.id.tvForgotPass);

        database = FirebaseDatabase.getInstance();
     //   usersRef = database.getReference("Users");         // Provide "Users" as path to show login details in database
        fAuth = FirebaseAuth.getInstance();


        connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        // Check connectivity when the activity starts
        checkConnectivity();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    etPassword.setError("Password is required");
                    return;
                }

                if(password.length() < 6)
                {
                    etPassword.setError("Password length is grater than 6");
                    return;
                }

       //         String userId = usersRef.push().getKey();

                // Create a user object
         //       User user = new User(email, password);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
//                            if (userId != null) {
//                                usersRef.child(userId).setValue(user)
//                                        .addOnSuccessListener(aVoid -> Toast.makeText(LoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show())
//                                        .addOnFailureListener(e -> Toast.makeText(LoginPage.this, "Login Failed " + e.getMessage(), Toast.LENGTH_SHORT).show());


                                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                // Redirect to HomePage

                                Intent intent = new Intent(LoginPage.this, HomeScreen.class);
                                startActivity(intent);
                                Toast.makeText(LoginPage.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        else{
                            Toast.makeText(LoginPage.this,"Email or Password is Incorrect",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });

        tvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, NewAccountPage.class);
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


        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginPage.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }

                fAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginPage.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginPage.this, "Error sending reset email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectivityReceiver);
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