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
import com.dooo.android.WebSeriesDetails;
import com.dooo.android.list.WebSeriesList;

import java.util.List;

public class ReletedWebSeriesListAdepter extends RecyclerView.Adapter<ReletedWebSeriesListAdepter.MyViewHolder> {
    private Context mContext;
    private List<WebSeriesList> mdata;

    Context context;

    public ReletedWebSeriesListAdepter(Context mContext, List<WebSeriesList> mdata) {
        this.mContext = mContext;
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ReletedWebSeriesListAdepter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(AppConfig.contentItem, parent, false);
        return new ReletedWebSeriesListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReletedWebSeriesListAdepter.MyViewHolder holder, int position) {
        holder.setTitle(mdata.get(position));
        holder.setYear(mdata.get(position));
        holder.setImage(mdata.get(position));

        holder.IsPremium(mdata.get(position));

        holder.Movie_Item.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, WebSeriesDetails.class);
            intent.putExtra("ID", mdata.get(position).getID());
            mContext.startActivity(intent);
            ((WebSeriesDetails)mContext).finish();

        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        TextView Year;
        ImageView Thumbnail;

        View Premium_Tag;

        CardView Movie_Item;

        CardView Show_All;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Movie_list_Title);
            Year = (TextView) itemView.findViewById(R.id.Movie_list_Year);
            Thumbnail = (ImageView) itemView.findViewById(R.id.Movie_Item_thumbnail);

            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);

            Movie_Item = itemView.findViewById(R.id.Movie_Item);

            Show_All = itemView.findViewById(R.id.show_all);
        }

        void IsPremium(WebSeriesList type) {
            if(AppConfig.all_series_type == 0) {
                if(type.getType() == 2) {
                    Premium_Tag.setVisibility(View.VISIBLE);
                } else {
                    Premium_Tag.setVisibility(View.GONE);
                }
            } else if(AppConfig.all_series_type == 1) {
                Premium_Tag.setVisibility(View.GONE);
            } else if(AppConfig.all_series_type == 2) {
                Premium_Tag.setVisibility(View.VISIBLE);
            }
        }

        void setTitle(WebSeriesList title_text) {
            Title.setText(title_text.getTitle());
        }

        void setYear(WebSeriesList year_text) {
            Year.setText(year_text.getYear());
        }

        void setImage(WebSeriesList Thumbnail_Image) {
            Glide.with(context)
                    .load(Thumbnail_Image.getThumbnail())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(Thumbnail);
        }
    }
}
