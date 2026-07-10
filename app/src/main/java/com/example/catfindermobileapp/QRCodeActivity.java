package com.example.catfindermobileapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRCodeActivity extends AppCompatActivity {

    ImageView imgQRCode;

    TextView tvCatName;
    TextView navHome;
    TextView navQR;
    TextView navProfile;

    Button btnDownloadQR;
    Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        imgQRCode = findViewById(R.id.imgQRCode);
        btnDownloadQR = findViewById(R.id.btnDownloadQR);

        navHome = findViewById(R.id.navHome);
        navQR = findViewById(R.id.navQR);
        navProfile = findViewById(R.id.navProfile);

        tvCatName = findViewById(R.id.tvCatName);

        String catId = getIntent().getStringExtra("catId");
        String catName = getIntent().getStringExtra("catName");

        if (catId == null || catId.isEmpty()) {
            catId = "UNKNOWN_CAT";
        }

        tvCatName.setText(catName);

        generateQRCode(catId);

        btnDownloadQR.setOnClickListener(v -> saveQRCode(catName));

        navHome.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            QRCodeActivity.this,
                            MainActivity.class
                    );

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
        });

        navQR.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            QRCodeActivity.this,
                            QRScannerActivity.class
                    );

            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            QRCodeActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);
        });
    }

    private void generateQRCode(String text) {

        try {

            BitMatrix bitMatrix =
                    new MultiFormatWriter().encode(
                            text,
                            BarcodeFormat.QR_CODE,
                            500,
                            500
                    );

            Bitmap bitmap =
                    Bitmap.createBitmap(
                            500,
                            500,
                            Bitmap.Config.RGB_565
                    );

            for (int x = 0; x < 500; x++) {

                for (int y = 0; y < 500; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? 0xFF000000
                                    : 0xFFFFFFFF
                    );
                }
            }

            imgQRCode.setImageBitmap(bitmap);
            qrBitmap = bitmap;

        } catch (WriterException e) {

            e.printStackTrace();
        }
    }

    private void saveQRCode(String catName) {

        if (qrBitmap == null) {

            Toast.makeText(
                    this,
                    "QR Code not ready",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            String fileName = catName + "_QR";

            android.provider.MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    qrBitmap,
                    fileName,
                    "CatFinder QR Code"
            );

            Toast.makeText(
                    this,
                    "QR Code saved to Gallery",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Failed to save QR Code",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();
        }
    }
}