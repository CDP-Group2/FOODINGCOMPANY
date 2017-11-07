package com.fooding.companyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tomakerecipe) Button toMakeRecipeButton;
    @BindView(R.id.tomypage) Button toMyPageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        toMakeRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MakeRecipeActivity.class));
            }
        });

        toMyPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MyPageActivity.class));
            }
        });
    }
}
