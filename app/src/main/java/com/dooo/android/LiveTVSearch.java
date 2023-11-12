package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.LiveTvAllListAdepter;
import com.dooo.android.adepter.SearchListAdepter;
import com.dooo.android.list.LiveTvAllList;
import com.dooo.android.list.SearchList;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.TinyDB;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jetradarmobile.snowfall.SnowfallView;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveTVSearch extends AppCompatActivity {
    Context context = this;
    int onlyPremium = 1;

    String config;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    boolean removeAds = false;
    boolean playPremium = false;
    boolean downloadPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Home_TitleBar_BG));

        setContentView(R.layout.activity_live_tvsearch);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(LiveTVSearch.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(LiveTVSearch.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
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

        EditText searchContentEditText = findViewById(R.id.Search_content_editText);
        View bigSearchLottieAnimation = findViewById(R.id.big_search_Lottie_animation);
        RecyclerView searchLayoutRecyclerView = findViewById(R.id.Search_Layout_RecyclerView);

        TinyDB tinyDB = new TinyDB(this);
        SwitchMaterial includePremiumSwitch = findViewById(R.id.includePremiumSwitch);
        includePremiumSwitch.setChecked(tinyDB.getBoolean("onlyPremiumLiveTV"));
        if(tinyDB.getBoolean("onlyPremiumLiveTV")) {
            onlyPremium = 1;
        } else {
            onlyPremium = 0;
        }
        includePremiumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tinyDB.putBoolean("onlyPremiumLiveTV", isChecked);
                if(isChecked) {
                    onlyPremium = 1;
                } else {
                    onlyPremium = 0;
                }
            }
        });

        searchContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(searchContentEditText.getText()).equals("")) {
                    bigSearchLottieAnimation.setVisibility(View.VISIBLE);
                    searchLayoutRecyclerView.setVisibility(View.GONE);
                } else  {
                    bigSearchLottieAnimation.setVisibility(View.GONE);
                    searchLayoutRecyclerView.setVisibility(View.VISIBLE);

                    searchContent(String.valueOf(searchContentEditText.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });
    }

    void searchContent(String text) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"searchLiveTV", response -> {
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

                RecyclerView searchLayoutRecyclerView = findViewById(R.id.Search_Layout_RecyclerView);
                LiveTvAllListAdepter myadepter = new LiveTvAllListAdepter(context, liveTvAllList);
                searchLayoutRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                searchLayoutRecyclerView.setAdapter(myadepter);

            } else {
                View bigSearchLottieAnimation = findViewById(R.id.big_search_Lottie_animation);
                RecyclerView searchLayoutRecyclerView = findViewById(R.id.Search_Layout_RecyclerView);
                bigSearchLottieAnimation.setVisibility(View.VISIBLE);
                searchLayoutRecyclerView.setVisibility(View.GONE);
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
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("search",text);
                params.put("onlypremium", String.valueOf(onlyPremium));
                return params;
            }
        };
        queue.add(sr);
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        config = sharedPreferences.getString("Config", null);
        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);

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

    @Override
    public void onBackPressed() {
        finish();
    }
}