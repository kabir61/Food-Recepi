package com.example.recepiapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Objects;

public class ImageUpload extends AppCompatActivity {
     private ImageView imageUpload;
     private Uri uri;
     private EditText recipeNameET, descriptionET, priceET;
     private String ImageUri;
     private StorageReference mStoreRef;
     private DatabaseReference mDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        init();

        mStoreRef = FirebaseStorage.getInstance().getReference("Images");
        mDataRef = FirebaseDatabase.getInstance().getReference("Recipes");

    }

    private void init() {
        imageUpload = findViewById(R.id.imageUp);
        recipeNameET = findViewById(R.id.recipeNameET);
        descriptionET = findViewById(R.id.descriptionET);
        priceET = findViewById(R.id.priceET);
    }

    public void selectBtn(View view){
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker,1);
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
    private String getFIleExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }
    public void UploadImage(){
        if (uri !=null){
            StorageReference storageReference = mStoreRef.
                    child(System.currentTimeMillis()+"."+ getFIleExtension(uri));

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Image Uploading....");
            progressDialog.show();

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

    }


    public void UpLoadImageBtn(View view) {
        UploadImage();

    }
    public void UploadRecipe(){
        String recipeNames= recipeNameET.getText().toString().trim();
        String desRecipe = descriptionET.getText().toString().trim();
        String priceRecipe = priceET.getText().toString().trim();
        String key = mDataRef.push().getKey();
        FoodData foodData = new FoodData(recipeNames,desRecipe,priceRecipe,ImageUri, key);

        mDataRef.child(key).setValue(foodData).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    Toast.makeText(ImageUpload.this, "Recipe Uploaded", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ImageUpload.this, "Data Not Added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

