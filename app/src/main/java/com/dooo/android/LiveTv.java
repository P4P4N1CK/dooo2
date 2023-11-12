package com.dooo.android;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.LiveTvAllListAdepter;
import com.dooo.android.adepter.LiveTvGenreListAdepter;
import com.dooo.android.list.LiveTvAllList;
import com.dooo.android.list.LiveTvGenreList;
import com.dooo.android.utils.HelperUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jetradarmobile.snowfall.SnowfallView;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class LiveTv extends AppCompatActivity {

    String config;
    int shuffleContents;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    boolean removeAds = false;
    boolean playPremium = false;
    boolean downloadPremium = false;

    SwipeRefreshLayout liveTVSwipeRefreshLayout;

    LinearLayout LivetvgenreLayout;
    RecyclerView live_tv_genre_list_Recycler_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Home_TitleBar_BG));

        setContentView(R.layout.activity_live_tv);


        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(LiveTv.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(LiveTv.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        // Optional
        properties.setConnectionCallback(hasActiveConnection -> {
            // ...
        });
        properties.setCancelable(true); // Optional
        properties.setNoInternetConnectionTitle("No Internet"); // Optional
        properties.setNoInternetConnectionMessage("Check your Internet connection and try again"); // Optional
        properties.setShowInternetOnButtons(true); // Optional
        properties.setPleaseTurnOnText("Please turn on"); // Optional
        properties.setWifiOnButtonText("Wifi"); // Optional
        properties.setMobileDataOnButtonText("Mobile data"); // Optional

        properties.setOnAirplaneModeTitle("No Internet"); // Optional
        properties.setOnAirplaneModeMessage("You have turned on the airplane mode."); // Optional
        properties.setPleaseTurnOffText("Please turn off"); // Optional
        properties.setAirplaneModeOffButtonText("Airplane mode"); // Optional
        properties.setShowAirplaneModeOffButtons(true); // Optional
        builder.build();

        loadConfig();
        loadUserSubscriptionDetails();

        liveTVSwipeRefreshLayout = findViewById(R.id.LiveTV_swipe_refresh_layout);
        liveTVSwipeRefreshLayout.setOnRefreshListener(this::loadChannels);

        LinearLayout searchTVChannel = findViewById(R.id.searchTVChannel);
        searchTVChannel.setOnClickListener(view-> startActivity(new Intent(LiveTv.this, LiveTVSearch.class)));

        LinearLayout filterTag = findViewById(R.id.filterTag);
        filterTag.setOnClickListener(view->{
            final Dialog dialog = new BottomSheetDialog(LiveTv.this);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.filter_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            ImageView dialogClose = (ImageView) dialog.findViewById(R.id.dialogClose);
            dialogClose.setOnClickListener(v -> dialog.dismiss());


            /*//Catagory
            ThemedToggleButtonGroup themedToggleButtonGroup = dialog.findViewById(R.id.livetvGenres);
            themedToggleButtonGroup.setSelectableAmount(themedToggleButtonGroup.getButtons().size());
            ThemedButton livetvAllGenreBtn = dialog.findViewById(R.id.livetvAllGenreBtn);

            List<ThemedButton> allButtons = themedToggleButtonGroup.getButtons();
            livetvAllGenreBtn.setOnClickListener(v->{
                for (ThemedButton btn : allButtons) {
                    themedToggleButtonGroup.selectButtonWithAnimation(btn.getId());
                }
            });*/







            dialog.show();
        });

        LivetvgenreLayout = findViewById(R.id.LivetvgenreLayout);
        live_tv_genre_list_Recycler_View = findViewById(R.id.live_tv_genre_list_Recycler_View);
        loadLiveTvGenre();


    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        config = sharedPreferences.getString("Config", null);

        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        shuffleContents = jsonObject.get("shuffle_contents").getAsInt();

        int onScreenEffect = jsonObject.get("onscreen_effect").getAsInt();
        SnowfallView SnowfallView = findViewById(R.id.SnowfallView);
        switch (onScreenEffect) {
            case 0:
                SnowfallView.setVisibility(View.GONE);
                break;
            case 1:
                SnowfallView.setVisibility(View.VISIBLE);
                break;
            default:
                SnowfallView.setVisibility(View.GONE);

        }

        loadChannels();
    }

    void loadLiveTvGenre() {
        List<LiveTvGenreList> liveTvGenreList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getLiveTvGenreList", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        liveTvGenreList.add(new LiveTvGenreList(id, name, status));
                    }
                }

                LiveTvGenreListAdepter myadepter = new LiveTvGenreListAdepter(this, liveTvGenreList);
                live_tv_genre_list_Recycler_View.setLayoutManager(new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false));
                live_tv_genre_list_Recycler_View.setAdapter(myadepter);
            } else {
                LivetvgenreLayout.setVisibility(View.GONE);
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
    }

    private void loadUserSubscriptionDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String subscriptionType = sharedPreferences.getString("subscription_type", null);

        String number = String.valueOf(subscriptionType);
        for(int i = 0; i < number.length(); i++) {
            int userSubType = Character.digit(number.charAt(i), 10);
            if(userSubType == 1) {
                removeAds = true;
            } else if(userSubType == 2) {
                playPremium = true;
            } else if(userSubType == 3) {
                downloadPremium = true;
            }
        }
    }

    void loadChannels() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getAllLiveTV", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<LiveTvAllList> liveTvAllList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String banner = rootObject.get("banner").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    String streamType = rootObject.get("stream_type").getAsString();
                    String url = rootObject.get("url").getAsString();
                    int contentType = rootObject.get("content_type").getAsInt();
                    String drm_uuid = rootObject.get("drm_uuid").isJsonNull() ? "" : rootObject.get("drm_uuid").getAsString();
                    String drm_license_uri = rootObject.get("drm_license_uri").isJsonNull() ? "" : rootObject.get("drm_license_uri").getAsString();

                    if (status == 1) {
                        liveTvAllList.add(new LiveTvAllList(id, name, banner, streamType, url, contentType, type, playPremium, drm_uuid, drm_license_uri));
                    }
                }

                if(shuffleContents == 1) {
                    Collections.shuffle(liveTvAllList);
                }

                RecyclerView allLiveTvRecyclerView = findViewById(R.id.All_live_tv_Recycler_View);
                LiveTvAllListAdepter myadepter = new LiveTvAllListAdepter(LiveTv.this, liveTvAllList);
                allLiveTvRecyclerView.setLayoutManager(new GridLayoutManager(LiveTv.this, 2));
                allLiveTvRecyclerView.setAdapter(myadepter);

                liveTVSwipeRefreshLayout.setRefreshing(false);

            } else {
                liveTVSwipeRefreshLayout.setRefreshing(false);
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
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(LiveTv.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(LiveTv.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}