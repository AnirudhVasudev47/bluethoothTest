package com.example.helmetbluetoothtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    EditText emailRegister, passwordRegister, usernameRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usernameRegister = findViewById(R.id.usernameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
    }

    public void registerUser(View view){

        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String username = usernameRegister.getText().toString();

        if (email.equals("") && password.equals("") && username.equals("")){
            Toast.makeText(this, "Please fill the fields above ", Toast.LENGTH_LONG).show();
        }else if (password.length() < 8){
            Toast.makeText(this, "Password too weak", Toast.LENGTH_LONG).show();
        }else  {
            registerEmail(username, email, password);
        }
    }

    private void registerEmail(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String currentUid = firebaseUser.getUid();

                    databaseReference = database.getInstance().getReference().child("Users").child(currentUid);

                    HashMap<String, String> users = new HashMap<>();
                    users.put("username", username);
                    users.put("points", String.valueOf(0));
                    users.put("kilometers", String.valueOf(0));

                    databaseReference.setValue(users);

                    Toast.makeText(RegisterActivity.this, "You are registered!!", Toast.LENGTH_SHORT).show();
                    Intent signIn = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(signIn);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}