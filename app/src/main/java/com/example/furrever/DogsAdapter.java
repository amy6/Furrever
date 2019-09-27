package com.example.furrever;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DogsAdapter extends RecyclerView.Adapter<DogsAdapter.DogsViewHolder> {

    private List<Dog> dogs;
    private Context context;

    public DogsAdapter(List<Dog> dogs, Context context) {
        this.dogs = dogs;
        this.context = context;
    }

    @NonNull
    @Override
    public DogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DogsViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DogsViewHolder holder, int position) {
        Dog dog = dogs.get(position);
        if (dog != null) {
            Glide.with(context)
                    .load(dog.getImgUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(250, 250)
                    .dontAnimate()
                    .dontTransform()
                    .centerCrop()
                    .into(holder.dogImage);
        }
    }

    @Override
    public int getItemCount() {
        return dogs == null ? 0 : dogs.size();
    }

    public void setDogs(List<Dog> dogsList) {
        dogs = dogsList;
        notifyDataSetChanged();
        Log.d("DOG", "Notified adapter about the update");
    }

    public static class DogsViewHolder extends RecyclerView.ViewHolder {

        private ImageView dogImage;

        public DogsViewHolder(@NonNull View itemView) {
            super(itemView);
            dogImage = itemView.findViewById(R.id.dogImage);
        }
    }
}
