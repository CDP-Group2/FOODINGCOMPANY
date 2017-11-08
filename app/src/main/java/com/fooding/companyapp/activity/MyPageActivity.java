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
import com.fooding.companyapp.data.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPageActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView title;
    @BindView(R.id.companyName) TextView companyName;
    @BindView(R.id.recipeList) ListView recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        User user = app.getUser();
        Map<String, String> CompanyRecipes = user.getRecipe();

        final ArrayList<String> recipes = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recipes) ;
        recipeList.setAdapter(adapter);

        companyName.setText(user.getName());

        Iterator<String> iterator = CompanyRecipes.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            recipes.add(CompanyRecipes.get(key));
        }
        adapter.notifyDataSetChanged();

    }
}