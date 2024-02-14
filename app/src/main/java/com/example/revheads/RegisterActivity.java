package com.example.revheads;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.revheads.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    EditText editTextFullName, editTextEmail, editTextPassword;
    private Button registerButton;
    private TextView loginLink;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextFullName = findViewById(R.id.fullName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
        }

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser () {
        final String FullName = editTextFullName.getText().toString().trim();
        final String Email = editTextEmail.getText().toString().trim();
        final String Password = editTextPassword.getText().toString().trim();

        if(FullName.isEmpty()){
            editTextFullName.setError("Full Name Required");
            editTextFullName.requestFocus();
            return;
        }

        if(Email.isEmpty()){
            editTextEmail.setError("Email Address Required");
            editTextEmail.requestFocus();
            return;
        }

        if(Password.isEmpty()){
            editTextPassword.setError("Password Required");
            editTextPassword.requestFocus();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                final String Role = "client";
                if(task.isSuccessful()){
                    User user = new User(
                            FullName,
                            Email,
                            Password,
                            Role
                    );
                    updateFirebaseDB(user);
                }
                else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateFirebaseDB(User user){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {

            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(userID);
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("fullname",user.getFullName());
            hashMap.put("email",user.getEmail());
            hashMap.put("role",user.getRole());

            documentReference.set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,("Registered Successfully!"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(RegisterActivity.this,("Register Failed!"), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this,("Failed!"), Toast.LENGTH_SHORT).show();
        }
    }

}
