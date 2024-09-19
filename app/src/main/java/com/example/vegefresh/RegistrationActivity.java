package com.example.vegefresh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etPhoneNumber;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();

        if (!validateInput(email, password, phone)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            saveUserToDatabase(user.getUid(), email, password, phone);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                etEmail.setError("This email is already registered");
                                etEmail.requestFocus();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Ошибка регистрации: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private boolean validateInput(String email, String password, String phone) {
        if (email.isEmpty()) {
            etEmail.setError("Введите почту");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Почта не соответствует формату");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Введите пароль");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Пароль должен содержать не менее 6 символов");
            etPassword.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhoneNumber.setError("Введите номер телефона");
            etPhoneNumber.requestFocus();
            return false;
        }

        if (!isValidPhone(phone)) {
            etPhoneNumber.setError("Неправильный формат телефонного номера");
            etPhoneNumber.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidPhone(String phone) {
        String phonePattern = "^[+]?[0-9]{10,13}$";
        return phone.matches(phonePattern);
    }

    private void saveUserToDatabase(String userId, String email, String password, String phone) {
        User user = new User(email, password, phone, "-", "-", "-", "User");
        db.collection("Users").document(userId).set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, "Пользователь успешно зарегистрирован!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this, RegistrationActivity2.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Ошибка в регистрации пользователя.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class User {
        public String email, password, phone, name, surname, secondName, role;

        public User(String email, String password, String phone, String name, String surname, String secondName, String role) {
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.name = name;
            this.surname = surname;
            this.secondName = secondName;
            this.role = role;
        }
    }
}
