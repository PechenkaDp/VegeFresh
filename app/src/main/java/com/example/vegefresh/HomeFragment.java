package com.example.vegefresh;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private LinearLayout itemsContainer;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        itemsContainer = rootView.findViewById(R.id.items_container2);

        db = FirebaseFirestore.getInstance();

        fetchItemsFromFirestore();

        return rootView;
    }

    private void fetchItemsFromFirestore() {
        db.collection("Items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            itemsContainer.removeAllViews();
                            List<Item> itemList = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Item item = document.toObject(Item.class);
                                    itemList.add(item);
                                    Log.d(TAG, "Item fetched: " + item.getName());
                                } else {
                                    Log.d(TAG, "No such document!");
                                }
                            }
                            if (itemList.isEmpty()) {
                                Log.d(TAG, "No items found in collection.");
                            } else {
                                for (Item item : itemList) {
                                    addItemCard(item);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void addItemCard(Item item) {
        View itemCard = LayoutInflater.from(getContext()).inflate(R.layout.item_card2, itemsContainer, false);

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

    public static class Item {
        private String itemId;
        private String name;
        private String description;
        private String price;
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

        public String getCount() {
            return count;
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
