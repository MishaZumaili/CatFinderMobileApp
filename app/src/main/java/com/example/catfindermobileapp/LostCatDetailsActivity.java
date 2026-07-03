package com.example.catfindermobileapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LostCatDetailsActivity extends AppCompatActivity {

    ImageView imgCat;

    TextView tvPetName;
    TextView tvAge;
    TextView tvBreed;
    TextView tvLocation;
    TextView tvDate;

    Button btnWhatsapp;

    String catId;
    String ownerId = "";
    String catName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_lost_cat_details
        );

        imgCat = findViewById(R.id.imgCat);

        tvPetName = findViewById(R.id.tvPetName);
        tvAge = findViewById(R.id.tvAge);
        tvBreed = findViewById(R.id.tvBreed);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);

        btnWhatsapp =
                findViewById(R.id.btnWhatsapp);

        catId =
                getIntent().getStringExtra(
                        "catId"
                );

        if (catId == null ||
                catId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Invalid Cat",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadCat();

        btnWhatsapp.setOnClickListener(v -> {

            if (ownerId.isEmpty()) {

                Toast.makeText(
                        this,
                        "Owner information not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(ownerId)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {

                                @Override
                                public void onDataChange(
                                        @NonNull DataSnapshot snapshot) {

                                    UserModel user =
                                            snapshot.getValue(
                                                    UserModel.class
                                            );

                                    if (user == null) {

                                        Toast.makeText(
                                                LostCatDetailsActivity.this,
                                                "Owner not found",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    String phone =
                                            user.getPhone();

                                    if (phone == null ||
                                            phone.isEmpty()) {

                                        Toast.makeText(
                                                LostCatDetailsActivity.this,
                                                "Phone number not available",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    phone =
                                            phone.replaceFirst(
                                                    "^0",
                                                    "60"
                                            );

                                    String message =
                                            "Hello. I found a cat matching your lost cat profile.\n\n"
                                                    + "Cat Name: "
                                                    + catName
                                                    + "\n\nPlease contact me.";

                                    String url =
                                            "https://wa.me/"
                                                    + phone
                                                    + "?text="
                                                    + Uri.encode(message);

                                    Intent intent =
                                            new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse(url)
                                            );

                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(
                                        @NonNull DatabaseError error) {

                                }
                            });
        });
    }

    private void loadCat() {

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .child(catId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                CatModel cat =
                                        snapshot.getValue(
                                                CatModel.class
                                        );

                                if (cat == null)
                                    return;

                                ownerId =
                                        cat.getOwnerId();

                                catName =
                                        cat.getCatName();

                                tvPetName.setText(
                                        cat.getCatName()
                                );

                                tvAge.setText(
                                        cat.getAge()
                                );

                                tvBreed.setText(
                                        cat.getBreed()
                                );

                                tvLocation.setText(
                                        cat.getLastLocation()
                                );

                                tvDate.setText(
                                        cat.getLastSeen()
                                );

                                if (cat.getImageBase64() != null &&
                                        !cat.getImageBase64().isEmpty()) {

                                    try {

                                        byte[] decodedBytes =
                                                Base64.decode(
                                                        cat.getImageBase64(),
                                                        Base64.DEFAULT
                                                );

                                        Bitmap bitmap =
                                                BitmapFactory.decodeByteArray(
                                                        decodedBytes,
                                                        0,
                                                        decodedBytes.length
                                                );

                                        imgCat.setImageBitmap(bitmap);

                                    } catch (Exception e) {

                                        imgCat.setImageResource(
                                                R.drawable.ipot
                                        );
                                    }

                                } else {

                                    imgCat.setImageResource(
                                            R.drawable.ipot
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                            }
                        });
    }
}