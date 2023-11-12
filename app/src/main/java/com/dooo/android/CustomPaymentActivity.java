package com.dooo.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.Utils;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class CustomPaymentActivity extends AppCompatActivity {
    Context context;
    MaterialCardView addImageCard;
    int SELECT_PICTURE = 200;
    int userID;
    Uri selectedImageUri;
    ImageView imageView, copyPaymentDetails;
    int id;
    TextView payableAmountValue, requredDetails;
    MaterialSpinner payment_options;
    JsonArray customPaymentTypeOptions;
    CardView paymentSubmit;
    String subscriptionName;
    int subscriptionTime;
    int subscriptionAmount;
    int subscriptionCurrency;
    int subscriptionType;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_payment);
        context = this;
        bindViews();
        Intent intent = getIntent();
        id = intent.getExtras().getInt("ID");
        loadSubscriptionDetails(id);
        custom_payment_type();
        loadUser();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);

        payment_options.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            requredDetails.setText(customPaymentTypeOptions.get(position).getAsJsonObject().get("payment_details").toString().substring(1, customPaymentTypeOptions.get(position).getAsJsonObject().get("payment_details").toString().length() - 1));
        });

        copyPaymentDetails.setOnClickListener(view->{
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Payment Details", requredDetails.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Payment Details Copied Successfully!", Toast.LENGTH_SHORT).show();
        });

        addImageCard.setOnClickListener(view->{
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
        });

        paymentSubmit.setOnClickListener(view->{
            progressDialog.show();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bitmap lastBitmap = null;
            lastBitmap = bitmap;
            String image = HelperUtils.getStringImage(lastBitmap);

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.POST, AppConfig.url + "/custom_payment_request", response -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Payment request created successfully.", Toast.LENGTH_SHORT).show();
            }, error -> {
                progressDialog.dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", String.valueOf(userID));
                    params.put("payment_type", payment_options.getItems().get(payment_options.getSelectedIndex()).toString());
                    params.put("payment_details", customPaymentTypeOptions.get(payment_options.getSelectedIndex()).getAsJsonObject().get("payment_details").toString().substring(1, customPaymentTypeOptions.get(payment_options.getSelectedIndex()).getAsJsonObject().get("payment_details").toString().length() - 1));
                    params.put("subscription_name", subscriptionName);
                    params.put("subscription_type", String.valueOf(subscriptionType));
                    params.put("subscription_time", String.valueOf(subscriptionTime));
                    params.put("subscription_amount", String.valueOf(subscriptionAmount));
                    params.put("subscription_currency", String.valueOf(subscriptionCurrency));
                    params.put("uploaded_image", image);
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
    }

    private void bindViews() {
        addImageCard = findViewById(R.id.addImageCard);
        imageView = findViewById(R.id.imageView);
        payableAmountValue = findViewById(R.id.payableAmountValue);
        payment_options = findViewById(R.id.payment_options);
        requredDetails = findViewById(R.id.requredDetails);
        copyPaymentDetails = findViewById(R.id.copyPaymentDetails);
        paymentSubmit = findViewById(R.id.paymentSubmit);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                }
            }
        }
    }

    void custom_payment_type() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"custom_payment_type", response -> {
            if(!response.equals("No Data Avaliable")) {
                //JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                //String name = jsonObject.get("name").getAsString();
                //int time = jsonObject.get("time").getAsInt();

                List<String> customPaymentTypeArrey = new ArrayList<>();
                customPaymentTypeOptions = new Gson().fromJson(response, JsonArray.class);
                for (JsonElement r : customPaymentTypeOptions) {
                    JsonObject rootObject = r.getAsJsonObject();
                    customPaymentTypeArrey.add(rootObject.get("type").toString().substring(1, rootObject.get("type").toString().length() - 1));
                }

                payment_options.setItems(customPaymentTypeArrey);

                if(customPaymentTypeArrey.get(payment_options.getSelectedIndex()).equals(customPaymentTypeOptions.get(payment_options.getSelectedIndex()).getAsJsonObject().get("type").getAsString())) {
                    requredDetails.setText(customPaymentTypeOptions.get(payment_options.getSelectedIndex()).getAsJsonObject().get("payment_details").toString().substring(1, customPaymentTypeOptions.get(payment_options.getSelectedIndex()).getAsJsonObject().get("payment_details").toString().length() - 1));
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

    void loadSubscriptionDetails(int id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"getSubscriptionDetails/"+id, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                subscriptionName = jsonObject.get("name").getAsString();
                subscriptionTime = jsonObject.get("time").getAsInt();
                subscriptionAmount = jsonObject.get("amount").getAsInt();
                subscriptionCurrency = jsonObject.get("currency").getAsInt();
                subscriptionType = jsonObject.get("subscription_type").getAsInt();

                payableAmountValue.setText(String.valueOf(subscriptionAmount));
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

    private void loadUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String UserData = sharedPreferences.getString("UserData", null);

        JsonObject jsonObject = new Gson().fromJson(UserData, JsonObject.class);

        if(UserData != null) {
            userID = jsonObject.get("ID").getAsInt();
        } else {
            finish();
        }
    }

}