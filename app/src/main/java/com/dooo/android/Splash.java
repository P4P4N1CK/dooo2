package com.dooo.android;

import static com.dooo.android.utils.Constants.AUTOPLAY;
import static com.dooo.android.utils.Constants.EXTENTIONS;
import static com.dooo.android.utils.Constants.SOFTWARE_EXTENTIONS;
import static com.unity3d.services.core.properties.ClientProperties.getActivity;
import static org.apache.commons.lang3.StringUtils.length;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.dooo.android.adepter.GenreListAdepter;
import com.dooo.android.adepter.LiquidPageradepter;
import com.dooo.android.list.GenreList;
import com.dooo.android.utils.AppOpenAdManagerActivity;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.TinyDB;
import com.dooo.android.utils.Utils;
import com.LiquidPager.liquid_swipe.LiquidPager;
import com.facebook.FacebookSdk;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jetradarmobile.snowfall.SnowfallView;
import com.onesignal.OneSignal;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import es.dmoral.toasty.Toasty;

public class Splash extends AppCompatActivity {
    Context context = this;

    public static String notificationData = "";
    String userData;
    String apiKey;
    Integer loginMandatory;
    Integer maintenance;
    String blocked_regions;

    String latestAPKVersionName;
    String latestAPKVersionCode;
    String apkFileUrl;
    String whatsNewOnLatestApk;
    int updateSkipable;
    int updateType;

    int googleplayAppUpdateType;
    AppUpdateManager appUpdateManager = null;


    private boolean vpnStatus = false;
    private HelperUtils helperUtils;

    ConstraintLayout splashLayout0, splashLayout1, splashLayout2, splashLayout3;

    TinyDB tinyDB;

    boolean pinLockStatus = false;
    String pinLockPin = "";

    @Override
    protected void onStart() {
        super.onStart();
        vpnStatus = new HelperUtils(Splash.this).isVpnConnectionAvailable();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(context);
        if(!tinyDB.getString("appLanguage").equals("") || tinyDB.getString("appLanguage") != null) {
            String languageToLoad  = tinyDB.getString("appLanguage");
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }

        StartAppAd.disableSplash();

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            AppConfig.rawUrl = mFirebaseRemoteConfig.getString("SERVER_URL");
                            AppConfig.url = mFirebaseRemoteConfig.getString("SERVER_URL")+"android/";
                            AppConfig.apiKey = mFirebaseRemoteConfig.getString("API_KEY");
                            AppConfig.allowVPN = true; //mFirebaseRemoteConfig.getBoolean("ALLOW_VPN");
                            AppConfig.FLAG_SECURE = mFirebaseRemoteConfig.getBoolean("FLAG_SECURE");
                            AppConfig.allowRoot = mFirebaseRemoteConfig.getBoolean("ALLOW_ROOT");

                            Log.d("test", mFirebaseRemoteConfig.getString("SERVER_URL"));

                            if(AppConfig.FLAG_SECURE) {
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                        WindowManager.LayoutParams.FLAG_SECURE);
                            }

                            getWindow().setFlags(
                                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                            );
                            getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                            switch (tinyDB.getInt("splashScreenType")) {
                                case 1:
                                    setContentView(R.layout.activity_splash);

                                    splashLayout0 = findViewById(R.id.splashLayout0);
                                    splashLayout1 = findViewById(R.id.splashLayout1);
                                    splashLayout2 = findViewById(R.id.splashLayout2);
                                    splashLayout3 = findViewById(R.id.splashLayout3);

                                    if(!Objects.equals(tinyDB.getString("splashImageUrl"), "")) {
                                        Glide.with(context)
                                                .load(tinyDB.getString("splashImageUrl"))
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .centerCrop()
                                                .into((ImageView) findViewById(R.id.splashScreenMainImage));
                                    }

                                    splashLayout0.setVisibility(View.GONE);
                                    splashLayout1.setVisibility(View.VISIBLE);
                                    splashLayout2.setVisibility(View.GONE);
                                    splashLayout3.setVisibility(View.GONE);
                                    break;
                                case 2:
                                    setContentView(R.layout.activity_splash);

                                    splashLayout0 = findViewById(R.id.splashLayout0);
                                    splashLayout1 = findViewById(R.id.splashLayout1);
                                    splashLayout2 = findViewById(R.id.splashLayout2);
                                    splashLayout3 = findViewById(R.id.splashLayout3);

                                    if(!Objects.equals(tinyDB.getString("splashLottieUrl"), "")) {
                                        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
                                        lottieAnimationView.setAnimationFromUrl(tinyDB.getString("splashLottieUrl"));
                                    }

                                    splashLayout0.setVisibility(View.GONE);
                                    splashLayout1.setVisibility(View.GONE);
                                    splashLayout2.setVisibility(View.VISIBLE);
                                    splashLayout3.setVisibility(View.GONE);
                                    break;
                                case 3:
                                    setContentView(R.layout.activity_splash);

                                    splashLayout0 = findViewById(R.id.splashLayout0);
                                    splashLayout1 = findViewById(R.id.splashLayout1);
                                    splashLayout2 = findViewById(R.id.splashLayout2);
                                    splashLayout3 = findViewById(R.id.splashLayout3);

                                    WebView webView = findViewById(R.id.webview);
                                    webView.getSettings().setJavaScriptEnabled(true);
                                    webView.loadUrl(AppConfig.url+"splash");

                                    splashLayout0.setVisibility(View.GONE);
                                    splashLayout1.setVisibility(View.GONE);
                                    splashLayout2.setVisibility(View.GONE);
                                    splashLayout3.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    Window window = Splash.this.getWindow();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    if(!Objects.equals(tinyDB.getString("splashBgColor"), "")) {
                                        window.setStatusBarColor(Color.parseColor(tinyDB.getString("splashBgColor")));
                                    } else {
                                        window.setStatusBarColor(ContextCompat.getColor(Splash.this,R.color.Splash_TitleBar_BG));
                                    }

                                    setContentView(R.layout.activity_splash);

                                    splashLayout0 = findViewById(R.id.splashLayout0);
                                    splashLayout1 = findViewById(R.id.splashLayout1);
                                    splashLayout2 = findViewById(R.id.splashLayout2);
                                    splashLayout3 = findViewById(R.id.splashLayout3);

                                    splashLayout0.setVisibility(View.VISIBLE);
                                    splashLayout1.setVisibility(View.GONE);
                                    splashLayout2.setVisibility(View.GONE);
                                    splashLayout3.setVisibility(View.GONE);
                            }

                            if(!Objects.equals(tinyDB.getString("splashBgColor"), "")) {
                                findViewById(R.id.splash).setBackgroundColor(Color.parseColor(tinyDB.getString("splashBgColor")));
                            }

                            helperUtils = new HelperUtils(Splash.this);
                            vpnStatus = new HelperUtils(Splash.this).isVpnConnectionAvailable();

                            ApplicationInfo restrictedApp = helperUtils.getRestrictApp();
                            if (restrictedApp != null){
                                Log.e("test", restrictedApp.loadLabel(Splash.this.getPackageManager()).toString());
                                HelperUtils.showWarningDialog(Splash.this, "Restricted App!", "Please Uninstall "+restrictedApp.loadLabel(Splash.this.getPackageManager()).toString()+" to use this App On this Device!", R.raw.sequre);
                            } else {
                                if (HelperUtils.cr(Splash.this, AppConfig.allowRoot)) {
                                    HelperUtils.showWarningDialog(Splash.this, "ROOT!", "You are Not Allowed To Use this App on Rooted Device!", R.raw.sequre);

                                } else {
                                    if (AppConfig.allowVPN) {
                                        loadData();
                                    } else {
                                        if (vpnStatus) {
                                            helperUtils.showWarningDialog(Splash.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
                                        } else {
                                            loadData();
                                        }
                                    }
                                }
                            }
                        } else {
                            serverError();
                        }
                    }
                });



        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                Splash.this,
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

        checkStoragePermission();
    }

    // ------------------ checking storage permission ------------
    private boolean checkStoragePermission() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Log.d("test", "Permission is granted");
                return true;

            } else {
                Log.d("test", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.d("test", "Permission is granted");
            return true;
        }*/
        return true;
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userData = sharedPreferences.getString("UserData", null);
        loadConfig();
    }

    private void loadConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"get_config", response -> {
            JsonObject jsonObjectJWT = new Gson().fromJson(response, JsonObject.class);
            String token = jsonObjectJWT.get("token").getAsString();
            try {
                Algorithm algorithm = Algorithm.HMAC256(AppConfig.apiKey);
                JWTVerifier verifier = JWT.require(algorithm)
                        .build();
                //DecodedJWT verify = verifier.verify(token);
                DecodedJWT jwt = JWT.decode(token);

                String config = jwt.getClaim("config").toString();

                JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
                apiKey = jsonObject.get("api_key").getAsString();
                loginMandatory = jsonObject.get("login_mandatory").getAsInt();
                maintenance = jsonObject.get("maintenance").getAsInt();
                blocked_regions = jsonObject.get("blocked_regions").isJsonNull() ? "" : jsonObject.get("blocked_regions").getAsString();
                AppConfig.adType = jsonObject.get("ad_type").getAsInt();
                saveConfig(config);
                saveNotification();

                // OneSignal Initialization
                OneSignal.initWithContext(Splash.this);
                OneSignal.setAppId(jsonObject.get("onesignal_appid").getAsString());
                OneSignal.setNotificationOpenedHandler(
                        result -> Splash.notificationData = result.getNotification().getAdditionalData().toString());

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

                int content_item_type = jsonObject.get("content_item_type").getAsInt();
                int live_tv_content_item_type = jsonObject.get("live_tv_content_item_type").getAsInt();
                int webSeriesEpisodeitemType = jsonObject.get("webSeriesEpisodeitemType").getAsInt();

                switch (content_item_type) {
                    case 0:
                        AppConfig.contentItem = R.layout.movie_item;
                        break;
                    case 1:
                        AppConfig.contentItem = R.layout.movie_item_v2;
                        break;
                    default:
                        AppConfig.contentItem = R.layout.movie_item;
                }

                switch (live_tv_content_item_type) {
                    case 0:
                        AppConfig.small_live_tv_channel_item = R.layout.small_live_tv_channel_item;
                        AppConfig.live_tv_channel_item = R.layout.live_tv_channel_item;
                        break;
                    case 1:
                        AppConfig.small_live_tv_channel_item = R.layout.small_live_tv_channel_item_v2;
                        AppConfig.live_tv_channel_item = R.layout.live_tv_channel_item_v2;
                        break;
                    default:
                        AppConfig.small_live_tv_channel_item = R.layout.small_live_tv_channel_item;
                        AppConfig.live_tv_channel_item = R.layout.live_tv_channel_item;
                }

                switch (webSeriesEpisodeitemType) {
                    case 0:
                        AppConfig.webSeriesEpisodeitem = R.layout.episode_item;
                        break;
                    case 1:
                        AppConfig.webSeriesEpisodeitem = R.layout.episode_item_v2;
                        break;
                    default:
                        AppConfig.webSeriesEpisodeitem = R.layout.episode_item;
                }

                latestAPKVersionName = jsonObject.get("Latest_APK_Version_Name").getAsString();
                latestAPKVersionCode = jsonObject.get("Latest_APK_Version_Code").getAsString();
                apkFileUrl = jsonObject.get("APK_File_URL").getAsString();
                whatsNewOnLatestApk = jsonObject.get("Whats_new_on_latest_APK").getAsString();
                updateSkipable = jsonObject.get("Update_Skipable").getAsInt();
                updateType = jsonObject.get("Update_Type").getAsInt();
                googleplayAppUpdateType = jsonObject.get("googleplayAppUpdateType").getAsInt();

                String whatsNew = whatsNewOnLatestApk.replace(",", "\n").trim();

                int version = BuildConfig.VERSION_CODE;
                int latestVersionCode;
                try{
                    latestVersionCode = Integer.parseInt(latestAPKVersionCode);
                } catch (NumberFormatException e) {
                    latestVersionCode = 1;
                }

                int onboarding_status = 0;
                if(!jsonObject.get("onboarding_status").isJsonNull()) {
                    onboarding_status = jsonObject.get("onboarding_status").getAsInt();
                }


                if(HelperUtils.isFirstOpen(context)) {
                    tinyDB.putBoolean(AUTOPLAY, true);
                    tinyDB.putBoolean(EXTENTIONS, true);
                    tinyDB.putBoolean(SOFTWARE_EXTENTIONS, true);

                    if(onboarding_status == 1) {
                        LiquidPager liquidPager = findViewById(R.id.liquidPager);
                        liquidPager.setAdapter(new LiquidPageradepter(getSupportFragmentManager()));
                        liquidPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                                if(position == 3) {
                                    liquidPager.setAdapter(null);
                                    int latestVersionCode;
                                    try{
                                        latestVersionCode = Integer.parseInt(latestAPKVersionCode);
                                    } catch (NumberFormatException e) {
                                        latestVersionCode = 1;
                                    }
                                    if(latestVersionCode > version) {
                                        if(updateType == 2) {
                                            appUpdateManager = AppUpdateManagerFactory.create(context);
                                            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                                                if(googleplayAppUpdateType == 0) {
                                                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                                        try {
                                                            appUpdateManager.startUpdateFlowForResult(
                                                                    appUpdateInfo,
                                                                    AppUpdateType.FLEXIBLE,
                                                                    Splash.this,
                                                                    15);
                                                        } catch (IntentSender.SendIntentException e) {
                                                            e.printStackTrace();
                                                        }

                                                        InstallStateUpdatedListener listener = state -> {
                                                            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                                Snackbar snackbar =
                                                                        Snackbar.make(
                                                                                findViewById(R.id.splash),
                                                                                "An update has just been downloaded.",
                                                                                Snackbar.LENGTH_INDEFINITE);
                                                                snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                                                snackbar.setActionTextColor(
                                                                        ContextCompat.getColor(context, R.color.white));
                                                                snackbar.show();
                                                            }
                                                        };
                                                    }
                                                } else if(googleplayAppUpdateType == 1) {
                                                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                                        try {
                                                            appUpdateManager.startUpdateFlowForResult(
                                                                    appUpdateInfo,
                                                                    AppUpdateType.IMMEDIATE,
                                                                    Splash.this,
                                                                    15);
                                                        } catch (IntentSender.SendIntentException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            });
                                        } else {
                                            MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                                                    .setTitle("Update "+latestAPKVersionName)
                                                    .setMessage(whatsNew)
                                                    .setCancelable(false)
                                                    .setAnimation(R.raw.rocket_telescope)
                                                    .setNegativeButton(updateSkipable==0?"Exit":"Cancel", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                                        if(updateSkipable == 0) { //NO
                                                            finish();
                                                        } else if(updateSkipable == 1) { //YES
                                                            dialogInterface.dismiss();
                                                            openApp();
                                                        }
                                                    })
                                                    .setPositiveButton("Update!", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                                        if(updateType == 0) {
                                                            Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                                            intent.putExtra("Update_Title", "Update "+latestAPKVersionName);
                                                            intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                                            intent.putExtra("APK_File_URL", apkFileUrl);
                                                            startActivity(intent);
                                                        } else if(updateType == 1) {
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .build();
                                            mDialog.show();
                                        }
                                    } else {
                                        openApp();
                                    }
                                }
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    } else {
                        if(latestVersionCode > version) {
                            if(updateType == 2) {
                                appUpdateManager = AppUpdateManagerFactory.create(context);
                                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                                    if(googleplayAppUpdateType == 0) {
                                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                            try {
                                                appUpdateManager.startUpdateFlowForResult(
                                                        appUpdateInfo,
                                                        AppUpdateType.FLEXIBLE,
                                                        this,
                                                        15);
                                            } catch (IntentSender.SendIntentException e) {
                                                e.printStackTrace();
                                            }

                                            InstallStateUpdatedListener listener = state -> {
                                                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                    Snackbar snackbar =
                                                            Snackbar.make(
                                                                    findViewById(R.id.splash),
                                                                    "An update has just been downloaded.",
                                                                    Snackbar.LENGTH_INDEFINITE);
                                                    snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                                    snackbar.setActionTextColor(
                                                            ContextCompat.getColor(context, R.color.white));
                                                    snackbar.show();
                                                }
                                            };
                                        }
                                    } else if(googleplayAppUpdateType == 1) {
                                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                            try {
                                                appUpdateManager.startUpdateFlowForResult(
                                                        appUpdateInfo,
                                                        AppUpdateType.IMMEDIATE,
                                                        this,
                                                        15);
                                            } catch (IntentSender.SendIntentException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            } else {
                                MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                                        .setTitle("Update "+latestAPKVersionName)
                                        .setMessage(whatsNew)
                                        .setCancelable(false)
                                        .setAnimation(R.raw.rocket_telescope)
                                        .setNegativeButton(updateSkipable==0?"Exit":"Cancel", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                            if(updateSkipable == 0) { //NO
                                                finish();
                                            } else if(updateSkipable == 1) { //YES
                                                dialogInterface.dismiss();
                                                openApp();
                                            }
                                        })
                                        .setPositiveButton("Update!", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                            if(updateType == 0) {
                                                Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                                intent.putExtra("Update_Title", "Update "+latestAPKVersionName);
                                                intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                                intent.putExtra("APK_File_URL", apkFileUrl);
                                                startActivity(intent);
                                            } else if(updateType == 1) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                                startActivity(intent);
                                            }
                                        })
                                        .build();
                                mDialog.show();
                            }
                        } else {
                            openApp();
                        }
                    }
                } else {
                    if(latestVersionCode > version) {
                        if(updateType == 2) {
                            appUpdateManager = AppUpdateManagerFactory.create(context);
                            Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                            appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                                if(googleplayAppUpdateType == 0) {
                                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.FLEXIBLE,
                                                    this,
                                                    15);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }

                                        InstallStateUpdatedListener listener = state -> {
                                            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                                                Snackbar snackbar =
                                                        Snackbar.make(
                                                                findViewById(R.id.splash),
                                                                "An update has just been downloaded.",
                                                                Snackbar.LENGTH_INDEFINITE);
                                                snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                                snackbar.setActionTextColor(
                                                        ContextCompat.getColor(context, R.color.white));
                                                snackbar.show();
                                            }
                                        };
                                    }
                                } else if(googleplayAppUpdateType == 1) {
                                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.IMMEDIATE,
                                                    this,
                                                    15);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            MaterialDialog mDialog = new MaterialDialog.Builder(Splash.this)
                                    .setTitle("Update "+latestAPKVersionName)
                                    .setMessage(whatsNew)
                                    .setCancelable(false)
                                    .setAnimation(R.raw.rocket_telescope)
                                    .setNegativeButton(updateSkipable==0?"Exit":"Cancel", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                        if(updateSkipable == 0) { //NO
                                            finish();
                                        } else if(updateSkipable == 1) { //YES
                                            dialogInterface.dismiss();
                                            openApp();
                                        }
                                    })
                                    .setPositiveButton("Update!", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                                        if(updateType == 0) {
                                            Intent intent = new Intent(Splash.this, InAppUpdate.class);
                                            intent.putExtra("Update_Title", "Update "+latestAPKVersionName);
                                            intent.putExtra("Whats_new_on_latest_APK", whatsNewOnLatestApk);
                                            intent.putExtra("APK_File_URL", apkFileUrl);
                                            startActivity(intent);
                                        } else if(updateType == 1) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(apkFileUrl));
                                            startActivity(intent);
                                        }
                                    })
                                    .build();
                            mDialog.show();
                        }
                    } else {
                        openApp();
                    }
                }
            } catch (JWTVerificationException exception){
                Log.d("test", String.valueOf(exception));
            }
        }, error -> {
            serverError();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void loadRemoteConfig() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, Utils.fromBase64("aHR0cHM6Ly9jbG91ZC50ZWFtLWRvb28uY29tL0Rvb28vYXBpL2dldENvbmZpZy5waHA/Y29kZT0=")+AppConfig.bGljZW5zZV9jb2Rl, response -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RemoteConfig", String.valueOf(response));
            editor.apply();
        }, error -> {
            // Do nothing because
        });

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    void openApp() {
        if(Objects.equals(AppConfig.packageName, "")) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, "http://ip-api.com/json", response -> {
                if(!response.equals("")) {
                    JsonObject rootObject = new Gson().fromJson(response, JsonObject.class);
                    String countryCode = rootObject.get("countryCode").getAsString();

                    String[] blocked_regions_array = blocked_regions.split(",");
                    if ( ArrayUtils.contains( blocked_regions_array, countryCode ) ) {
                        SpinKitView spin_kit = findViewById(R.id.spin_kit);
                        spin_kit.setVisibility(View.INVISIBLE);

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.blocked_country_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);

                        Button dialogClose = dialog.findViewById(R.id.Dialog_Close);
                        dialogClose.setBackgroundColor(Color.parseColor(AppConfig.primeryThemeColor));
                        dialogClose.setOnClickListener(v1 -> finish());

                        dialog.show();
                    } else {
                        initApp();
                    }
                } else {
                    initApp();
                }
            }, error -> {
                initApp();
            });
            queue.add(sr);
        } else if(BuildConfig.APPLICATION_ID.equals(AppConfig.packageName)) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, "http://ip-api.com/json", response -> {
                if(!response.equals("")) {
                    JsonObject rootObject = new Gson().fromJson(response, JsonObject.class);
                    String countryCode = rootObject.get("countryCode").getAsString();

                    String[] blocked_regions_array = blocked_regions.split(",");
                    if ( ArrayUtils.contains( blocked_regions_array, countryCode ) ) {
                        SpinKitView spin_kit = findViewById(R.id.spin_kit);
                        spin_kit.setVisibility(View.INVISIBLE);

                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.blocked_country_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCanceledOnTouchOutside(false);

                        Button dialogClose = dialog.findViewById(R.id.Dialog_Close);
                        dialogClose.setBackgroundColor(Color.parseColor(AppConfig.primeryThemeColor));
                        dialogClose.setOnClickListener(v1 -> finish());

                        dialog.show();
                    } else {
                        initApp();
                    }
                } else {
                    initApp();
                }
            }, error -> {
                initApp();
            });
            queue.add(sr);
        } else {
            Toasty.error(context, "Invalid Package Name!", Toast.LENGTH_SHORT, true).show();
            finish();
        }

    }

    void initApp() {
        if(checkStoragePermission()) {
            if (maintenance == 0) {
                if (apiKey.equals(AppConfig.apiKey)) {
                    if(pinLockStatus) {
                        RelativeLayout lockView = findViewById(R.id.lockView);
                        lockView.setVisibility(View.VISIBLE);
                        PinLockView mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
                        IndicatorDots mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
                        mPinLockView.setPinLockListener(new PinLockListener() {
                            @Override
                            public void onComplete(String pin) {
                                if(Objects.equals(pin, pinLockPin)) {
                                    lockView.setVisibility(View.GONE);
                                    if (userData == null) {

                                        if (AppConfig.adType == 1) {
                                            Application application = getApplication();
                                            if (!(application instanceof AppOpenAdManagerActivity)) {
                                                mainAppOpen();
                                                return;
                                            }
                                            ((AppOpenAdManagerActivity) application).showAdIfAvailable(Splash.this, Splash.this::mainAppOpen);
                                        } else {
                                            mainAppOpen();
                                        }

                                    } else {
                                        if (AppConfig.adType == 1) {
                                            Application application = getApplication();
                                            if (!(application instanceof AppOpenAdManagerActivity)) {
                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.postDelayed(Splash.this::verifyUser, 500);
                                                return;
                                            }
                                            ((AppOpenAdManagerActivity) application).showAdIfAvailable(Splash.this, Splash.this::verifyUser);
                                        } else {
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            handler.postDelayed(Splash.this::verifyUser, 500);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onEmpty() {

                            }

                            @Override
                            public void onPinChange(int pinLength, String intermediatePin) {

                            }
                        });
                        mPinLockView.attachIndicatorDots(mIndicatorDots);
                        mPinLockView.setPinLength(length(Integer.toString(Integer.parseInt(pinLockPin))));
                        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
                        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
                    } else {
                        RelativeLayout lockView = findViewById(R.id.lockView);
                        lockView.setVisibility(View.GONE);
                        if (userData == null) {

                            if (AppConfig.adType == 1) {
                                Application application = getApplication();
                                if (!(application instanceof AppOpenAdManagerActivity)) {
                                    mainAppOpen();
                                    return;
                                }
                                ((AppOpenAdManagerActivity) application).showAdIfAvailable(Splash.this, Splash.this::mainAppOpen);
                            } else {
                                mainAppOpen();
                            }

                        } else {
                            if (AppConfig.adType == 1) {
                                Application application = getApplication();
                                if (!(application instanceof AppOpenAdManagerActivity)) {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(Splash.this::verifyUser, 500);
                                    return;
                                }
                                ((AppOpenAdManagerActivity) application).showAdIfAvailable(Splash.this, Splash.this::verifyUser);
                            } else {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(Splash.this::verifyUser, 500);
                            }
                        }
                    }
                } else {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } else {
                Intent intent = new Intent(Splash.this, Maintenance.class);
                startActivity(intent);
                finish();
            }
        } else {
            openApp();
        }
    }

    void mainAppOpen() {
        if (loginMandatory == 0) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                saveNotification();
                Intent intent = new Intent(Splash.this, Home.class);
                intent.putExtra("Notification_Data", notificationData);
                startActivity(intent);
                notificationData = "";
                finish();
            }, 500);
        } else if (loginMandatory == 1) {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                saveNotification();
                Intent intent = new Intent(Splash.this, LoginSignup.class);
                startActivity(intent);
                finish();
            }, 500);
        }
    }

    void verifyUser() {
        JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
        String email = jsonObject.get("Email").getAsString();
        String password = jsonObject.get("Password").getAsString();

        String originalInput = "login:"+email+":" + password;
        String encoded = Utils.toBase64(originalInput);

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"authentication", response -> {
            if(!response.equals("")) {
                JsonObject jsonObject1 = new Gson().fromJson(response, JsonObject.class);
                String status = jsonObject1.get("Status").toString();
                status = status.substring(1, status.length() - 1);

                if (status.equals("Successful")) {
                    saveData(response);

                    JsonObject subObj = new Gson().fromJson(response, JsonObject.class);
                    int subscriptionType = subObj.get("subscription_type").getAsInt();
                    saveUserSubscriptionDetails(subscriptionType);

                    setOneSignalExternalID(String.valueOf(subObj.get("ID").getAsInt()));

                    saveNotification();
                    Intent intent = new Intent(Splash.this, Home.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else if (status.equals("Invalid Credential")) {
                    deleteData();
                    if (loginMandatory == 0) {
                        saveNotification();
                        Intent intent = new Intent(Splash.this, Home.class);
                        intent.putExtra("Notification_Data", notificationData);
                        startActivity(intent);
                        notificationData = "";
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, LoginSignup.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                deleteData();
                if (loginMandatory == 0) {
                    saveNotification();
                    Intent intent = new Intent(Splash.this, Home.class);
                    intent.putExtra("Notification_Data", notificationData);
                    startActivity(intent);
                    notificationData = "";
                    finish();
                } else {
                    Intent intent = new Intent(Splash.this, LoginSignup.class);
                    startActivity(intent);
                    finish();
                }
            }

        }, error -> {
            // Do nothing because
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
                params.put("encoded",encoded);
                params.put("device", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(sr);
    }

    private void setOneSignalExternalID(String externalID) {
        OneSignal.setExternalUserId(externalID, new OneSignal.OSExternalUserIdUpdateCompletionHandler() {
            @Override
            public void onSuccess(JSONObject results) {
                //Log.d("test", results.toString());
            }
            @Override
            public void onFailure(OneSignal.ExternalIdError error) {
                //Log.d("test", error.toString());
            }
        });
    }

    private void saveUserSubscriptionDetails(int subscriptionType) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"dmVyaWZ5", response -> {
            if(!response.equals(Utils.fromBase64("ZmFsc2U=")) && !response.equals(Utils.fromBase64("SW5hY3RpdmUgcHVyY2hhc2UgY29kZQ==")) && !response.equals(Utils.fromBase64("SW52YWxpZCBwdXJjaGFzZSBjb2Rl"))) {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subscription_type", String.valueOf(subscriptionType));
                editor.apply();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("subscription_type", "0");
                editor.apply();
            }
        }, error -> {
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("subscription_type", "0");
            editor.apply();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("x-api-key", AppConfig.apiKey);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void saveData(String userData) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", userData);
        editor.apply();
    }

    private void deleteData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserData");
        editor.apply();
    }

    private void saveNotification() {
        SharedPreferences sharedPreferences = getSharedPreferences("Notificatin_Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", notificationData);
        editor.apply();
    }

    private void saveConfig(String config) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Config", config);
        editor.apply();


        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
        AppConfig.adMobNative = jsonObject.get("adMob_Native").isJsonNull() ? "" : jsonObject.get("adMob_Native").getAsString();
        AppConfig.adMobBanner = jsonObject.get("adMob_Banner").isJsonNull() ? "" : jsonObject.get("adMob_Banner").getAsString();
        AppConfig.adMobInterstitial = jsonObject.get("adMob_Interstitial").isJsonNull() ? "" : jsonObject.get("adMob_Interstitial").getAsString();
        AppConfig.adMobAppOpenAd = jsonObject.get("adMob_AppOpenAd").isJsonNull() ? "" : jsonObject.get("adMob_AppOpenAd").getAsString();
        String StartApp_App_ID = jsonObject.get("StartApp_App_ID").isJsonNull() ? "" : jsonObject.get("StartApp_App_ID").getAsString();
        String Admob_APP_ID = jsonObject.get("Admob_APP_ID").isJsonNull() ? "" : jsonObject.get("Admob_APP_ID").getAsString();
        String facebook_app_id = jsonObject.get("facebook_app_id").isJsonNull() ? "" : jsonObject.get("facebook_app_id").getAsString();

        AppConfig.all_live_tv_type= jsonObject.get("all_live_tv_type").isJsonNull() ? 0 : jsonObject.get("all_live_tv_type").getAsInt();
        AppConfig.all_movies_type= jsonObject.get("all_movies_type").isJsonNull() ? 0 : jsonObject.get("all_movies_type").getAsInt();
        AppConfig.all_series_type= jsonObject.get("all_series_type").isJsonNull() ? 0 : jsonObject.get("all_series_type").getAsInt();

        AppConfig.facebook_banner_ads_placement_id = jsonObject.get("facebook_banner_ads_placement_id").isJsonNull() ? "" : jsonObject.get("facebook_banner_ads_placement_id").getAsString();
        AppConfig.facebook_interstitial_ads_placement_id = jsonObject.get("facebook_interstitial_ads_placement_id").isJsonNull() ? "" : jsonObject.get("facebook_interstitial_ads_placement_id").getAsString();

        AppConfig.AdColony_APP_ID= jsonObject.get("AdColony_app_id").isJsonNull() ? "" : jsonObject.get("AdColony_app_id").getAsString();
        AppConfig.AdColony_BANNER_ZONE_ID= jsonObject.get("AdColony_banner_zone_id").isJsonNull() ? "" : jsonObject.get("AdColony_banner_zone_id").getAsString();
        AppConfig.AdColony_INTERSTITIAL_ZONE_ID= jsonObject.get("AdColony_interstitial_zone_id").isJsonNull() ? "" : jsonObject.get("AdColony_interstitial_zone_id").getAsString();

        AppConfig.Unity_Game_ID= jsonObject.get("unity_game_id").isJsonNull() ? "" : jsonObject.get("unity_game_id").getAsString();
        AppConfig.Unity_Banner_ID= jsonObject.get("unity_banner_id").isJsonNull() ? "" : jsonObject.get("unity_banner_id").getAsString();
        AppConfig.Unity_rewardedVideo_ID = jsonObject.get("unity_interstitial_id").isJsonNull() ? "" : jsonObject.get("unity_interstitial_id").getAsString();

        AppConfig.Custom_Banner_url= jsonObject.get("custom_banner_url").isJsonNull() ? "" : jsonObject.get("custom_banner_url").getAsString();
        AppConfig.Custom_Banner_click_url_type = jsonObject.get("custom_banner_click_url_type").isJsonNull() ? 0 : jsonObject.get("custom_banner_click_url_type").getAsInt();
        AppConfig.Custom_Banner_click_url= jsonObject.get("custom_banner_click_url").isJsonNull() ? "" : jsonObject.get("custom_banner_click_url").getAsString();
        AppConfig.Custom_Interstitial_url= jsonObject.get("custom_interstitial_url").isJsonNull() ? "" : jsonObject.get("custom_interstitial_url").getAsString();
        AppConfig.Custom_Interstitial_click_url_type= jsonObject.get("custom_interstitial_click_url_type").isJsonNull() ? 0 : jsonObject.get("custom_interstitial_click_url_type").getAsInt();
        AppConfig.Custom_Interstitial_click_url= jsonObject.get("custom_interstitial_click_url").isJsonNull() ? "" : jsonObject.get("custom_interstitial_click_url").getAsString();

        AppConfig.applovin_sdk_key= jsonObject.get("applovin_sdk_key").isJsonNull() ? "" : jsonObject.get("applovin_sdk_key").getAsString();
        AppConfig.applovin_apiKey= jsonObject.get("applovin_apiKey").isJsonNull() ? "" : jsonObject.get("applovin_apiKey").getAsString();
        AppConfig.applovin_Banner_ID= jsonObject.get("applovin_Banner_ID").isJsonNull() ? "" : jsonObject.get("applovin_Banner_ID").getAsString();
        AppConfig.applovin_Interstitial_ID= jsonObject.get("applovin_Interstitial_ID").isJsonNull() ? "" : jsonObject.get("applovin_Interstitial_ID").getAsString();
        AppConfig.ironSource_app_key= jsonObject.get("ironSource_app_key").isJsonNull() ? "" : jsonObject.get("ironSource_app_key").getAsString();

        if(!jsonObject.get("pinLockStatus").isJsonNull()) {
            if(jsonObject.get("pinLockStatus").getAsInt() == 1) {
                pinLockStatus = true;
            } else {
                pinLockStatus = false;
            }
        }
        pinLockPin = jsonObject.get("pinLockPin").isJsonNull() ? "" : jsonObject.get("pinLockPin").getAsString();

        FacebookSdk.setApplicationId(facebook_app_id);
        StartAppSDK.init(this, StartApp_App_ID, false);

        AppConfig.bGljZW5zZV9jb2Rl = jsonObject.get("license_code").isJsonNull() ? "" : jsonObject.get("license_code").getAsString();

        if (jsonObject.get("safeMode").getAsInt() == 1) {
            AppConfig.safeMode = true;
        } else {
            AppConfig.safeMode = false;
        }

        AppConfig.safeModeVersions = jsonObject.get("safeModeVersions").isJsonNull() ? "" : jsonObject.get("safeModeVersions").getAsString();

        if(!jsonObject.get("primeryThemeColor").isJsonNull()) {
            AppConfig.primeryThemeColor = jsonObject.get("primeryThemeColor").getAsString().isEmpty() ? "#DF4674" : jsonObject.get("primeryThemeColor").getAsString();
        }

        AppConfig.packageName = jsonObject.get("package_name").isJsonNull() ? "" : jsonObject.get("package_name").getAsString();

        if(tinyDB != null) {
            tinyDB.putInt("splashScreenType", jsonObject.get("splash_screen_type").isJsonNull() ? 0 : jsonObject.get("splash_screen_type").getAsInt());
            tinyDB.putString("splashImageUrl", jsonObject.get("splash_image_url").isJsonNull() ? "" : jsonObject.get("splash_image_url").getAsString());
            tinyDB.putString("splashLottieUrl", jsonObject.get("splash_lottie_url").isJsonNull() ? "" : jsonObject.get("splash_lottie_url").getAsString());
            tinyDB.putString("splashBgColor", jsonObject.get("splash_bg_color").isJsonNull() ? "" : jsonObject.get("splash_bg_color").getAsString());
        }

        AppConfig.messageAnimationUrl= jsonObject.get("message_animation_url").isJsonNull() ? "" : jsonObject.get("message_animation_url").getAsString();

        loadRemoteConfig();
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
            helperUtils = new HelperUtils(Splash.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Splash.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        if(updateType == 2) {
            if(googleplayAppUpdateType == 0) {
                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnSuccessListener(appUpdateInfo -> {
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                Snackbar snackbar =
                                        Snackbar.make(
                                                findViewById(R.id.splash),
                                                "An update has just been downloaded.",
                                                Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                snackbar.setActionTextColor(
                                        ContextCompat.getColor(context, R.color.white));
                                snackbar.show();
                            }
                        });
            } else if(googleplayAppUpdateType == 1) {
                appUpdateManager
                        .getAppUpdateInfo()
                        .addOnSuccessListener(
                                appUpdateInfo -> {
                                    if (appUpdateInfo.updateAvailability()
                                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                        try {
                                            appUpdateManager.startUpdateFlowForResult(
                                                    appUpdateInfo,
                                                    AppUpdateType.IMMEDIATE,
                                                    this,
                                                    15);
                                        } catch (IntentSender.SendIntentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
            }

        }
    }

    private void serverError() {
        SpinKitView spin_kit = findViewById(R.id.spin_kit);
        spin_kit.setVisibility(View.INVISIBLE);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.server_not_responding_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        Button Dialog_Retry = dialog.findViewById(R.id.Dialog_Retry);
        Dialog_Retry.setOnClickListener(v1 -> {
            dialog.dismiss();
            Intent intent = new Intent(this, Splash.class);
            this.startActivity(intent);
            this.finishAffinity();
        });

        Button dialogClose = dialog.findViewById(R.id.Dialog_Close);
        dialogClose.setOnClickListener(v1 -> {
            dialog.dismiss();
            getActivity().finish();
            System.exit(0);
        });

        dialog.show();
    }
}