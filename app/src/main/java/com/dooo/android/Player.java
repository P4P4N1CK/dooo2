package com.dooo.android;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static com.dooo.android.utils.Constants.AUTOPLAY;
import static com.dooo.android.utils.Constants.EXTENTIONS;
import static com.dooo.android.utils.Constants.SOFTWARE_EXTENTIONS;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.db.resume_content.ResumeContent;
import com.dooo.android.db.resume_content.ResumeContentDatabase;
import com.dooo.android.dialog.TrackSelectionDialog;
import com.dooo.android.list.YTStreamList;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.MediaHelper;
import com.dooo.android.utils.TinyDB;
import com.dooo.android.utils.Yts;
import com.dooo.stream.DoooStream;
import com.dooo.stream.Model.StreamList;
import com.github.se_bastiaan.torrentstream.StreamStatus;
import com.github.se_bastiaan.torrentstream.Torrent;
import com.github.se_bastiaan.torrentstream.TorrentOptions;
import com.github.se_bastiaan.torrentstreamserver.TorrentServerListener;
import com.github.se_bastiaan.torrentstreamserver.TorrentStreamNotInitializedException;
import com.github.se_bastiaan.torrentstreamserver.TorrentStreamServer;
import com.github.vkay94.dtpv.DoubleTapPlayerView;
import com.github.vkay94.dtpv.youtube.YouTubeOverlay;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import es.dmoral.toasty.Toasty;

public class Player extends AppCompatActivity {
    Context context = this;
    private DoubleTapPlayerView playerView;
    private ExoPlayer simpleExoPlayer;

    YouTubeOverlay youtube_overlay;

    private DefaultTrackSelector trackSelector;

    int skip_available;
    String intro_start;
    String intro_end;
    Button Skip_Intro_btn;

    String ContentType = null;
    int Current_List_Position = 0;

    Button Play_Next_btn;

    String Next_Ep_Avilable;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    Boolean contentLoaded = false;

    static ProgressDialog progressDialog;

    ResumeContentDatabase db;
    int resumeContentID;

    long resumePosition = 0;

    String userData = null;
    int userId;

    String mContentType = "";

    int wsType;

    int sourceID;

    int ct;

    MergingMediaSource nMediaSource = null;

    PowerManager.WakeLock wakeLock;

    int maxBrightness;

    String source;

    int contentID;
    String cpUrl = "";

    private String streamSBMatcher;

    Map<String, String> defaultRequestProperties = new HashMap<>();
    String userAgent = "";

    List<StreamList> multiqualityListWeb = new ArrayList<>();
    android.webkit.WebView webView;

    private String bGljZW5zZV9jb2Rl;

    private TorrentStreamServer torrentStreamServer;

    TinyDB tinyDB;

    String DrmUuid = "";
    String DrmLicenseUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        if (AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:No Sleep");
        wakeLock.acquire(300 * 60 * 1000L /*300 minutes*/);

        maxBrightness = getMaxBrightness(this, 1000);

        loadConfig();
        loadData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_player);

        if (!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Player.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Player.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        tinyDB = new TinyDB(this);

        userAgent = WebSettings.getDefaultUserAgent(context);

        playerView = findViewById(R.id.player_view);

        youtube_overlay = findViewById(R.id.ytOverlay);
        youtube_overlay.performListener(new YouTubeOverlay.PerformListener() {
            @Override
            public void onAnimationStart() {
                youtube_overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd() {
                youtube_overlay.setVisibility(View.GONE);
            }

            @SuppressLint("WrongConstant")
            @Nullable
            @Override
            public Boolean shouldForward(@NotNull com.google.android.exoplayer2.Player player, @NotNull DoubleTapPlayerView doubleTapPlayerView, float v) {
                if (player.getPlaybackState() == PlaybackState.STATE_ERROR ||
                        player.getPlaybackState() == PlaybackState.STATE_NONE ||
                        player.getPlaybackState() == PlaybackState.STATE_STOPPED) {

                    playerView.cancelInDoubleTapMode();
                    return null;
                }

                if (player.getCurrentPosition() > 500 && v < playerView.getWidth() * 0.35)
                    return false;

                if (player.getCurrentPosition() < player.getDuration() && v > playerView.getWidth() * 0.65)
                    return true;

                return null;
            }
        });

        Intent intent = getIntent();
        contentID = intent.getExtras().getInt("contentID");
        sourceID = intent.getExtras().getInt("SourceID");
        String name = intent.getExtras().getString("name");
        source = intent.getExtras().getString("source");
        String url = intent.getExtras().getString("url");
        cpUrl = url;


        if (intent.getExtras().getString("Content_Type") != null) {
            mContentType = intent.getExtras().getString("Content_Type");

            if (mContentType.equals("Movie")) {
                ct = 1;
            } else if (mContentType.equals("WebSeries")) {
                ct = 2;
            }
        }

        DrmUuid = intent.getExtras().getString("DrmUuid");
        DrmLicenseUri = intent.getExtras().getString("DrmLicenseUri");

        //ResumePlayback
        db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
        resumePosition = intent.getExtras().getLong("position");

        TextView contentFirstName = findViewById(R.id.contentFirstName);
        TextView contentSecondName = findViewById(R.id.contentSecondName);
        contentSecondName.setText(name);

        if (mContentType.equals("Movie")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "getMovieDetails/" + contentID, response -> {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                contentFirstName.setText(jsonObject.get("name").getAsString());

                String releaseDate = "";
                if (!jsonObject.get("release_date").getAsString().equals("")) {
                    releaseDate = jsonObject.get("release_date").getAsString();
                }

                int type = jsonObject.get("type").getAsInt();

                db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
                if (db.resumeContentDao().getResumeContentid(contentID) == 0) {
                    db.resumeContentDao().insert(new ResumeContent(0, contentID, source, url, jsonObject.get("poster").getAsString(), jsonObject.get("name").getAsString(), releaseDate, 0, 0, intent.getExtras().getString("Content_Type"), type));
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                } else {
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                    db.resumeContentDao().updateSource(source, url, type, resumeContentID);
                }

                if (userData != null) {
                    HelperUtils.setWatchLog(context, String.valueOf(userId), jsonObject.get("id").getAsInt(), 1, AppConfig.apiKey);
                } else {
                    HelperUtils.setWatchLog(context, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), jsonObject.get("id").getAsInt(), 1, AppConfig.apiKey);
                }

            }, error -> {
                // Do nothing
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", AppConfig.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        } else if (mContentType.equals("WebSeries")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "getWebSeriesDetails/" + contentID, response -> {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                contentFirstName.setText(jsonObject.get("name").getAsString());

                String releaseDate = "";
                if (!jsonObject.get("release_date").getAsString().equals("")) {
                    releaseDate = jsonObject.get("release_date").getAsString();
                }

                int type = jsonObject.get("type").getAsInt();
                wsType = type;

                db = ResumeContentDatabase.getDbInstance(this.getApplicationContext());
                if (db.resumeContentDao().getResumeContentid(contentID) == 0) {
                    db.resumeContentDao().insert(new ResumeContent(0, contentID, source, url, jsonObject.get("poster").getAsString(), jsonObject.get("name").getAsString(), releaseDate, 0, 0, intent.getExtras().getString("Content_Type"), type));
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                } else {
                    resumeContentID = db.resumeContentDao().getResumeContentid(contentID);
                }

                if (userData != null) {
                    HelperUtils.setWatchLog(context, String.valueOf(userId), jsonObject.get("id").getAsInt(), 2, AppConfig.apiKey);
                } else {
                    HelperUtils.setWatchLog(context, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), jsonObject.get("id").getAsInt(), 2, AppConfig.apiKey);
                }

            }, error -> {
                // Do nothing
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", AppConfig.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        }


        int content_type = intent.getExtras().getInt("content_type");
        ImageView Live_logo = findViewById(R.id.Live_logo);
        if (content_type == 3) {
            Live_logo.setVisibility(View.VISIBLE);

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url + "getLiveTVDetails/" + contentID, response -> {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

                if (!Objects.equals(jsonObject.get("user_agent").getAsString(), "")) {
                    userAgent = jsonObject.get("user_agent").getAsString();
                } else {
                    userAgent = WebSettings.getDefaultUserAgent(context);
                }

                if (!Objects.equals(jsonObject.get("headers").getAsString(), "")) {
                    JsonArray headers = new Gson().fromJson(jsonObject.get("headers").getAsString(), JsonArray.class);
                    defaultRequestProperties.clear();
                    for (JsonElement headerElement : headers) {
                        JsonObject headerObject = headerElement.getAsJsonObject();
                        String[] header = headerObject.get("header").getAsString().split(":");
                        if(header.length == 2) {
                            defaultRequestProperties.put(header[0], header[1]);
                        }
                    }
                }

            }, error -> {
                // Do nothing
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-api-key", AppConfig.apiKey);
                    return params;
                }
            };
            queue.add(sr);
        } else {
            Live_logo.setVisibility(View.GONE);
        }

        skip_available = intent.getExtras().getInt("skip_available");
        intro_start = intent.getExtras().getString("intro_start");
        intro_end = intent.getExtras().getString("intro_end");

        if (intent.getExtras().getString("Content_Type") != null || intent.getExtras().getString("Current_List_Position") != null || intent.getExtras().getString("Next_Ep_Avilable") != null) {
            ContentType = intent.getExtras().getString("Content_Type");
            Current_List_Position = intent.getExtras().getInt("Current_List_Position");
            Next_Ep_Avilable = intent.getExtras().getString("Next_Ep_Avilable");

        }

        if (resumePosition == 0) {
            if (db.resumeContentDao().getResumeContentid(contentID) != 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Resume!");
                alertDialog.setMessage("Do You Want to Resume From Where You Left?");
                alertDialog.setCancelable(false);

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "Start Over", (dialog, which) -> {
                    if (mContentType.equals("Movie")) {
                        dialog.dismiss();
                        Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
                    } else if (mContentType.equals("WebSeries")) {
                        dialog.dismiss();
                        Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
                        db.resumeContentDao().updateSource(source, url, wsType, resumeContentID);
                    }
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Resume", (dialog, which) -> {
                    if (mContentType.equals("Movie")) {
                        dialog.dismiss();
                        resumePosition = db.resumeContentDao().getResumeContentPosition(contentID);
                        Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
                    } else if (mContentType.equals("WebSeries")) {
                        resumePosition = db.resumeContentDao().getResumeContentPosition(contentID);
                        Prepare_Source(db.resumeContentDao().getResumeContentSourceType(contentID), db.resumeContentDao().getResumeContentSourceUrl(contentID), "", "");
                        contentSecondName.setText(db.resumeContentDao().getResumeContentName(contentID));
                    }

                });
                alertDialog.show();
            } else {
                Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
            }
        } else {
            Prepare_Source(source, url, DrmUuid, DrmLicenseUri);
        }

        ImageView img_full_scr = findViewById(R.id.img_full_scr);
        img_full_scr.setOnClickListener(view -> {
            if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                img_full_scr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
            } else if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                img_full_scr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
            } else {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                img_full_scr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
            }
        });

        ImageView img_settings = findViewById(R.id.img_settings);
        img_settings.setOnClickListener(view -> {
            if (contentLoaded) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo;
                DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForPlayer(
                                simpleExoPlayer,
                                /* onDismissListener= */ dismissedDialog -> {
                                });
                trackSelectionDialog.show(getSupportFragmentManager(), null);
            } else {
                Toasty.warning(this, "Please Wait! Content Not Loaded.", Toast.LENGTH_SHORT, true).show();
            }
        });


        ImageView Back_btn_img = findViewById(R.id.Back_btn_img);
        Back_btn_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBackPressed = true;
                releasePlayer();
                handler.removeCallbacks(runnable);
                finish();
            }
        });

        Skip_Intro_btn = findViewById(R.id.Skip_Intro_btn);
        Skip_Intro_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleExoPlayer.seekTo(Get_mil_From_Time(intro_end));
            }
        });

        handler.post(runnable);

        FullScreencall();

        Play_Next_btn = findViewById(R.id.Play_Next_btn);
        Play_Next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContentType.equals("WebSeries")) {
                    if (Next_Ep_Avilable.equals("Yes")) {
                        Play_Next_btn.setText("Playing Now");
                        Intent intent = new Intent();
                        intent.putExtra("Current_List_Position", Current_List_Position);
                        setResult(RESULT_OK, intent);

                        isBackPressed = true;
                        releasePlayer();
                        handler.removeCallbacks(runnable);
                        finish();
                    }
                }
            }
        });

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        properties.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
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


        ImageView imgBrightness = findViewById(R.id.img_Brightness);
        BoxedVertical brightness = findViewById(R.id.brightness);
        ConstraintLayout brightnessLayout = findViewById(R.id.brightnessLayout);

        imgBrightness.setOnClickListener(view -> {
            if (brightnessLayout.getVisibility() == View.VISIBLE) {
                brightnessLayout.setVisibility(View.GONE);
            } else if (brightnessLayout.getVisibility() == View.GONE) {
                brightnessLayout.setVisibility(View.VISIBLE);
            }
        });

        brightness.setMax(maxBrightness);
        brightness.setValue(getBrightness(this));
        setBrightness(Player.this, getBrightness(this));
        brightness.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                setBrightness(Player.this, boxedPoints.getValue());
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {

            }
        });

        ImageView imgVolume = findViewById(R.id.img_Volume);
        BoxedVertical volume = findViewById(R.id.volume);
        ConstraintLayout volumeLayout = findViewById(R.id.volumeLayout);

        imgVolume.setOnClickListener(view -> {
            if (volumeLayout.getVisibility() == View.VISIBLE) {
                volumeLayout.setVisibility(View.GONE);
            } else if (volumeLayout.getVisibility() == View.GONE) {
                volumeLayout.setVisibility(View.VISIBLE);
            }
        });

        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volumeLevel = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        volume.setMax(maxVolume);
        volume.setValue(volumeLevel);
        volume.setOnBoxedPointsChangeListener(new BoxedVertical.OnValuesChangeListener() {
            @Override
            public void onPointsChanged(BoxedVertical boxedPoints, int points) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, boxedPoints.getValue(), 0);
            }

            @Override
            public void onStartTrackingTouch(BoxedVertical boxedPoints) {

            }

            @Override
            public void onStopTrackingTouch(BoxedVertical boxedPoints) {

            }
        });
    }
    int getMaxBrightness(Context context, int defaultValue){
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            Field[] fields = powerManager.getClass().getDeclaredFields();
            for (Field field: fields) {
                if(field.getName().equals("BRIGHTNESS_ON")) {
                    field.setAccessible(true);
                    try {
                        return (int) field.get(powerManager);
                    } catch (IllegalAccessException e) {
                        return defaultValue;
                    }
                }
            }
        }
        return defaultValue;
    }

    public void setBrightness(Context context, int brightness){
        //ContentResolver cResolver = context.getContentResolver();
        //Settings.System.putInt(cResolver,  Settings.System.SCREEN_BRIGHTNESS,brightness);

        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = (float) brightness/maxBrightness;
        getWindow().setAttributes(layout);

    }

    public static int getBrightness(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(cResolver,  Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }


    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(simpleExoPlayer != null) {
                if (simpleExoPlayer.isPlaying()) {
                    long apprxDuration = simpleExoPlayer.getDuration() - 5000;
                    if (simpleExoPlayer.getCurrentPosition() > apprxDuration) {
                        db.resumeContentDao().delete(resumeContentID);
                    } else {
                        db.resumeContentDao().update(simpleExoPlayer.getCurrentPosition(), resumeContentID);
                    }
                }
            }

            //Skip Feature
            if(skip_available == 1) {
                if(simpleExoPlayer != null) {
                    if(intro_start.equals("") || intro_start.equals("0") || intro_start.equals(null) || intro_end.equals("") || intro_end.equals("0") || intro_end.equals(null)) {
                      Skip_Intro_btn.setVisibility(View.GONE);
                    } else {
                      if(Get_mil_From_Time(intro_start) < simpleExoPlayer.getContentPosition() && Get_mil_From_Time(intro_end) > simpleExoPlayer.getContentPosition()) {
                        Skip_Intro_btn.setVisibility(View.VISIBLE);
                      } else {
                        Skip_Intro_btn.setVisibility(View.GONE);
                      }
                    }
                }

            } else {
                Skip_Intro_btn.setVisibility(View.GONE);
            }


            ////
            if(!multiqualityListWeb.isEmpty()) {
                multiQualityDialog(multiqualityListWeb);
                webView.destroy();
                multiqualityListWeb.clear();
            }

            // Repeat every 1 seconds
            handler.postDelayed(runnable, 1000);
        }
    };

    long Get_mil_From_Time(String Time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:SS");
        Date parsed_date = null;
        try {
            parsed_date = format.parse(Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        SimpleDateFormat min = new SimpleDateFormat("mm");
        SimpleDateFormat sec = new SimpleDateFormat("SS");

        String Hour = hour.format(parsed_date);
        String Min = min.format(parsed_date);
        String Sec = sec.format(parsed_date);


        long m_hour = 0;
        long m_min = 0;
        long m_sec = 0;
        if (!Hour.equals("00")) {
            m_hour = Integer.parseInt(Hour)*3600000;
        }

        if (!Min.equals("00")) {
            m_min = Integer.parseInt(Min)*60000;
        }

        if (!Sec.equals("00")) {
            m_sec = Integer.parseInt(Sec)*1000;
        }

        Long F_mil = m_hour + m_min + m_sec;

        return F_mil;
    }

    public static InetAddress getIpAddress(Context context) throws UnknownHostException {
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        if (ip == 0) {
            return null;
        } else {
            byte[] ipAddress = convertIpAddress(ip);
            return InetAddress.getByAddress(ipAddress);
        }
    }

    private static byte[] convertIpAddress(int ip) {
        return new byte[]{
                (byte) (ip & 0xFF),
                (byte) ((ip >> 8) & 0xFF),
                (byte) ((ip >> 16) & 0xFF),
                (byte) ((ip >> 24) & 0xFF)
        };
    }

    void Prepare_Source(String source, String url, String DrmUuid, String DrmLicenseUri) {
        Log.d("test", source+" "+url);

        DataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);
                DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context, httpDataSourceFactory);

        UUID drmUUID = C.UUID_NIL;
        if(DrmUuid!=null) {
            if(DrmUuid.equals("WIDEVINE")) {
                drmUUID = C.WIDEVINE_UUID;
            } else if(DrmUuid.equals("PLAYREADY")) {
                drmUUID = C.PLAYREADY_UUID;
            }else if(DrmUuid.equals("CLEARKEY")) {
                drmUUID = C.CLEARKEY_UUID;
            } else {
                drmUUID = C.UUID_NIL;
            }
        }


        if (source.equalsIgnoreCase("m3u8")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .setUri(url)
                    .setDrmConfiguration(new MediaItem.DrmConfiguration.Builder(drmUUID).
                            setLicenseUri(DrmLicenseUri).build())
                    .build();
            MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem);
            initializePlayer(hlsMediaSource);
        } else if (source.equalsIgnoreCase("dash")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setUri(url)
                    .setDrmConfiguration(new MediaItem.DrmConfiguration.Builder(drmUUID).
                            setLicenseUri(DrmLicenseUri).build())
                    .build();
            MediaSource dashMediaSource = new DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(dashMediaSource);
        } else if (source.equalsIgnoreCase("mp4")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .setUri(url)
                    .build();
            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(progressiveMediaSource);
        } else if (source.equalsIgnoreCase("mkv")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MATROSKA)
                    .setUri(url)
                    .build();
            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(progressiveMediaSource);
        } else if (source.equals("Youtube")) {
            progressDialog.show();
            Yts.getlinks(this, url, new Yts.VolleyCallback() {
                @Override
                public void onSuccess(List<YTStreamList> result) {
                    ytMultipleQualityDialog(Player.this, result);
                }

                @Override
                public void onError(VolleyError error) {
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                }
            });
        } else if (source.equals("Torrent")) {
            progressDialog.show();
            TorrentOptions torrentOptions = new TorrentOptions.Builder()
                    .saveLocation(getFilesDir())
                    .removeFilesAfterStop(true)
                    .build();
            String ipAddress = "127.0.0.1";
            try {
                InetAddress inetAddress = getIpAddress(this);
                if (inetAddress != null) {
                    ipAddress = inetAddress.getHostAddress();
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            torrentStreamServer = TorrentStreamServer.getInstance();
            torrentStreamServer.setTorrentOptions(torrentOptions);
            torrentStreamServer.setServerHost(ipAddress);
            torrentStreamServer.setServerPort(8080);
            torrentStreamServer.startTorrentStream();
            torrentStreamServer.addListener(new TorrentServerListener() {
                @Override
                public void onServerReady(String url) {
                    progressDialog.dismiss();
                    List<StreamList> streamList = new ArrayList<>();streamList.add(new StreamList("Normal", "mp4", url, "", ""));
                    multiQualityDialog(streamList);
                }

                @Override
                public void onStreamPrepared(Torrent torrent) {
                }

                @Override
                public void onStreamStarted(Torrent torrent) {

                }

                @Override
                public void onStreamError(Torrent torrent, Exception e) {
                    progressDialog.dismiss();
                    finishPlayer();
                }

                @Override
                public void onStreamReady(Torrent torrent) {

                }

                @Override
                public void onStreamProgress(Torrent torrent, StreamStatus status) {

                }

                @Override
                public void onStreamStopped() {

                }
            });

            try {
                torrentStreamServer.startStream(url);
            } catch (IOException | TorrentStreamNotInitializedException e) {
                e.printStackTrace();
            }
        } else if (source.equals("Dailymotion") || source.equals("DoodStream") || source.equals("Dropbox") || (source.equals("Fembed"))
                || source.equals("Facebook") || source.equals("GogoAnime") || source.equals("GoogleDrive") || source.equals("MediaShore")
                || source.equals("MixDrop") || source.equals("Onedrive") || source.equals("OKru") ||source.equals("StreamTape")
                || source.equals("Twitter") || source.equals("Voesx") || source.equals("VK")
                || source.equals("Voot") || source.equals("Vudeo") || source.equals("Vimeo")|| source.equals("YoutubeLive")
                || source.equals("Yandex") || source.equals("Zoro")) {
            progressDialog.show();

            new DoooStream(context, bGljZW5zZV9jb2Rl, new DoooStream.OnInitialize() {
                @Override
                public void onSuccess(DoooStream doooStream) {
                    doooStream.find(source, url, false, new DoooStream.OnTaskCompleted() {
                        @Override
                        public void onSuccess(List<StreamList> streamList) {
                            multiQualityDialog(streamList);
                        }

                        @Override
                        public void onError() {
                            progressDialog.dismiss();
                            finishPlayer();
                        }
                    });
                }

                @Override
                public void onError(RuntimeException e) {
                    progressDialog.dismiss();
                    finishPlayer();
                }
            });
        } else if (source.equals("Streamwish") ) {
            progressDialog.show();
            webView = new WebView(context);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public WebResourceResponse shouldInterceptRequest(android.webkit.WebView view, WebResourceRequest request) {
                    if(request.getUrl().toString().toLowerCase().contains("master.m3u8")) {
                        if(multiqualityListWeb.isEmpty()) {
                            multiqualityListWeb.add(new StreamList("Auto", "m3u8", request.getUrl().toString(), request.getUrl().toString(), ""));
                        }
                    }else if(request.getUrl().toString().toLowerCase().contains(".m3u8")) {
                        if(multiqualityListWeb.isEmpty()) {
                            multiqualityListWeb.add(new StreamList("Auto", "m3u8", request.getUrl().toString(), request.getUrl().toString(), ""));
                        }
                    }else if(request.getUrl().toString().toLowerCase().contains(".mp4") && !request.getUrl().toString().toLowerCase().contains("blank.mp4")) {
                        if(multiqualityListWeb.isEmpty()) {
                            multiqualityListWeb.add(new StreamList("Auto", "mp4", request.getUrl().toString(), request.getUrl().toString(), ""));
                        }
                    }else if(request.getUrl().toString().toLowerCase().contains(".mp4")) {
                        if(multiqualityListWeb.isEmpty()) {
                            multiqualityListWeb.add(new StreamList("Auto", "mp4", request.getUrl().toString(), request.getUrl().toString(), ""));
                        }
                    }else if(request.getUrl().toString().toLowerCase().contains(".mkv")) {
                        if(multiqualityListWeb.isEmpty()) {
                            multiqualityListWeb.add(new StreamList("Auto", "mkv", request.getUrl().toString(), request.getUrl().toString(), ""));
                        }
                    }else if(request.getUrl().toString().toLowerCase().contains(".ts")) {
                        if(multiqualityListWeb.isEmpty()) {
                            multiqualityListWeb.add(new StreamList("Auto", "ts", request.getUrl().toString(), request.getUrl().toString(), ""));
                        }
                    }
                    return super.shouldInterceptRequest(view, request);
                }
            });
            String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";
            webView.getSettings().setUserAgentString(ua);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);
        } else {
            finishPlayer();
        }

    }


    public void multiQualityDialog(List<StreamList> streamList) {

        if(streamList.isEmpty()) {
            finishPlayer();
        } else if(streamList.size() == 1) {
            multiQualityPlayer(streamList.get(0).getExtension(), streamList.get(0).getUrl(), streamList.get(0).getReferer(), streamList.get(0).getCookie());
            progressDialog.hide();
        } else {
            streamList.size();
            CharSequence[] name = new CharSequence[streamList.size()];
            for (int i = 0; i < streamList.size(); i++) {
                name[i] = streamList.get(i).getQuality();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Quality!")
                    .setCancelable(false)
                    .setItems(name, (dialog, which) -> {
                        multiQualityPlayer(streamList.get(which).getExtension(), streamList.get(which).getUrl(), streamList.get(which).getReferer(), streamList.get(which).getCookie());
                    })
                    .setPositiveButton("Close", (dialog, which) -> {
                        isBackPressed = true;
                        releasePlayer();
                        handler.removeCallbacks(runnable);
                        finish();
                    });
            progressDialog.hide();
            builder.show();
        }
    }

    public void multiQualityPlayer(String extension, String Url, String referer, String cookie) {
        Log.d("test", extension+" : "+Url+" : "+referer+" : "+cookie);

        if(!referer.equals("")) {
            defaultRequestProperties.put("Referer", referer);
        }
        if(!cookie.equals("")) {
            defaultRequestProperties.put("Cookie", cookie);
        }


        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setKeepPostFor302Redirects(true)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                .setDefaultRequestProperties(defaultRequestProperties);


        if (extension.equalsIgnoreCase("m3u8")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .setUri(Url)
                    .build();
            MediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(mediaItem);
            initializePlayer(hlsMediaSource);
        } else if (extension.equalsIgnoreCase("dash")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setUri(Url)
                    .build();
            MediaSource dashMediaSource = new DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(dashMediaSource);
        } else if (extension.equalsIgnoreCase("mp4")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MP4)
                    .setUri(Url)
                    .build();
            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(progressiveMediaSource);
        } else if (extension.equalsIgnoreCase("mkv")) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setMimeType(MimeTypes.APPLICATION_MATROSKA)
                    .setUri(Url)
                    .build();
            MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            initializePlayer(progressiveMediaSource);
        } else if (extension.equalsIgnoreCase("Embed")) {
            finishPlayer();
            finish();
            Intent intent = new Intent(context, EmbedPlayer.class);
            intent.putExtra("url", Url);
            context.startActivity(intent);
        }
    }

    void ytMultipleQualityDialog(Context context, List<YTStreamList> list) {
        progressDialog.dismiss();
        Collections.reverse(list);
        CharSequence[] name = new CharSequence[list.size()];
        CharSequence[] vid = new CharSequence[list.size()];
        CharSequence[] token = new CharSequence[list.size()];
        for (int i = 0; i < list.size(); i++) {
            name[i] = list.get(i).getName();
            vid[i] = list.get(i).getVid();
            token[i] = list.get(i).getToken();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Quality!")
                .setCancelable(false)
                .setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Yts.getStreamLinks(context, (String) token[which], (String) vid[which], new Yts.VolleyCallback2(){

                            @Override
                            public void onSuccess(String result) {
                                DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                                        .setUserAgent(userAgent)
                                        .setKeepPostFor302Redirects(true)
                                        .setAllowCrossProtocolRedirects(true)
                                        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                                        .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                                        .setDefaultRequestProperties(defaultRequestProperties);
                                MediaItem mediaItem = new MediaItem.Builder()
                                        .setMimeType(MimeTypes.APPLICATION_MP4)
                                        .setUri(result)
                                        .build();
                                MediaSource progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(mediaItem);
                                initializePlayer(progressiveMediaSource);
                            }

                            @Override
                            public void onError(VolleyError error) {
                            }
                        });
                    }
                })
                .setPositiveButton("Close", (dialog, which) -> {
                    isBackPressed = true;
                    releasePlayer();
                    handler.removeCallbacks(runnable);
                    finish();
                });
        builder.show();
    }

    void initializePlayer(MediaSource mediaSource) {
        ExoTrackSelection.Factory videoTrackSelectionFactory = new
                AdaptiveTrackSelection.Factory();

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        trackSelector = new
                DefaultTrackSelector(Player.this, videoTrackSelectionFactory);
        RenderersFactory renderersFactory;
        renderersFactory = MediaHelper.buildRenderersFactory(this,sharedPreferences.getBoolean(EXTENTIONS,false)
                ,sharedPreferences.getBoolean(SOFTWARE_EXTENTIONS, false));
        simpleExoPlayer = new ExoPlayer.Builder(this)
                .setRenderersFactory(renderersFactory)
                .setTrackSelector(trackSelector)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();

            youtube_overlay.player(simpleExoPlayer);

            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);

            //Player Speed


        ImageView playerSpeed = findViewById(R.id.playerSpeed);
        String grpname[] = { "0.5x", "1.0x", "1.5x", "2.0x" };
        float playerSpeedgroup[] = { 0.5f, 1f, 1.5f, 2f };
        final int[] selectedGorup = {1};
        final int[] tempSelectedGorup = {1};
        playerSpeed.setOnClickListener(view->{
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
            //alt_bld.setIcon(R.drawable.ic_baseline_watch);
            alt_bld.setTitle("Select Player Speed");
            alt_bld.setSingleChoiceItems(grpname, selectedGorup[0], new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    tempSelectedGorup[0] = item;

                }
            })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlaybackParameters param = new PlaybackParameters(playerSpeedgroup[tempSelectedGorup[0]]);
                            simpleExoPlayer.setPlaybackParameters(param);
                            selectedGorup[0] = tempSelectedGorup[0];
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = alt_bld.create();
            alert.show();
        });


        // Custom Subtitle

        AtomicInteger subContentID = new AtomicInteger();
        if(resumePosition != 0 && simpleExoPlayer != null) {
           if(ct == 1) {
               RequestQueue queue0 = Volley.newRequestQueue(context);
               StringRequest sr0 = new StringRequest(Request.Method.POST, AppConfig.url +"getcontentidfromurl/"+contentID+"/"+ct, response0 -> {
                   JsonObject jsonObject = new Gson().fromJson(response0, JsonObject.class);
                   int subCiD = jsonObject.get("id").getAsInt();
                   subContentID.set(subCiD);

                   RequestQueue queue = Volley.newRequestQueue(context);
                   StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"getsubtitle/"+subContentID.get()+"/"+ct, response -> {

                       if(!response.equals("No Data Avaliable")) {
                           JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                           int count = 0;
                           for (JsonElement rootElement : jsonArray) {
                               count++;

                               JsonObject rootObject = rootElement.getAsJsonObject();

                               MediaItem.Subtitle subtitle = null;
                               if(rootObject.get("mime_type").getAsString().equals("WebVTT")) {
                                   subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_VTT, rootObject.get("language").getAsString());
                               } else if(rootObject.get("mime_type").getAsString().equals("TTML") || rootObject.get("mime_type").getAsString().equals("SMPTE-TT")) {
                                   subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_TTML, rootObject.get("language").getAsString());
                               } else if(rootObject.get("mime_type").getAsString().equals("SubRip")) {
                                   subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                               } else if(rootObject.get("mime_type").getAsString().equals("SubStationAlpha-SSA)") || rootObject.get("mime_type").getAsString().equals("SubStationAlpha-ASS)")) {
                                   subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_SSA, rootObject.get("language").getAsString());
                               } else {
                                   subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                               }

                               MediaSource textMediaSource = new SingleSampleMediaSource.Factory(new DefaultDataSource.Factory(this)).createMediaSource(subtitle, C.TIME_UNSET);

                               if(nMediaSource == null) {
                                   nMediaSource = new MergingMediaSource(mediaSource, textMediaSource);
                               } else {
                                   nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
                               }

                               if(count == jsonArray.size()) {
                                   if(nMediaSource != null) {
                                       simpleExoPlayer.setMediaSource(nMediaSource);
                                       simpleExoPlayer.prepare();
                                       simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                                       if(resumePosition != 0 && simpleExoPlayer != null) {
                                           simpleExoPlayer.seekTo(resumePosition);
                                       }
                                   } else {
                                       simpleExoPlayer.setMediaSource(mediaSource);
                                       simpleExoPlayer.prepare();
                                       simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                                       if(resumePosition != 0 && simpleExoPlayer != null) {
                                           simpleExoPlayer.seekTo(resumePosition);
                                       }
                                   }
                               }
                           }
                       } else {
                           simpleExoPlayer.setMediaSource(mediaSource);
                           simpleExoPlayer.prepare();
                           simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                           if(resumePosition != 0 && simpleExoPlayer != null) {
                               simpleExoPlayer.seekTo(resumePosition);
                           }
                       }
                   }, error -> {
                       simpleExoPlayer.setMediaSource(mediaSource);
                       simpleExoPlayer.prepare();
                       simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                       if(resumePosition != 0 && simpleExoPlayer != null) {
                           simpleExoPlayer.seekTo(resumePosition);
                       }
                   }) {
                       @Override
                       public Map<String, String> getHeaders() throws AuthFailureError {
                           Map<String,String> params = new HashMap<>();
                           params.put("x-api-key", AppConfig.apiKey);
                           return params;
                       }
                   };
                   queue.add(sr);
               }, error -> {
                   //Do Nothing
               }) {
                   @Override
                   protected Map<String,String> getParams(){
                       Map<String,String> params = new HashMap<String, String>();
                       params.put("url",cpUrl);
                       return params;
                   }

                   @Override
                   public Map<String, String> getHeaders() throws AuthFailureError {
                       Map<String,String> params = new HashMap<>();
                       params.put("x-api-key", AppConfig.apiKey);
                       return params;
                   }
               };
               queue0.add(sr0);
           } else {
               subContentID.set(contentID);

               RequestQueue queue = Volley.newRequestQueue(context);
               StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"getsubtitle"+subContentID.get()+"/"+ct, response -> {

                   if(!response.equals("No Data Avaliable")) {
                       JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                       int count = 0;
                       for (JsonElement rootElement : jsonArray) {
                           count++;

                           JsonObject rootObject = rootElement.getAsJsonObject();

                           MediaItem.Subtitle subtitle = null;
                           if(rootObject.get("mime_type").getAsString().equals("WebVTT")) {
                               subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_VTT, rootObject.get("language").getAsString());
                           } else if(rootObject.get("mime_type").getAsString().equals("TTML") || rootObject.get("mime_type").getAsString().equals("SMPTE-TT")) {
                               subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_TTML, rootObject.get("language").getAsString());
                           } else if(rootObject.get("mime_type").getAsString().equals("SubRip")) {
                               subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                           } else if(rootObject.get("mime_type").getAsString().equals("SubStationAlpha-SSA)") || rootObject.get("mime_type").getAsString().equals("SubStationAlpha-ASS)")) {
                               subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_SSA, rootObject.get("language").getAsString());
                           } else {
                               subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                           }

                           MediaSource textMediaSource = new SingleSampleMediaSource.Factory(new DefaultDataSource.Factory(this)).createMediaSource(subtitle, C.TIME_UNSET);

                           if(nMediaSource == null) {
                               nMediaSource = new MergingMediaSource(mediaSource, textMediaSource);
                           } else {
                               nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
                           }

                           if(count == jsonArray.size()) {
                               if(nMediaSource != null) {
                                   simpleExoPlayer.setMediaSource(nMediaSource);
                                   simpleExoPlayer.prepare();
                                   simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                                   if(resumePosition != 0 && simpleExoPlayer != null) {
                                       simpleExoPlayer.seekTo(resumePosition);
                                   }
                               } else {
                                   simpleExoPlayer.setMediaSource(mediaSource);
                                   simpleExoPlayer.prepare();
                                   simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                                   if(resumePosition != 0 && simpleExoPlayer != null) {
                                       simpleExoPlayer.seekTo(resumePosition);
                                   }
                               }
                           }
                       }
                   } else {
                       simpleExoPlayer.setMediaSource(mediaSource);
                       simpleExoPlayer.prepare();
                       simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                       if(resumePosition != 0 && simpleExoPlayer != null) {
                           simpleExoPlayer.seekTo(resumePosition);
                       }
                   }
               }, error -> {
                   simpleExoPlayer.setMediaSource(mediaSource);
                   simpleExoPlayer.prepare();
                   simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                   if(resumePosition != 0 && simpleExoPlayer != null) {
                       simpleExoPlayer.seekTo(resumePosition);
                   }
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

        } else {
            subContentID.set(sourceID);

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"getsubtitle/"+subContentID.get()+"/"+ct, response -> {

                if(!response.equals("No Data Avaliable")) {
                    JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                    int count = 0;
                    for (JsonElement rootElement : jsonArray) {
                        count++;

                        JsonObject rootObject = rootElement.getAsJsonObject();

                        MediaItem.Subtitle subtitle = null;
                        if(rootObject.get("mime_type").getAsString().equals("WebVTT")) {
                            subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_VTT, rootObject.get("language").getAsString());
                        } else if(rootObject.get("mime_type").getAsString().equals("TTML") || rootObject.get("mime_type").getAsString().equals("SMPTE-TT")) {
                            subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_TTML, rootObject.get("language").getAsString());
                        } else if(rootObject.get("mime_type").getAsString().equals("SubRip")) {
                            subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                        } else if(rootObject.get("mime_type").getAsString().equals("SubStationAlpha-SSA)") || rootObject.get("mime_type").getAsString().equals("SubStationAlpha-ASS)")) {
                            subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.TEXT_SSA, rootObject.get("language").getAsString());
                        } else {
                            subtitle = new  MediaItem.Subtitle(Uri.parse(rootObject.get("subtitle_url").getAsString()), MimeTypes.APPLICATION_SUBRIP, rootObject.get("language").getAsString());
                        }

                        MediaSource textMediaSource = new SingleSampleMediaSource.Factory(new DefaultDataSource.Factory(this)).createMediaSource(subtitle, C.TIME_UNSET);

                        if(nMediaSource == null) {
                            nMediaSource = new MergingMediaSource(mediaSource, textMediaSource);
                        } else {
                            nMediaSource = new MergingMediaSource(nMediaSource, textMediaSource);
                        }

                        if(count == jsonArray.size()) {
                            if(nMediaSource != null) {
                                simpleExoPlayer.setMediaSource(nMediaSource);
                                simpleExoPlayer.prepare();
                                simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                                if(resumePosition != 0 && simpleExoPlayer != null) {
                                    simpleExoPlayer.seekTo(resumePosition);
                                }
                            } else {
                                simpleExoPlayer.setMediaSource(mediaSource);
                                simpleExoPlayer.prepare();
                                simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                                if(resumePosition != 0 && simpleExoPlayer != null) {
                                    simpleExoPlayer.seekTo(resumePosition);
                                }
                            }
                        }
                    }
                } else {
                    simpleExoPlayer.setMediaSource(mediaSource);
                    simpleExoPlayer.prepare();
                    simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                    if(resumePosition != 0 && simpleExoPlayer != null) {
                        simpleExoPlayer.seekTo(resumePosition);
                    }
                }
            }, error -> {
                simpleExoPlayer.setMediaSource(mediaSource);
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));

                if(resumePosition != 0 && simpleExoPlayer != null) {
                    simpleExoPlayer.seekTo(resumePosition);
                }
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






        simpleExoPlayer.addListener(new com.google.android.exoplayer2.Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == com.google.android.exoplayer2.Player.STATE_READY) {
                    // Active playback.
                    contentLoaded = true;

                    db.resumeContentDao().updateDuration(simpleExoPlayer.getDuration(), resumeContentID);

                } else if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
                    if (ContentType != null) {
                        if (ContentType.equals("WebSeries")) {
                            if (Next_Ep_Avilable.equals("Yes")) {
                                Play_Next_btn.setVisibility(View.VISIBLE);
                                CountDownTimer mTimer = new CountDownTimer(5000, 100) {
                                    public void onTick(long millisUntilFinished) {
                                        Play_Next_btn.setText("Playing Next In " + Long.toString(millisUntilFinished / 1000) + "Sec");
                                    }

                                    @Override
                                    public void onFinish() {
                                        Play_Next_btn.setText("Playing Now");
                                        Intent intent = new Intent();
                                        intent.putExtra("Current_List_Position", Current_List_Position);
                                        setResult(RESULT_OK, intent);

                                        isBackPressed = true;
                                        releasePlayer();
                                        handler.removeCallbacks(runnable);
                                        finish();
                                    }
                                };
                                mTimer.start();
                            }

                        }
                    }

                }  else {
                    // Paused by app.
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                //PlayBack Error
            }
        });

    }

    private void pausePlayer(){
        if(simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));
            simpleExoPlayer.getPlaybackState();
            simpleExoPlayer.pause();
        }
    }
    private void startPlayer(){
        if(simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(tinyDB.getBoolean(AUTOPLAY));
            simpleExoPlayer.getPlaybackState();
        }
    }

    private void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer.clearVideoSurface();
            simpleExoPlayer = null;
        }
    }

    Boolean isBackPressed = false;
    @Override
    public void onBackPressed() {
        isBackPressed = true;
        releasePlayer();
        handler.removeCallbacks(runnable);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isBackPressed) {
            pausePlayer();
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
        bGljZW5zZV9jb2Rl = jsonObject.get("license_code").getAsString();
    }

    private void finishPlayer() {
        progressDialog.hide();
        isBackPressed = true;
        releasePlayer();
        handler.removeCallbacks(runnable);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Player.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Player.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(torrentStreamServer!=null) {
            torrentStreamServer.stopTorrentStream();
        }
    }
}