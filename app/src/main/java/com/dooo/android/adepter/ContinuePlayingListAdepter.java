package com.dooo.android.adepter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dooo.android.AppConfig;
import com.dooo.android.Home;
import com.dooo.android.MovieDetails;
import com.dooo.android.Player;
import com.dooo.android.R;
import com.dooo.android.WebSeriesDetails;
import com.dooo.android.db.resume_content.ResumeContentDatabase;
import com.dooo.android.fragment.HomeFragment;
import com.dooo.android.list.ContinuePlayingList;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContinuePlayingListAdepter extends RecyclerView.Adapter<ContinuePlayingListAdepter.MyViewHolder> {
    private Context context;
    private List<ContinuePlayingList> mData;

    public ContinuePlayingListAdepter(Context context, List<ContinuePlayingList> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @NotNull
    @Override
    public ContinuePlayingListAdepter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.continue_playing_item,parent,false);
        return new ContinuePlayingListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ContinuePlayingListAdepter.MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setYear(mData.get(position));
        holder.setImage(mData.get(position));
        holder.IsPremium(mData.get(position));

        holder.Movie_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mData.get(position).getContent_type().equals("Movie")) {
                    Intent intent = new Intent(context, Player.class);
                    intent.putExtra("contentID", mData.get(position).getContentID());
                    intent.putExtra("SourceID", 0);
                    intent.putExtra("Content_Type", mData.get(position).getContent_type());
                    intent.putExtra("name", mData.get(position).getName());
                    intent.putExtra("source", mData.get(position).getSourceType());
                    intent.putExtra("url", mData.get(position).getSourceUrl());

                    intent.putExtra("position", mData.get(position).getPosition());

                    intent.putExtra("skip_available", 0);
                    intent.putExtra("intro_start", 0);
                    intent.putExtra("intro_end", 0);

                    context.startActivity(intent);
                } else if(mData.get(position).getContent_type().equals("WebSeries")) {
                    Intent intent = new Intent(context, Player.class);
                    intent.putExtra("contentID", mData.get(position).getContentID());
                    intent.putExtra("SourceID", 0);
                    intent.putExtra("name", mData.get(position).getName());
                    intent.putExtra("source", mData.get(position).getSourceType());
                    intent.putExtra("url", mData.get(position).getSourceUrl());

                    intent.putExtra("position", mData.get(position).getPosition());

                    intent.putExtra("skip_available", 0);
                    intent.putExtra("intro_start", 0);
                    intent.putExtra("intro_end", 0);

                    intent.putExtra("Content_Type", "WebSeries");
                    intent.putExtra("Current_List_Position", position);

                    intent.putExtra("Next_Ep_Avilable", "No");

                    context.startActivity(intent);
                }

            }
        });

        holder.deleteItem.setOnClickListener(view -> {
            ResumeContentDatabase db = ResumeContentDatabase.getDbInstance(context);
            db.resumeContentDao().delete(mData.get(position).getId());
            mData.remove(mData.get(position));
            notifyDataSetChanged();

            if(db.resumeContentDao().getResumeContents().isEmpty()) {
                HomeFragment.resume_Layout.setVisibility(View.GONE);
            } else {
                HomeFragment.resume_Layout.setVisibility(View.VISIBLE);
            }
        });

        holder.contentProgress.setMax((int) mData.get(position).getDuration());
        holder.contentProgress.setProgress((int) mData.get(position).getPosition());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Year;
        ImageView poster;
        View Premium_Tag;
        CardView Movie_Item;
        ImageView deleteItem;
        LinearProgressIndicator contentProgress;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            poster = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);
            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);
            Movie_Item = itemView.findViewById(R.id.Movie_Item);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            contentProgress = itemView.findViewById(R.id.contentProgress);
        }

        void setTitle(ContinuePlayingList title_text) {
            Title.setText(title_text.getName());
        }

        void setYear(ContinuePlayingList year_text) {
            Year.setText(year_text.getYear());
        }

        void setImage(ContinuePlayingList Thumbnail_Image) {
            Glide.with(context)
                    .load(Thumbnail_Image.getPoster())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(poster);
        }

        void IsPremium(ContinuePlayingList type) {
            if(AppConfig.all_movies_type == 0) {
                if(type.getType() == 1) {
                    Premium_Tag.setVisibility(View.VISIBLE);
                } else {
                    Premium_Tag.setVisibility(View.GONE);
                }
            } else if(AppConfig.all_movies_type == 1) {
                Premium_Tag.setVisibility(View.GONE);
            } else if(AppConfig.all_movies_type == 2) {
                Premium_Tag.setVisibility(View.VISIBLE);
            }
        }
    }
}
