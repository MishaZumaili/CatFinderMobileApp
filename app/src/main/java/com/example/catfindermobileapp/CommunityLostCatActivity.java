package com.example.catfindermobileapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.content.Intent;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class CommunityLostCatActivity extends AppCompatActivity {

    RecyclerView recyclerLostCats;

    LostCatAdapter lostAdapter;

    ArrayList<CatModel> lostCatsList =
            new ArrayList<>();
    TextView navHome, navQR, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_community_lost_cat
        );

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        recyclerLostCats =
                findViewById(
                        R.id.recyclerLostCats
                );

        recyclerLostCats.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadLostCats();

        navHome.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            CommunityLostCatActivity.this,
                            MainActivity.class
                    )
            );
        });

        navQR.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            CommunityLostCatActivity.this,
                            QRScannerActivity.class
                    )
            );
        });

        navProfile.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            CommunityLostCatActivity.this,
                            ProfileActivity.class
                    )
            );
        });
    }

    private void loadLostCats() {

        FirebaseDatabase.getInstance()
                .getReference("cats")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot) {

                                lostCatsList.clear();

                                for (DataSnapshot snap :
                                        snapshot.getChildren()) {

                                    CatModel cat =
                                            snap.getValue(
                                                    CatModel.class
                                            );

                                    if (cat != null) {

                                        cat.setId(
                                                snap.getKey()
                                        );

                                        if ("LOST".equalsIgnoreCase(
                                                cat.getStatus()
                                        )) {

                                            lostCatsList.add(cat);
                                        }
                                    }
                                }

                                lostAdapter =
                                        new LostCatAdapter(
                                                CommunityLostCatActivity.this,
                                                lostCatsList,
                                                false
                                        );

                                recyclerLostCats.setAdapter(
                                        lostAdapter
                                );
                            }



                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {

                            }
                        });
    }
}