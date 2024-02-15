package com.example.revheads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    EditText nameEditText, priceEditText, descriptionEditText;
    private Uri imageUri;
    String downloadURL;
    Button addItem, uploadImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        ImageView backIcon = findViewById(R.id.back_icon);
        uploadImage  = findViewById(R.id.upload_button);
        addItem = findViewById(R.id.add_item_button);
        nameEditText = findViewById(R.id.name);
        priceEditText = findViewById(R.id.price);
        descriptionEditText = findViewById(R.id.description);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { onBackPressed(); }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                addItem.setEnabled(false);
                addItem.setBackgroundResource(R.drawable.search_bar);
                choosePicture();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirm to add a the RC Item");
                builder.setMessage(
                        "");

                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addRCItem();

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
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode ==1 && resultCode==RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadItemImage();
        }
    }

    private void uploadItemImage() {
        final String randomKey = UUID.randomUUID().toString();

        StorageReference itemRef = storageRef.child("images/" + randomKey);

        itemRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                itemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        downloadURL = downloadUri.toString();
                        Log.d("Download URL", downloadURL);
                        Toast.makeText(AddItemActivity.this, "Image Added Successfully", Toast.LENGTH_SHORT).show();
                        addItem.setEnabled(true);
                        addItem.setBackgroundResource(R.drawable.primary_button);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddItemActivity.this, "Failed to Add the Image", Toast.LENGTH_SHORT).show();
                        addItem.setEnabled(true);
                        addItem.setBackgroundResource(R.drawable.primary_button);
                    }
                });
            }
        });
    }

    private void addRCItem() {
        String Name = nameEditText.getText().toString().trim();
        String Price = "LKR " + priceEditText.getText().toString().trim() + ".00";
        String Description = descriptionEditText.getText().toString().trim();

        if(Name.isEmpty()){
            nameEditText.setError("Name is Required");
            nameEditText.requestFocus();
            return;
        }

        if(Price.isEmpty()){
            priceEditText.setError("Price is Required");
            priceEditText.requestFocus();
            return;
        }

        if(Description.isEmpty()){
            descriptionEditText.setError("Description is Required");
            descriptionEditText.requestFocus();
            return;
        }


        if(downloadURL == null){
            Toast.makeText(AddItemActivity.this, "Image Required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("name", Name);
        data.put("description", Description);
        data.put("image", downloadURL);
        data.put("price", Price);

        db.collection("items")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AddItemActivity.this, AdminHomeActivity.class);
                    startActivity(i);

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error while adding item", Toast.LENGTH_SHORT).show();
                });



    }

}