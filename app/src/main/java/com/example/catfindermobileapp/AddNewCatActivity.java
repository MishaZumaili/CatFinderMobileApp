package com.example.catfindermobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewCatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    Uri imageUri;

    EditText etPetName, etAge;
    Spinner spinnerBreed, spinnerGender, spinnerSize;
    ImageView imgPreview;

    TextView btnAddCat;
    Button btnAddPhoto;

    TextView navHome, navQR, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_cat);

        etPetName = findViewById(R.id.etPetName);
        etAge = findViewById(R.id.etAge);

        spinnerBreed = findViewById(R.id.spinnerBreed);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerSize = findViewById(R.id.spinnerSize);

        imgPreview = findViewById(R.id.imgPreview);

        btnAddCat = findViewById(R.id.btnAddCat);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        setupSpinners();

        btnAddPhoto.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");

            startActivityForResult(
                    intent,
                    PICK_IMAGE
            );
        });

        btnAddCat.setOnClickListener(v -> saveCat());

        // HOME

        navHome.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                AddNewCatActivity.this,
                                MainActivity.class
                        )
                )
        );

        // QR

        navQR.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                AddNewCatActivity.this,
                                QRScannerActivity.class
                        )
                )
        );

        // PROFILE

        navProfile.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                AddNewCatActivity.this,
                                ProfileActivity.class
                        )
                )
        );
    }

    private void saveCat() {

        String name =
                etPetName.getText()
                        .toString()
                        .trim();

        String age =
                etAge.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            etPetName.setError("Required");
            etPetName.requestFocus();
            return;
        }

        if (age.isEmpty()) {

            etAge.setError("Required");
            etAge.requestFocus();
            return;
        }

        if (spinnerBreed.getSelectedItem()
                .toString()
                .equals("Select Breed")) {

            Toast.makeText(
                    this,
                    "Please select breed",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (spinnerGender.getSelectedItem()
                .toString()
                .equals("Select Gender")) {

            Toast.makeText(
                    this,
                    "Please select gender",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (spinnerSize.getSelectedItem()
                .toString()
                .equals("Select Size")) {

            Toast.makeText(
                    this,
                    "Please select size",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (imageUri == null) {

            Toast.makeText(
                    this,
                    "Please select image",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("cats");

        String id = ref.push().getKey();

        if (id == null)
            return;

        String ownerId =
                getSharedPreferences(
                        "CatFinder",
                        MODE_PRIVATE
                )
                        .getString(
                                "userId",
                                ""
                        );

        String imageBase64 =
                imageToBase64();

        CatModel cat = new CatModel(
                id,
                ownerId,
                name,
                age,
                spinnerBreed.getSelectedItem().toString(),
                spinnerGender.getSelectedItem().toString(),
                spinnerSize.getSelectedItem().toString(),
                "SAFE",
                imageUri.toString(),
                imageBase64
        );


        ref.child(id)
                .setValue(cat)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddNewCatActivity.this,
                            "Cat Added Successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                AddNewCatActivity.this,
                                "Failed to add cat",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void setupSpinners() {

        String[] breed = {
                "Select Breed",
                "Persian",
                "British",
                "Maine Coon",
                "Siamese",
                "Kampung"
        };

        String[] gender = {
                "Select Gender",
                "Male",
                "Female"
        };

        String[] size = {
                "Select Size",
                "Small",
                "Medium",
                "Large"
        };

        spinnerBreed.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        breed
                )
        );

        spinnerGender.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        gender
                )
        );

        spinnerSize.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        size
                )
        );
    }

    private String imageToBase64() {

        try {

            Bitmap bitmap =
                    ((BitmapDrawable)
                            imgPreview.getDrawable())
                            .getBitmap();

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    70,
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

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {

            imageUri = data.getData();

            imgPreview.setImageURI(
                    imageUri
            );
        }
    }
}