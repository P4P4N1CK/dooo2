package com.dooo.android.adepter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.dooo.android.LiveTv;
import com.dooo.android.Player;
import com.dooo.android.R;
import com.dooo.android.EmbedPlayer;
import com.dooo.android.YoutubeLivePlayer;
import com.dooo.android.list.LiveTvChannelList;
import com.dooo.android.utils.HelperUtils;

import java.util.List;

public class LiveTvChannelListAdepter extends RecyclerView.Adapter<LiveTvChannelListAdepter.myViewHolder> {
    private Context mContext;
    private List<LiveTvChannelList> mData;

    public LiveTvChannelListAdepter(Context mContext, List<LiveTvChannelList> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mData.size()) ? R.layout.show_all_live_tv_channels : AppConfig.live_tv_channel_item;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
        if(viewType == AppConfig.live_tv_channel_item){
            view = LayoutInflater.from(parent.getContext()).inflate(AppConfig.live_tv_channel_item, parent, false);
        }

        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_all_live_tv_channels, parent, false);
        }
        return new LiveTvChannelListAdepter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        if(position != mData.size()) {
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
                                HelperUtils helperUtils = new HelperUtils((Home) mContext);
                                helperUtils.Buy_Premium_Dialog((Home) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
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
                            HelperUtils helperUtils = new HelperUtils((Home) mContext);
                            helperUtils.Buy_Premium_Dialog((Home) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                        }
                    }
                }
            });
        } else {
            holder.showAllText.setTextColor(Color.parseColor(AppConfig.primeryThemeColor));
            holder.show_all_live_tv_channels.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, LiveTv.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView Title;
        ImageView Banner;

        CardView live_tv_channel_Item;
        CardView show_all_live_tv_channels;

        View Premium_Tag;

        TextView showAllText;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.Live_Tv_Title);
            Banner = (ImageView) itemView.findViewById(R.id.Live_Tv_Banner);
            live_tv_channel_Item = itemView.findViewById(R.id.live_tv_channel_Item);
            show_all_live_tv_channels = itemView.findViewById(R.id.show_all_live_tv_channels);
            Premium_Tag = (View) itemView.findViewById(R.id.Premium_Tag);

            showAllText= (TextView)  itemView.findViewById(R.id.showAllText);
        }

        void setTitle(LiveTvChannelList title_text) {
            Title.setText(title_text.getName());
        }
        void setImage(LiveTvChannelList Banner_Image) {
            Glide.with(mContext)
                    .load(Banner_Image.getBanner())
                    .placeholder(R.drawable.poster_placeholder)
                    .into(Banner);
        }

        void IsPremium(LiveTvChannelList type) {
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
