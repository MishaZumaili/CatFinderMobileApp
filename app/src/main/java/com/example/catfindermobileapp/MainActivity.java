package com.example.catfindermobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView btnAddCat, btnViewMyCats, btnViewLostCats, txtCatCount;
    TextView navHome, navQR, navProfile;

    RecyclerView rvMyCats, rvLostCats;

    CatAdapter myAdapter;
    LostCatAdapter lostAdapter;
    ArrayList<CatModel> myCatsList = new ArrayList<>();
    ArrayList<CatModel> lostCatsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddCat = findViewById(R.id.btnAddCat);
        btnViewMyCats = findViewById(R.id.btnViewMyCats);
        btnViewLostCats = findViewById(R.id.btnViewLostCats);
        txtCatCount = findViewById(R.id.txtCatCount);

        rvMyCats = findViewById(R.id.rvMyCats);
        rvLostCats = findViewById(R.id.rvLostCats);

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        rvMyCats.setLayoutManager(new LinearLayoutManager(this));
        rvLostCats.setLayoutManager(new LinearLayoutManager(this));

        btnAddCat.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            MainActivity.this,
                            AddNewCatActivity.class
                    )
            );
        });

        btnViewMyCats.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MyCatsActivity.class)));

        btnViewLostCats.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CommunityLostCatActivity.class)));

        navHome.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MainActivity.class)));
        navQR.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, QRScannerActivity.class)));
        navProfile.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        loadHomeData();
    }

    private void loadHomeData() {

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("cats");

        ref.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        myCatsList.clear();
                        lostCatsList.clear();

                        int total = 0;

                        String currentUserId =
                                getSharedPreferences(
                                        "CatFinder",
                                        MODE_PRIVATE
                                )
                                        .getString(
                                                "userId",
                                                ""
                                        );

                        for (DataSnapshot snap :
                                snapshot.getChildren()) {

                            CatModel cat =
                                    snap.getValue(
                                            CatModel.class
                                    );

                            if (cat == null)
                                continue;

                            if (currentUserId.equals(
                                    cat.getOwnerId())) {

                                total++;

                                if (!"LOST".equalsIgnoreCase(
                                        cat.getStatus())
                                        && myCatsList.size() < 3) {

                                    myCatsList.add(cat);
                                }
                            }

                            if ("LOST".equalsIgnoreCase(
                                    cat.getStatus())
                                    && lostCatsList.size() < 3) {

                                lostCatsList.add(cat);
                            }
                        }

                        txtCatCount.setText(
                                total + " Registered Cats"
                        );

                        if (myAdapter == null) {

                            myAdapter =
                                    new CatAdapter(
                                            MainActivity.this,
                                            myCatsList,
                                            true
                                    );

                            rvMyCats.setAdapter(
                                    myAdapter
                            );

                        } else {

                            myAdapter.notifyDataSetChanged();
                        }

                        if (lostAdapter == null) {

                            lostAdapter = new LostCatAdapter(
                                    MainActivity.this,
                                    lostCatsList,
                                    true
                            );

                            rvLostCats.setAdapter(
                                    lostAdapter
                            );

                        } else {

                            lostAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                    }
                }
        );
    }
}