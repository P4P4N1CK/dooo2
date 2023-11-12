package com.dooo.android.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.AppConfig;
import com.dooo.android.LoginSignup;
import com.dooo.android.R;
import com.dooo.android.Splash;
import com.dooo.android.Subscription;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import es.dmoral.toasty.Toasty;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.ColorUtils;

public class HelperUtils {
    private Activity activity;

    public HelperUtils(Activity activity) {
        this.activity = activity;
    }

    public boolean isVpnConnectionAvailable(){
        String iface = "";
        try {
            for (NetworkInterface networkInst : Collections.list(NetworkInterface.getNetworkInterfaces())){
                if (networkInst.isUp())
                    iface = networkInst.getName();
                if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }


        }catch (SocketException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean cr(Activity activity, boolean allowRoot) {
        if(!allowRoot) {
            for(String pathDir : System.getenv("PATH").split(":")){
                if(new File(pathDir, "su").exists()) {
                    return true;
                } else {
                    ApplicationInfo restrictedApp = getRootApp(activity);
                    if (restrictedApp != null){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static ApplicationInfo getRootApp(Activity activity) {
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.thirdparty.superuser") ||
                    packageInfo.packageName.equals("eu.chainfire.supersu") ||
                    packageInfo.packageName.equals("com.noshufou.android.su") ||
                    packageInfo.packageName.equals("com.koushikdutta.superuser") ||
                    packageInfo.packageName.equals("com.zachspong.temprootremovejb") ||
                    packageInfo.packageName.equals("com.ramdroid.appquarantine") ||
                    packageInfo.packageName.equals("com.topjohnwu.magisk") ||
                    packageInfo.packageName.equals("me.weishu.kernelsu")
            ) {
                //restrictPackageName = packageInfo.packageName;
                //restrictPackageName = packageInfo.loadLabel(activity.getPackageManager()).toString();
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    public ApplicationInfo getRestrictApp() {
        ApplicationInfo restrictPackageInfo = null;
        final PackageManager pm = activity.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals("com.guoshi.httpcanary") ||
                    packageInfo.packageName.equals("app.greyshirts.sslcapture") ||
                    packageInfo.packageName.equals("com.guoshi.httpcanary.premium") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture.pro") ||
                    packageInfo.packageName.equals("com.minhui.networkcapture") ||
                    packageInfo.packageName.equals("com.egorovandreyrm.pcapremote") ||
                    packageInfo.packageName.equals("com.packagesniffer.frtparlak") ||
                    packageInfo.packageName.equals("jp.co.taosoftware.android.packetcapture") ||
                    packageInfo.packageName.equals("com.emanuelef.remote_capture") ||
                    packageInfo.packageName.equals("com.minhui.wifianalyzer") ||
                    packageInfo.packageName.equals("com.evbadroid.proxymon") ||
                    packageInfo.packageName.equals("com.evbadroid.wicapdemo") ||
                    packageInfo.packageName.equals("com.evbadroid.wicap") ||
                    packageInfo.packageName.equals("com.luckypatchers.luckypatcherinstaller") ||
                    packageInfo.packageName.equals("ru.UbLBBRLf.jSziIaUjL")
            ) {
                //restrictPackageName = packageInfo.packageName;
                //restrictPackageName = packageInfo.loadLabel(activity.getPackageManager()).toString();
                restrictPackageInfo = packageInfo;
            }
        }

        return restrictPackageInfo;
    }

    public boolean isForeground( String myPackage){
        ActivityManager manager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > runningTaskInfo = manager.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        Log.e("test", "Background Apps: " + componentInfo.getPackageName());
        return componentInfo != null && componentInfo.getPackageName().equals(myPackage);
    }

    public boolean isAppRunning(Context context, String packageName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos != null){
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos){
                if (processInfo.processName.equals(packageName)){
                    return true;
                }
            }
        }
        return false;
    }

    public static void showWarningDialog(Activity context, String title, String message, int Animation) {
        MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(Animation)
                .setPositiveButton("Exit", R.drawable.ic_baseline_exit, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        System.exit(0);
                        context.finish();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();
    }

    public void showMsgDialog(Activity context, String title, String message, String animation) {
        /*MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(Animation)
                .setPositiveButton("Dismiss", R.drawable.close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();*/
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.initial_msg_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.lottieAnimationView);
        if(!Objects.equals(animation, "")) {
            lottieAnimationView.setAnimationFromUrl(animation);
        }

        TextView msgTitle = dialog.findViewById(R.id.msgTitle);
        msgTitle.setText(title);

        TextView msgBody = dialog.findViewById(R.id.msgBody);
        msgBody.setText(message);

        Button dialogClose = dialog.findViewById(R.id.Dialog_Close);
        dialogClose.setOnClickListener(v1 -> dialog.dismiss());

        dialog.show();
    }

    public void Buy_Premium_Dialog(Activity context, String title, String message, int Animation) {
        MaterialDialog mDialog = new MaterialDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setAnimation(Animation)
                .setPositiveButton("Upgrade", new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
                        if (sharedPreferences.getString("UserData", null) != null) {
                            dialogInterface.dismiss();
                            Intent subscriptionActivity = new Intent(context, Subscription.class);
                            context.startActivity(subscriptionActivity);
                        } else {
                            dialogInterface.dismiss();
                            Intent loginSingupActivity = new Intent(context, LoginSignup.class);
                            context.startActivity(loginSingupActivity);
                        }

                    }
                })
                .setNegativeButton("Cancel", R.drawable.close, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show dialog
        mDialog.show();
    }


    public boolean isInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (Exception ex)
        {
            return false;
        }
    }

    public static String getYearFromDate(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = null;
        try {
            parsedDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy");
        return df.format(parsedDate);
    }

    public static void setWatchLog(Context context, String user_id, int content_id, int content_type, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "addwatchlog", response -> {
            try
            {
                Integer.parseInt(response);
                Log.d("test", "Watch Log Added!");
            } catch (NumberFormatException ex)
            {
                Log.d("test", "Watch Log Not Added!");
            }

        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("content_id", String.valueOf(content_id));
                params.put("content_type", String.valueOf(content_type));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", apiKey);
                return params;
            }
        };
        queue.add(sr);
    }

    public static void setViewLog(Context context, String user_id, int content_id, int content_type, String apiKey) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "addviewlog", response -> {
            try
            {
                Integer.parseInt(response);
                Log.d("test", "View Log Added!");
            } catch (NumberFormatException ex)
            {
                Log.d("test", "View Log Not Added!");
            }

        }, error -> {
            // Do nothing because There is No Error if error It will return 0
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("content_id", String.valueOf(content_id));
                params.put("content_type", String.valueOf(content_type));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-api-key", apiKey);
                return params;
            }
        };
        queue.add(sr);
    }


    public static String[] storge_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    public static boolean checkStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions((Activity) context,
                    permissions(),
                    1);
            return true;
        } else {
            Log.d("test", "Permission is granted");
            return true;
        }
    }

    public String getStringBetweenTwoChars(String input, String startChar, String endChar) {
        try {
            int start = input.indexOf(startChar);
            if (start != -1) {
                int end = input.indexOf(endChar, start + startChar.length());
                if (end != -1) {
                    return input.substring(start + startChar.length(), end);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input; // return null; || return "" ;
    }

    public static void resetPassword(Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.forgot_password);

        TextView forgetPasswordEmailEditText = bottomSheetDialog.findViewById(R.id.Forget_Password_Email_EditText);
        View sendOtp = bottomSheetDialog.findViewById(R.id.Send_OTP);
        //---------Send OTP----------//
        sendOtp.setOnClickListener(view1 -> {
            if(forgetPasswordEmailEditText.getText().toString().matches("")) {
                Toasty.warning(context, "Please Enter Your Email Address Correctly.", Toast.LENGTH_SHORT, true).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"passwordResetMail", response -> {
                    switch (response) {
                        case "Email Not Registered":
                            Toasty.warning(context, "Email Not Registered", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "Message has been sent":
                            Toasty.success(context, "Code Sended To the Mail Address!", Toast.LENGTH_SHORT, true).show();
                            bottomSheetDialog.dismiss();
                            checkPassword(context);
                            break;
                        case "":
                        case "Something Went Wrong!":
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
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
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put("mail",forgetPasswordEmailEditText.getText().toString().trim());
                        return params;
                    }
                };
                sr.setRetryPolicy(new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(sr);
            }
        });

        bottomSheetDialog.show();
    }

    public static void checkPassword(Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.reset_password_otp);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText editText1 = bottomSheetDialog.findViewById(R.id.editText1);
        EditText editText2 = bottomSheetDialog.findViewById(R.id.editText2);
        EditText editText3 = bottomSheetDialog.findViewById(R.id.editText3);
        EditText editText4 = bottomSheetDialog.findViewById(R.id.editText4);
        editText1.addTextChangedListener(new GenericTextWatcher(editText1, editText2));
        editText2.addTextChangedListener(new GenericTextWatcher(editText2, editText3));
        editText3.addTextChangedListener(new GenericTextWatcher(editText3, editText4));
        editText4.addTextChangedListener(new GenericTextWatcher(editText4, null));

        editText2.setOnKeyListener(new GenericKeyEvent(editText2, editText1));
        editText3.setOnKeyListener(new GenericKeyEvent(editText3, editText2));
        editText4.setOnKeyListener(new GenericKeyEvent(editText4, editText3));

        View verifyOtp = bottomSheetDialog.findViewById(R.id.verifyOtp);
        verifyOtp.setOnClickListener(view->{
            if(editText1.getText().toString().matches("") && editText2.getText().toString().matches("")
             && editText3.getText().toString().matches("") && editText4.getText().toString().matches("")) {
                Toasty.warning(context, "Please Enter a valid OTP.", Toast.LENGTH_SHORT, true).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "passwordResetCheckCode", response -> {
                    switch (response) {
                        case "Expired":
                            Toasty.warning(context, "Code already Expired!", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "valid Code":
                            bottomSheetDialog.dismiss();
                            changePassword(context, editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString());
                            break;
                        case "Used Code":
                            Toasty.warning(context, "Code already Used!", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "Invalid Request":
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                    }

                }, error -> {
                    //Do Nothing
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("x-api-key", AppConfig.apiKey);
                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("code", editText1.getText().toString() + editText2.getText().toString() + editText3.getText().toString() + editText4.getText().toString());
                        return params;
                    }
                };
                queue.add(sr);
            }
        });

        bottomSheetDialog.show();
    }

    public static void changePassword(Context context, String code) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.reset_password);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText newPassEditText = bottomSheetDialog.findViewById(R.id.newPassEditText);
        EditText newConfirmPassEditText = bottomSheetDialog.findViewById(R.id.newConfirmPassEditText);

        View resetPass = bottomSheetDialog.findViewById(R.id.resetPass);
        //---------Send OTP----------//
        resetPass.setOnClickListener(view1 -> {
            if(newPassEditText.getText().toString().matches("") && newConfirmPassEditText.getText().toString().matches("")
             && newPassEditText.getText().toString().equals(newConfirmPassEditText.getText().toString())) {
                Toasty.warning(context, "Please Enter a Valid Password!", Toast.LENGTH_SHORT, true).show();
            } else {
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"passwordResetPassword", response -> {
                    switch (response) {
                        case "Password Updated successfully":
                            Toasty.success(context, "Password Updated successfully!", Toast.LENGTH_SHORT, true).show();
                            bottomSheetDialog.dismiss();
                            SharedPreferences settings = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
                            settings.edit().clear().apply();

                            Intent splashActivity = new Intent(context, Splash.class);
                            splashActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(splashActivity);
                            break;
                        case "":
                        case "Invalid Request":
                        case "Something Went Wrong!":
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            Toasty.success(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                            break;
                    }

                }, error -> {
                    //Do Nothing
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
                        params.put("code",code);
                        params.put("pass",Utils.getMd5(newPassEditText.getText().toString()));
                        return params;
                    }
                };
                queue.add(sr);
            }
        });

        bottomSheetDialog.show();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isColorDark(int color){
        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static Boolean isFirstOpen(Context context){
        TinyDB tinyDB = new TinyDB(context);
        Boolean isFirstRun = tinyDB.getBoolean("isFirstRun");
        if(!isFirstRun) {
            tinyDB.putBoolean("isFirstRun", true);
            return true;
        } else {
            return false;
        }
    }

    public static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
