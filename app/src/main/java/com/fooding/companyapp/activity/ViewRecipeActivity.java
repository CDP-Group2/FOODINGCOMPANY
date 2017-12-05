package com.fooding.companyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewRecipeActivity extends AppCompatActivity {
    @BindView(R.id.toSendOutButton) Button toSendOutBtn;
    @BindView(R.id.toHomeButton) Button toHomeBtn;
    @BindView(R.id.title) TextView titleText;
    @BindView(R.id.recipeName) TextView recipeNameText;
    @BindView(R.id.ingredientList) ListView ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ButterKnife.bind(this);

        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        Food food = app.getCurrentFood();
        String rName=food.getName();
        Map<String, String> FoodIngredients = food.getIngredient();

        final ArrayList<String> ingredients = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients) ;
        ingredientList.setAdapter(adapter);

        recipeNameText.setText(rName);

        Iterator<String> iterator = FoodIngredients.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            ingredients.add(FoodIngredients.get(key));
        }
        adapter.notifyDataSetChanged();

        toSendOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewRecipeActivity.this, SendOutQRActivity.class);
                startActivity(intent);
                finish();
            }
        });
        toHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}