package com.example.kephas73.meera;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private EditText mUserName, mEmail, mPassword;
    private Button btnRegister;

    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Mapping();
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUserName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (CheckInput(userName, email, password) == true ) {
                    Register(userName, email, password);
                }
            }
        });

    }

    // Ánh xạ các thuộc tính trên màn hình android
    private void Mapping() {
        mUserName = findViewById(R.id.userName);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btnRegister);
        android.support.v7.widget.Toolbar toolBar = findViewById(R.id.toolBal);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean CheckInput(String userName, String email, String password) {
        boolean flag = false;
        if (userName.length() == 0 || email.length() == 0 || password.length() == 0) {
            Toast.makeText(RegisterActivity.this, Message.ERROR_INPUT_EMPTY , Toast.LENGTH_SHORT).show();
            flag = false;
        } else if (password.length() < Const.MINIMUM_CHARACTERS_PASSWORD ) {
            Toast.makeText(RegisterActivity.this, Message.ERROR_INPUT_PASSWORD , Toast.LENGTH_SHORT).show();
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    private void Register (final String userName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference(Database.TABLE_USER).child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put(Database.TABLE_USER_ID, userId);
                            hashMap.put(Database.TABLE_USER_NAME, userName);
                            hashMap.put(Database.TABLE_USER_IMAGE,"default");

                            // Add new database
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, Message.ERROR_REGISTER , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
