package com.dreamprogramming.secuher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Add_contact extends AppCompatActivity {

    private static final int PICK_CONTACT = 1;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    TextView helpIcon;
    Button btn_save, btn_delete;
    private ListView listView;
    private FloatingActionButton btnPickContact;
    private ArrayList<String> selectedContacts;
    FirebaseAuth fAuth;
    FirebaseDatabase database;
    DatabaseReference contactsRef;
    private ArrayAdapter<String> adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        btnPickContact = findViewById(R.id.btnPickContact);
        listView = findViewById(R.id.contactListView);
        helpIcon = findViewById(R.id.helpIcon);
        btn_save = findViewById(R.id.btn_save);
        btn_delete = findViewById(R.id.btn_delete);
        selectedContacts = new ArrayList<>();

        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            contactsRef = database.getReference("Users").child(userId).child("contacts");
        } else {
            Toast.makeText(this, "User not logged in. Please log in first.", Toast.LENGTH_SHORT).show();
            finish(); // Exit activity if no user is logged in
            return;
        }

        // Load the contacts from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE);
        Set<String> savedContacts = sharedPreferences.getStringSet("contacts", new HashSet<>());
        if (savedContacts != null) {
            selectedContacts.addAll(savedContacts);
        }

        // Set up adapter to display selected contacts
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedContacts);
        listView.setAdapter(adapter);

        btn_save.setOnClickListener(v -> loadContactsFromDevice());

        btn_delete.setOnClickListener(v -> new AlertDialog.Builder(Add_contact.this)
                .setTitle("Delete All Contacts")
                .setMessage("Are you sure you want to delete all contacts?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAllContactsFromFirebase())
                .setNegativeButton(android.R.string.no, null)
                .show());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        btnPickContact.setOnClickListener(v -> pickContact());

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            selectedContacts.remove(position);
            adapter.notifyDataSetChanged();

            // Save updated contact list to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("contacts", new HashSet<>(selectedContacts));
            editor.apply();

            Toast.makeText(Add_contact.this, "Contact deleted", Toast.LENGTH_SHORT).show();
            return true;
        });

        helpIcon.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int DRAWABLE_RIGHT = 2;
                if (helpIcon.getCompoundDrawables()[DRAWABLE_RIGHT] != null &&
                        event.getRawX() >= (helpIcon.getRight() - helpIcon.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    Toast.makeText(getApplicationContext(), "Long press on a contact to delete it from the list!", Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            return false;
        });
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK && data != null) {
            retrieveContactInfo(data.getData());
        }
    }

    private void retrieveContactInfo(Uri contactUri) {
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        ContentResolver contentResolver = getContentResolver();

        try (Cursor cursor = contentResolver.query(contactUri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String contactInfo = name + " : " + phoneNumber;
                if (!selectedContacts.contains(contactInfo)) {
                    selectedContacts.add(contactInfo);
                    adapter.notifyDataSetChanged();

                    SharedPreferences sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet("contacts", new HashSet<>(selectedContacts));
                    editor.apply();
                } else {
                    Toast.makeText(this, "Contact already added", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Add_contact", "Error retrieving contact info", e);
        }
    }

    private void loadContactsFromDevice() {
        saveContactsToFirebase();
    }

    private void saveContactsToFirebase() {
        for (String contact : selectedContacts) {
            String contactId = contactsRef.push().getKey();
            if (contactId != null) {
                contactsRef.child(contactId).setValue(contact)
                        .addOnSuccessListener(aVoid -> Toast.makeText(Add_contact.this, "Contact saved successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(Add_contact.this, "Failed to save contact: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void deleteAllContactsFromFirebase() {
        contactsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    selectedContacts.clear();
                    adapter.notifyDataSetChanged();

                    // Save updated contact list to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("contacts_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet("contacts", new HashSet<>(selectedContacts));
                    editor.apply();

                    Toast.makeText(Add_contact.this, "All contacts deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(Add_contact.this, "Failed to delete contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContactsFromDevice();
            } else {
                Toast.makeText(this, "Permission is required to access contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
