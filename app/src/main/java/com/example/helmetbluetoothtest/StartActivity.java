package com.example.helmetbluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseApp.initializeApp(StartActivity.this);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent userCheckIntent = new Intent(getApplicationContext(), MainActivity.class);
            userCheckIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(userCheckIntent);
        }
    }

    public void signUserIn(View view) {
        String userEmail = email.getText().toString();
        String userPass = password.getText().toString();

        if (userEmail.equals("") && userPass.equals("")){
            Toast.makeText(this, "Please fill the fields above ", Toast.LENGTH_LONG).show();
        }else {
            signIn(userEmail, userPass);
        }
    }

    private void signIn(String userEmail, String userPass) {
        mAuth.signInWithEmailAndPassword(userEmail, userPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent logIn = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(logIn);
                            Toast.makeText(StartActivity.this, "You are signed in", Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(StartActivity.this, "Please check your email and password!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void registerPage(View view) {
        Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}