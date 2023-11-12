package com.dooo.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.adepter.ImageSliderAdepter;
import com.dooo.android.list.ImageSliderItem;
import com.dooo.android.list.MovieList;
import com.dooo.android.utils.HelperUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jetradarmobile.snowfall.SnowfallView;

import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback;
import org.imaginativeworld.oopsnointernet.dialogs.signal.DialogPropertiesSignal;
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class Subscription extends AppCompatActivity {
    int userID;

    String UserData;

    CardView Upgrade_to_premium;

    private boolean vpnStatus;
    private HelperUtils helperUtils;

    DataTable dataTable;
    DataTableHeader header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Subscription.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Subscription.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.Home_TitleBar_BG));

        LoadData();

        Upgrade_to_premium = findViewById(R.id.Upgrade_to_premium);
        Upgrade_to_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Subscription.this, SubscriptionList.class);
                startActivity(intent);
            }
        });

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder builder = new NoInternetDialogSignal.Builder(
                this,
                getLifecycle()
        );
        DialogPropertiesSignal properties = builder.getDialogProperties();
        properties.setConnectionCallback(new ConnectionCallback() { // Optional
            @Override
            public void hasActiveConnection(boolean hasActiveConnection) {
                // ...
            }
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

        dataTable = findViewById(R.id.data_table);
        header = new DataTableHeader.Builder()
                .item("Name", 1)
                .item("Ammount", 1)
                .item("Time (Days)", 1)
                .item("Subscription Start", 1)
                .item("Subscription End", 1)
                 .build();


        loadSubscriptionLog(userID);

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor));
    }

    void setColorTheme(int color) {
        TextView titleText = findViewById(R.id.titleText);
        titleText.setTextColor(color);

        LinearLayout upgradeToPremiumLinearLayout = findViewById(R.id.upgradeToPremiumLinearLayout);
        upgradeToPremiumLinearLayout.setBackgroundColor(color);
    }

    void loadSubscriptionLog(int userID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getSubscriptionLog/"+userID, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                ArrayList<DataTableRow> rows = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();

                    String name = rootObject.get("name").getAsString();
                    String amount = rootObject.get("amount").getAsString();
                    String time = rootObject.get("time").getAsString();
                    String subscriptionStart = rootObject.get("subscription_start").getAsString();
                    String subscriptionExp = rootObject.get("subscription_exp").getAsString();

                    DataTableRow row = new DataTableRow.Builder()
                            .value(name)
                            .value(amount)
                            .value(time)
                            .value(subscriptionStart)
                            .value(subscriptionExp)
                            .build();
                    rows.add(row);
                }
                dataTable.setHeader(header);
                dataTable.setRows(rows);
                dataTable.inflate(this);


            } else {

            }
        }, error -> {
            // Do nothing because There is No Error if error It will return 0
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

    private void LoadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        UserData = sharedPreferences.getString("UserData", null);

        JsonObject jsonObject = new Gson().fromJson(UserData, JsonObject.class);
        TextView User_Name = findViewById(R.id.User_Name);
        TextView User_Email = findViewById(R.id.User_Email);
        TextView Active_Plan = findViewById(R.id.Active_Plan);
        TextView Expire_Date = findViewById(R.id.Expire_Date);

        if(UserData != null) {
            userID = jsonObject.get("ID").getAsInt();

            String Name = jsonObject.get("Name").getAsString();
            User_Name.setText(Name);

            String Email = jsonObject.get("Email").getAsString();
            User_Email.setText(Email);

            String active_subscription = jsonObject.get("active_subscription").getAsString();
            Active_Plan.setText(active_subscription);

            String subscription_exp = jsonObject.get("subscription_exp").getAsString();
            Expire_Date.setText(subscription_exp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!AppConfig.allowVPN) {
            //check vpn connection
            helperUtils = new HelperUtils(Subscription.this);
            vpnStatus = helperUtils.isVpnConnectionAvailable();
            if (vpnStatus) {
                helperUtils.showWarningDialog(Subscription.this, "VPN!", "You are Not Allowed To Use VPN Here!", R.raw.network_activity_icon);
            }
        }
    }
}