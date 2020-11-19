package com.example.recepiapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
   private RecyclerView recyclerView;
   private List<FoodData> myFoodList;
   private DatabaseReference databaseReference;
   private ValueEventListener valueEventListener;
   private ProgressDialog progressDialog;
   private MyAdapter myAdapter;
   private TextView textET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycleViewId);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        textET = findViewById(R.id.textET);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Items.....");

        myFoodList = new ArrayList<>();

        myAdapter = new MyAdapter(MainActivity.this, myFoodList);
        recyclerView.setAdapter(myAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");
        progressDialog.show();

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myFoodList.clear();

                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    FoodData food = itemSnapshot.getValue(FoodData.class);
                    myFoodList.add(food);
                }
                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();

            }
        });

        textET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });

    }

    private void filter(String text) {
        ArrayList<FoodData> filterList = new ArrayList<>();
        for (FoodData item: myFoodList){
            if (item.getItemName().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }

        myAdapter.filteredList(filterList);

    }
    public void UploadBtn(View view) {
        startActivity(new Intent(this,ImageUpload.class));
    }
}