package com.dooo.android;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.dooo.android.list.YTStreamList;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.Yts;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrailerPlayer extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer  simpleExoPlayer;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    static ProgressDialog progressDialog;

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

        if(AppConfig.FLAG_SECURE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        setContentView(R.layout.activity_trailer_player);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(TrailerPlayer.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(TrailerPlayer.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        playerView = findViewById(R.id.player_view);

        Intent intent = getIntent();
        String trailerUrl = intent.getExtras().getString("Trailer_URL");

        progressDialog.show();
        Yts.getlinks(this, trailerUrl, new Yts.VolleyCallback(){
            @Override
            public void onSuccess(List<YTStreamList> result) {
                ytMultipleQualityDialog(TrailerPlayer.this, result);
            }

            @Override
            public void onError(VolleyError error) {

            }
        });

        ImageView imgFullScr = findViewById(R.id.img_full_scr);
        imgFullScr.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    imgFullScr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
                } else if(playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    imgFullScr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_exit_24));
                } else {
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    imgFullScr.setImageDrawable(getDrawable(R.drawable.ic_baseline_fullscreen_24));
                }

            }
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

        fullScreenall();
    }

    public void fullScreenall() {
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
                .setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Yts.getStreamLinks(context, (String) token[which], (String) vid[which], new Yts.VolleyCallback2(){

                            @Override
                            public void onSuccess(String result) {
                                TrustManager[] trustAllCerts = new TrustManager[] {
                                        new X509TrustManager() {
                                            public java.security.cert.X509Certificate[] getAcceptedIssuers()
                                            {
                                                return null;
                                            }
                                            public void checkClientTrusted(X509Certificate[] certs, String authType)
                                            {
                                                //
                                            }
                                            public void checkServerTrusted(X509Certificate[] certs, String authType)
                                            {
                                                //
                                            }
                                        }
                                };
                                try {
                                    SSLContext sc = SSLContext.getInstance("TLS");
                                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                                } catch (KeyManagementException | NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }

                                initializePlayer(result);
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
                    finish();
                });
        builder.show();
    }

    void initializePlayer(String URL) {
        simpleExoPlayer=new SimpleExoPlayer.Builder(this)
                .setSeekForwardIncrementMs(10000)
                .setSeekBackIncrementMs(10000)
                .build();
        DataSource.Factory factory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "KAIOS"));

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(MediaItem.fromUri(Uri.parse(URL)));
        playerView.setPlayer(simpleExoPlayer);
        playerView.setKeepScreenOn(true);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);

    }

    private void pausePlayer(){
        if(simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.getPlaybackState();
        }
    }
    private void startPlayer(){
        if(simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
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
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!isBackPressed) {
            pausePlayer();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(TrailerPlayer.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(TrailerPlayer.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}