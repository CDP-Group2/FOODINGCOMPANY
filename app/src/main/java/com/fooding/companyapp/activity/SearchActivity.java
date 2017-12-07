package com.fooding.companyapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;
import com.fooding.companyapp.data.model.Ingredient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.resultList) ListView resultList;
    @BindView(R.id.searchText) EditText searchText;
    @BindView(R.id.addButton) ImageButton addBtn;
    @BindView(R.id.title) TextView title;
    /*@BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.search) ImageButton searchBtn;
    @BindView(R.id.camera) ImageButton cameraBtn;
    @BindView(R.id.recentlyViewed) ImageButton recentlyViewedBtn;*/
    @BindView(R.id.JsonTextview) TextView debuggingView; //debugging purpose

    public ArrayList<String> results;
    public ArrayList<String> resultsID;
    public ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        /*************************************************************************************************************/
        // font setting
        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        SharedPreferences fontSP = app.getMyPref();

        final String pathT = fontSP.getString("titleFont", "none");
        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
        title.setTypeface(font);
        debuggingView.setTypeface(font);

        final String pathK = fontSP.getString("koreanFont", "none");
        Typeface fontK = Typeface.createFromAsset(getAssets(), pathK);
        searchText.setTypeface(fontK);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(fontSP.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.searchActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            // change text color
            title.setTextColor(Color.parseColor("#ffffff"));
            searchText.setBackgroundResource(R.drawable.edit_text_border_white);
            searchText.setHintTextColor(Color.parseColor("#ececec"));
            searchText.setTextColor(Color.parseColor("#ffffff"));
            debuggingView.setTextColor(Color.parseColor("#ffffff"));

            // change buttons
            /*searchBtn.setImageResource(R.mipmap.search_white);
            cameraBtn.setImageResource(R.mipmap.camera_white);
            settingBtn.setImageResource(R.mipmap.settings_white);
            recentlyViewedBtn.setImageResource(R.mipmap.list_white);*/
            addBtn.setImageResource(R.mipmap.add_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));

            // listview divider/separator
            /*resultListView.setDivider(new ColorDrawable(0xF0ECECEC));
            resultListView.setDividerHeight(1);*/
        }
        /*************************************************************************************************************/

        results = new ArrayList<String>();
        resultsID = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, results) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(getResources().getColor(R.color.myBlack));

                final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                final String pathT = myPref.getString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                Typeface font = Typeface.createFromAsset(getAssets(), pathT);
                textView.setTypeface(font);

                final Integer fontSize = myPref.getInt("fontSize", 16);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

                if(myPref.getBoolean("theme", false)) { // dark theme
                    textView.setTextColor(Color.parseColor("#ffffff"));

                    // 선택된 항목 텍스트 색 변화 (바탕이 검은색이라 체크 항목이 안 보임)
                    if(position == resultList.getCheckedItemPosition())
                        textView.setTextColor(getResources().getColor(R.color.myBlue));
                }

                return view;
            }
        };
        resultList.setAdapter(adapter);

        resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.notifyDataSetChanged();
            }
        });

        TextWatcher watcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                //텍스트 변경 후 발생할 이벤트를 작성.
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //텍스트의 길이가 변경되었을 경우 발생할 이벤트를 작성.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Retrofit retrofit;
                APIService apiService;

                retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                apiService = retrofit.create(APIService.class);

                String text = searchText.getText().toString();
                Call<List<Ingredient>> comment = apiService.searchIngredient(text);
                comment.enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        if(response.isSuccessful()){
                            results.clear();
                            resultsID.clear();
                            Log.i("size : ",response.body().toString());
                            if(response.body().toString()=="fail") {
                                debuggingView.setVisibility(View.VISIBLE);
                                resultList.setVisibility(View.INVISIBLE);
                            }
                            else {
                                debuggingView.setVisibility(View.INVISIBLE);
                                resultList.setVisibility(View.VISIBLE);
                            }
                            for(int i=0;i<response.body().size();i++){
                                results.add(response.body().get(i).getName());
                                resultsID.add(response.body().get(i).getId());
                            }
                            if(response.body().size()!=0) adapter.notifyDataSetChanged();
                        } else{
                            results.clear();
                            resultsID.clear();
                            adapter.notifyDataSetChanged();
                            Log.i("Test1", "fail");
                            debuggingView.setVisibility(View.VISIBLE);
                            resultList.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        Log.i("Test1", "onfailure");
                        t.printStackTrace();
                        results.clear();
                        resultsID.clear();
                        adapter.notifyDataSetChanged();
                        debuggingView.setVisibility(View.VISIBLE);
                        resultList.setVisibility(View.INVISIBLE);
                    }
                });

                //텍스트가 변경될때마다 발생할 이벤트를 작성.
                if (searchText.isFocusable())
                {
                    //mXMLBuyCount EditText 가 포커스 되어있을 경우에만 실행됩니다.
                }
            }
        };

        searchText.addTextChangedListener(watcher);

        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // 엔터키 눌렀을 시 감지
                if((keyEvent.getAction() == keyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);   // hide keyboard

                    return true;
                }
                return false;
            }
        });


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count, checked ;
                count = adapter.getCount() ;
                if (count > 0) {
                    checked = resultList.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("addIngredient",results.get(checked).toString());
                        returnIntent.putExtra("addIngredientID",resultsID.get(checked).toString());
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
            }
        });

        /*settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchActivity.this, SettingsActivity.class));
                finish();
            }
        });*/
    }
}
