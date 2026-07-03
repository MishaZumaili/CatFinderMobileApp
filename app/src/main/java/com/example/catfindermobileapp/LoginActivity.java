package com.example.catfindermobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        txtRegister.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            LoginActivity.this,
                            RegisterActivity.class
                    );

            startActivity(intent);

        });
    }

    private void loginUser() {

        String username =
                etUsername.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Please enter username");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Please enter password");
            return;
        }

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("users");

        ref.get().addOnSuccessListener(snapshot -> {

            boolean found = false;

            for (DataSnapshot snap : snapshot.getChildren()) {

                UserModel user =
                        snap.getValue(UserModel.class);

                if (user == null) continue;

                if (username.equals(user.getUsername())
                        &&
                        password.equals(user.getPassword())) {

                    found = true;

                    getSharedPreferences(
                            "CatFinder",
                            MODE_PRIVATE
                    )
                            .edit()
                            .putString(
                                    "userId",
                                    user.getId()
                            )
                            .apply();

                    Toast.makeText(
                            LoginActivity.this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    getSharedPreferences(
                            "CatFinder",
                            MODE_PRIVATE
                    ).edit()
                            .putString(
                                    "userId",
                                    user.getId()
                            )
                            .apply();

                    startActivity(
                            new Intent(
                                    LoginActivity.this,
                                    MainActivity.class
                            )
                    );

                    finish();
                    break;
                }
            }

            if (!found) {

                Toast.makeText(
                        LoginActivity.this,
                        "Invalid Username or Password",
                        Toast.LENGTH_SHORT
                ).show();
            }

        }).addOnFailureListener(e ->

                Toast.makeText(
                        LoginActivity.this,
                        "Database Error",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}