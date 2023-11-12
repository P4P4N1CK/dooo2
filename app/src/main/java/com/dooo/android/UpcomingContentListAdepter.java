package com.dooo.android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dooo.android.adepter.AllGenreListAdepter;
import com.dooo.android.adepter.AllMovieListAdepter;
import com.dooo.android.list.GenreList;
import com.dooo.android.list.MovieList;
import com.dooo.android.list.UpcomingContentList;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Random;

public class UpcomingContentListAdepter extends RecyclerView.Adapter<UpcomingContentListAdepter.MyViewHolder> {

    private Context mContext;
    private List<UpcomingContentList> mData;

    public UpcomingContentListAdepter(Context mContext, List<UpcomingContentList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public UpcomingContentListAdepter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_item, parent, false);
        return new UpcomingContentListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingContentListAdepter.MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setImage(mData.get(position));
        holder.setReleaseDate(mData.get(position));
        holder.setDescription(mData.get(position));

        if(!mData.get(position).getTrailer_url().equals("")) {
            holder.trailer_btn.setVisibility(View.VISIBLE);
        } else {
            holder.trailer_btn.setVisibility(View.GONE);
        }
        holder.trailer_btn.setOnClickListener(view->{
            if(!mData.get(position).getTrailer_url().equals("")) {
                Intent intent1 = new Intent(mContext, TrailerPlayer.class);
                intent1.putExtra("Trailer_URL", mData.get(position).getTrailer_url());
                mContext.startActivity(intent1);
            }
        });
        if(mData.get(position).getType() == 1) {
            holder.contentTypeText.setText("Movie");
            holder.contentType.setVisibility(View.VISIBLE);
        } else if(mData.get(position).getType() == 2) {
            holder.contentTypeText.setText("Web Series");
            holder.contentType.setVisibility(View.VISIBLE);
        } else {
            holder.contentTypeText.setText("--");
            holder.contentType.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Title, description, release_date;
        ImageView poster;
        MaterialButton trailer_btn;
        TextView contentTypeText;
        CardView contentType;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            release_date = (TextView) itemView.findViewById(R.id.release_date);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            trailer_btn = (MaterialButton) itemView.findViewById(R.id.trailer_btn);
            contentTypeText = (TextView) itemView.findViewById(R.id.contentTypeText);
            contentType = (CardView) itemView.findViewById(R.id.contentType);
        }

        void setTitle(UpcomingContentList title_text) {
            Title.setText(title_text.getName());
        }
        void setDescription(UpcomingContentList description_text) {
            description.setText(description_text.getDescription());
        }
        void setReleaseDate(UpcomingContentList release_date_text) {
            release_date.setText(release_date_text.getRelease_date());
        }
        void setImage(UpcomingContentList image) {
            Glide.with(mContext)
                    .load(image.getPoster())
                    .placeholder(R.drawable.poster_placeholder)
                    .into(poster);
        }
    }
}
