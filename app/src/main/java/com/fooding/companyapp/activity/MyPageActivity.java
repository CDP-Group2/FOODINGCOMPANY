package com.fooding.companyapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fooding.companyapp.FoodingApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.util.Iterator;
import java.util.Map;

public class MyPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        Food food = FoodingApplication.getInstance().getCurrentFood();
        Map<String, String> ttt=food.getIngredient();
        String temp="";
        Iterator<String> iterator = ttt.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            temp+=ttt.get(key);
        }
        Toast.makeText(this, food.getName().toString() + temp, Toast.LENGTH_SHORT).show();
        //사용방법은 카메라 액티비티 참고
    }
}
