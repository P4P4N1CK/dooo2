package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SubscriptionDetails extends AppCompatActivity {
    private boolean vpnStatus;
    private HelperUtils helperUtils;
    LinearLayout payNow;
    View payment_dialog;


    int id;

    String name;
    int subscriptionType, amount, time;
    String strCurrency;

    int userID;
    String userName, email;

    int payment_gateway_type, razorpay_status, paypal_status, flutterwave_status;
    String razorpay_key_id, razorpay_key_secret, paypal_clint_id, flutterwave_public_key, flutterwave_secret_key, flutterwave_encryption_key;

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

        setContentView(R.layout.activity_subscription_details);

        loadData();
        loadConfig();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(SubscriptionDetails.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(SubscriptionDetails.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
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

        Intent intent = getIntent();
        id = intent.getExtras().getInt("ID");
        loadSubscriptionDetails(id);

        payment_dialog = findViewById(R.id.payment_dialog);

        payNow = findViewById(R.id.Pay_Now);
        payNow.setOnClickListener(view -> {
            if(payment_gateway_type == 0) {
                if (payment_dialog.getVisibility() != View.VISIBLE) {
                    paymentDialog(true);
                } else {
                    paymentDialog(false);
                }
            } else {
                Intent intent1 = new Intent(SubscriptionDetails.this, CustomPaymentActivity.class);
                intent1.putExtra("ID", id);
                startActivity(intent1);
            }
        });

        LinearLayout payment_dialog_Extra_space = findViewById(R.id.payment_dialog_Extra_space);
        payment_dialog_Extra_space.setOnClickListener(v -> {
            paymentDialog(false);
        });



        CardView razorPay = findViewById(R.id.razorPay);
        switch (razorpay_status) {
            case 0:
                razorPay.setVisibility(View.GONE);
                break;
            case 1:
                razorPay.setVisibility(View.VISIBLE);
                break;
            default:
                razorPay.setVisibility(View.GONE);
        }
        razorPay.setOnClickListener(view->{
            Intent intent00 = new Intent(SubscriptionDetails.this, Razorpay_Payment_gatway.class);
            intent00.putExtra("id", id);
            startActivity(intent00);
        });

        CardView paypalPayment = findViewById(R.id.paypalPayment);
        switch (paypal_status) {
            case 0:
                paypalPayment.setVisibility(View.GONE);
                break;
            case 1:
                paypalPayment.setVisibility(View.VISIBLE);
                break;
            default:
                paypalPayment.setVisibility(View.GONE);
        }
        paypalPayment.setOnClickListener(view->{
            Intent intent00 = new Intent(SubscriptionDetails.this, PaypalPaymentGatway.class);
            intent00.putExtra("id", id);
            startActivity(intent00);
        });

        CardView upi = findViewById(R.id.upi);
        upi.setOnClickListener(view -> {
            Intent intent00 = new Intent(SubscriptionDetails.this, UPI.class);
            intent00.putExtra("id", id);
            startActivity(intent00);
        });

        CardView paytm = findViewById(R.id.paytm);
        paytm.setOnClickListener(view->{
            Intent intent00 = new Intent(SubscriptionDetails.this, PaytmPaymentGatway.class);
            intent00.putExtra("id", id);
            startActivity(intent00);
        });

        CardView flutterwave = findViewById(R.id.flutterwave);
        switch (flutterwave_status) {
            case 0:
                flutterwave.setVisibility(View.GONE);
                break;
            case 1:
                flutterwave.setVisibility(View.VISIBLE);
                break;
            default:
                flutterwave.setVisibility(View.GONE);
        }
        flutterwave.setOnClickListener(view->{
            Intent intent00 = new Intent(SubscriptionDetails.this, FlutterwavePaymentGatway.class);
            intent00.putExtra("id", id);
            startActivity(intent00);
        });

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor));
    }

    void setColorTheme(int color) {
        TextView titleText = findViewById(R.id.titleText);
        titleText.setTextColor(color);

        LinearLayout Pay_Now = findViewById(R.id.Pay_Now);
        Pay_Now.setBackgroundColor(color);
    }

    private void paymentDialog(boolean show) {
        CardView Upgrade_to_premium = findViewById(R.id.Upgrade_to_premium);
        ViewGroup subscription_details = findViewById(R.id.subscription_details);

        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(payment_dialog);

        TransitionManager.beginDelayedTransition(subscription_details, transition);
        Upgrade_to_premium.setVisibility(show ? View.GONE : View.VISIBLE);
        payment_dialog.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    void loadSubscriptionDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getSubscriptionDetails/"+id, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                int id1 = jsonObject.get("id").getAsInt();
                name = jsonObject.get("name").getAsString();
                time = jsonObject.get("time").getAsInt();
                amount = jsonObject.get("amount").getAsInt();
                int currency = jsonObject.get("currency").getAsInt();
                String background = jsonObject.get("background").getAsString();
                subscriptionType = jsonObject.get("subscription_type").getAsInt();
                int status = jsonObject.get("status").getAsInt();
                String symbol = "$";
                switch (currency) {
                    case 0:
                        strCurrency = "INR";
                        symbol = Currency.getInstance("INR").getSymbol();
                        break;
                    case 1:
                        strCurrency = "USD";
                        symbol = Currency.getInstance("USD").getSymbol();
                        break;
                }


                TextView nameTextView = findViewById(R.id.Name_TextView);
                nameTextView.setText(name);

                TextView timeTextView = findViewById(R.id.Time_TextView);
                timeTextView.setText(String.valueOf(time+"Days"));

                TextView amountTextView= findViewById(R.id.Amount_TextView);
                amountTextView.setText(String.valueOf(symbol+amount));

                ImageView subscriptionTtemBg= findViewById(R.id.Subscription_item_bg);
                Glide.with(SubscriptionDetails.this)
                        .load(background)
                        .placeholder(R.drawable.thumbnail_placeholder)
                        .into(subscriptionTtemBg);

                LinearLayout GATAC = findViewById(R.id.GATAC);
                LinearLayout RAA = findViewById(R.id.RAA);
                LinearLayout WPC = findViewById(R.id.WPC);
                LinearLayout DPC = findViewById(R.id.DPC);


                String number = String.valueOf(subscriptionType);
                for(int i = 0; i < number.length(); i++) {
                    int userSubType = Character.digit(number.charAt(i), 10);
                    if(userSubType == 1) {
                        RAA.setVisibility(View.VISIBLE);
                    } else if(userSubType == 2) {
                        GATAC.setVisibility(View.VISIBLE);
                        WPC.setVisibility(View.VISIBLE);
                    } else if(userSubType == 3) {
                        DPC.setVisibility(View.VISIBLE);
                    } else {
                        GATAC.setVisibility(View.GONE);
                        RAA.setVisibility(View.GONE);
                        WPC.setVisibility(View.GONE);
                        DPC.setVisibility(View.GONE);
                    }
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
        if (sharedPreferences.getString("UserData", null) != null) {
            String userData = sharedPreferences.getString("UserData", null);
            JsonObject jsonObject = new Gson().fromJson(userData, JsonObject.class);

            userID = jsonObject.get("ID").getAsInt();
            userName = jsonObject.get("Name").getAsString();
            email = jsonObject.get("Email").getAsString();
        }
    }

    private void loadConfig() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String config = sharedPreferences.getString("Config", null);

        JsonObject jsonObject = new Gson().fromJson(config, JsonObject.class);

        payment_gateway_type = jsonObject.get("payment_gateway_type").getAsInt();



        razorpay_status = jsonObject.get("razorpay_status").getAsInt();
        razorpay_key_id = jsonObject.get("razorpay_key_id").getAsString();
        razorpay_key_secret = jsonObject.get("razorpay_key_secret").getAsString();

        paypal_status = jsonObject.get("paypal_status").getAsInt();
        paypal_clint_id = jsonObject.get("paypal_clint_id").getAsString();

        flutterwave_status = jsonObject.get("flutterwave_status").getAsInt();
        flutterwave_public_key = jsonObject.get("flutterwave_public_key").getAsString();
        flutterwave_secret_key = jsonObject.get("flutterwave_secret_key").getAsString();
        flutterwave_encryption_key = jsonObject.get("flutterwave_encryption_key").getAsString();
    }

    @Override
    public void onBackPressed() {
        if(payment_dialog.getVisibility() == View.VISIBLE) {
            paymentDialog(false);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(SubscriptionDetails.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(SubscriptionDetails.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}