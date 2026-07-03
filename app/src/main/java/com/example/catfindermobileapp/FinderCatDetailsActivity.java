package com.example.catfindermobileapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FinderCatDetailsActivity extends AppCompatActivity {

    ImageView imgCat;
    TextView tvPetName;
    TextView tvAge;
    TextView tvBreed;
    TextView tvGender;
    TextView tvSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finder_cat_details);

        imgCat = findViewById(R.id.imgCat);

        tvPetName = findViewById(R.id.tvPetName);
        tvAge = findViewById(R.id.tvAge);

        tvBreed = findViewById(R.id.tvBreed);
        tvGender = findViewById(R.id.tvGender);
        tvSize = findViewById(R.id.tvSize);

        loadCat();
    }

    private void loadCat() {

        String catId = getIntent().getStringExtra("catId");

        if (catId == null)
            return;

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("cats")
                        .child(catId);

        ref.get().addOnSuccessListener(snapshot -> {

            CatModel cat = snapshot.getValue(CatModel.class);

            if (cat == null)
                return;

            tvPetName.setText(cat.getCatName());
            tvAge.setText(cat.getAge());
            tvBreed.setText(cat.getBreed());
            tvGender.setText(cat.getGender());
            tvSize.setText(cat.getSize());

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

                    imgCat.setImageResource(R.drawable.ipot);

                }

            } else {

                imgCat.setImageResource(R.drawable.ipot);

            }

        });
    }
}
