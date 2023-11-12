package com.dooo.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.AppConfig;
import com.dooo.android.Downloads;
import com.dooo.android.Home;
import com.dooo.android.R;
import com.dooo.android.Splash;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class App extends Application  {
    Notification.Builder builder;
    NotificationManagerCompat nmc;
    static final String FETCH_NAMESPACE = "DownloadList";
    private Fetch fetch;
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(5)
                .setHttpDownloader(new OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
                .setNamespace(FETCH_NAMESPACE)
                .build();

        Fetch.Impl.setDefaultInstanceConfiguration(fetchConfiguration);

        fetch = Fetch.Impl.getDefaultInstance();

        fetch.addListener(fetchListener);


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int userID = getUserID();
                if(userID != 0) {
                    if(getForceSingleDeviceStatus() == 1) {
                        checkDevice(userID);
                    }
                }
            }
        }, 0, 30000);
    }

    private int getUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("UserData", null) != null) {
            String userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
            return jsonObject.get("ID").getAsInt();
        } else {
            return 0;
        }
    }

    private int getForceSingleDeviceStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        if (sharedPreferences.getString("Config", null) != null) {
            String config = sharedPreferences.getString("Config", null);
            JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);
            return jsonObject.get("force_single_device").getAsInt();
        } else {
            return 0;
        }
    }

    private void saveData(String userData) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserData", userData);
        editor.apply();
    }
    private void saveUserSubscriptionDetails(int subscriptionType) {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("subscription_type", String.valueOf(subscriptionType));
        editor.apply();
    }

    public void checkDevice(int userID) {
        RequestQueue queue = Volley.newRequestQueue(App.this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"check_device/"+userID, response -> {
            JsonObject jsonObject1 = new Gson().fromJson(response, JsonObject.class);
            String status = jsonObject1.get("Status").toString();
            status = status.substring(1, status.length() - 1);

            if (status.equals("successful")) {
                saveData(response);

                JsonObject subObj = new Gson().fromJson(response, JsonObject.class);
                int subscriptionType = subObj.get("subscription_type").getAsInt();
                saveUserSubscriptionDetails(subscriptionType);

                String device_id = subObj.get("device_id").getAsString();
                if(!device_id.equals(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID))) {
                    Toast.makeText(App.this, "Session Expired!", Toast.LENGTH_SHORT).show();

                    SharedPreferences settings = App.this.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                    settings.edit().clear().apply();

                    Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        }, error -> {
            Log.d("test", error.getMessage());
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

    private final FetchListener fetchListener = new AbstractFetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {
            showNotification(download.getId(), download);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            if (builder != null) {
                builder.setContentText("Download Queued");
                builder.setOngoing(true);
                builder.setProgress(0, 0, true);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onCompleted(@NotNull Download download) {

            if (builder != null) {
                builder.setContentText("Download Finished");
                builder.setOngoing(false);
                builder.setAutoCancel(true);
                builder.setProgress(0, 0, false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {
            super.onError(download, error, throwable);

            if (builder != null) {
                builder.setContentText("Download Failed");
                builder.setOngoing(false);
                builder.setAutoCancel(true);
                builder.setProgress(0, 0, false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {

            if (builder != null) {
                builder.setProgress(100, download.getProgress(), false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onPaused(@NotNull Download download) {

            if (builder != null) {
                builder.setContentText("Download Paused");
                builder.setOngoing(false);
                builder.setAutoCancel(true);
                builder.setProgress(100, download.getProgress(), false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onResumed(@NotNull Download download) {

            if (builder != null) {
                builder.setProgress(100, download.getProgress(), false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onCancelled(@NotNull Download download) {

            if (builder != null) {
                builder.setContentText("Download Cancelled");
                builder.setOngoing(false);
                builder.setAutoCancel(true);
                builder.setProgress(0, 0, false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onRemoved(@NotNull Download download) {

            if (builder != null) {
                builder.setContentText("Download Removed");
                builder.setOngoing(false);
                builder.setAutoCancel(true);
                builder.setProgress(0, 0, false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }

        @Override
        public void onDeleted(@NotNull Download download) {

            if (builder != null) {
                builder.setContentText("Download Deleted");
                builder.setOngoing(false);
                builder.setAutoCancel(true);
                builder.setProgress(0, 0, false);
                if (ActivityCompat.checkSelfPermission(App.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                nmc.notify(download.getId(), builder.build());
            }
        }
    };

    private void showNotification(int notificationID, Download download) {

        Intent resultIntent = new Intent(this, Downloads.class);
        Intent homeIntent = new Intent(this, Splash.class);;
        PendingIntent resultPendingIntent = TaskStackBuilder.create(this)
                .addParentStack(Downloads.class)
                .addNextIntent(homeIntent)
                .addNextIntent(resultIntent)
                .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channelid1", "001", NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            builder = new Notification.Builder(getApplicationContext(), "channelid1");
            builder.setContentTitle(download.getFileUri().getLastPathSegment().split("\\.")[0]);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            builder.setContentIntent(resultPendingIntent);
            builder.setProgress(100,0,false);
            builder.setWhen(System.currentTimeMillis());
            builder.setPriority(Notification.PRIORITY_LOW);

            nmc = NotificationManagerCompat.from(getApplicationContext());
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            nmc.notify(notificationID, builder.build());

        } else {
            builder = new Notification.Builder(getApplicationContext());
            builder.setContentTitle(download.getFileUri().getLastPathSegment().split("\\.")[0]);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            builder.setContentIntent(resultPendingIntent);
            builder.setProgress(100,0,false);
            builder.setWhen(System.currentTimeMillis());
            builder.setPriority(Notification.PRIORITY_LOW);

            nmc = NotificationManagerCompat.from(getApplicationContext());
            nmc.notify(notificationID, builder.build());
        }
    }

}
