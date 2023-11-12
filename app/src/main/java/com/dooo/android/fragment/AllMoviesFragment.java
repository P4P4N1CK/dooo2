package com.dooo.android.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.AllMoviesActivity;
import com.dooo.android.AppConfig;
import com.dooo.android.Home;
import com.dooo.android.R;
import com.dooo.android.adepter.AllMovieListAdepter;
import com.dooo.android.list.MovieList;
import com.dooo.android.sharedpreferencesmanager.ConfigManager;
import com.dooo.android.utils.HelperUtils;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllMoviesFragment extends Fragment {
    private Context context;
    int shuffleContents;
    RecyclerView movieListRecycleview;
    View moviesShimmerLayout;
    SwipeRefreshLayout movieSwipeRefreshLayout;
    LinearLayout movieFilterTag;

    public AllMoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View layoutInflater =  inflater.inflate(R.layout.fragment_all_movies, container, false);
        bindViews(layoutInflater);

        try {
            JSONObject configObject = ConfigManager.loadConfig(context);
            shuffleContents = configObject.getInt("shuffle_contents");
        } catch (Exception e) {
            Log.d("test", e.getMessage());
        }

        movieList();

        movieSwipeRefreshLayout.setOnRefreshListener(() -> movieList());

        movieFilterTag.setOnClickListener(view -> {
            final Dialog dialog = new BottomSheetDialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.filter_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            ImageView dialogClose = (ImageView) dialog.findViewById(R.id.dialogClose);
            dialogClose.setOnClickListener(v -> dialog.dismiss());



            dialog.show();
        });

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor), layoutInflater);

        return layoutInflater;
    }

    private void bindViews(View layoutInflater) {
        movieListRecycleview = layoutInflater.findViewById(R.id.movie_list_recycleview);
        moviesShimmerLayout = layoutInflater.findViewById(R.id.Movies_Shimmer_Layout);
        movieSwipeRefreshLayout = layoutInflater.findViewById(R.id.Movie_Swipe_Refresh_Layout);
        movieFilterTag = layoutInflater.findViewById(R.id.movieFilterTag);
    }

    void setColorTheme(int color, View layoutInflater) {
        TextView moviesTitleText = layoutInflater.findViewById(R.id.moviesTitleText);
        moviesTitleText.setTextColor(color);
    }

    void movieList() {
        final int[] previousTotal = {0};
        final boolean[] loading = {true};
        int visibleThreshold = 3;
        final int[] firstVisibleItem = new int[1];
        final int[] visibleItemCount = new int[1];
        final int[] totalItemCount = new int[1];
        final int[] currentPage = {0};

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getAllMovies/"+ currentPage[0], new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    List<MovieList> movieList = new ArrayList<>();
                    for (JsonElement r : jsonArray) {
                        JsonObject rootObject = r.getAsJsonObject();
                        int id = rootObject.get("id").getAsInt();
                        String name = rootObject.get("name").getAsString();

                        String year = "";
                        if(!rootObject.get("release_date").getAsString().equals("")) {
                            year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                        }

                        String poster = rootObject.get("poster").getAsString();
                        int type = rootObject.get("type").getAsInt();
                        int status = rootObject.get("status").getAsInt();

                        if (status == 1) {
                            //movieList.add(new MovieList(id, type, name, year, poster));
                        }
                    }

                    if(shuffleContents == 1) {
                        Collections.shuffle(movieList);
                    }

                    //GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                    //movieListRecycleview.setLayoutManager(gridLayoutManager);

                    FlexboxLayoutManager gridLayoutManager = new FlexboxLayoutManager(context);
                    gridLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
                    movieListRecycleview.setLayoutManager(gridLayoutManager);


                    AllMovieListAdepter myadepter = new AllMovieListAdepter(context, movieList);
                    movieListRecycleview.setAdapter(myadepter);

                    movieListRecycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

                        @Override
                        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            visibleItemCount[0] = movieListRecycleview.getChildCount();
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

                                RequestQueue queue = Volley.newRequestQueue(context);
                                StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getAllMovies/"+ currentPage[0], response1 -> {
                                    if (!response1.equals("No Data Avaliable")) {
                                        JsonArray jsonArray1 = new Gson().fromJson(response1, JsonArray.class);
                                        for (JsonElement r : jsonArray1) {
                                            JsonObject rootObject = r.getAsJsonObject();
                                            int id = rootObject.get("id").getAsInt();
                                            String name = rootObject.get("name").getAsString();

                                            String year = "";
                                            if(!rootObject.get("release_date").getAsString().equals("")) {
                                                year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                                            }

                                            String poster = rootObject.get("poster").getAsString();
                                            int type = rootObject.get("type").getAsInt();
                                            int status = rootObject.get("status").getAsInt();

                                            if (status == 1) {
                                                movieList.add(new MovieList(id, type, name, year, poster));
                                            }
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

                    moviesShimmerLayout.setVisibility(View.GONE);
                    movieListRecycleview.setVisibility(View.VISIBLE);

                    movieSwipeRefreshLayout.setRefreshing(false);

                } else {
                    movieSwipeRefreshLayout.setRefreshing(false);
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