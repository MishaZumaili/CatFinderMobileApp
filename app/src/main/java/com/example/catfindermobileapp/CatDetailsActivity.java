package com.example.catfindermobileapp;

import android.content.Intent;
import android.net.Uri;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseError;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

public class CatDetailsActivity extends AppCompatActivity {

    EditText etPetName, etAge;

    Spinner spinnerBreed;
    Spinner spinnerGender;
    Spinner spinnerSize;

    Button btnUpdate;
    Button btnViewQR;
    Button btnSetLost;
    Button btnChangePhoto;

    ImageView imgCat;

    TextView navHome;
    TextView navQR;
    TextView navProfile;

    private static final int PICK_IMAGE = 1;

    String catId;

    Uri imageUri;

    String currentStatus = "SAFE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_details);

        etPetName = findViewById(R.id.etPetName);
        etAge = findViewById(R.id.etAge);

        spinnerBreed = findViewById(R.id.spinnerBreed);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerSize = findViewById(R.id.spinnerSize);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnViewQR = findViewById(R.id.btnViewQR);
        btnSetLost = findViewById(R.id.btnSetLost);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        btnChangePhoto.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_PICK);

            intent.setType("image/*");

            startActivityForResult(
                    intent,
                    PICK_IMAGE
            );

        });

        imgCat = findViewById(R.id.imgCat);

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        boolean isFinder =
                getIntent().getBooleanExtra(
                        "isFinder",
                        false
                );

        setupSpinners();

        if (!getIntent().hasExtra("catId")) {
            finish();
            return;
        }

        catId = getIntent().getStringExtra("catId");

        loadCat();

        // UPDATE

        btnUpdate.setOnClickListener(v -> updateCat());

        // VIEW QR

        btnViewQR.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            CatDetailsActivity.this,
                            QRCodeActivity.class
                    );

            intent.putExtra("catId", catId);

            startActivity(intent);
        });

        // SET LOST

        btnSetLost.setOnClickListener(v -> {

            if (currentStatus.equalsIgnoreCase("SAFE")) {

                showLostPopup();

            } else {

                showSafePopup();

            }

        });

        // HIDE OWNER BUTTONS IF OPENED FROM QR SCANNER
        if (isFinder) {

            btnUpdate.setVisibility(View.GONE);

            btnViewQR.setVisibility(View.GONE);

            btnSetLost.setVisibility(View.GONE);

            btnChangePhoto.setVisibility(View.GONE);

        }

        // HOME

        navHome.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            CatDetailsActivity.this,
                            MainActivity.class
                    )
            );
        });

        // QR

        navQR.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            CatDetailsActivity.this,
                            QRScannerActivity.class
                    )
            );
        });

        // PROFILE

        navProfile.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            CatDetailsActivity.this,
                            ProfileActivity.class
                    )
            );
        });
    }

    private void updateCat() {

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
            return;
        }

        if (age.isEmpty()) {

            etAge.setError("Required");
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .child(catId)
                .child("catName")
                .setValue(name);

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .child(catId)
                .child("age")
                .setValue(age);

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .child(catId)
                .child("breed")
                .setValue(
                        spinnerBreed
                                .getSelectedItem()
                                .toString()
                );

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .child(catId)
                .child("gender")
                .setValue(
                        spinnerGender
                                .getSelectedItem()
                                .toString()
                );

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .child(catId)
                .child("size")
                .setValue(
                        spinnerSize
                                .getSelectedItem()
                                .toString()
                );

        if (imageUri != null) {

            FirebaseDatabase.getInstance()
                    .getReference("cats")
                    .child(catId)
                    .child("imageBase64")
                    .setValue(imageToBase64());

        }

        Toast.makeText(
                this,
                "Updated Successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showLostPopup() {

        View view =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_lost_cat,
                                null
                        );

        EditText etDate =
                view.findViewById(R.id.etDate);

        EditText etLocation =
                view.findViewById(R.id.etLocation);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Mark Cat as LOST")
                        .setView(view)
                        .setPositiveButton(
                                "SAVE",
                                null
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String date =
                            etDate.getText()
                                    .toString()
                                    .trim();

                    String location =
                            etLocation.getText()
                                    .toString()
                                    .trim();

                    if (date.isEmpty()) {

                        etDate.setError(
                                "Last Seen Date is required"
                        );

                        etDate.requestFocus();
                        return;
                    }

                    if (location.isEmpty()) {

                        etLocation.setError(
                                "Location is required"
                        );

                        etLocation.requestFocus();
                        return;
                    }

                    FirebaseDatabase.getInstance()
                            .getReference("cats")
                            .child(catId)
                            .child("status")
                            .setValue("LOST")
                            .addOnSuccessListener(unused -> {

                                FirebaseDatabase.getInstance()
                                        .getReference("cats")
                                        .child(catId)
                                        .child("lastSeen")
                                        .setValue(date);

                                FirebaseDatabase.getInstance()
                                        .getReference("cats")
                                        .child(catId)
                                        .child("lastLocation")
                                        .setValue(location);

                                Toast.makeText(
                                        CatDetailsActivity.this,
                                        "Marked as LOST",
                                        Toast.LENGTH_SHORT
                                ).show();

                                currentStatus = "LOST";

                                dialog.dismiss();

                                loadCat();

                            });
                });
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

    private void setSpinner(
            Spinner spinner,
            String value) {

        if (value == null)
            return;

        ArrayAdapter adapter =
                (ArrayAdapter)
                        spinner.getAdapter();

        int position =
                adapter.getPosition(value);

        if (position >= 0) {

            spinner.setSelection(position);
        }
    }
        private void loadCat() {

            FirebaseDatabase.getInstance()
                    .getReference("cats")
                    .child(catId)
                    .addListenerForSingleValueEvent(
                            new com.google.firebase.database.ValueEventListener() {

                                @Override
                                public void onDataChange(
                                        com.google.firebase.database.DataSnapshot snapshot) {

                                    CatModel cat =
                                            snapshot.getValue(
                                                    CatModel.class
                                            );

                                    if (cat == null)
                                        return;

                                    currentStatus = cat.getStatus();

                                    if (currentStatus == null || currentStatus.isEmpty()) {

                                        currentStatus = "SAFE";

                                    }

                                    etPetName.setText(
                                            cat.getCatName()
                                    );

                                    etAge.setText(
                                            cat.getAge()
                                    );

                                    setSpinner(
                                            spinnerBreed,
                                            cat.getBreed()
                                    );

                                    setSpinner(
                                            spinnerGender,
                                            cat.getGender()
                                    );

                                    setSpinner(
                                            spinnerSize,
                                            cat.getSize()
                                    );

                                    if (cat.getImageBase64() != null &&
                                            !cat.getImageBase64().isEmpty()) {

                                        loadBase64Image(
                                                cat.getImageBase64()
                                        );
                                    }

                                    if (currentStatus.equalsIgnoreCase("SAFE")) {

                                        btnSetLost.setText("SET LOST");

                                        btnSetLost.setBackgroundTintList(
                                                android.content.res.ColorStateList.valueOf(
                                                        0xFFFF3B3B
                                                )
                                        );

                                    } else {

                                        btnSetLost.setText("SET SAFE");

                                        btnSetLost.setBackgroundTintList(
                                                android.content.res.ColorStateList.valueOf(
                                                        0xFF4CAF50
                                                )
                                        );

                                    }
                                }

                                @Override
                                public void onCancelled(
                                        DatabaseError error) {

                                }
                            });
        }

    private void loadBase64Image(String base64) {

        try {

            byte[] decodedString =
                    Base64.decode(
                            base64,
                            Base64.DEFAULT
                    );

            Bitmap bitmap =
                    BitmapFactory.decodeByteArray(
                            decodedString,
                            0,
                            decodedString.length
                    );

            imgCat.setImageBitmap(bitmap);

        } catch (Exception e) {

            e.printStackTrace();
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

            imgCat.setImageURI(imageUri);

        }

    }

    private String imageToBase64() {

        try {

            Bitmap bitmap =
                    ((BitmapDrawable)
                            imgCat.getDrawable())
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

    private void showSafePopup() {


        new AlertDialog.Builder(this)

                .setTitle("Cat Found")

                .setMessage("Has your cat been found?")

                .setPositiveButton("YES", (dialog, which) -> {

                    FirebaseDatabase.getInstance()
                            .getReference("cats")
                            .child(catId)
                            .child("status")
                            .setValue("SAFE")
                            .addOnSuccessListener(unused -> {

                                FirebaseDatabase.getInstance()
                                        .getReference("cats")
                                        .child(catId)
                                        .child("lastSeen")
                                        .setValue("");

                                FirebaseDatabase.getInstance()
                                        .getReference("cats")
                                        .child(catId)
                                        .child("lastLocation")
                                        .setValue("");

                                Toast.makeText(
                                        CatDetailsActivity.this,
                                        "Cat marked as SAFE",
                                        Toast.LENGTH_SHORT
                                ).show();

                                currentStatus = "SAFE";

                                dialog.dismiss();

                                loadCat();

                            });

                })

                .setNegativeButton("NO", null)

                .show();

    }

}