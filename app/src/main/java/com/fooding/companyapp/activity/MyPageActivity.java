package com.fooding.companyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;
import com.fooding.companyapp.data.User;
import com.fooding.companyapp.data.model.Ingredient;
import com.fooding.companyapp.data.model.Recipe;

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

public class MyPageActivity extends AppCompatActivity {
//    @BindView(R.id.title) TextView titleText;
    @BindView(R.id.companyName) TextView companyNameText;
    @BindView(R.id.recipeList) ListView recipeList;
    @BindView(R.id.viewRecipeList) Button viewRecipeList;
    @BindView(R.id.addRecipe) Button addRecipe;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        Retrofit retrofit;
        APIService apiService;
        final Map<String, String> tempRecipes = new LinkedHashMap<String, String>();

        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        user = app.getUser();

        retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

        String companyID = user.getKey();
        Log.i("companyID",companyID);
        Call<List<Recipe>> comment = apiService.getRecipe(companyID);
        comment.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.i("num",Integer.toString((response.body().size())));
                Log.i("response",response.body().toString());
                for(int i=0; i< response.body().size();i++){
                    tempRecipes.put(response.body().get(i).getId(), response.body().get(i).getName());
                    Log.i("put :",response.body().get(i).getId()+","+response.body().get(i).getName());
                }
                user.setRecipe(tempRecipes);

                Map<String, String> CompanyRecipes = user.getRecipe();

                final ArrayList<String> recipes = new ArrayList<String>();
                final ArrayAdapter adapter = new ArrayAdapter(MyPageActivity.this, android.R.layout.simple_list_item_1, recipes) ;
                recipeList.setAdapter(adapter);

                companyNameText.setText(user.getName());

                Iterator<String> iterator = CompanyRecipes.keySet().iterator();
                while(iterator.hasNext()){
                    String key=iterator.next();
                    recipes.add(CompanyRecipes.get(key));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.i("Test1", "onfailure");
                t.printStackTrace();
            }
        });



        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
                Map<String, String> tempRecipe = user.getRecipe();
                Iterator<String> iterator = tempRecipe.keySet().iterator();
                Integer i=0;
                String foodID="";
                while(iterator.hasNext()){
                    foodID = iterator.next();
                    if(i==position) break;
                    i++;
                }
                final String foodName=tempRecipe.get(foodID);
                Log.i("foodid",foodID);


                Retrofit retrofit;
                APIService apiService;
                retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                apiService = retrofit.create(APIService.class);

                Call<List<Ingredient>> comment = apiService.getIngredient(foodID);
                comment.enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        Food food=new Food();
                        String temp=foodName;
                        food.setName(temp);
                        Map<String, String> ttt=new LinkedHashMap<String, String>();
                        for(int i=0; i< response.body().size();i++){
                            Log.i("put :",response.body().get(i).getId()+","+response.body().get(i).getName());
                            ttt.put(response.body().get(i).getId(),response.body().get(i).getName());
                        }
                        food.setIngredient(ttt);
                        FoodingCompanyApplication.getInstance().setCurrentFood(food);
                        Intent intent = new Intent(MyPageActivity.this, ViewRecipeActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        Log.i("Test1", "onfailure");
                        t.printStackTrace();
                    }
                });
//                FoodingCompanyApplication.getInstance().setCurrentFood(food);


            }
        });

        viewRecipeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, ViewRecipeActivity.class));
//                finish();
            }
        });

        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, MakeRecipeActivity.class));
//                finish();
            }
        });
    }
}