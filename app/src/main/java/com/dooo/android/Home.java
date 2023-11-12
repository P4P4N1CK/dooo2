package com.dooo.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.fragment.AllMoviesFragment;
import com.dooo.android.fragment.AllWebseriesFragment;
import com.dooo.android.fragment.HomeFragment;
import com.dooo.android.fragment.MoreFragment;
import com.dooo.android.fragment.SearchFragment;
import com.dooo.android.sharedpreferencesmanager.ConfigManager;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.LoadingDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jetradarmobile.snowfall.SnowfallView;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class Home extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    private boolean vpnStatus;
    private HelperUtils helperUtils;

    LoadingDialog loadingAnimation;

    BottomNavigationView bottomNavigationView;

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

        setContentView(R.layout.activity_home);

        RequestQueue queue0 = Volley.newRequestQueue(this);
        StringRequest sr0 = new StringRequest(Request.Method.POST, AppConfig.url +"registerDevice", response -> {
            //Log.d("test", response);
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("device", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
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

        if(!AppConfig.allowVPN) {
            helperUtils = new HelperUtils(Home.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Home.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        loadingAnimation = new LoadingDialog(this);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        Intent intent = getIntent();
        String notificationData = intent.getExtras().getString("Notification_Data");
        JsonObject notificationDataJsonObj = new Gson().fromJson(notificationData, JsonObject.class);
        if(notificationDataJsonObj != null) {
            String type = notificationDataJsonObj.get("Type").getAsString();
            switch (type) {
                case "External_Browser": {
                    String url = notificationDataJsonObj.get("URL").getAsString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    break;
                }
                case "Web View": {
                    String url = notificationDataJsonObj.get("URL").getAsString();
                    Intent intent1 = new Intent(Home.this, WebView.class);
                    intent1.putExtra("URL", url);
                    startActivity(intent1);
                    break;
                }
                case "Movie": {
                    int movieId = notificationDataJsonObj.get("Movie_id").getAsInt();
                    Intent movieIntent = new Intent(Home.this, MovieDetails.class);
                    movieIntent.putExtra("ID", movieId);
                    startActivity(movieIntent);
                    break;
                }
                case "Web Series": {
                    int webSeriesId = notificationDataJsonObj.get("Web_Series_id").getAsInt();
                    Intent webseriesIntent = new Intent(Home.this, WebSeriesDetails.class);
                    webseriesIntent.putExtra("ID", webSeriesId);
                    startActivity(webseriesIntent);
                    break;
                }
                default:
            }
        }

        try {
            JSONObject configObject = ConfigManager.loadConfig(this);
            int onScreenEffect = configObject.getInt("onscreen_effect");
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
        } catch (Exception e) {
            Log.d("test", e.getMessage());
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

        checkNotificationPermission();

    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                } else {
                }
            });

    private boolean checkNotificationPermission() {
        if(ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
        }else {
            requestPermissionLauncher.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS);

        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, new HomeFragment())
                    .commit();
            return true;
        } else if(item.getItemId() == R.id.search) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, new SearchFragment())
                    .commit();
            return true;
        } else if(item.getItemId() == R.id.movies) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, new AllMoviesFragment())
                    .commit();
            return true;
        } else if(item.getItemId() == R.id.series) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, new AllWebseriesFragment())
                    .commit();
            return true;
        } else if(item.getItemId() == R.id.account) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mainFragment, new MoreFragment())
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Home.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Home.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(bottomNavigationView.getSelectedItemId()==R.id.home) {
            MaterialDialog mDialog = new MaterialDialog.Builder(Home.this)
                    .setTitle("Exit!")
                    .setMessage("Want to Exit?")
                    .setCancelable(true)
                    .setNegativeButton("Cancel", R.drawable.close, (dialogInterface, which) -> dialogInterface.dismiss())
                    .setPositiveButton("Exit", R.drawable.ic_baseline_exit, (dialogInterface, which) -> {
                        finish();
                    })
                    .build();
            mDialog.show();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}