package com.example.recepiapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsActivity extends AppCompatActivity {

    private ImageView foodImageView;
    private TextView foodTitle,foodDescription, foodPrice;
    private String key= "";
    private String ImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        foodImageView = findViewById(R.id.imageV);
        foodTitle = findViewById(R.id.txtTitle);
        foodDescription = findViewById(R.id.txtDescription);
        foodPrice = findViewById(R.id.txtPrice);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            foodTitle.setText(bundle.getString("Title"));
            foodDescription.setText(bundle.getString("Description"));
            foodPrice.setText(bundle.getString("Price"));
            key = bundle.getString("KeyValue");
            ImageUrl = bundle.getString("Image");

            //foodImageView.setImageResource(bundle.getInt("Image"));

            Glide.with(this).
                    load(bundle.getString("Image")).
                    into(foodImageView);
        }
    }

    public void deleteButton(View view) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Recipes");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(ImageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.child(key).removeValue();
                Toast.makeText(DetailsActivity.this, "Recipe Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();


            }
        });
    }
    public void updateButton(View view) {
        startActivity(new Intent(getApplicationContext(),UpdateActivity.class)
        .putExtra("Title",foodTitle.getText().toString())
         .putExtra("Description",foodDescription.getText().toString())
         .putExtra("Price",foodPrice.getText().toString())
         .putExtra("KeyValue",key)
            .putExtra("OldImage",ImageUrl)



        );

    }
}
