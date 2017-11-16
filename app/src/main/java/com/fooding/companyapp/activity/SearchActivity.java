package com.fooding.companyapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

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
    @BindView(R.id.addButton) Button addBtn;

    public ArrayList<String> results;
    public ArrayList<String> resultsID;
    public ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        results = new ArrayList<String>();
        resultsID = new ArrayList<String>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, results) ;
        resultList.setAdapter(adapter);

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
                            for(int i=0;i<response.body().size();i++){
                                results.add(response.body().get(i).getName());
                                resultsID.add(response.body().get(i).getId());
                            }
                            if(response.body().size()!=0) adapter.notifyDataSetChanged();
                        } else{
                            Log.i("Test1", "fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        Log.i("Test1", "onfailure");
                        t.printStackTrace();
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
    }
}
