package com.example.vegefresh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityAuthorization extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnBack = findViewById(R.id.btnBack);

        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAuthorization.this, PasswordRecoveryActivity.class));
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(ActivityAuthorization.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ActivityAuthorization", "Attempting to log in with email: " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("ActivityAuthorization", "Login successful for email: " + email);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                user.getIdToken(true);
                            }
                            getUserRole(task.getResult().getUser().getUid());
                        } else {
                            Log.w("ActivityAuthorization", "Login failed for email: " + email, task.getException());
                            Toast.makeText(ActivityAuthorization.this, "Ошибка авторизации.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserRole(String uid) {
        Log.d("ActivityAuthorization", "Fetching user role for UID: " + uid);
        db.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");
                                Log.d("ActivityAuthorization", "User role: " + role);
                                if (role != null) {
                                    switch (role) {
                                        case "User":
                                            startActivity(new Intent(ActivityAuthorization.this, MainWindow.class));
                                            break;
                                        case "Farmer":
                                            startActivity(new Intent(ActivityAuthorization.this, MainWindowFerm.class));
                                            break;
                                        default:
                                            Log.w("ActivityAuthorization", "Unknown user role: " + role);
                                            Toast.makeText(ActivityAuthorization.this, "Такой роли не существует.",
                                                    Toast.LENGTH_SHORT).show();
                                            break;
                                    }
                                } else {
                                    Log.d("ActivityAuthorization", "Role field is missing");
                                    Toast.makeText(ActivityAuthorization.this, "пользователю не назначена роль.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            } else {
                                Log.d("ActivityAuthorization", "No such document for UID: " + uid);
                                Toast.makeText(ActivityAuthorization.this, "Пользователь не найден.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("ActivityAuthorization", "Failed to fetch user role", task.getException());
                            Toast.makeText(ActivityAuthorization.this, "Ошибка в получении роли.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
