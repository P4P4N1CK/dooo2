package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Razorpay_Payment_gatway extends AppCompatActivity implements PaymentResultListener {

    String name;
    int subscriptionType, amount, time;
    String strCurrency;

    int userID;
    String userName, email;

    int razorpay_status;
    String razorpay_key_id, razorpay_key_secret;

    View rootView;

    Checkout checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_razorpay_payment_gatway);

        rootView = findViewById(R.id.Razorpay_Payment_gatway);

        loadConfig();
        loadData();


        Intent intent = getIntent();
        int id = intent.getExtras().getInt("id");
        loadSubscriptionDetails(id);

        checkout = new Checkout();
        checkout.setKeyID(razorpay_key_id);
        checkout.setImage(R.mipmap.ic_launcher);
    }

    private String getReceiptString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 40) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public void startPayment(Order order) {
        checkout.setKeyID(razorpay_key_id);
        checkout.setImage(R.mipmap.ic_launcher);

        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();

            options.put("name", getApplicationContext().getString(R.string.app_name));
            options.put("description", name);
            options.put("image", getApplicationContext().getDrawable(R.mipmap.ic_launcher));
            options.put("order_id", new JSONObject(String.valueOf(order)).getString("id"));//from response of step 3.
            options.put("theme.color", "#DF4674");
            options.put("currency", strCurrency);
            options.put("amount", amount*100);//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact", "");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("test", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "dXBncmFkZQ", response -> {
            if(response.equals("Account Upgraded Succefully")) {

                Snackbar snackbar = Snackbar.make(rootView, "Account Upgraded Succefully!", Snackbar.LENGTH_SHORT);
                snackbar.show();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> startActivity(new Intent(Razorpay_Payment_gatway.this, Splash.class)), 2000);
            }
        }, error -> {

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("User_ID", String.valueOf(userID));
                params.put("name", name);
                params.put("subscription_type", String.valueOf(subscriptionType));
                params.put("time", String.valueOf(time));
                params.put("amount", String.valueOf(amount));
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
    }

    @Override
    public void onPaymentError(int code, String response) {
        finish();
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

        razorpay_status = jsonObject.get("razorpay_status").getAsInt();
        razorpay_key_id = jsonObject.get("razorpay_key_id").getAsString();
        razorpay_key_secret = jsonObject.get("razorpay_key_secret").getAsString();


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

                switch (currency) {
                    case 0:
                        strCurrency = "INR";
                        break;
                    case 1:
                        strCurrency = "USD";
                        break;
                }



                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                Checkout.preload(getApplicationContext());

                try {
                    RazorpayClient razorpayClient = new RazorpayClient(razorpay_key_id, razorpay_key_secret);

                    JSONObject orderRequest = new JSONObject();
                    orderRequest.put("amount", amount*100); // amount in the smallest currency unit
                    orderRequest.put("currency", strCurrency);
                    orderRequest.put("receipt", getReceiptString());

                    Order order = razorpayClient.Orders.create(orderRequest);

                    startPayment(order);

                } catch (RazorpayException | JSONException e) {
                    // Handle Exception
                    System.out.println(e.getMessage());
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
}