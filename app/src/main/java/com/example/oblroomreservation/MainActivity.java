package com.example.oblroomreservation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authListener;

    private EditText EmailLogin;
    private EditText PasswLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        EmailLogin = findViewById(R.id.EmailEditT);
        PasswLogin = findViewById(R.id.PasswEditT);

        Button loginBtn = findViewById(R.id.LoginBtn);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() !=null) {
                    String email = EmailLogin.getText().toString();
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    private void startSignIn() {
        String email = EmailLogin.getText().toString();
        String passw = PasswLogin.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(passw)) {
            Toast.makeText(MainActivity.this, "Login fields are empty", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}
