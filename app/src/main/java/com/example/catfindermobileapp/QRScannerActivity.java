package com.example.catfindermobileapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        );

        IntentIntegrator integrator =
                new IntentIntegrator(this);

        integrator.setPrompt("Scan Cat QR Code");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);

        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result =
                IntentIntegrator.parseActivityResult(
                        requestCode,
                        resultCode,
                        data
                );

        if (result != null &&
                result.getContents() != null) {

            String catId = result.getContents();

            FirebaseDatabase.getInstance()
                    .getReference("cats")
                    .child(catId)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (!snapshot.exists()) {

                                        Toast.makeText(
                                                QRScannerActivity.this,
                                                "Cat not found",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        finish();
                                        return;
                                    }

                                    String status = "";

                                    if (snapshot.child("status").exists()) {

                                        status = snapshot.child("status")
                                                .getValue(String.class);

                                    }

                                    Intent intent;

                                    if ("LOST".equalsIgnoreCase(status)) {

                                        intent = new Intent(
                                                QRScannerActivity.this,
                                                LostCatDetailsActivity.class
                                        );

                                        intent.putExtra("isFinder", true);

                                    } else {

                                        intent = new Intent(
                                                QRScannerActivity.this,
                                                FinderCatDetailsActivity.class
                                        );

                                        intent.putExtra("isFinder", true);

                                    }

                                    intent.putExtra("catId", catId);

                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Toast.makeText(
                                            QRScannerActivity.this,
                                            "Database Error",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    finish();

                                }
                            });

        } else {

            finish();

        }
    }
}