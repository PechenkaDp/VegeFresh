package com.example.vegefresh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RedactActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etEmail, etName, etSurname, etSecondName, etAddress;
    private Button btnApply, btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        etPhoneNumber = findViewById(R.id.phoneNumber);
        etEmail = findViewById(R.id.email);
        etName = findViewById(R.id.name);
        etSurname = findViewById(R.id.surname);
        etSecondName = findViewById(R.id.secondName);
        etAddress = findViewById(R.id.address);

        btnApply = findViewById(R.id.registerButton);
        btnBack = findViewById(R.id.backButton);

        loadUserData();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadUserData() {
        if (currentUser != null) {
            db.collection("Users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    etPhoneNumber.setText(document.getString("phone"));
                                    etEmail.setText(document.getString("email"));
                                    etName.setText(document.getString("name"));
                                    etSurname.setText(document.getString("surname"));
                                    etSecondName.setText(document.getString("secondName"));
                                    etAddress.setText(document.getString("address"));
                                }
                            } else {
                                Toast.makeText(RedactActivity.this, "Ошибка в получении данных", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUserData() {
        if (currentUser != null) {
            String phone = etPhoneNumber.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String secondName = etSecondName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            Map<String, Object> user = new HashMap<>();
            user.put("phone", phone);
            user.put("email", email);
            user.put("name", name);
            user.put("surname", surname);
            user.put("secondName", secondName);
            user.put("address", address);

            db.collection("Users").document(currentUser.getUid())
                    .set(user, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RedactActivity.this, "Информация сохранена", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RedactActivity.this, "Ошибка сохранения информации", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
