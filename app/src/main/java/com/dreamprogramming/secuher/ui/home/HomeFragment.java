package com.dreamprogramming.secuher.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.telephony.SmsManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dreamprogramming.secuher.Contact;
import com.dreamprogramming.secuher.Self_Defence;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import com.dreamprogramming.secuher.Add_contact;
import com.dreamprogramming.secuher.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class HomeFragment extends Fragment {
    CardView card_add, card_defence, card_map, card_alert;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 100;
    private static final int MY_PERMISSIONS_REQUEST_SMS = 101;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double lat = 0.0;
    private double lng = 0.0;
    private String message1, address;
    ArrayList<String> phoneNumbers;
    List<String> contactListView;
    private ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        FirebaseApp.initializeApp(this.getActivity());

        card_add = view.findViewById(R.id.card_add);
        card_defence = view.findViewById(R.id.card_defence);
        card_map = view.findViewById(R.id.card_map);
        card_alert = view.findViewById(R.id.card_Alert);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("contacts_prefs", getActivity().MODE_PRIVATE);
        Set<String> savedContacts = sharedPreferences.getStringSet("contacts", new HashSet<>());
        phoneNumbers = new ArrayList<>(savedContacts);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 seconds
        locationRequest.setFastestInterval(2000); // 2 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), Add_contact.class);
                    Toast.makeText(getActivity(), "Add Contacts", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        });

        card_defence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), Self_Defence.class);
                    Toast.makeText(getActivity(), "Add Contacts", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }
        });

        // Initialize LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocation(location);
                }
            }
        };

        // Request Location Permissions
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            startLocationUpdates();
        }

        card_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSmsPermission();
            }
        });

        return view;
    }

    private void updateLocation(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        try {
            Geocoder geocoder = new Geocoder(this.getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SMS);
        } else {
            if (lat != 0.0 && lng != 0.0) {
                sendSms();
            } else {
                Toast.makeText(this.getActivity(), "Wait", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSms() {
     //   String phoneNumber = phoneNumbers.get(0); // Replace with your phone number
        String message = "Please Help me I am in danger!!! \n";

        if (lat == 0.0 && lng == 0.0) {
            message1 = "Network Issue!!! \nCannot share live location";
        } else {
            message1 = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng;
        }

        SmsManager smsManager = SmsManager.getDefault();

        for (String phoneNumber : phoneNumbers) {
            try {
                Log.d("HomeFragment", "Sending SMS to: " + phoneNumber);
                smsManager.sendTextMessage(phoneNumber, null, message + "\nLocation : " + address, null, null);
                smsManager.sendTextMessage(phoneNumber, null, message1, null, null);
                Toast.makeText(getContext(), "SMS SENT", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("HomeFragment", "Failed to send SMS: ", e);
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to send SMS to " + phoneNumber, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this.getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSms();
            } else {
                Toast.makeText(this.getContext(), "SMS permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
}