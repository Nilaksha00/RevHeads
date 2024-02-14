package com.example.revheads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText editTextEmail, editTextPassword;
    private Button loginButton;
    private TextView registerLink;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("UserDetails", MODE_PRIVATE);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        final String Email = editTextEmail.getText().toString().trim();
        String Password = editTextPassword.getText().toString().trim();

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

        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUserRole();
                    Toast.makeText(LoginActivity.this, "Login Successfully",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Incorrect Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkUserRole() {
        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userRole = document.getString("role");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserID", userID);
                        editor.putString("Email", document.getString("email"));
                        editor.putString("FullName", document.getString("fullname"));
                        editor.apply();

                        if ("admin".equals(userRole)) {
//                            Intent memberIntent = new Intent(LoginActivity.this, AdminHomeActivity.class);
//                            startActivity(memberIntent);
                        } else {
                            Intent adminIntent = new Intent(LoginActivity.this, ClientHomeActivity.class);
                            adminIntent.putExtra("FullName", document.getString("fullname"));
                            startActivity(adminIntent);
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error! Try Again",Toast.LENGTH_SHORT).show( );
                }
            }
        });
    }
}