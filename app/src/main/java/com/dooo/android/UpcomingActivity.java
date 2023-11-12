package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.AllMovieListAdepter;
import com.dooo.android.list.MovieList;
import com.dooo.android.list.UpcomingContentList;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpcomingActivity extends AppCompatActivity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        UpcommingContentList();
        SwipeRefreshLayout movieSwipeRefreshLayout = findViewById(R.id.Swipe_Refresh_Layout);
        movieSwipeRefreshLayout.setOnRefreshListener(this::UpcommingContentList);
    }

    void UpcommingContentList() {
        final int[] previousTotal = {0};
        final boolean[] loading = {true};
        int visibleThreshold = 3;
        final int[] firstVisibleItem = new int[1];
        final int[] visibleItemCount = new int[1];
        final int[] totalItemCount = new int[1];
        final int[] currentPage = {0};

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getAllUpcomingContents/"+ currentPage[0], new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    List<UpcomingContentList> upcomingContentList = new ArrayList<>();
                    for (JsonElement r : jsonArray) {
                        JsonObject rootObject = r.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        String name = rootObject.get("name").getAsString();
                        String description = rootObject.get("description").getAsString();
                        String poster = rootObject.get("poster").getAsString();
                        String trailer_url = rootObject.get("trailer_url").getAsString();
                        DateFormat outputFormat = new SimpleDateFormat("ddMMMM yyyy");
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = inputFormat.parse(rootObject.get("release_date").getAsString());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        String release_date = outputFormat.format(date);

                        int type = rootObject.get("type").getAsInt();

                        upcomingContentList.add(new UpcomingContentList(id, name, description, poster, trailer_url, release_date, type));
                    }

                    RecyclerView upcomming_contents_list_recycleview = findViewById(R.id.upcomming_contents_list_recycleview);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
                    upcomming_contents_list_recycleview.setLayoutManager(gridLayoutManager);

//                    FlexboxLayoutManager gridLayoutManager = new FlexboxLayoutManager(context);
//                    gridLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
//                    upcomming_contents_list_recycleview.setLayoutManager(gridLayoutManager);

                    UpcomingContentListAdepter myadepter = new UpcomingContentListAdepter(context, upcomingContentList);
                    upcomming_contents_list_recycleview.setAdapter(myadepter);

                    upcomming_contents_list_recycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            visibleItemCount[0] = upcomming_contents_list_recycleview.getChildCount();
                            totalItemCount[0] = gridLayoutManager.getItemCount();
                            firstVisibleItem[0] = gridLayoutManager.findFirstVisibleItemPosition();

                            if (loading[0]) {
                                if (totalItemCount[0] > previousTotal[0]) {
                                    loading[0] = false;
                                    previousTotal[0] = totalItemCount[0];
                                }
                            }
                            if (!loading[0] && (totalItemCount[0] - visibleItemCount[0])
                                    <= (firstVisibleItem[0] + visibleThreshold)) {
                                // End has been reached
                                loading[0] = true;

                                currentPage[0]++;

                                RequestQueue queue = Volley.newRequestQueue(UpcomingActivity.this);
                                StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getAllUpcomingContents/"+ currentPage[0]+1, response1 -> {
                                    if (!response1.equals("No Data Avaliable")) {
                                        JsonArray jsonArray1 = new Gson().fromJson(response1, JsonArray.class);
                                        for (JsonElement r : jsonArray1) {
                                            JsonObject rootObject = r.getAsJsonObject();
                                            int id = rootObject.get("id").getAsInt();
                                            String name = rootObject.get("name").getAsString();
                                            String description = rootObject.get("description").getAsString();
                                            String poster = rootObject.get("poster").getAsString();
                                            String trailer_url = rootObject.get("trailer_url").getAsString();
                                            DateFormat outputFormat = new SimpleDateFormat("ddMMMM yyyy");
                                            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Date date = null;
                                            try {
                                                date = inputFormat.parse(rootObject.get("release_date").getAsString());
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                            String release_date = outputFormat.format(date);

                                            int type = rootObject.get("type").getAsInt();

                                            upcomingContentList.add(new UpcomingContentList(id, name, description, poster, trailer_url, release_date, type));
                                        }
                                        myadepter.notifyDataSetChanged();
                                        loading[0] = false;
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
                        }
                    });


//                    View moviesShimmerLayout = findViewById(R.id.Movies_Shimmer_Layout);
//                    moviesShimmerLayout.setVisibility(View.GONE);
//                    movieListRecycleview.setVisibility(View.VISIBLE);

                    SwipeRefreshLayout Swipe_Refresh_Layout = findViewById(R.id.Swipe_Refresh_Layout);
                    Swipe_Refresh_Layout.setRefreshing(false);

                } else {
                    SwipeRefreshLayout Swipe_Refresh_Layout = findViewById(R.id.Swipe_Refresh_Layout);
                    Swipe_Refresh_Layout.setRefreshing(false);
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
    }
}