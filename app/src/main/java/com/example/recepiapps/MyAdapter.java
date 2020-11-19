package com.example.recepiapps;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<FoodViewHolder> {

    private Context context;
    private List<FoodData> myFoodData;
    private int lastPosition = -1;

    public MyAdapter(Context context, List<FoodData> myFoodData) {
        this.context = context;
        this.myFoodData = myFoodData;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_row_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position) {

        Glide.with(context).load(myFoodData.get(position).getItemImage()).into(holder.imageView);

       // holder.imageView.setImageResource(myFoodData.get(position).getItemImage());
        holder.title.setText(myFoodData.get(position).getItemName());
        holder.description.setText(myFoodData.get(position).getItemDescription());
        holder.price.setText(myFoodData.get(position).getItemPrice());

        holder.myCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("Image", myFoodData.get(holder.getAdapterPosition()).getItemImage());
                intent.putExtra("Title",myFoodData.get(holder.getAdapterPosition()).getItemName());
                intent.putExtra("Description", myFoodData.get(holder.getAdapterPosition()).getItemDescription());
                intent.putExtra("Price",myFoodData.get(holder.getAdapterPosition()).getItemPrice());
                intent.putExtra("KeyValue",myFoodData.get(holder.getAdapterPosition()).getKey());
                context.startActivity(intent);

            }
        });
        setAnimation(holder.imageView,position);
    }

    public void setAnimation(View viewToAnimate, int position){
        if (position> lastPosition){
            ScaleAnimation animation=new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,
                    Animation.RELATIVE_TO_SELF,0.5f,
                    Animation.RELATIVE_TO_SELF,0.5f);
            animation.setDuration(1500);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;

        }

    }

    @Override
    public int getItemCount() {
        return myFoodData.size();
    }

    public void filteredList(ArrayList<FoodData> filterList) {
        myFoodData = filterList;
        notifyDataSetChanged();

    }
}

class FoodViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView title, description, price;
    CardView myCardView;


    public FoodViewHolder(View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.cardIV);
        title = itemView.findViewById(R.id.tvTitle);
        description = itemView.findViewById(R.id.tvDescription);
        price = itemView.findViewById(R.id.tvPrice);
        myCardView = itemView.findViewById(R.id.myCardViewId);
    }
}