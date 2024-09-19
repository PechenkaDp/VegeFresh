package com.example.vegefresh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vegefresh.MainWindow;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity2 extends AppCompatActivity {

    private EditText etName, etSurname, etSecondName, etAddress;
    private Button btnApply;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etSecondName = findViewById(R.id.etSecondName);
        etAddress = findViewById(R.id.etAddress);

        btnApply = findViewById(R.id.btnApply);

        loadUserData();

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
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
                                if (document != null) {
                                    etName.setText(document.getString("name"));
                                    etSurname.setText(document.getString("surname"));
                                    etSecondName.setText(document.getString("secondName"));
                                    etAddress.setText(document.getString("address"));
                                }
                            } else {
                                Toast.makeText(RegistrationActivity2.this, "Ошибка загрузки информации", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void saveUserData() {
        if (currentUser != null) {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String secondName = etSecondName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            Map<String, Object> user = new HashMap<>();
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
                                Toast.makeText(RegistrationActivity2.this, "Информация сохранена", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrationActivity2.this, MainWindow.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity2.this, "Ошибка сохранения информации", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
