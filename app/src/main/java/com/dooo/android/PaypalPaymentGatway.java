package com.dooo.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.datatransport.runtime.logging.Logging;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.PaymentButtonIntent;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.config.UIConfig;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PaypalPaymentGatway extends AppCompatActivity {

    String name;
    int subscriptionType, amount, time;
    CurrencyCode currencyCode;

    int userID;
    String userName, email;

    int paypal_status;
    String paypal_clint_id = "";

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_paypal_payment_gatway);

        rootView = findViewById(R.id.PaypalPaymentGatway);

        loadConfig();
        loadData();


        Intent intent = getIntent();
        int id = intent.getExtras().getInt("id");
        loadSubscriptionDetails(id);
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

        paypal_status = jsonObject.get("paypal_status").getAsInt();
        paypal_clint_id = jsonObject.get("paypal_clint_id").getAsString();
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
                        currencyCode = CurrencyCode.INR;
                        break;
                    case 1:
                        currencyCode = CurrencyCode.USD;
                        break;
                }

                if(!Objects.equals(paypal_clint_id, "")) {
                    startPayment();
                } else {
                    finish();
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

    private void startPayment() {

        PayPalCheckout.setConfig(new CheckoutConfig(
                getApplication(),
                paypal_clint_id,
                Environment.LIVE,
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                PaymentButtonIntent.CAPTURE,
                new SettingsConfig(false, false),
                new UIConfig(true),
                BuildConfig.APPLICATION_ID + "://paypalpay"
        ));

        PayPalCheckout.registerCallbacks(
                approval -> {
                    approval.getOrderActions().capture(result -> {
                        // Order successfully captured
                        RequestQueue queue = Volley.newRequestQueue(this);
                        StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "dXBncmFkZQ", response -> {
                            if(response.equals("Account Upgraded Succefully")) {

                                Snackbar snackbar = Snackbar.make(rootView, "Account Upgraded Succefully!", Snackbar.LENGTH_SHORT);
                                snackbar.show();

                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(() -> startActivity(new Intent(PaypalPaymentGatway.this, Splash.class)), 2000);
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
                    });
                },
                () -> {
                    // Optional callback for when a buyer cancels the paysheet
                    finish();
                },
                errorInfo -> {
                    finish();
                    // Optional error callback
                }
        );

        PayPalCheckout.startCheckout(
                createOrderActions -> {
                    ArrayList purchaseUnits = new ArrayList<>();
                    purchaseUnits.add(
                            new PurchaseUnit.Builder()
                                    .amount(
                                            new Amount.Builder()
                                                    .currencyCode(currencyCode)
                                                    .value(String.valueOf(amount))
                                                    .build()
                                    )
                                    .build()
                    );

                    OrderRequest order = new OrderRequest(
                            OrderIntent.CAPTURE,
                            new AppContext.Builder()
                                    .userAction(UserAction.PAY_NOW)
                                    .build(),
                            purchaseUnits,
                            ProcessingInstruction.NO_INSTRUCTION
                    );
                    createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                }
        );

    }

}