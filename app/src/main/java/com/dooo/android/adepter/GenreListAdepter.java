package com.dooo.android.adepter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dooo.android.AllGenre;
import com.dooo.android.AppConfig;
import com.dooo.android.GenreDetails;
import com.dooo.android.R;
import com.dooo.android.list.GenreList;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class GenreListAdepter extends RecyclerView.Adapter<GenreListAdepter.MyViewHolder> {
    private Context context;
    private List<GenreList> genreData;

    public GenreListAdepter(Context context, List<GenreList> genreData) {
        this.context = context;
        this.genreData = genreData;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == genreData.size()) ? R.layout.show_all_genre : R.layout.genre_item;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == R.layout.genre_item){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_item, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_genre, parent, false);
        }
        return new GenreListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        if(position != genreData.size()) {
            holder.setText(genreData.get(position));
            holder.setImage(genreData.get(position));

            holder.genreItem.setOnClickListener(view->{
                Intent intent = new Intent(context, GenreDetails.class);
                intent.putExtra("ID", genreData.get(position).getId());
                intent.putExtra("Name", genreData.get(position).getName());
                context.startActivity(intent);
            });
        } else {
            holder.genreShowMoreItem.setOnClickListener(view->{
                Intent intent2 = new Intent(context, AllGenre.class);
                context.startActivity(intent2);
            });
        }
    }

    @Override
    public int getItemCount() {
        return genreData.size() + 1;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout genreItem;
        RoundedImageView genreImageView;
        TextView genreTextView;

        LinearLayout genreShowMoreItem;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            genreItem = itemView.findViewById(R.id.genreItem);
            genreImageView = itemView.findViewById(R.id.genreImageView);
            genreTextView = itemView.findViewById(R.id.genreTextView);

            genreShowMoreItem = itemView.findViewById(R.id.genreShowMoreItem);
        }

        void setText(GenreList text) {
            genreTextView.setText(text.getName());
        }

        void setImage(GenreList image) {
            if(image.getIcon().equals("")) {
                int no = new Random().nextInt((20 - 1) + 1) + 1;
                Glide.with(context)
                        .load(AppConfig.rawUrl+"assets/images/genre/"+no+".png")
                        .placeholder(R.drawable.poster_placeholder)
                        .into(genreImageView);
            } else {
                Glide.with(context)
                        .load(image.getIcon())
                        .placeholder(R.drawable.poster_placeholder)
                        .into(genreImageView);
            }
        }
    }
}
