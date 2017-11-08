package com.fooding.companyapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TempActivity extends AppCompatActivity {
    Retrofit retrofit;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).build();
        apiService = retrofit.create(APIService.class);

        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        Food food=new Food();
        String temp="오뚜기 케챱";
        food.setName(temp);
        Map<String, String> ttt=new LinkedHashMap<String, String>();
        ttt.put("a123","ketchap1");
        ttt.put("b123","ketchap2");
        ttt.put("c123","ketchap3");
        food.setIngredient(ttt);
        app.setCurrentFood(food);

/*
        JSONArray tmp = new JSONArray();

        food = app.getCurrentFood();
        Map<String, String> ingredients= food.getIngredient();
        Iterator<String> iterator = ingredients.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            tmp.put(ingredients.get(key));
        }*/

        ArrayList<String> tmp = new ArrayList<String>();
        food = app.getCurrentFood();
        Map<String, String> ingredients= food.getIngredient();
        Iterator<String> iterator = ingredients.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            tmp.add(key);
        }

        Log.i("lenof tmp", Integer.toString(tmp.size()));

        Call<ResponseBody> comment = apiService.makeRecipe("1", "ket", tmp);
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    Log.i("Test1", response.body().string());
                } catch(IOException e){
                    Log.i("Test1", "fail");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Test1", "onfailure");
                t.printStackTrace();
            }
        });
    }
}
