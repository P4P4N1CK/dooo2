package com.dooo.android.adepter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.AppConfig;
import com.dooo.android.R;
import com.dooo.android.list.DownloadLinkList;
import com.dooo.android.list.MultiqualityList;
import com.dooo.android.list.YTStreamList;
import com.dooo.android.utils.DownloadHelper;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.Yts;
import com.dooo.stream.DoooStream;
import com.dooo.stream.Model.StreamList;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadLinkListAdepter extends RecyclerView.Adapter<DownloadLinkListAdepter.MyViewHolder> {
    private Context mContext;
    private View rootView;
    private Dialog downloadDialog;
    private List<DownloadLinkList> mData;
    static ProgressDialog progressDialog;
    String title = "";

    public DownloadLinkListAdepter(Context mContext, View mView, Dialog mDialog, List<DownloadLinkList> mData) {
        this.mContext = mContext;
        this.rootView = mView;
        this.downloadDialog = mDialog;
        this.mData = mData;
    }

    @NonNull
    @NotNull
    @Override
    public DownloadLinkListAdepter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.download_link_item,parent,false);
        return new DownloadLinkListAdepter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DownloadLinkListAdepter.MyViewHolder holder, int position) {
        holder.setName(mData.get(position));
        holder.setQuality(mData.get(position));
        holder.setSize(mData.get(position));

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        holder.link_card.setOnClickListener(view -> {
            if(HelperUtils.checkStoragePermission(mContext)) {
                downloadDialog.dismiss();
                if (mData.get(position).getDownload_type().equalsIgnoreCase("External")) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mData.get(position).getUrl())));
                } else if (mData.get(position).getDownload_type().equalsIgnoreCase("Internal")) {
                    if (mData.get(position).getType().equalsIgnoreCase("mp4")) {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", mData.get(position).getUrl(), "", "");
                    } else if (mData.get(position).getType().equalsIgnoreCase("mkv")) {
                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mkv", mData.get(position).getUrl(), "", "");
                    } else if (mData.get(position).getType().equals("Youtube")) {

                        progressDialog.show();
                        Yts.getlinks(mContext, mData.get(position).getUrl(), new Yts.VolleyCallback() {
                            @Override
                            public void onSuccess(List<YTStreamList> result) {
                                progressDialog.dismiss();
                                Collections.reverse(result);
                                CharSequence[] name = new CharSequence[result.size()];
                                CharSequence[] vid = new CharSequence[result.size()];
                                CharSequence[] token = new CharSequence[result.size()];
                                for (int i = 0; i < result.size(); i++) {
                                    name[i] = result.get(i).getName();
                                    vid[i] = result.get(i).getVid();
                                    token[i] = result.get(i).getToken();
                                }


                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                                        .setTitle("Quality!")
                                        .setItems(name, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Yts.getStreamLinks(mContext, (String) token[which], (String) vid[which], new Yts.VolleyCallback2(){

                                                    @Override
                                                    public void onSuccess(String result) {
                                                        DownloadHelper.startDownload(mContext, rootView, mData.get(position).getName(), "mp4", result, "", "");
                                                    }

                                                    @Override
                                                    public void onError(VolleyError error) {
                                                    }
                                                });
                                            }
                                        })
                                        .setPositiveButton("Close", (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                builder.show();
                            }

                            @Override
                            public void onError(VolleyError error) {
                            }
                        });
                    } else if (mData.get(position).getType().equals("DoodStream") || mData.get(position).getType().equals("Dropbox")
                            || (mData.get(position).getType().equals("Fembed")) || mData.get(position).getType().equals("Facebook")
                            || mData.get(position).getType().equals("MediaShore") || mData.get(position).getType().equals("MixDrop")
                            || mData.get(position).getType().equals("Onedrive") || mData.get(position).getType().equals("OKru")
                            || mData.get(position).getType().equals("StreamTape") || mData.get(position).getType().equals("Twitter")
                            || mData.get(position).getType().equals("VK") || mData.get(position).getType().equals("Vudeo")
                            || mData.get(position).getType().equals("Vimeo")|| mData.get(position).getType().equals("Voesx")
                            || mData.get(position).getType().equals("Yandex") || mData.get(position).getType().equals("GoogleDrive")) {
                        progressDialog.show();

                        new DoooStream(mContext, AppConfig.bGljZW5zZV9jb2Rl, new DoooStream.OnInitialize() {
                            @Override
                            public void onSuccess(DoooStream doooStream) {
                                doooStream.find(mData.get(position).getType(), mData.get(position).getUrl(), true, new DoooStream.OnTaskCompleted() {
                                    @Override
                                    public void onSuccess(List<StreamList> streamList) {
                                        multiQualityDialog(streamList);
                                    }

                                    @Override
                                    public void onError() {
                                        progressDialog.dismiss();
                                        Snackbar snackbar = Snackbar.make(rootView, "No Download Server Avaliable!", Snackbar.LENGTH_SHORT);
                                        snackbar.setAction("Close", v -> snackbar.dismiss());
                                        snackbar.show();
                                    }
                                });
                            }

                            @Override
                            public void onError(RuntimeException e) {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(rootView, "No Download Server Avaliable!", Snackbar.LENGTH_SHORT);
                                snackbar.setAction("Close", v -> snackbar.dismiss());
                                snackbar.show();
                            }
                        });
                    } else {
                        Snackbar snackbar = Snackbar.make(rootView, "No Download Server Avaliable!", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Close", v -> snackbar.dismiss());
                        snackbar.show();
                    }
                }
            }
        });
    }

    public void multiQualityDialog(List<StreamList> streamList) {
        if(streamList.size() == 1) {
            DownloadHelper.startDownload(mContext, rootView, streamList.get(0).getQuality(), streamList.get(0).getExtension(), streamList.get(0).getUrl(), streamList.get(0).getReferer(), streamList.get(0).getCookie());
            progressDialog.hide();
        } else {
            CharSequence[] name = new CharSequence[streamList.size()];
            for (int i = 0; i < streamList.size(); i++) {
                name[i] = streamList.get(i).getQuality();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle("Quality!")
                    .setItems(name, (dialog, which) -> {
                        Log.d("test", streamList.get(which).getQuality()+" : "+streamList.get(which).getExtension()+" : "+streamList.get(which).getUrl()+" : "+streamList.get(which).getReferer()+" : "+streamList.get(which).getCookie());
                        DownloadHelper.startDownload(mContext, rootView, streamList.get(which).getQuality(), streamList.get(which).getExtension(), streamList.get(which).getUrl(), streamList.get(which).getReferer(), streamList.get(which).getCookie());
                    })
                    .setPositiveButton("Close", (dialog, which) -> {

                    });
            progressDialog.hide();
            builder.show();
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView link_card;
        TextView linkName;
        TextView linkQuality;
        TextView linkSize;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            link_card = itemView.findViewById(R.id.link_card);
            linkName = itemView.findViewById(R.id.linkName);
            linkQuality = itemView.findViewById(R.id.linkQuality);
            linkSize = itemView.findViewById(R.id.linkSize);
        }

        void setName(DownloadLinkList nameText) {
            linkName.setText(nameText.getName());
        }
        void setQuality(DownloadLinkList qualityText) {
            linkQuality.setText(qualityText.getQuality());
        }
        void setSize(DownloadLinkList sizeText) {
            linkSize.setText(", "+sizeText.getSize());
        }
    }
}
