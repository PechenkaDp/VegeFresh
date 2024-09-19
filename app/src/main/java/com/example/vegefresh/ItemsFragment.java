package com.example.vegefresh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemsFragment extends Fragment {

    private static final String TAG = "ItemsFragment";
    private static final int REQUEST_EDIT_ITEM = 1;

    private LinearLayout itemsContainer;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items, container, false);

        itemsContainer = rootView.findViewById(R.id.items_container);
        Button addItemButton = rootView.findViewById(R.id.add_item_button);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchItemsFromFirestore();

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateItem.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void fetchItemsFromFirestore() {
        db.collection("Items")
                .whereEqualTo("seller", currentUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            itemsContainer.removeAllViews();
                            List<Item> itemList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                itemList.add(item);
                            }
                            for (Item item : itemList) {
                                addItemCard(item);
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void addItemCard(Item item) {
        View itemCard = LayoutInflater.from(getContext()).inflate(R.layout.item_card, itemsContainer, false);

        TextView itemName = itemCard.findViewById(R.id.item_name);
        ImageView itemImage = itemCard.findViewById(R.id.item_image);
        TextView itemPrice = itemCard.findViewById(R.id.item_price);

        itemName.setText(item.getName());
        Picasso.get().load(item.getPhoto()).into(itemImage);
        itemPrice.setText("Цена: " + item.getPrice());

        itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RedactItem.class);
                intent.putExtra("itemId", item.getItemId());
                intent.putExtra("itemName", item.getName());
                intent.putExtra("itemDescription", item.getDescription());
                intent.putExtra("itemPrice", item.getPrice());
                intent.putExtra("itemCount", item.getCount());
                intent.putExtra("itemWeight", item.getWeight());
                intent.putExtra("itemSeller", item.getSeller());
                intent.putExtra("itemPhoto", item.getPhoto());
                startActivity(intent);
            }
        });

        itemsContainer.addView(itemCard);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_ITEM) {
            if (resultCode == getActivity().RESULT_OK) {
                fetchItemsFromFirestore();
            }
        }
    }

    public static class Item {
        private String itemId;
        private String name;
        private String description;
        private String price;
        private String data;
        private String count;
        private String weight;
        private String seller;
        private String photo;

        public Item() {
        }

        public String getItemId() {
            return itemId;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getPrice() {
            return price;
        }

        public String getData() {
            return data;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getWeight() {
            return weight;
        }

        public String getSeller() {
            return seller;
        }

        public String getPhoto() {
            return photo;
        }
    }
}
