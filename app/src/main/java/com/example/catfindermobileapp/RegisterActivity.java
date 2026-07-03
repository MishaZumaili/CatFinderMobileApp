package com.example.catfindermobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPhone,
            etState, etCity, etUsername,
            etPassword, etConfirmPassword;

    Button btnRegister;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etState = findViewById(R.id.etState);
        etCity = findViewById(R.id.etCity);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        btnRegister.setOnClickListener(v -> validateInput());

        txtLogin.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    )
            );
            finish();
        });
    }

    private void validateInput() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()
                || state.isEmpty() || city.isEmpty()
                || username.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid Email Address");
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError("Invalid Phone Number");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password does not match");
            return;
        }

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("users");

        String id = ref.push().getKey();

        if (id == null) return;

        UserModel user = new UserModel(
                id,
                name,
                email,
                phone,
                state,
                city,
                username,
                password
        );

        ref.child(id)
                .setValue(user)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    startActivity(
                            new Intent(
                                    RegisterActivity.this,
                                    LoginActivity.class
                            )
                    );

                    finish();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration Failed",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}