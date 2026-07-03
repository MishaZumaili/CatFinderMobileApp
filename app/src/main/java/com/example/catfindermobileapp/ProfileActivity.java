package com.example.catfindermobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.widget.ImageView;

import android.widget.EditText;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    Button btnUpdate, btnLogout, btnChangePhoto;
    TextView navHome, navQR, navProfile;

    EditText etName;
    EditText etEmail;
    EditText etPhone;
    EditText etState;

    ImageView imgProfile;
    Uri imageUri;
    DatabaseReference databaseReference;

    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etState = findViewById(R.id.etState);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        loadProfile();

        btnChangePhoto.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);

        });

        // UPDATE PROFILE
        btnUpdate.setOnClickListener(v -> {

            String imageBase64 = "";

            if (imageUri != null) {

                imageBase64 = imageToBase64(imageUri);

            }

            updateProfile(imageBase64);

        });

        // LOGOUT
        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

        });

        // HOME
        navHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    MainActivity.class
            );

            startActivity(intent);

        });

        // QR SCANNER
        navQR.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    QRScannerActivity.class
            );

            startActivity(intent);

        });

        // PROFILE
        navProfile.setOnClickListener(v -> {

            Toast.makeText(
                    ProfileActivity.this,
                    "You are already on Profile Page",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }

    private void updateProfile(String imageBase64){
        String name =
                etName.getText()
                        .toString()
                        .trim();

        String email =
                etEmail.getText()
                        .toString()
                        .trim();

        String phone =
                etPhone.getText()
                        .toString()
                        .trim();

        String state =
                etState.getText()
                        .toString()
                        .trim();

        String userId =
                getSharedPreferences(
                        "CatFinder",
                        MODE_PRIVATE
                )
                        .getString(
                                "userId",
                                ""
                        );

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId);

        ref.child("name")
                .setValue(name);

        ref.child("email")
                .setValue(email);

        ref.child("phone")
                .setValue(phone);

        ref.child("state")
                .setValue(state);

        if (!imageBase64.isEmpty()) {

            ref.child("profileImage")
                    .setValue(imageBase64);

        }

        Toast.makeText(
                ProfileActivity.this,
                "Profile Updated Successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void loadProfile() {

        String userId =
                getSharedPreferences(
                        "CatFinder",
                        MODE_PRIVATE
                )
                        .getString(
                                "userId",
                                ""
                        );

        if (userId.isEmpty())
            return;

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId);

        ref.get().addOnSuccessListener(snapshot -> {

            UserModel user =
                    snapshot.getValue(
                            UserModel.class
                    );

            if (user == null)
                return;

            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etState.setText(user.getState());

            if (user.getProfileImage() != null &&
                    !user.getProfileImage().isEmpty()) {

                try {

                    byte[] decodedBytes =
                            Base64.decode(
                                    user.getProfileImage(),
                                    Base64.DEFAULT
                            );

                    Bitmap bitmap =
                            BitmapFactory.decodeByteArray(
                                    decodedBytes,
                                    0,
                                    decodedBytes.length
                            );

                    imgProfile.setImageBitmap(bitmap);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            imageUri = data.getData();
            imgProfile.setImageURI(imageUri);

        }
    }

        private String imageToBase64(Uri uri) {

            try {

                InputStream inputStream =
                        getContentResolver().openInputStream(uri);

                Bitmap bitmap =
                        BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream baos =
                        new ByteArrayOutputStream();

                bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        40,
                        baos
                );

                byte[] imageBytes =
                        baos.toByteArray();

                return Base64.encodeToString(
                        imageBytes,
                        Base64.DEFAULT
                );

            } catch (Exception e) {

                e.printStackTrace();
                return "";

            }

        }
    }