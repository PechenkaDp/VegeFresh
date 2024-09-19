package com.example.vegefresh;

import android.os.AsyncTask;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class PasswordRecoveryActivity extends AppCompatActivity {

    private EditText etPhone, etEmail;
    private Button btnSendMail, btnBack;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        db = FirebaseFirestore.getInstance();

        etPhone = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        btnSendMail = findViewById(R.id.btnSendMail);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecoveryEmail();
            }
        });
    }

    private void sendRecoveryEmail() {
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(PasswordRecoveryActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Users")
                .whereEqualTo("phone", phone)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                String password = document.getString("password");
                                Executor executor = Executors.newSingleThreadExecutor();
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendEmail(email, password);
                                    }
                                });
                            } else {
                                Toast.makeText(PasswordRecoveryActivity.this, "Пользователь с такой почтой и номером телефона не найден", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("PasswordRecovery", "Возникла ошибка:  ", task.getException());
                        }
                    }
                });
    }

    private void sendEmail(String email, String password) {
        final String username = "e_d.s.produvalov@mpt.ru";
        final String appPassword = "12345678";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, appPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from_email@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Password Recovery");
            message.setText("Пароль от вашего аккаунта: " + password);

            Transport.send(message);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PasswordRecoveryActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
