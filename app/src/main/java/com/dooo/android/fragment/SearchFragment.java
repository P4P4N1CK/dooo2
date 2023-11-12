package com.dooo.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dooo.android.AppConfig;
import com.dooo.android.R;
import com.dooo.android.adepter.SearchListAdepter;
import com.dooo.android.list.SearchList;
import com.dooo.android.utils.HelperUtils;
import com.dooo.android.utils.TinyDB;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {
    private Context context;
    private static final int nRESULT_SPEECH = 0147;

    AutoCompleteTextView searchContentEditText;
    ImageView search_mic_icon;
    View bigSearchLottieAnimation;
    RecyclerView searchLayoutRecyclerView;
    SwitchMaterial includePremiumSwitch;
    int onlyPremium = 1;
    TinyDB tinyDB;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        View layoutInflater = inflater.inflate(R.layout.fragment_search, container, false);
        bindViews(layoutInflater);
        tinyDB = new TinyDB(context);

        search_mic_icon.setOnClickListener(view ->{
            Intent mic_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            mic_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
            startActivityForResult(mic_intent, nRESULT_SPEECH);
        });

        searchContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(searchContentEditText.getText()).equals("")) {
                    bigSearchLottieAnimation.setVisibility(View.VISIBLE);
                    searchLayoutRecyclerView.setVisibility(View.GONE);
                } else  {
                    bigSearchLottieAnimation.setVisibility(View.GONE);
                    searchLayoutRecyclerView.setVisibility(View.VISIBLE);

                    searchContent(String.valueOf(searchContentEditText.getText()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        includePremiumSwitch.setChecked(tinyDB.getBoolean("onlyPremium"));
        if(tinyDB.getBoolean("onlyPremium")) {
            onlyPremium = 1;
        } else {
            onlyPremium = 0;
        }
        includePremiumSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tinyDB.putBoolean("onlyPremium", isChecked);
                if(isChecked) {
                    onlyPremium = 1;
                } else {
                    onlyPremium = 0;
                }
            }
        });

        searchContentEditText.setOnItemClickListener((parent, view, position, id) -> {
            bigSearchLottieAnimation.setVisibility(View.GONE);
            searchLayoutRecyclerView.setVisibility(View.VISIBLE);
            searchContent(searchContentEditText.getAdapter().getItem(position).toString());
        });

        setColorTheme(Color.parseColor(AppConfig.primeryThemeColor), layoutInflater);

        return layoutInflater;
    }

    private void bindViews(View layoutInflater) {
        searchContentEditText = layoutInflater.findViewById(R.id.Search_content_editText);
        search_mic_icon = layoutInflater.findViewById(R.id.search_mic_icon);
        bigSearchLottieAnimation = layoutInflater.findViewById(R.id.big_search_Lottie_animation);
        searchLayoutRecyclerView = layoutInflater.findViewById(R.id.Search_Layout_RecyclerView);
        includePremiumSwitch = layoutInflater.findViewById(R.id.includePremiumSwitch);
    }

    void setColorTheme(int color, View layoutInflater) {
        TextView searchText = layoutInflater.findViewById(R.id.searchText);
        searchText.setTextColor(color);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case nRESULT_SPEECH:
                if (null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String textCapturedFromVoice=text.get(0);
                    searchContentEditText.setText(textCapturedFromVoice);
                }
                break;
        }
    }

    void searchContent(String text) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest sr = new StringRequest(Request.Method.GET, AppConfig.url +"searchContent/"+text+"/"+onlyPremium, response -> {
            if(!response.equals("No Data Avaliable")) {
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                List<SearchList> searchList = new ArrayList<>();
                for (JsonElement r : jsonArray) {
                    JsonObject rootObject = r.getAsJsonObject();
                    int id = rootObject.get("id").getAsInt();
                    String name = rootObject.get("name").getAsString();

                    String year = "";
                    if(!rootObject.get("release_date").getAsString().equals("")) {
                        year = HelperUtils.getYearFromDate(rootObject.get("release_date").getAsString());
                    }

                    String poster = rootObject.get("poster").getAsString();
                    int type = rootObject.get("type").getAsInt();
                    int status = rootObject.get("status").getAsInt();
                    int contentType = rootObject.get("content_type").getAsInt();

                    if (status == 1) {
                        searchList.add(new SearchList(id, type, name, year, poster, contentType));
                    }
                }

                SearchListAdepter myadepter = new SearchListAdepter(context, searchList);
                searchLayoutRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                searchLayoutRecyclerView.setAdapter(myadepter);

            } else {
                bigSearchLottieAnimation.setVisibility(View.VISIBLE);
                searchLayoutRecyclerView.setVisibility(View.GONE);
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


}