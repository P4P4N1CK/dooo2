package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dooo.android.adepter.UpdateListAdepter;
import com.dooo.android.list.UpdateList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.Utils;
import com.google.common.io.Files;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import es.dmoral.toasty.Toasty;

public class InAppUpdate extends AppCompatActivity {

    String updateTitle = null;
    String apkFileUrl = null;
    String whatsNewOnLatestApk = null;

    private Fetch fetch;
    FetchListener fetchListener;

    ProgressBar progressBar;

    TextView eta;
    TextView downloadProgress;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in__app__update);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(InAppUpdate.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(InAppUpdate.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        Intent intent = getIntent();
        updateTitle = intent.getExtras().getString("Update_Title");
        whatsNewOnLatestApk = intent.getExtras().getString("Whats_new_on_latest_APK");
        apkFileUrl = intent.getExtras().getString("APK_File_URL");

        TextView updateTitleText = findViewById(R.id.Update_Title);
        updateTitleText.setText(updateTitle);

        List<UpdateList> updateList = new ArrayList<>();
        String[] values = whatsNewOnLatestApk.split(",");
        for (int i = 0; i < values.length; i++) {
            updateList.add(new UpdateList(values[i].trim()));
        }

        RecyclerView updateListRecycleview = findViewById(R.id.Whats_New_Recycleview);
        UpdateListAdepter myadepter = new UpdateListAdepter(this, updateList);
        updateListRecycleview.setLayoutManager(new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false));
        updateListRecycleview.setAdapter(myadepter);

        File dDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dFile = new File(dDirectory, "/" + InAppUpdate.this.getResources().getString(R.string.app_name) +
                "/" + "Update");
        Utils.deleteRecursive(dFile);


        Button update = findViewById(R.id.UPDATE);

        progressBar = findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            progressBar.setMin(1);
        }
        progressBar.setMax(100);
        progressBar.setIndeterminate(true);

        Button cancelButton= findViewById(R.id.Cancel);
        cancelButton.setOnClickListener(view -> {
            update.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            eta.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.GONE);
            fetch.removeListener(fetchListener);
            fetch.close();
        });

        eta = findViewById(R.id.ETA);
        downloadProgress = findViewById(R.id.Download_Progress);


        update.setOnClickListener(view -> {
            update.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            eta.setVisibility(View.VISIBLE);
            downloadProgress.setVisibility(View.VISIBLE);


            String url = apkFileUrl;
            Uri uri = Uri.parse(url);

            FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(InAppUpdate.this)
                    .setDownloadConcurrentLimit(3)
                    .build();

            fetch = Fetch.Impl.getInstance(fetchConfiguration);

            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File dir = new File(directory, "/" + InAppUpdate.this.getResources().getString(R.string.app_name) +
                    "/" + "Update");
            if(dir.exists()) {
                String deleteCmd = "rm -r " + dir.getPath();
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec(deleteCmd);
                } catch (IOException e) { }
            }


            File filew = new File(directory, "/" + InAppUpdate.this.getResources().getString(R.string.app_name) +
                    "/" + "Update" +
                    "/" + HelperUtils.getApplicationName(context) + ".apk");
            String file = String.valueOf(filew);

            final Request request = new Request(url, file);
            request.setPriority(Priority.HIGH);
            request.setNetworkType(NetworkType.ALL);

            fetch.enqueue(request, updatedRequest -> {
                //Request was successfully enqueued for download.
            }, error -> {
                //An error occurred enqueuing the request.
            });

            fetchListener = new FetchListener() {

                @Override
                public void onWaitingNetwork(@NotNull Download download) {
                    progressBar.setIndeterminate(true);
                }

                @Override
                public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
                    // Do nothing
                }

                @Override
                public void onResumed(@NotNull Download download) {
                    // Do nothing
                }

                @Override
                public void onRemoved(@NotNull Download download) {
                    // Do nothing
                }

                @Override
                public void onQueued(@NotNull Download download, boolean b) {
                    // Do nothing
                }

                @Override
                public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                    progressBar.setIndeterminate(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        progressBar.setProgress(download.getProgress(), true);
                    } else {
                        progressBar.setProgress(download.getProgress());
                    }

                    downloadProgress.setText(download.getProgress() + "%");

                    if((int) etaInMilliSeconds < 60000) {
                        eta.setText((int) etaInMilliSeconds/1000 + "Sec Remaining");
                    } else {
                        eta.setText((int) etaInMilliSeconds/60000 + "Min Remaining");
                    }
                }

                @Override
                public void onPaused(@NotNull Download download) {
                    // Do nothing
                }

                @Override
                public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
                    Toasty.error(InAppUpdate.this, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                }

                @Override
                public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {
                    // Do nothing
                }

                @Override
                public void onDeleted(@NotNull Download download) {
                    // Do nothing
                }

                @Override
                public void onCompleted(@NotNull Download download) {
                    update.setText("INSTALL UPDATE");
                    update.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    eta.setVisibility(View.GONE);
                    downloadProgress.setVisibility(View.GONE);

                    Log.d("test", download.getFileUri().toString()+" , "+uri);

                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                    File file = new File(directory, "/" + InAppUpdate.this.getResources().getString(R.string.app_name) +
                            "/" + "Update" +
                            "/" + HelperUtils.getApplicationName(context) + ".apk"); // assume refers to "sdcard/myapp_folder/myapp.apk"


                    Uri fileUri = Uri.fromFile(file); //for Build.VERSION.SDK_INT <= 24

                    if (Build.VERSION.SDK_INT >= 24) {

                        fileUri = FileProvider.getUriForFile(InAppUpdate.this, BuildConfig.APPLICATION_ID + ".provider", file);
                    }
                    Intent intent2 = new Intent(Intent.ACTION_VIEW, fileUri);
                    intent2.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                    intent2.setDataAndType(fileUri, "application/vnd.android.package-archive");
                    intent2.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
                    startActivity(intent2);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

                @Override
                public void onCancelled(@NotNull Download download) {
                    // Do nothing
                }

                @Override
                public void onAdded(@NotNull Download download) {
                    // Do nothing
                }
            };
            fetch.addListener(fetchListener);
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(fetch != null) {
            fetch.removeListener(fetchListener);
            fetch.close();
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        if(fetch != null) {
            fetch.removeListener(fetchListener);
            fetch.close();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(InAppUpdate.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(InAppUpdate.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}