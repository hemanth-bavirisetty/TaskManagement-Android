package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameInput, emailInput, passwordInput, firstnameInput, lastnameInput;
    private String username, email, password,firstname, lastname;
    private final String REGISTER_URL = "http://172.16.20.111:8000/api/register/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        EdgeToEdge.enable(this);


        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        firstnameInput = findViewById(R.id.firstname_input);
        lastnameInput = findViewById(R.id.lastname_input);

        findViewById(R.id.register_btn).setOnClickListener(this::registerUser);

        findViewById(R.id.login_redirect_btn).setOnClickListener(v -> navigateToLogin());
    }

    public void registerUser(View view) {
        username = usernameInput.getText().toString().trim();
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        firstname = firstnameInput.getText().toString().trim();
        lastname = lastnameInput.getText().toString().trim();



        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(REGISTER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);

                String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8")+
                        "&first_name="+URLEncoder.encode(firstname, "UTF-8")+
                        "&last_name="+URLEncoder.encode(lastname, "UTF-8");

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes());
                    os.flush();
                }

                int responseCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode == 201 ? connection.getInputStream() : connection.getErrorStream()
                ));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                runOnUiThread(() -> {
                    if (responseCode == 201) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToLogin();
                    } else {
                        Toast.makeText(this, "Registration failed: " + response, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e("RegistrationError", "Error: ", e);
                runOnUiThread(() -> Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void navigateToLogin()
    {
        Intent i=new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
