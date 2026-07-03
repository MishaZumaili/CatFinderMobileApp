package com.example.catfindermobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCatsActivity extends AppCompatActivity {

    RecyclerView recyclerCats;
    CatAdapter adapter;
    TextView navHome, navQR, navProfile;
    ArrayList<CatModel> myCatsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cats);

        recyclerCats = findViewById(R.id.recyclerCats);
        recyclerCats.setLayoutManager(new LinearLayoutManager(this));

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        loadCats();


        navHome.setOnClickListener(v -> {
            startActivity(new Intent(MyCatsActivity.this,
                    MainActivity.class));
        });

        navQR.setOnClickListener(v -> {
            startActivity(new Intent(MyCatsActivity.this,
                    QRScannerActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(MyCatsActivity.this,
                    ProfileActivity.class));
        });
    }


    private void loadCats() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("cats");

        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myCatsList.clear();

                String currentUserId =
                        getSharedPreferences(
                                "CatFinder",
                                MODE_PRIVATE
                        )
                                .getString(
                                        "userId",
                                        ""
                                );


                for (DataSnapshot snap : snapshot.getChildren()) {
                    CatModel cat = snap.getValue(CatModel.class);
                    if (cat != null
                            && currentUserId.equals(cat.getOwnerId())) {

                        myCatsList.add(cat);
                    }
                }
                adapter = new CatAdapter(
                        MyCatsActivity.this,
                        myCatsList,
                        false
                );

                recyclerCats.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyCatsActivity.this, "Failed to load cats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}