package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.SearchListAdepter;
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

public class GenreDetails extends AppCompatActivity {
    Context context = this;
    int id;
    String name;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView genreName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_details);

        Intent intent = getIntent();
        id = intent.getIntExtra("ID", 0);
        name = intent.getStringExtra("Name");

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

    private void loadContents(String name) {

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getContentsReletedToGenre/"+name, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String contentName = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, contentName, year, poster, contentType));
                    }
                }


                RecyclerView genreContentsRecylerView = findViewById(R.id.genreContentsRecylerView);
                SearchListAdepter myadepter = new SearchListAdepter(context, searchList);
                genreContentsRecylerView.setLayoutManager(new GridLayoutManager(context, 3));
                genreContentsRecylerView.setAdapter(myadepter);

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

    @Override
    public void onBackPressed() {
        finish();
    }
}