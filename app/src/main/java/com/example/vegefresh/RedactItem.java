package com.example.vegefresh;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class RedactItem extends AppCompatActivity {

    private EditText itemName, itemDescription, itemPrice, itemCount, itemWeight;
    private String itemId;
    private String itemPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redact_item);

        itemName = findViewById(R.id.item_name);
        itemDescription = findViewById(R.id.item_description);
        itemPrice = findViewById(R.id.item_price);
        itemCount = findViewById(R.id.item_count);
        itemWeight = findViewById(R.id.item_weight);
        ImageView itemImage = findViewById(R.id.item_image);
        Button editItemButton = findViewById(R.id.edit_item_button);
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        itemId = getIntent().getStringExtra("itemId");
        itemPath = getIntent().getStringExtra("itemPath");
        Log.d("RedactItem", "Received itemId: " + itemId);
        Log.d("RedactItem", "Received itemPath: " + itemPath);

        String name = getIntent().getStringExtra("itemName");
        String description = getIntent().getStringExtra("itemDescription");
        String price = getIntent().getStringExtra("itemPrice");
        String count = getIntent().getStringExtra("itemCount");
        String weight = getIntent().getStringExtra("itemWeight");
        String photoUrl = getIntent().getStringExtra("itemPhoto");

        itemName.setText(name);
        itemDescription.setText(description);
        itemPrice.setText(price);
        itemCount.setText(count);
        itemWeight.setText(weight);
        Picasso.get().load(photoUrl).into(itemImage);

        editItemButton.setOnClickListener(v -> editItem());

    }

    private void editItem() {
        String name = itemName.getText().toString().trim();
        String description = itemDescription.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String count = itemCount.getText().toString().trim();
        String weight = itemWeight.getText().toString().trim();

        if (itemId != null) {
            DocumentReference itemRef = FirebaseFirestore.getInstance().document(itemPath);

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("name", name);
            updatedData.put("description", description);
            updatedData.put("price", price);
            updatedData.put("count", count);
            updatedData.put("weight", weight);

            itemRef.update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RedactItem.this, "Данные товара обновлены", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RedactItem.this, "Ошибка при обновлении данных товара", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
