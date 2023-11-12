package com.dooo.android.adepter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import com.dooo.android.AppConfig;
import com.dooo.android.MovieDetails;
import com.dooo.android.list.DownloadLinkList;
import com.dooo.android.list.EpisodeList;
import com.dooo.android.EmbedPlayer;
import com.dooo.android.Player;
import com.dooo.android.R;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.WebSeriesDetails;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpisodeListAdepter extends RecyclerView.Adapter<EpisodeListAdepter.MyViewHolder> {

    private Context mContext;
    private View rootView;
    private String rootUrl;
    private String apiKey;
    private List<EpisodeList> mData;
    private int contentID;

    Context context;

    public EpisodeListAdepter(int contentID, Context mContext, View mView, String rootUrl, String apiKey, List<EpisodeList> mData) {
        this.contentID = contentID;
        this.mContext = mContext;
        this.rootView = mView;
        this.rootUrl = rootUrl;
        this.apiKey = apiKey;
        this.mData = mData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(AppConfig.webSeriesEpisodeitem,parent,false);
        return new EpisodeListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setEpisode_image(mData.get(position));
        holder.setTitle(mData.get(position));
        holder.setDescription(mData.get(position));
        holder.IsDownloadable(mData.get(position));
        holder.IsPremium(mData.get(position));

        holder.episode_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AppConfig.all_series_type == 0) {
                    if(mData.get(position).getType()== 1) {

                        if (mData.get(position).isPlay_Premium()) {
                            if(mData.get(position).getSource().equals("Embed")){
                                Intent intent = new Intent(mContext, EmbedPlayer.class);
                                intent.putExtra("url", mData.get(position).getUrl());
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, Player.class);
                                intent.putExtra("contentID", contentID);
                                intent.putExtra("SourceID", mData.get(position).getId());
                                intent.putExtra("name", mData.get(position).getEpisoade_Name());
                                intent.putExtra("source", mData.get(position).getSource());
                                intent.putExtra("url", mData.get(position).getUrl());

                                intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                                intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                                intent.putExtra("skip_available", mData.get(position).getSkip_available());
                                intent.putExtra("intro_start", mData.get(position).getIntro_start());
                                intent.putExtra("intro_end", mData.get(position).getIntro_end());

                                intent.putExtra("Content_Type", "WebSeries");
                                intent.putExtra("Current_List_Position", position);

                                int r_pos = position+1;
                                if(r_pos < mData.size()) {
                                    intent.putExtra("Next_Ep_Avilable", "Yes");
                                } else {
                                    intent.putExtra("Next_Ep_Avilable", "No");
                                }

                                //mContext.startActivity(intent);
                                ((WebSeriesDetails) mContext).startActivityForResult(intent, 1);
                            }
                        } else {
                            HelperUtils helperUtils = new HelperUtils((WebSeriesDetails) mContext);
                            helperUtils.Buy_Premium_Dialog((WebSeriesDetails) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                        }

                    } else {
                        if(mData.get(position).getSource().equals("Embed")){
                            Intent intent = new Intent(mContext, EmbedPlayer.class);
                            intent.putExtra("url", mData.get(position).getUrl());
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, Player.class);
                            intent.putExtra("contentID", contentID);
                            intent.putExtra("SourceID", mData.get(position).getId());
                            intent.putExtra("name", mData.get(position).getEpisoade_Name());
                            intent.putExtra("source", mData.get(position).getSource());
                            intent.putExtra("url", mData.get(position).getUrl());

                            intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                            intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                            intent.putExtra("skip_available", mData.get(position).getSkip_available());
                            intent.putExtra("intro_start", mData.get(position).getIntro_start());
                            intent.putExtra("intro_end", mData.get(position).getIntro_end());

                            intent.putExtra("Content_Type", "WebSeries");
                            intent.putExtra("Current_List_Position", position);

                            int r_pos = position+1;
                            if(r_pos < mData.size()) {
                                intent.putExtra("Next_Ep_Avilable", "Yes");
                            } else {
                                intent.putExtra("Next_Ep_Avilable", "No");
                            }

                            //mContext.startActivity(intent);
                            ((WebSeriesDetails) mContext).startActivityForResult(intent, 1);
                        }
                    }
                } else if(AppConfig.all_series_type == 1) {
                    if(mData.get(position).getSource().equals("Embed")){
                        Intent intent = new Intent(mContext, EmbedPlayer.class);
                        intent.putExtra("url", mData.get(position).getUrl());
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, Player.class);
                        intent.putExtra("contentID", contentID);
                        intent.putExtra("SourceID", mData.get(position).getId());
                        intent.putExtra("name", mData.get(position).getEpisoade_Name());
                        intent.putExtra("source", mData.get(position).getSource());
                        intent.putExtra("url", mData.get(position).getUrl());

                        intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                        intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                        intent.putExtra("skip_available", mData.get(position).getSkip_available());
                        intent.putExtra("intro_start", mData.get(position).getIntro_start());
                        intent.putExtra("intro_end", mData.get(position).getIntro_end());

                        intent.putExtra("Content_Type", "WebSeries");
                        intent.putExtra("Current_List_Position", position);

                        int r_pos = position+1;
                        if(r_pos < mData.size()) {
                            intent.putExtra("Next_Ep_Avilable", "Yes");
                        } else {
                            intent.putExtra("Next_Ep_Avilable", "No");
                        }

                        //mContext.startActivity(intent);
                        ((WebSeriesDetails) mContext).startActivityForResult(intent, 1);
                    }
                } else if(AppConfig.all_series_type == 2) {
                    if (mData.get(position).isPlay_Premium()) {
                        if(mData.get(position).getSource().equals("Embed")){
                            Intent intent = new Intent(mContext, EmbedPlayer.class);
                            intent.putExtra("url", mData.get(position).getUrl());
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, Player.class);
                            intent.putExtra("contentID", contentID);
                            intent.putExtra("SourceID", mData.get(position).getId());
                            intent.putExtra("name", mData.get(position).getEpisoade_Name());
                            intent.putExtra("source", mData.get(position).getSource());
                            intent.putExtra("url", mData.get(position).getUrl());

                            intent.putExtra("DrmUuid", mData.get(position).getDrmUuid());
                            intent.putExtra("DrmLicenseUri", mData.get(position).getDrmLicenseUri());

                            intent.putExtra("skip_available", mData.get(position).getSkip_available());
                            intent.putExtra("intro_start", mData.get(position).getIntro_start());
                            intent.putExtra("intro_end", mData.get(position).getIntro_end());

                            intent.putExtra("Content_Type", "WebSeries");
                            intent.putExtra("Current_List_Position", position);

                            int r_pos = position+1;
                            if(r_pos < mData.size()) {
                                intent.putExtra("Next_Ep_Avilable", "Yes");
                            } else {
                                intent.putExtra("Next_Ep_Avilable", "No");
                            }

                            //mContext.startActivity(intent);
                            ((WebSeriesDetails) mContext).startActivityForResult(intent, 1);
                        }
                    } else {
                        HelperUtils helperUtils = new HelperUtils((WebSeriesDetails) mContext);
                        helperUtils.Buy_Premium_Dialog((WebSeriesDetails) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                    }
                }
            }
        });

        holder.Download_btn_image.setOnClickListener(view->{
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getEpisodeDownloadLinks/"+mData.get(position).getId(), response -> {
                if(HelperUtils.checkStoragePermission(context)) {
                    if (!response.equals("No Data Avaliable")) {
                        if (AppConfig.all_series_type == 0) {
                            if (mData.get(position).getType() == 1) {
                                if (mData.get(position).isPlay_Premium()) {
                                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                                    List<DownloadLinkList> downloadLinkList = new ArrayList<>();
                                    for (JsonElement r : jsonArray) {
                                        JsonObject rootObject = r.getAsJsonObject();
                                        int LinkID = rootObject.get("id").getAsInt();
                                        String name = rootObject.get("name").getAsString();
                                        String size = rootObject.get("size").getAsString();
                                        String quality = rootObject.get("quality").getAsString();
                                        int link_order = rootObject.get("link_order").getAsInt();
                                        int episode_id = rootObject.get("episode_id").getAsInt();
                                        String url = rootObject.get("url").getAsString();
                                        String type = rootObject.get("type").getAsString();
                                        String download_type = rootObject.get("download_type").getAsString();

                                        downloadLinkList.add(new DownloadLinkList(LinkID, name, size, quality, link_order, episode_id, url, type, download_type));
                                    }

                                    final Dialog downloadDialog = new Dialog(context);
                                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    downloadDialog.setCancelable(false);
                                    downloadDialog.setContentView(R.layout.download_dialog);
                                    downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    downloadDialog.setCanceledOnTouchOutside(true);

                                    ImageView coupanDialogClose = (ImageView) downloadDialog.findViewById(R.id.Coupan_Dialog_Close);
                                    coupanDialogClose.setOnClickListener(v -> downloadDialog.dismiss());

                                    RecyclerView downloadLinksRecylerView = (RecyclerView) downloadDialog.findViewById(R.id.downloadLinksRecylerView);
                                    DownloadLinkListAdepter myadepter = new DownloadLinkListAdepter(context, rootView, downloadDialog, downloadLinkList);
                                    downloadLinksRecylerView.setLayoutManager(new GridLayoutManager(context, 1));
                                    downloadLinksRecylerView.setAdapter(myadepter);

                                    downloadDialog.show();
                                } else {
                                    HelperUtils helperUtils = new HelperUtils((WebSeriesDetails) mContext);
                                    helperUtils.Buy_Premium_Dialog((WebSeriesDetails) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                                }
                            } else {
                                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                                List<DownloadLinkList> downloadLinkList = new ArrayList<>();
                                for (JsonElement r : jsonArray) {
                                    JsonObject rootObject = r.getAsJsonObject();
                                    int LinkID = rootObject.get("id").getAsInt();
                                    String name = rootObject.get("name").getAsString();
                                    String size = rootObject.get("size").getAsString();
                                    String quality = rootObject.get("quality").getAsString();
                                    int link_order = rootObject.get("link_order").getAsInt();
                                    int episode_id = rootObject.get("episode_id").getAsInt();
                                    String url = rootObject.get("url").getAsString();
                                    String type = rootObject.get("type").getAsString();
                                    String download_type = rootObject.get("download_type").getAsString();

                                    downloadLinkList.add(new DownloadLinkList(LinkID, name, size, quality, link_order, episode_id, url, type, download_type));
                                }

                                final Dialog downloadDialog = new Dialog(context);
                                downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                downloadDialog.setCancelable(false);
                                downloadDialog.setContentView(R.layout.download_dialog);
                                downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                downloadDialog.setCanceledOnTouchOutside(true);

                                ImageView coupanDialogClose = (ImageView) downloadDialog.findViewById(R.id.Coupan_Dialog_Close);
                                coupanDialogClose.setOnClickListener(v -> downloadDialog.dismiss());

                                RecyclerView downloadLinksRecylerView = (RecyclerView) downloadDialog.findViewById(R.id.downloadLinksRecylerView);
                                DownloadLinkListAdepter myadepter = new DownloadLinkListAdepter(context, rootView, downloadDialog, downloadLinkList);
                                downloadLinksRecylerView.setLayoutManager(new GridLayoutManager(context, 1));
                                downloadLinksRecylerView.setAdapter(myadepter);

                                downloadDialog.show();
                            }
                        } else if (AppConfig.all_series_type == 1) {//free
                            JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                            List<DownloadLinkList> downloadLinkList = new ArrayList<>();
                            for (JsonElement r : jsonArray) {
                                JsonObject rootObject = r.getAsJsonObject();
                                int LinkID = rootObject.get("id").getAsInt();
                                String name = rootObject.get("name").getAsString();
                                String size = rootObject.get("size").getAsString();
                                String quality = rootObject.get("quality").getAsString();
                                int link_order = rootObject.get("link_order").getAsInt();
                                int episode_id = rootObject.get("episode_id").getAsInt();
                                String url = rootObject.get("url").getAsString();
                                String type = rootObject.get("type").getAsString();
                                String download_type = rootObject.get("download_type").getAsString();

                                downloadLinkList.add(new DownloadLinkList(LinkID, name, size, quality, link_order, episode_id, url, type, download_type));
                            }

                            final Dialog downloadDialog = new Dialog(context);
                            downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            downloadDialog.setCancelable(false);
                            downloadDialog.setContentView(R.layout.download_dialog);
                            downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            downloadDialog.setCanceledOnTouchOutside(true);

                            ImageView coupanDialogClose = (ImageView) downloadDialog.findViewById(R.id.Coupan_Dialog_Close);
                            coupanDialogClose.setOnClickListener(v -> downloadDialog.dismiss());

                            RecyclerView downloadLinksRecylerView = (RecyclerView) downloadDialog.findViewById(R.id.downloadLinksRecylerView);
                            DownloadLinkListAdepter myadepter = new DownloadLinkListAdepter(context, rootView, downloadDialog, downloadLinkList);
                            downloadLinksRecylerView.setLayoutManager(new GridLayoutManager(context, 1));
                            downloadLinksRecylerView.setAdapter(myadepter);

                            downloadDialog.show();
                        } else if (AppConfig.all_series_type == 2) { //premium
                            if (mData.get(position).isDownloadPremium()) {
                                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                                List<DownloadLinkList> downloadLinkList = new ArrayList<>();
                                for (JsonElement r : jsonArray) {
                                    JsonObject rootObject = r.getAsJsonObject();
                                    int LinkID = rootObject.get("id").getAsInt();
                                    String name = rootObject.get("name").getAsString();
                                    String size = rootObject.get("size").getAsString();
                                    String quality = rootObject.get("quality").getAsString();
                                    int link_order = rootObject.get("link_order").getAsInt();
                                    int episode_id = rootObject.get("episode_id").getAsInt();
                                    String url = rootObject.get("url").getAsString();
                                    String type = rootObject.get("type").getAsString();
                                    String download_type = rootObject.get("download_type").getAsString();

                                    downloadLinkList.add(new DownloadLinkList(LinkID, name, size, quality, link_order, episode_id, url, type, download_type));
                                }

                                final Dialog downloadDialog = new Dialog(context);
                                downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                downloadDialog.setCancelable(false);
                                downloadDialog.setContentView(R.layout.download_dialog);
                                downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                downloadDialog.setCanceledOnTouchOutside(true);

                                ImageView coupanDialogClose = (ImageView) downloadDialog.findViewById(R.id.Coupan_Dialog_Close);
                                coupanDialogClose.setOnClickListener(v -> downloadDialog.dismiss());

                                RecyclerView downloadLinksRecylerView = (RecyclerView) downloadDialog.findViewById(R.id.downloadLinksRecylerView);
                                DownloadLinkListAdepter myadepter = new DownloadLinkListAdepter(context, rootView, downloadDialog, downloadLinkList);
                                downloadLinksRecylerView.setLayoutManager(new GridLayoutManager(context, 1));
                                downloadLinksRecylerView.setAdapter(myadepter);

                                downloadDialog.show();
                            } else {
                                HelperUtils helperUtils = new HelperUtils((WebSeriesDetails) mContext);
                                helperUtils.Buy_Premium_Dialog((WebSeriesDetails) mContext, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                            }
                        }


                    } else {
                        Snackbar snackbar = Snackbar.make(rootView, "No Download Server Avaliable!", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Close", v -> snackbar.dismiss());
                        snackbar.show();
                    }
                }
            }, error -> {
                // Do nothing because There is No Error if error It will return 0
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("x-api-key", AppConfig.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Episode_image;
        TextView Title;
        TextView Description;
        ImageView Download_btn_image;
        LinearLayout Premium_Tag;

        CardView episode_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Episode_image = (ImageView) itemView.findViewById(R.id.Episode_image);
            Title = (TextView) itemView.findViewById(R.id.Title);
            Description = (TextView) itemView.findViewById(R.id.Description);
            Download_btn_image = (ImageView) itemView.findViewById(R.id.Download_btn_image);
            Premium_Tag = (LinearLayout) itemView.findViewById(R.id.Premium_Tag);

            episode_item = itemView.findViewById(R.id.episode_item);
        }

        void setEpisode_image(EpisodeList image) {
            Glide.with(context)
                    .load(image.getEpisoade_image())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(Episode_image);
        }

        void setTitle(EpisodeList title_text) {
            Title.setText(title_text.getEpisoade_Name());
        }

        void setDescription(EpisodeList description_text) {
            Description.setText(description_text.getEpisoade_description());
        }

        void IsDownloadable(EpisodeList type) {
            if(type.getDownloadable() == 1) {
                Download_btn_image.setVisibility(View.VISIBLE);
            } else {
                Download_btn_image.setVisibility(View.GONE);
            }
        }

        void IsPremium(EpisodeList type) {
            if(AppConfig.all_series_type == 0) {

                if(type.getType() == 1) {
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
    }
}
