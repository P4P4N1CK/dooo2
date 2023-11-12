package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.FavoriteListAdepter;
import com.dooo.android.list.FavoriteList;
import com.dooo.android.utils.HelperUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Favorites extends AppCompatActivity {
    boolean shouldExecuteOnResume;

    int userId;

    List<FavoriteList> favoriteList;
    Context context = this;

    RecyclerView favoriteListRecycleview;
    FavoriteListAdepter myadepter;

    SwipeRefreshLayout favoriteSwipeRefreshLayout;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    LottieAnimationView animationView;
    LinearLayout favouriteListLayout;

    String userData = null;

    String tempUserID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.TitleBar_BG));

        setContentView(R.layout.activity_favorites);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Favorites.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Favorites.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        shouldExecuteOnResume = false;

        loadData();

        if(userData != null) {
            tempUserID = String.valueOf(userId);
        } else {
            tempUserID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        favoriteListRecycleview = findViewById(R.id.favorite_list_Recycler_View);
        favoriteSwipeRefreshLayout = findViewById(R.id.favorite_swipe_refresh_layout);
        animationView = findViewById(R.id.animationView);
        favouriteListLayout = findViewById(R.id.favouriteListLayout);

        loadFavouriteList();

        favoriteSwipeRefreshLayout.setOnRefreshListener(this::loadFavouriteList);

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

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor));
    }

    void setColorTheme(int color) {
        TextView titleText = findViewById(R.id.titleText);
        titleText.setTextColor(color);
    }

    void loadFavouriteList() {
        favoriteList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getFavouriteList/"+ tempUserID, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    String contentType = rootObject.get("content_type").getAsString();
                    int contentId = rootObject.get("content_id").getAsInt();

                    if (contentType.equals("Movie")) {
                        loadMovie(contentId, contentType);
                    } else if (contentType.equals("Web%20Series")) {
                        loadWebseries(contentId, contentType);
                    }
                }

                favouriteListLayout.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.GONE);
            } else {
                favoriteSwipeRefreshLayout.setRefreshing(false);

                favouriteListLayout.setVisibility(View.GONE);
                animationView.setVisibility(View.VISIBLE);
            }
        }, error -> {
          // Do nothing
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

    void loadMovie(int contentId, String contentType) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getMovieDetails/"+contentId, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                int id = jsonObject.get("id").getAsInt();
                String name = jsonObject.get("name").getAsString();
                String year = getYearFromDate(jsonObject.get("release_date").getAsString());
                String poster = jsonObject.get("poster").getAsString();
                int type = jsonObject.get("type").getAsInt();
                int status = jsonObject.get("status").getAsInt();

                if (status == 1) {
                    favoriteList.add(new FavoriteList(id, type, name, year, poster, contentType));
                }


                myadepter = new FavoriteListAdepter(context, favoriteList);
                favoriteListRecycleview.setLayoutManager(new GridLayoutManager(context, 3));
                favoriteListRecycleview.setAdapter(myadepter);

                favoriteSwipeRefreshLayout.setRefreshing(false);
            } else {
                favoriteSwipeRefreshLayout.setRefreshing(false);
            }
        }, error -> {
            // Do nothing
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

    void loadWebseries(int contentId, String contentType) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getWebSeriesDetails/"+contentId, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                int id = jsonObject.get("id").getAsInt();
                String name = jsonObject.get("name").getAsString();
                String year = getYearFromDate(jsonObject.get("release_date").getAsString());
                String poster = jsonObject.get("poster").getAsString();
                int type = jsonObject.get("type").getAsInt();
                int status = jsonObject.get("status").getAsInt();

                if(status == 1) {
                    favoriteList.add(new FavoriteList(id, type, name, year, poster, contentType));
                }


                myadepter = new FavoriteListAdepter(context, favoriteList);
                favoriteListRecycleview.setLayoutManager(new GridLayoutManager(context,3));
                favoriteListRecycleview.setAdapter(myadepter);

                favoriteSwipeRefreshLayout.setRefreshing(false);
            } else {
                favoriteSwipeRefreshLayout.setRefreshing(false);
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

    String getYearFromDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = null;
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(parsedDate);
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            userId = jsonObject.get("ID").getAsInt();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldExecuteOnResume) {
            favoriteList.clear();
            myadepter.notifyDataSetChanged();
            loadFavouriteList();
        } else {
            shouldExecuteOnResume = true;
        }

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Favorites.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Favorites.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}