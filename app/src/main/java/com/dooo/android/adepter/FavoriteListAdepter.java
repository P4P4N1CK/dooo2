package com.dooo.android.adepter;

import android.content.Context;
import android.content.Intent;
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
import com.dooo.android.R;
import com.dooo.android.list.FavoriteList;
import com.dooo.android.MovieDetails;
import com.dooo.android.WebSeriesDetails;

import java.util.List;

public class FavoriteListAdepter extends RecyclerView.Adapter<FavoriteListAdepter.MyViewHolder> {

    Context mContext;
    private List<FavoriteList> mData;
    Context context;

    public FavoriteListAdepter(Context mContext, List<FavoriteList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public FavoriteListAdepter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(AppConfig.contentItem,parent,false);
        return new FavoriteListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteListAdepter.MyViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setYear(mData.get(position));
        holder.setImage(mData.get(position));

        holder.IsPremium(mData.get(position));

        holder.Movie_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mData.get(position).getContent_type().equals("Movie")) {
                    Intent intent = new Intent(mContext, MovieDetails.class);
                    intent.putExtra("ID", mData.get(position).getID());
                    mContext.startActivity(intent);
                } else if(mData.get(position).getContent_type().equals("Web%20Series")) {
                    Intent intent = new Intent(mContext, WebSeriesDetails.class);
                    intent.putExtra("ID", mData.get(position).getID());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView Title;
        TextView Year;
        ImageView Thumbnail;

        View Premium_Tag;

        CardView Movie_Item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            Thumbnail = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);

            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);

            Movie_Item = itemView.findViewById(R.id.Movie_Item);
        }

        void IsPremium(FavoriteList type) {
            if(type.getType() == 1) {
                Premium_Tag.setVisibility(View.VISIBLE);
            } else {
                Premium_Tag.setVisibility(View.GONE);
            }
        }

        void setTitle(FavoriteList title_text) {
            Title.setText(title_text.getName());
        }

        void setYear(FavoriteList year_text) {
            Year.setText(year_text.getRelease_Date());
        }

        void setImage(FavoriteList Thumbnail_Image) {
            Glide.with(context)
                    .load(Thumbnail_Image.getPoster())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(Thumbnail);
        }
    }
}
