package com.example.vegefresh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfFermFragment extends Fragment {

    private TextView tvName, tvLastName, tvMiddleName, tvPhoneNumber, tvEmail, tvAdres;
    private Button btnRedact, btnLogout;
    private FirebaseFirestore db;
    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_ferm, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvLastName = view.findViewById(R.id.tvLastName);
        tvMiddleName = view.findViewById(R.id.tvMiddleName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAdres = view.findViewById(R.id.tvAdres);

        btnRedact = view.findViewById(R.id.redactButton);
        btnLogout = view.findViewById(R.id.LogOutBtn);

        db = FirebaseFirestore.getInstance();

        btnRedact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RedactActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                String lastName = documentSnapshot.getString("lastName");
                                String middleName = documentSnapshot.getString("middleName");
                                String phoneNumber = documentSnapshot.getString("phoneNumber");
                                String email = user.getEmail();
                                String address = documentSnapshot.getString("address");

                                tvName.setText("Имя: " + name);
                                tvLastName.setText("Фамилия: " + lastName);
                                tvMiddleName.setText("Отчество: " + middleName);
                                tvPhoneNumber.setText("Телефон: " + phoneNumber);
                                tvEmail.setText("Почта: " + email);
                                tvAdres.setText("Адрес: " + address);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Ошибка: " + e.getMessage());
                        }
                    });
        }
    }
}
