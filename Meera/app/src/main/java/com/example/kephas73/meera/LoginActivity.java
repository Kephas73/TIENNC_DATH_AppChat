package com.example.kephas73.meera;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button btnLogin;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Mapping();
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (CheckInput(email, password) ==true ) {
                    Login(email, password);
                }
            }
        });
    }

    // Ánh xạ các thuộc tính trên màn hình android
    private void Mapping() {
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        android.support.v7.widget.Toolbar toolBar = findViewById(R.id.toolBal);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean CheckInput(String email, String password) {
        boolean flag = true;
        if (email.length() == 0 || password.length() == 0) {
            Toast.makeText(LoginActivity.this, Message.ERROR_INPUT_EMPTY , Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }

    private void Login (String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, Message.ERROR_LOGIN , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
