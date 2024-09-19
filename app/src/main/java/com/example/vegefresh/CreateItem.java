package com.example.vegefresh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.HashMap;
import java.util.Map;

public class CreateItem extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private EditText itemName, itemDescription, itemPrice, itemCount, itemWeight;
    private Button uploadPhotoButton, createItemButton, backButton;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        imageView = findViewById(R.id.item_image);
        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemPrice = findViewById(R.id.item_price);
        itemCount = findViewById(R.id.item_count);
        itemWeight = findViewById(R.id.item_weight);
        uploadPhotoButton = findViewById(R.id.upload_photo_button);
        createItemButton = findViewById(R.id.create_item_button);
        backButton = findViewById(R.id.back_button);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        uploadPhotoButton.setOnClickListener(v -> openFileChooser());

        createItemButton.setOnClickListener(v -> uploadItem());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadItem() {
        String name = itemName.getText().toString();
        String description = itemDescription.getText().toString();
        String price = itemPrice.getText().toString();
        String count = itemCount.getText().toString();
        String weight = itemWeight.getText().toString();
        String sellerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || count.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Выберите изображение", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = storage.getReference().child("item_images/" + System.currentTimeMillis() + ".jpg");
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        Map<String, Object> item = new HashMap<>();
                        item.put("name", name);
                        item.put("description", description);
                        item.put("price", price);
                        item.put("count", count);
                        item.put("weight", weight);
                        item.put("seller", sellerId);
                        item.put("photo", imageUrl);

                        db.collection("Items")
                                .add(item)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(CreateItem.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(CreateItem.this, "Ошибка добавления товара", Toast.LENGTH_SHORT).show());
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(CreateItem.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show());
    }
}
