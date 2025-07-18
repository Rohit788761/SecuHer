package com.dreamprogramming.secuher.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dreamprogramming.secuher.LoginPage;
import com.dreamprogramming.secuher.MainActivity;
import com.dreamprogramming.secuher.R;

public class ProfileFragment extends Fragment {
    Button logout, profile;

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile = view.findViewById(R.id.btnProfile);
        logout = view.findViewById(R.id.btnLogout);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putBoolean("logged_out", true);
                editor.apply();
                
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                
                // Redirect to LoginPage
                Intent intent = new Intent(getActivity(), LoginPage.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Logout Successful", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        return view;
    }

    private void setResult(int resultOk, Intent resultIntent) {
    }
}