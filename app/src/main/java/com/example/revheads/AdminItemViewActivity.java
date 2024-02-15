package com.example.revheads;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class AdminItemViewActivity extends AppCompatActivity {

    EditText nameEditText, priceEditText, descriptionEditText;
    ImageView itemImageView, backIcon;
    private FirebaseFirestore db;
    Button updateButton, deleteButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_view);

        db = FirebaseFirestore.getInstance();

        backIcon = findViewById(R.id.back_icon);
        updateButton = findViewById(R.id.edit_item_button);
        deleteButton = findViewById(R.id.delete_item_button);
        nameEditText = findViewById(R.id.name);
        priceEditText = findViewById(R.id.price);
        descriptionEditText = findViewById(R.id.description);
        itemImageView = findViewById(R.id.item_image);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String image = intent.getStringExtra("image");
        String description = intent.getStringExtra("description");

        nameEditText.setText(name);
        priceEditText.setText(price);
        descriptionEditText.setText(description);
        Picasso.get().load(image).into(itemImageView);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminItemViewActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirm the delete");
                builder.setMessage(
                        "");

                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem(id);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasChanges = !name.equals(nameEditText.getText().toString().trim())
                        || !price.equals(priceEditText.getText().toString().trim())
                        || !description.equals(descriptionEditText.getText().toString().trim());

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminItemViewActivity.this);
                builder.setCancelable(true);

                if(hasChanges){
                    builder.setTitle("Confirm the change");
                    builder.setMessage(
                            "");

                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateItem(
                                            id,
                                            nameEditText.getText().toString().trim(),
                                            priceEditText.getText().toString().trim(),
                                            descriptionEditText.getText().toString().trim()

                                    );
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                }else{
                    builder.setTitle("Item details has not changed");
                    builder.setMessage(
                            "");

                    builder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
                builder.show();
            }
        });


    }

    private void deleteItem(String id) {
        DocumentReference itemRef = db.collection("items").document(id);

        itemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminItemViewActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(AdminItemViewActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminItemViewActivity.this, "Failed to delete Item", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateItem(String id, String updatedName, String updatedPrice, String updatedDescription) {
        if(updatedName.isEmpty()){
            nameEditText.setError("Name is Required");
            nameEditText.requestFocus();
            return;
        }

        if(updatedPrice.isEmpty()){
            priceEditText.setError("Price is Required");
            priceEditText.requestFocus();
            return;
        }

        if(updatedDescription.isEmpty()){
            descriptionEditText.setError("Description is Required");
            descriptionEditText.requestFocus();
            return;
        }

        DocumentReference itemRef = db.collection("items").document(id);

        itemRef.update("name", updatedName,
                        "price", updatedPrice,
                        "description", updatedDescription
                        )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminItemViewActivity.this, "Item updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminItemViewActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminItemViewActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                });
    }

}