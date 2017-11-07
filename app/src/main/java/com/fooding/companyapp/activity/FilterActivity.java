package com.fooding.companyapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fooding.companyapp.FoodingApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Food food = FoodingApplication.getInstance().getCurrentFood();
        Toast.makeText(this, food.getName().toString(), Toast.LENGTH_SHORT).show();
        //사용방법은 카메라 액티비티 참고
    }
}
