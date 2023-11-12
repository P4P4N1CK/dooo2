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
import com.dooo.android.LiveTv;
import com.dooo.android.R;
import com.dooo.android.EmbedPlayer;
import com.dooo.android.YoutubeLivePlayer;
import com.dooo.android.list.LiveTvAllList;
import com.dooo.android.Player;
import com.dooo.android.utils.HelperUtils;

import java.util.List;

public class LiveTvAllListAdepter extends RecyclerView.Adapter<LiveTvAllListAdepter.myViewHolder> {
    private Context mContext;
    private List<LiveTvAllList> mData;

    public LiveTvAllListAdepter(Context mContext, List<LiveTvAllList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(AppConfig.small_live_tv_channel_item, parent, false);
        return new LiveTvAllListAdepter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        holder.setTitle(mData.get(position));
        holder.setImage(mData.get(position));

        holder.IsPremium(mData.get(position));

        holder.live_tv_channel_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                if(AppConfig.all_live_tv_type == 0) {
                    if(mData.get(position).getType()== 1) {
                        if (mData.get(position).isPlay_Premium()) {
                            if(mData.get(position).getStream_type().equals("Embed")){
                                Intent intent = new Intent(mContext, EmbedPlayer.class);
                                intent.putExtra("url", mData.get(position).getUrl());
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, Player.class);
                                intent.putExtra("contentID", mData.get(position).getID());
                                intent.putExtra("source", mData.get(position).getStream_type());
                                intent.putExtra("url", mData.get(position).getUrl());
                                intent.putExtra("content_type", mData.get(position).getContent_type());
                                intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                                intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());
                                mContext.startActivity(intent);
                            }
                        } else {
                            HelperUtils helperUtils = new HelperUtils((LiveTv) mContext);
                            helperUtils.Buy_Premium_Dialog((LiveTv) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                        }
                    } else {
                        if(mData.get(position).getStream_type().equals("Embed")){
                            Intent intent = new Intent(mContext, EmbedPlayer.class);
                            intent.putExtra("url", mData.get(position).getUrl());
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, Player.class);
                            intent.putExtra("contentID", mData.get(position).getID());
                            intent.putExtra("source", mData.get(position).getStream_type());
                            intent.putExtra("url", mData.get(position).getUrl());
                            intent.putExtra("content_type", mData.get(position).getContent_type());
                            intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                            intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());
                            mContext.startActivity(intent);
                        }
                    }
                } else if(AppConfig.all_live_tv_type == 1) {
                    if(mData.get(position).getStream_type().equals("Embed")){
                        Intent intent = new Intent(mContext, EmbedPlayer.class);
                        intent.putExtra("url", mData.get(position).getUrl());
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, Player.class);
                        intent.putExtra("contentID", mData.get(position).getID());
                        intent.putExtra("source", mData.get(position).getStream_type());
                        intent.putExtra("url", mData.get(position).getUrl());
                        intent.putExtra("content_type", mData.get(position).getContent_type());
                        intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                        intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());
                        mContext.startActivity(intent);
                    }
                } else if(AppConfig.all_live_tv_type == 2) {
                    if (mData.get(position).isPlay_Premium()) {
                        if(mData.get(position).getStream_type().equals("Embed")){
                            Intent intent = new Intent(mContext, EmbedPlayer.class);
                            intent.putExtra("url", mData.get(position).getUrl());
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, Player.class);
                            intent.putExtra("contentID", mData.get(position).getID());
                            intent.putExtra("source", mData.get(position).getStream_type());
                            intent.putExtra("url", mData.get(position).getUrl());
                            intent.putExtra("content_type", mData.get(position).getContent_type());
                            intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                            intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());
                            mContext.startActivity(intent);
                        }
                    } else {
                        HelperUtils helperUtils = new HelperUtils((LiveTv) mContext);
                        helperUtils.Buy_Premium_Dialog((LiveTv) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        ImageView Banner;

        CardView live_tv_channel_Item;

        View Premium_Tag;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Live_Tv_Title);
            Banner = (ImageView) itemView.findViewById(R.id.Live_Tv_Banner);
            live_tv_channel_Item = itemView.findViewById(R.id.live_tv_channel_Item);
            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);
        }

        void setTitle(LiveTvAllList title_text) {
            Title.setText(title_text.getName());
        }
        void setImage(LiveTvAllList Banner_Image) {
            Glide.with(mContext)
                    .load(Banner_Image.getBanner())
                    .placeholder(R.drawable.poster_placeholder)
                    .into(Banner);
        }

        void IsPremium(LiveTvAllList type) {
            if(AppConfig.all_live_tv_type == 0) {
                if(type.getType() == 1) {
                    Premium_Tag.setVisibility(View.VISIBLE);
                } else {
                    Premium_Tag.setVisibility(View.GONE);
                }
            } else if(AppConfig.all_live_tv_type == 1) {
                Premium_Tag.setVisibility(View.GONE);
            } else if(AppConfig.all_live_tv_type == 2) {
                Premium_Tag.setVisibility(View.VISIBLE);
            }
        }
    }
}
