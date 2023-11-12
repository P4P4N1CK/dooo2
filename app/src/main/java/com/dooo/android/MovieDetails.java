package com.dooo.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyInterstitial;
import com.adcolony.sdk.AdColonyInterstitialListener;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.bumptech.glide.Glide;
import com.dooo.android.adepter.CastAdepter;
import com.dooo.android.adepter.CommentListAdepter;
import com.dooo.android.adepter.DownloadLinkListAdepter;
import com.dooo.android.adepter.PlayMovieItemListAdepter;
import com.dooo.android.adepter.ReletedMovieListAdepter;
import com.dooo.android.list.CastList;
import com.dooo.android.list.DownloadLinkList;
import com.dooo.android.list.MovieList;
import com.dooo.android.list.PlayMovieItemIist;
import com.dooo.android.list.CommentList;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.LoadingDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.jetradarmobile.snowfall.SnowfallView;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class MovieDetails extends AppCompatActivity {
    Context context;

    int id;

    int userId;

    String trailerUrl;

    int contentId;
    String name;
    String releaseDate;
    String runtime;
    String genres;
    String poster;
    String banner;
    int downloadable;
    int type;
    int status;
    String description;

    ImageView trailerIcon;
    ImageView favouriteIcon;
    ImageView downloadIcon;

    Boolean isFavourite = false;

    int adType;

    RelativeLayout adViewLayout;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    boolean removeAds = false;
    boolean playPremium = false;
    boolean downloadPremium = false;

    View rootView;

    String userData = null;

    String tempUserID = null;

    LoadingDialog loadingDialog;

    int TMDB_ID;

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
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Home_TitleBar_BG));

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.comment_tag_bg);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(AppConfig.primeryThemeColor));

        setContentView(R.layout.activity_movie_details);

        loadingDialog = new LoadingDialog(this);

        rootView = findViewById(R.id.movie_details);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(MovieDetails.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(MovieDetails.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        context = this;

        loadConfig();

        loadData();

        loadUserSubscriptionDetails();


        if(userData != null) {
            tempUserID = String.valueOf(userId);
        } else {
            tempUserID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        Intent intent = getIntent();
        id = intent.getExtras().getInt("ID");

        if(userData != null) {
            HelperUtils.setViewLog(context, String.valueOf(userId), id, 1, AppConfig.apiKey);
        } else {
            HelperUtils.setViewLog(context, tempUserID, id,1, AppConfig.apiKey);
        }

        favouriteIcon = findViewById(R.id.Favourite_Icon);
        downloadIcon = findViewById(R.id.Download_Icon);
        trailerIcon = findViewById(R.id.Trailer_Icon);

        ImageView movieDetailsBack =  findViewById(R.id.Movie_Details_Back);
        movieDetailsBack.setOnClickListener(view -> finish());

        loadMovieDetails(id);

        View trailerLayout = findViewById(R.id.Trailer_Layout);
        trailerLayout.setOnClickListener(view -> {
            if(!trailerUrl.equals("")) {
                Intent intent1 = new Intent(MovieDetails.this, TrailerPlayer.class);
                intent1.putExtra("Trailer_URL", trailerUrl);
                startActivity(intent1);
            }
        });

        View favouriteLayout = findViewById(R.id.Favourite_Layout);
        favouriteLayout.setOnClickListener(view -> {
            if(isFavourite) {
                removeFavourite();
            } else {
                setFavourite();
            }
        });


        if(AppConfig.safeMode) {
            if(AppConfig.safeModeVersions.isEmpty()) {
                findViewById(R.id.playLayout).setVisibility(View.INVISIBLE);
                findViewById(R.id.downloadLayout).setVisibility(View.GONE);
            } else {
                String[] safeModeVersionsArrey = AppConfig.safeModeVersions.split(",");
                for (String safeModeVersion : safeModeVersionsArrey) {
                    if(BuildConfig.VERSION_NAME.equals(safeModeVersion.trim())) {
                        findViewById(R.id.playLayout).setVisibility(View.INVISIBLE);
                        findViewById(R.id.downloadLayout).setVisibility(View.GONE);
                    }
                }
            }

        } else {
            findViewById(R.id.playLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.downloadLayout).setVisibility(View.VISIBLE);
        }

        LinearLayout playMovie = findViewById(R.id.Play_Movie);
        playMovie.setOnClickListener(view -> {
            if(AppConfig.all_movies_type == 0) {
                if(type== 1) {

                    if (playPremium) {
                        loadStreamLinks(id);
                        //playMovieTab(true);
                    } else {
                        HelperUtils helperUtils = new HelperUtils(MovieDetails.this);
                        helperUtils.Buy_Premium_Dialog(MovieDetails.this, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                    }

                } else {
                    loadStreamLinks(id);
                    //playMovieTab(true);
                }
            } else if(AppConfig.all_movies_type == 1) {
                loadStreamLinks(id);
                //playMovieTab(true);
            } else if(AppConfig.all_movies_type == 2) {
                if (playPremium) {
                    loadStreamLinks(id);
                    //playMovieTab(true);
                } else {
                    HelperUtils helperUtils = new HelperUtils(MovieDetails.this);
                    helperUtils.Buy_Premium_Dialog(MovieDetails.this, "Buy Premium!", "Buy Premium Subscription To Watch Premium Content", R.raw.rocket_telescope);
                }
            }
        });

        LinearLayout clickToHideMoviePlayTab = findViewById(R.id.Click_to_hide_movie_play_tab);
        clickToHideMoviePlayTab.setOnClickListener(view -> playMovieTab(false));

        //Ad Controller
        if(!removeAds) {
            loadAd();
        }


        ConstraintLayout shareImgBtn = findViewById(R.id.Share_IMG_Btn);
        shareImgBtn.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_app_text));
            startActivity(Intent.createChooser(sharingIntent, "Share app via"));
        });

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

        ConstraintLayout downloadLayout = findViewById(R.id.downloadLayout);
        downloadLayout.setOnClickListener(v -> {
            if(downloadable == 1) {
                if(AppConfig.all_movies_type == 0) {
                    if(type== 1) {

                        if (downloadPremium) {
                            showDownloadOption(id);
                        } else {
                            HelperUtils helperUtils = new HelperUtils(MovieDetails.this);
                            helperUtils.Buy_Premium_Dialog(MovieDetails.this, "Buy Premium!", "Buy Premium Subscription To Download Premium Content", R.raw.rocket_telescope);
                        }

                    } else {
                        showDownloadOption(id);
                    }
                } else if(AppConfig.all_movies_type == 1) {
                    showDownloadOption(id);
                } else if(AppConfig.all_movies_type == 2) {
                    if (downloadPremium) {
                        showDownloadOption(id);
                    } else {
                        HelperUtils helperUtils = new HelperUtils(MovieDetails.this);
                        helperUtils.Buy_Premium_Dialog(MovieDetails.this, "Buy Premium!", "Buy Premium Subscription To Download Premium Content", R.raw.rocket_telescope);
                    }
                }
            }
        });

        LinearLayout reportButtonLinearLayout= findViewById(R.id.reportButton);
        reportButtonLinearLayout.setOnClickListener(view -> {
            if(userData != null) {
                final Dialog dialog = new Dialog(MovieDetails.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.report_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);

                ImageView dialogClose = (ImageView) dialog.findViewById(R.id.Coupan_Dialog_Close);
                dialogClose.setOnClickListener(v -> dialog.dismiss());

                EditText titleEditText = dialog.findViewById(R.id.titleEditText);
                titleEditText.setText(name);

                EditText descriptionEditText = dialog.findViewById(R.id.descriptionEditText);

                Button submitBtnButton = dialog.findViewById(R.id.submitBtn);
                submitBtnButton.setOnClickListener(btnView -> {
                    RequestQueue queue = Volley.newRequestQueue(this);
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "createReport", response -> {
                        if (Boolean.valueOf(response)) {
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(rootView, "Report Successfully Submited!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Close", v -> snackbar.dismiss());
                            snackbar.show();
                        } else {
                            dialog.dismiss();
                            Snackbar snackbar = Snackbar.make(rootView, "Error: Something went Wrong!", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Close", v -> snackbar.dismiss());
                            snackbar.show();
                        }
                    }, error -> {
                        // Do nothing because There is No Error if error It will return 0
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", String.valueOf(userId));
                            params.put("title", titleEditText.getText().toString());
                            params.put("description", descriptionEditText.getText().toString());
                            params.put("report_type", String.valueOf(1));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-api-key", AppConfig.apiKey);
                            return params;
                        }
                    };
                    queue.add(sr);
                });
                dialog.show();
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "Login to Report Content!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Login", v -> {
                    Intent lsIntent = new Intent(MovieDetails.this, LoginSignup.class);
                    startActivity(lsIntent);
                });
                snackbar.show();
            }
        });

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor));
    }

    void setColorTheme(int color) {
        CardView playLayout = findViewById(R.id.playLayout);
        playLayout.setBackgroundTintList(ColorStateList.valueOf(color));

        CardView castLayoutColorBar = findViewById(R.id.castLayoutColorBar);
        castLayoutColorBar.setBackgroundTintList(ColorStateList.valueOf(color));

        CardView reletedContentLayoutColorBar = findViewById(R.id.reletedContentLayoutColorBar);
        reletedContentLayoutColorBar.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    void loadAdditionalDetails() {
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, "https://cloud.team-dooo.com/Dooo/IMDB/index.php", response -> {
                if(!Objects.equals(response, "false")) {
                    LinearLayout ratingLayout = findViewById(R.id.ratingLayout);
                    ratingLayout.setVisibility(View.VISIBLE);
                    JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                    TextView rating = findViewById(R.id.rating);
                    rating.setText(jsonObject.get("rating").getAsString());

                    TextView maxrating = findViewById(R.id.maxrating);
                    maxrating.setText(jsonObject.get("maxrating").getAsString());

                    JsonArray jsonArray = new Gson().fromJson(jsonObject.get("cast").getAsJsonArray(), JsonArray.class);
                    List<CastList> castList = new ArrayList<>();
                    for (JsonElement r : jsonArray) {
                        JsonObject rootObject = r.getAsJsonObject();

                        String name = rootObject.get("name").isJsonNull() ? "" : rootObject.get("name").getAsString();
                        String role = rootObject.get("role").isJsonNull() ? "" : rootObject.get("role").getAsString();
                        String image = rootObject.get("image").isJsonNull() ? "" : rootObject.get("image").getAsString();

                        castList.add(new CastList(name, role, image));
                    }
                    RecyclerView castListRecyclerView = findViewById(R.id.castListRecyclerView);
                    CastAdepter myadepter = new CastAdepter(context, castList);
                    castListRecyclerView.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                    castListRecyclerView.setAdapter(myadepter);

                    if(!castList.isEmpty()) {
                        LinearLayout castLayout = findViewById(R.id.castLayout);
                        castLayout.setVisibility(View.VISIBLE);
                    }
                }
            }, error -> {

            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("ctype", String.valueOf(1));
                    params.put("tmdbid", String.valueOf(TMDB_ID));
                    params.put("bGljZW5zZV9jb2Rl", AppConfig.bGljZW5zZV9jb2Rl);
                    return params;
                }
            };
            queue.add(sr);
        } catch (Exception ignored){}
    }

    private void initComment() {
        LinearLayout commentBtn = findViewById(R.id.commentBtn);
        commentBtn.setVisibility(View.VISIBLE);
        commentBtn.setOnClickListener(view->{
            if(findViewById(R.id.comment_tab).getVisibility() == View.GONE) {
                commentTab(true);
                loadComments();
            } else {
                commentTab(false);
            }
        });

        findViewById(R.id.commentTabExtraSpace).setOnClickListener(v1->commentTab(false));
        findViewById(R.id.commentTabClose).setOnClickListener(v1->commentTab(false));

        CardView sendComment = findViewById(R.id.sendComment);
        EditText commentEditText = findViewById(R.id.commentEditText);
        sendComment.setOnClickListener(view->{
            msgSending(true);
            if(userData != null) {
                if(!commentEditText.getText().toString().equals("")) {
                    RequestQueue queue = Volley.newRequestQueue(context);
                    StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "addComments", response -> {
                        loadComments();
                        commentEditText.setText("");
                    }, error -> {
                        msgSending(false);
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user_id", String.valueOf(userId));
                            params.put("content_id", String.valueOf(id));
                            params.put("content_type", String.valueOf(1));
                            params.put("comment", commentEditText.getText().toString());
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-api-key", AppConfig.apiKey);
                            return params;
                        }
                    };
                    queue.add(sr);
                } else {
                    msgSending(false);
                }
            } else {
                msgSending(false);
                Toasty.warning(context, "Please Login to Comment Here!.", Toast.LENGTH_SHORT, true).show();
            }

        });

    }

    private void loadComments() {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getComments/"+id+"/1", response -> {
            msgSending(false);
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<CommentList> commentList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();

                    int cUserID = rootObject.get("userID").getAsInt();
                    String userName = rootObject.get("userName").getAsString();
                    String comment = rootObject.get("comment").getAsString();

                    commentList.add(new CommentList(cUserID, userName, comment));


                    RecyclerView commentRecylerview = findViewById(R.id.commentRecylerview);
                    CommentListAdepter myadepter = new CommentListAdepter(userId, context, commentList);
                    commentRecylerview.setLayoutManager(new GridLayoutManager(context, 1));
                    commentRecylerview.setAdapter(myadepter);
                    commentRecylerview.scrollToPosition(commentList.size() - 1);
                }
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

    private void msgSending(boolean bool) {
        CardView sendComment = findViewById(R.id.sendComment);
        ImageView msgSentIconImageView= findViewById(R.id.msgSentIcon);
        SpinKitView loadingMsgSent = findViewById(R.id.loadingMsgSent);
        if(bool) {
            sendComment.setClickable(false);
            msgSentIconImageView.setVisibility(View.GONE);
            loadingMsgSent.setVisibility(View.VISIBLE);
        } else {
            msgSentIconImageView.setVisibility(View.VISIBLE);
            loadingMsgSent.setVisibility(View.GONE);
            sendComment.setClickable(true);
        }
    }

    private void commentTab(boolean show) {
        View commentTab = findViewById(R.id.comment_tab);
        ViewGroup movieDetails = findViewById(R.id.movie_details);

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(500);
        transition.addTarget(R.id.comment_tab);

        TransitionManager.beginDelayedTransition(movieDetails, transition);
        commentTab.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        EditText searchContentEditText = findViewById(R.id.commentEditText);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (searchContentEditText.isFocused()) {
                Rect outRect = new Rect();
                searchContentEditText.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    searchContentEditText.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void showDownloadOption(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getMovieDownloadLinks/"+id, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<DownloadLinkList> downloadLinkList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int LinkID = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String size = rootObject.get("size").getAsString();
                    String quality = rootObject.get("quality").getAsString();
                    int link_order = rootObject.get("link_order").getAsInt();
                    int movie_id = rootObject.get("movie_id").getAsInt();
                    String url = rootObject.get("url").getAsString();
                    String type = rootObject.get("type").getAsString();
                    String download_type = rootObject.get("download_type").getAsString();

                    downloadLinkList.add(new DownloadLinkList(LinkID, name, size, quality, link_order, movie_id, url, type, download_type));
                }

                final Dialog downloadDialog = new Dialog(MovieDetails.this);
                downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                downloadDialog.setCancelable(false);
                downloadDialog.setContentView(R.layout.download_dialog);
                downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                downloadDialog.setCanceledOnTouchOutside(true);

                ImageView coupanDialogClose = (ImageView) downloadDialog.findViewById(R.id.Coupan_Dialog_Close);
                coupanDialogClose.setOnClickListener(v -> downloadDialog.dismiss());

                RecyclerView downloadLinksRecylerView = (RecyclerView) downloadDialog.findViewById(R.id.downloadLinksRecylerView);
                DownloadLinkListAdepter myadepter = new DownloadLinkListAdepter(context,rootView, downloadDialog, downloadLinkList);
                downloadLinksRecylerView.setLayoutManager(new GridLayoutManager(context, 1));
                downloadLinksRecylerView.setAdapter(myadepter);

                downloadDialog.show();

            } else {
                Snackbar snackbar = Snackbar.make(rootView, "No Download Server Avaliable!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Close", v -> snackbar.dismiss());
                snackbar.show();
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

    private void loadAd() {
        adViewLayout = findViewById(R.id.ad_View_Layout);

        if(adType == 1) {   //AdMob
            MobileAds.initialize(this, initializationStatus -> {
                // Do nothing
            });
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(this, AppConfig.adMobInterstitial, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    interstitialAd.show(MovieDetails.this);

                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                }
            });

            //Banner ad
            AdView mAdView = new AdView(context);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(AppConfig.adMobBanner);
            (adViewLayout).addView(mAdView);
            AdRequest bannerAdRequest = new AdRequest.Builder().build();
            mAdView.loadAd(bannerAdRequest);
        } else if(adType == 2) { //StartApp
            // and show interstitial ad
            StartAppAd.showAd(this);

            // Define StartApp Banner
            Banner startAppBanner = new Banner(context);
            RelativeLayout.LayoutParams bannerParameters =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
            bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            // Add to main Layout
            adViewLayout.addView(startAppBanner, bannerParameters);
        } else if(adType == 3) { //Facebook

            AudienceNetworkAds.initialize(context);
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(this, AppConfig.facebook_banner_ads_placement_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            adViewLayout.addView(adView);
            adView.loadAd();

            com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(this, AppConfig.facebook_interstitial_ads_placement_id);
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {

                @Override
                public void onError(Ad ad, AdError adError) {

                }

                @Override
                public void onAdLoaded(Ad ad) {
                    interstitialAd.show();
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {

                }
            };
            interstitialAd.loadAd(
                    interstitialAd.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());


        } else if(adType == 4) { //AdColony
            String[] AdColony_AD_UNIT_Zone_Ids = new String[] {AppConfig.AdColony_BANNER_ZONE_ID,AppConfig.AdColony_INTERSTITIAL_ZONE_ID};
            AdColony.configure(this, AppConfig.AdColony_APP_ID, AdColony_AD_UNIT_Zone_Ids);

            AdColonyInterstitialListener listener1 = new AdColonyInterstitialListener() {
                @Override
                public void onRequestFilled(AdColonyInterstitial adColonyInterstitial) {
                    adColonyInterstitial.show();
                }
            };
            AdColony.requestInterstitial(AppConfig.AdColony_INTERSTITIAL_ZONE_ID, listener1);

            AdColonyAdViewListener listener = new AdColonyAdViewListener() {
                @Override
                public void onRequestFilled(AdColonyAdView adColonyAdView) {
                    adViewLayout.addView(adColonyAdView);
                }
            };
            AdColony.requestAdView(AppConfig.AdColony_BANNER_ZONE_ID, listener, AdColonyAdSize.BANNER);
        } else if(adType == 5) { //unityads
            IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
                @Override
                public void onUnityAdsAdLoaded(String placementId) {
                    UnityAds.show(MovieDetails.this, AppConfig.Unity_rewardedVideo_ID, new UnityAdsShowOptions(), new IUnityAdsShowListener() {
                        @Override
                        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {

                        }

                        @Override
                        public void onUnityAdsShowStart(String placementId) {

                        }

                        @Override
                        public void onUnityAdsShowClick(String placementId) {

                        }

                        @Override
                        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {

                        }
                    });
                }

                @Override
                public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {

                }
            };
            UnityAds.initialize (this, AppConfig.Unity_Game_ID, false);
            BannerView bannerView = new BannerView(MovieDetails.this, AppConfig.Unity_Banner_ID, new UnityBannerSize(320, 50));
            bannerView.load();
            adViewLayout.addView(bannerView);
            UnityAds.load(AppConfig.Unity_rewardedVideo_ID, loadListener);
        } else if(adType == 6) { //Custom Ads
            adViewLayout.setVisibility(View.GONE);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                public void run() {
                    if(!AppConfig.Custom_Interstitial_url.equals("")) {
                        ConstraintLayout customIntertial_layout = findViewById(R.id.customIntertial_layout);
                        customIntertial_layout.setVisibility(View.VISIBLE);
                        ImageView customIntertial_ad = findViewById(R.id.customIntertial_ad);
                        Glide.with(context)
                                .load(AppConfig.Custom_Interstitial_url)
                                .into(customIntertial_ad);
                        ImageView customIntertial_close_btn = findViewById(R.id.customIntertial_close_btn);
                        customIntertial_close_btn.setOnClickListener(view -> {
                            customIntertial_layout.setVisibility(View.GONE);
                        });
                        customIntertial_ad.setOnClickListener(view -> {
                            if(!AppConfig.Custom_Banner_click_url.equals("")) {
                                switch (AppConfig.Custom_Interstitial_click_url_type) {
                                    case 1:
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.Custom_Banner_click_url)));
                                        break;
                                    case 2:
                                        Intent intent = new Intent(MovieDetails.this, WebView.class);
                                        intent.putExtra("URL", AppConfig.Custom_Interstitial_click_url);
                                        startActivity(intent);
                                        break;
                                    default:
                                }
                            }
                        });
                    }
                }
            }, 2000);


            if(!AppConfig.Custom_Banner_url.equals("")) {
                ImageView custom_banner_ad = findViewById(R.id.custom_banner_ad);
                custom_banner_ad.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(AppConfig.Custom_Banner_url)
                        .into(custom_banner_ad);
                custom_banner_ad.setOnClickListener(view -> {
                    if(!AppConfig.Custom_Banner_click_url.equals("")) {
                        switch (AppConfig.Custom_Banner_click_url_type) {
                            case 1:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.Custom_Banner_click_url)));
                                break;
                            case 2:
                                Intent intent = new Intent(MovieDetails.this, WebView.class);
                                intent.putExtra("URL", AppConfig.Custom_Banner_click_url);
                                startActivity(intent);
                                break;
                            default:
                        }
                    }
                });
            }
        } else if(adType == 7) { // AppLovin Ads
            AppLovinSdk.getInstance( context ).setMediationProvider( "max" );
            AppLovinSdk.getInstance(context).getSettings().setTestDeviceAdvertisingIds(Arrays.asList("b4d923d0-7f39-41a4-a3cb-0f16ce8e3058"));
            AppLovinSdk.initializeSdk( context, configuration -> {
                MaxAdView adView = new MaxAdView( AppConfig.applovin_Banner_ID, this );
                adViewLayout.addView(adView);
                adView.loadAd();

                MaxInterstitialAd interstitialAd = new MaxInterstitialAd( AppConfig.applovin_Interstitial_ID, this );
                interstitialAd.loadAd();
                interstitialAd.setListener(new MaxAdListener() {
                    int retryAttempt = 0;
                    @Override
                    public void onAdLoaded(MaxAd ad) {
                        retryAttempt = 0;
                        interstitialAd.showAd();
                    }

                    @Override
                    public void onAdDisplayed(MaxAd ad) {

                    }

                    @Override
                    public void onAdHidden(MaxAd ad) {

                    }

                    @Override
                    public void onAdClicked(MaxAd ad) {

                    }

                    @Override
                    public void onAdLoadFailed(String adUnitId, MaxError error) {
                        retryAttempt++;
                        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

                        new Handler().postDelayed( new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                interstitialAd.loadAd();
                            }
                        }, delayMillis );
                    }

                    @Override
                    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                        interstitialAd.loadAd();
                    }
                });
            });
        } else if(adType == 8) { // IronSource Ads
            IronSource.init(this, AppConfig.ironSource_app_key, IronSource.AD_UNIT.OFFERWALL, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO, IronSource.AD_UNIT.BANNER);

            IronSourceBannerLayout banner = IronSource.createBanner(MovieDetails.this, ISBannerSize.BANNER);
            adViewLayout.addView(banner);
            IronSource.loadBanner(banner);

            IronSource.loadInterstitial();
            IronSource.setInterstitialListener(new InterstitialListener() {
                @Override
                public void onInterstitialAdReady() {
                    IronSource.showInterstitial();
                }

                @Override
                public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {

                }

                @Override
                public void onInterstitialAdOpened() {

                }

                @Override
                public void onInterstitialAdClosed() {

                }

                @Override
                public void onInterstitialAdShowSucceeded() {

                }

                @Override
                public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

                }

                @Override
                public void onInterstitialAdClicked() {

                }
            });
        } else {
            adViewLayout.setVisibility(View.GONE);
        }
    }

    private void playMovieTab(boolean show) {
        View playMovieTab = findViewById(R.id.Play_Movie_Tab);
        ViewGroup movieDetails = findViewById(R.id.movie_details);
        TextView Play_Text = findViewById(R.id.Play_Text);
        Play_Text.setTextColor(Color.parseColor(AppConfig.primeryThemeColor));

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(R.id.Play_Movie_Tab);

        TransitionManager.beginDelayedTransition(movieDetails, transition);
        playMovieTab.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void loadStreamLinks(int id) {
        loadingDialog.animate(true);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getMoviePlayLinks/"+id, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<PlayMovieItemIist> playMovieItemList = new ArrayList<>();

                RecyclerView playMovieItemRecylerview = findViewById(R.id.Play_movie_item_Recylerview);

                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();

                    int id1 = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    String size = rootObject.get("size").getAsString();
                    String quality = rootObject.get("quality").getAsString();
                    int movieId = rootObject.get("movie_id").getAsInt();
                    String url = rootObject.get("url").getAsString();
                    String type = rootObject.get("type").getAsString();
                    int status = rootObject.get("status").getAsInt();
                    int skipAvailable = rootObject.get("skip_available").getAsInt();
                    String introStart = rootObject.get("intro_start").getAsString();
                    String introEnd = rootObject.get("intro_end").getAsString();
                    int link_type = rootObject.get("link_type").getAsInt();
                    String drm_uuid = rootObject.get("drm_uuid").isJsonNull() ? "" : rootObject.get("drm_uuid").getAsString();
                    String drm_license_uri = rootObject.get("drm_license_uri").isJsonNull() ? "" : rootObject.get("drm_license_uri").getAsString();

                    if (status == 1) {
                        playMovieItemList.add(new PlayMovieItemIist(id1, name, size, quality, movieId, url, type, skipAvailable, introStart, introEnd, link_type, drm_uuid, drm_license_uri));
                    }



                    PlayMovieItemListAdepter myadepter = new PlayMovieItemListAdepter(id, context, playMovieItemList, playPremium);
                    playMovieItemRecylerview.setLayoutManager(new GridLayoutManager(context, 1));
                    playMovieItemRecylerview.setAdapter(myadepter);
                }

                playMovieTab(true);
            } else {
                Snackbar snackbar = Snackbar.make(rootView, "No Stream Avaliable!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Close", v -> snackbar.dismiss());
                snackbar.show();
            }
            loadingDialog.animate(false);
        }, error -> {
            loadingDialog.animate(false);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    void loadMovieDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getMovieDetails/"+id, response -> {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

            trailerUrl = jsonObject.get("youtube_trailer").getAsString();

            contentId = jsonObject.get("id").getAsInt();

            TMDB_ID = jsonObject.get("TMDB_ID").getAsInt();
            loadAdditionalDetails();

            name = jsonObject.get("name").getAsString();
            if (!jsonObject.get("release_date").getAsString().equals("")) {
                releaseDate = jsonObject.get("release_date").getAsString();
            }
            runtime = jsonObject.get("runtime").getAsString();
            genres = jsonObject.get("genres").getAsString();
            poster = jsonObject.get("poster").getAsString();
            banner = jsonObject.get("banner").getAsString();
            downloadable = jsonObject.get("downloadable").getAsInt();
            type = jsonObject.get("type").getAsInt();
            status = jsonObject.get("status").getAsInt();
            description = jsonObject.get("description").getAsString();

            TextView titleTextView = findViewById(R.id.Title_TextView);
            titleTextView.setText(name);

            TextView releaseDateTextView = findViewById(R.id.ReleaseDate_TextView);
            releaseDateTextView.setText(releaseDate);

            TextView runtimeTextView = findViewById(R.id.Runtime_TextView);
            runtimeTextView.setText(runtime);

            TextView genreTextView = findViewById(R.id.Genre_TextView);
            genreTextView.setText(genres);

            ImageView movieDetailsBanner = findViewById(R.id.Movie_Details_Banner);
            Glide.with(MovieDetails.this)
                    .load(banner)
                    .override(80, 80)
                    .placeholder(R.drawable.poster_placeholder)
                    .into(movieDetailsBanner);

            ImageView movieDetailsPoster = findViewById(R.id.Movie_Details_Poster);
            Glide.with(MovieDetails.this)
                    .load(poster)
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(movieDetailsPoster);

            View premiumTag = findViewById(R.id.Premium_Tag);
            if(AppConfig.all_movies_type == 0) {
                if(type== 1) {
                    premiumTag.setVisibility(View.VISIBLE);
                } else {
                    premiumTag.setVisibility(View.GONE);
                }
            } else if(AppConfig.all_movies_type == 1) {
                premiumTag.setVisibility(View.GONE);
            } else if(AppConfig.all_movies_type == 2) {
                premiumTag.setVisibility(View.VISIBLE);
            }

            TextView descriptionTextView = findViewById(R.id.Description_TextView);
            descriptionTextView.setText(description);

            if(trailerUrl.equals("")) {
                trailerIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trailer_blocked_icon));
            } else {
                trailerIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.trailer_icon));
            }

            if(downloadable == 0) {
                downloadIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.download_blocked_icon));
            } else if(downloadable == 1) {
                downloadIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.download_icon));
            }


            searchFavourite();

            getRelated(genres);

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

    void getRelated(String genres) {
        LinearLayoutCompat reletedContentLayout = findViewById(R.id.reletedContentLayout);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"getRelatedMovies/"+id+"/10", response -> {
            if(!response.equals("No Data Avaliable")) {
                reletedContentLayout.setVisibility(View.VISIBLE);
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<MovieList> movieList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int m_id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1 && id != m_id) {
                        movieList.add(new MovieList(m_id, type, name, year, poster));
                    }

                    Collections.shuffle(movieList);

                    RecyclerView reletedContentRecycleview = findViewById(R.id.reletedContentRecycleview);
                    ReletedMovieListAdepter myadepter = new ReletedMovieListAdepter(context, movieList);
                    reletedContentRecycleview.setLayoutManager(new GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false));
                    reletedContentRecycleview.setAdapter(myadepter);
                }
            } else {
                reletedContentLayout.setVisibility(View.GONE);
            }

        }, error -> {
            // Do nothing
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("genres", String.valueOf(genres));
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    void setFavourite() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"favourite/SET/"+ tempUserID +"/Movie/"+contentId, response -> {
            if(response.equals("New favourite created successfully")) {
               isFavourite = true;
               favouriteIcon.setImageDrawable(ContextCompat.getDrawable(MovieDetails.this, R.drawable.red_heart_favorite));
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

    void searchFavourite() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"favourite/SEARCH/"+ tempUserID +"/Movie/"+contentId, response -> {
            if(response.equals("Record Found")) {
                isFavourite = true;
                favouriteIcon.setImageDrawable(ContextCompat.getDrawable(MovieDetails.this, R.drawable.red_heart_favorite));
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

    void removeFavourite() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"favourite/REMOVE/"+ tempUserID +"/Movie/"+contentId, response -> {
            if(response.equals("Favourite successfully Removed")) {
                isFavourite = false;
                favouriteIcon.setImageDrawable(ContextCompat.getDrawable(MovieDetails.this, R.drawable.heart_favorite));
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
            } else {
                removeAds = false;
                playPremium = false;
                downloadPremium = false;
            }
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            userId = jsonObject.get("ID").getAsInt();
        }

    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);
        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        adType = jsonObject.get("ad_type").getAsInt();

        if(jsonObject.get("movie_comments").getAsInt() == 1) {
            initComment();
        }

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

    @Override
    public void onBackPressed() {
        View playMovieTab = findViewById(R.id.Play_Movie_Tab);
        ConstraintLayout customIntertial_layout = findViewById(R.id.customIntertial_layout);
        if(playMovieTab.getVisibility() == View.VISIBLE) {
            playMovieTab(false);
        } else if(customIntertial_layout.getVisibility() == View.VISIBLE) {
            customIntertial_layout.setVisibility(View.GONE);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        playMovieTab(false);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(MovieDetails.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(MovieDetails.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}