package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.SubscriptionPlanListAdepter;
import com.dooo.android.list.SubscriptionPlanList;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SubscriptionList extends AppCompatActivity {
    Context context = this;

    int userId;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(SubscriptionList.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(SubscriptionList.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Home_TitleBar_BG));

        loadData();

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

        loadSubscriptionPlans();

        LinearLayout coupanItem = findViewById(R.id.Coupan_Item);
        coupanItem.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(SubscriptionList.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.cupan_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);

            ImageView coupanDialogClose = (ImageView) dialog.findViewById(R.id.Coupan_Dialog_Close);
            coupanDialogClose.setOnClickListener(v -> dialog.dismiss());

            Button redeemCouponBtn = (Button) dialog.findViewById(R.id.Redeem_Coupon_btn);
            redeemCouponBtn.setBackgroundColor(Color.parseColor(AppConfig.primeryThemeColor));
            redeemCouponBtn.setOnClickListener(v -> {
                EditText couponEditText = (EditText) dialog.findViewById(R.id.Coupon_editText);
                String coupon = couponEditText.getText().toString();
                String originalInput = userId + ":" + coupon;
                String encoded = Utils.toBase64(originalInput);


                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url +"redeemCoupon", response -> {
                    if(response == null && response.equals("")) {
                        Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                    } else {
                        if(response.equals("Coupan Expired")) {
                            Toasty.error(context, "Coupan Expired!", Toast.LENGTH_SHORT, true).show();
                        } else if(response.equals("invalid Coupan")) {
                            Toasty.error(context, "Invalid Coupon!", Toast.LENGTH_SHORT, true).show();
                        } else if(response.equals("Coupan Used")) {
                            Toasty.warning(context, "Coupon Already Used!", Toast.LENGTH_SHORT, true).show();
                        } else if(response.equals("User Already Have Subscription")) {
                            Toasty.warning(context, "User Already Have Subscription!", Toast.LENGTH_SHORT, true).show();
                        } else if(response.equals("Coupan Successfully Redeemed")) {
                            Toasty.success(context, "Coupan Redeemed Successfully!", Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(SubscriptionList.this, Splash.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show();
                        }
                    }

                }, error -> Toasty.error(context, "Something Went Wrong!", Toast.LENGTH_SHORT, true).show()) {
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put("couponCode", coupon);
                        params.put("C_User_ID", String.valueOf(userId));
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
            });

            dialog.show();
        });

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor));
    }

    void setColorTheme(int color) {
        TextView firstLetterOfTitle = findViewById(R.id.firstLetterOfTitle);
        firstLetterOfTitle.setTextColor(color);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.comment_tag_bg);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);

        LinearLayout coupanItem = findViewById(R.id.Coupan_Item);
        coupanItem.setBackground(wrappedDrawable);
    }

    void loadSubscriptionPlans() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getSubscriptionPlans", response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SubscriptionPlanList> subscriptionPlanList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();
                    int time = rootObject.get("time").getAsInt();
                    int amount = rootObject.get("amount").getAsInt();
                    int currency = rootObject.get("currency").getAsInt();
                    String background = rootObject.get("background").getAsString();
                    int status = rootObject.get("status").getAsInt();

                    if (status == 1) {
                        subscriptionPlanList.add(new SubscriptionPlanList(id, name, time, amount, currency, background, status));
                    }


                    RecyclerView subscriptionPlanListRecyclerView = findViewById(R.id.Subscription_Plan_List_RecyclerView);
                    SubscriptionPlanListAdepter myadepter = new SubscriptionPlanListAdepter(context, subscriptionPlanList);
                    subscriptionPlanListRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
                    subscriptionPlanListRecyclerView.setAdapter(myadepter);
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

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String userData = sharedPreferences.getString("UserData", null);
        JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);
        userId = jsonObject.get("ID").getAsInt();
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
            helperUtils = new HelperUtils(SubscriptionList.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(SubscriptionList.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}