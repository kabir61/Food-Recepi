package com.example.recepiapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {
    ImageView imageUpload;
    Uri uri;
    EditText recipeNameET, descriptionET, priceET;
    String ImageUri;
    String key, OldImageUrl;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String recipeNames, describe, prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        init();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            Glide.with(UpdateActivity.this)
                    .load(bundle.getString("OldImage"))
                    .into(imageUpload);
            recipeNameET.setText(bundle.getString("Title"));
            descriptionET.setText(bundle.getString("Description"));
            priceET.setText(bundle.getString("Price"));
            key = bundle.getString("KeyValue");
            OldImageUrl = bundle.getString("OldImage");
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

    }

    private void init() {
        imageUpload = findViewById(R.id.imageUp);
        recipeNameET = findViewById(R.id.recipeNameET);
        descriptionET = findViewById(R.id.descriptionET);
        priceET = findViewById(R.id.priceET);
    }

    public void UpdateSelectBtn(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            imageUpload.setImageURI(uri);
        } else {
            Toast.makeText(this, "You haven't Pick Image", Toast.LENGTH_SHORT).show();
        }
    }

    public void UpdateImageBtn(View view) {
        recipeNames = recipeNameET.getText().toString().trim();
        describe = descriptionET.getText().toString().trim();
        prices = priceET.getText().toString().trim();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Image Uploading....");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference().child("RecipeImage").child(uri.getLastPathSegment());
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri>uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                ImageUri = urlImage.toString();
                UploadRecipe();
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });

    }

    public void UploadRecipe(){
        String recipeNames= recipeNameET.getText().toString().trim();
        String desRecipe = descriptionET.getText().toString().trim();
        String priceRecipe = priceET.getText().toString().trim();
        FoodData foodData = new FoodData(recipeNames,desRecipe,priceRecipe,ImageUri, key);

       databaseReference.child(key).setValue(foodData).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               Toast.makeText(UpdateActivity.this, "Data Updated", Toast.LENGTH_SHORT).show();
           }
       });
    }
}
