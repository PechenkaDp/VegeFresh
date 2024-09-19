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
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvLastName, tvMiddleName, tvPhoneNumber, tvEmail;
    private Button btnRedact, btnZakaz, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userDataListener;
    private static final String TAG = "ProfileFragment";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvLastName = view.findViewById(R.id.tvLastName);
        tvMiddleName = view.findViewById(R.id.tvMiddleName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        tvEmail = view.findViewById(R.id.tvEmail);

        btnRedact = view.findViewById(R.id.redactButton);
        btnLogout = view.findViewById(R.id.LogOutBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRedact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RedactActivity.class);
                startActivity(intent);
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserData(currentUser.getUid());
        }
    }

    private void loadUserData(String uid) {
        userDataListener = db.collection("Users").document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String lastName = documentSnapshot.getString("surname");
                            String middleName = documentSnapshot.getString("secondName");
                            String phoneNumber = documentSnapshot.getString("phone");
                            String email = documentSnapshot.getString("email");

                            tvName.setText("Имя: " + name);
                            tvLastName.setText("Фамилия: " + lastName);
                            tvMiddleName.setText("Отчество: " + middleName);
                            tvPhoneNumber.setText("Номер телефона: " + phoneNumber);
                            tvEmail.setText("Почта: " + email);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userDataListener != null) {
            userDataListener.remove();
        }
    }
}
