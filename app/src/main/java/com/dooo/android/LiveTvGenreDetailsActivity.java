package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveTvGenreDetailsActivity extends AppCompatActivity {
    Context context = this;
    int id;
    String name;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView genreName;

    boolean removeAds = false;
    boolean playPremium = false;
    boolean downloadPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tv_genre_details);

        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);
        name = intent.getStringExtra("Name");

        loadUserSubscriptionDetails();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        genreName = findViewById(R.id.genreName);

        genreName.setText(name);

        loadContents(name);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadContents(name);
        });

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor));
    }

    void setColorTheme(int color) {
        TextView genreName = findViewById(R.id.genreName);
        genreName.setTextColor(color);
    }

    private void loadContents(String search) {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getLiveTvReletedToGenre/"+search, response -> {
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

                RecyclerView liveTvGenreContentsRecylerView = findViewById(R.id.liveTvGenreContentsRecylerView);
                LiveTvAllListAdepter myadepter = new LiveTvAllListAdepter(context, liveTvAllList);
                liveTvGenreContentsRecylerView.setLayoutManager(new GridLayoutManager(context, 2));
                liveTvGenreContentsRecylerView.setAdapter(myadepter);

            }
            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}