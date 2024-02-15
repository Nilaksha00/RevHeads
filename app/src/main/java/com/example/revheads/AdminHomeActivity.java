package com.example.revheads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.revheads.adapter.RCItemListAdapter;
import com.example.revheads.model.RCItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {

    RecyclerView itemRecycler;
    RCItemListAdapter rcItemListAdapter;
    List<RCItem> itemList;
    FirebaseFirestore firebaseFirestore;
    ImageView addItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        addItem = findViewById(R.id.add_button);

        firebaseFirestore = FirebaseFirestore.getInstance();

        itemList = new ArrayList<>();
        rcItemListAdapter = new RCItemListAdapter(this, itemList);
        setItemRecycler();
        fetchItems();

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setItemRecycler() {
        itemRecycler = findViewById(R.id.item_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        itemRecycler.setLayoutManager(layoutManager);
        itemRecycler.setAdapter(rcItemListAdapter);
    }

    private void fetchItems() {
        firebaseFirestore.collection("items")
                .get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {

                            String id = document.getId();
                            String name = document.getString("name");
                            String price = document.getString("price");
                            String description = document.getString("description");
                            String image = document.getString("image");

                            RCItem item = new RCItem(id, name, price, description, image);
                            itemList.add(item);

                        }

                        rcItemListAdapter.notifyDataSetChanged();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}