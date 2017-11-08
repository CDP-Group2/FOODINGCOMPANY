package com.fooding.companyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MakeRecipeActivity extends AppCompatActivity {
    @BindView(R.id.addButton) Button addButton;
    @BindView(R.id.deleteButton) Button deleteButton;
    @BindView(R.id.makeButton) Button makeButton;
    @BindView(R.id.ingredientList) ListView ingredientList;
    @BindView(R.id.recipeName) EditText recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_recipe);
        ButterKnife.bind(this);

        final ArrayList<String> ingredients = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, ingredients) ;
        ingredientList.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count;
                count = adapter.getCount();
                ingredients.add("LIST" + Integer.toString(count + 1));
                adapter.notifyDataSetChanged();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count, checked ;
                count = adapter.getCount() ;
                if (count > 0) {
                    checked = ingredientList.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {
                        ingredients.remove(checked) ;
                        ingredientList.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        makeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                Food food=new Food();
                food.setName(recipeName.getText().toString());
                Map<String, String> ttt=new LinkedHashMap<String, String>();
                int count;
                count=adapter.getCount();
                for(int i=0;i<count;i++){
                    ttt.put("S"+Integer.toString(i),ingredients.get(i));
                }

                food.setIngredient(ttt);
                app.setCurrentFood(food);


                Retrofit retrofit;
                APIService apiService;

                retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).build();
                apiService = retrofit.create(APIService.class);

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

                startActivity(new Intent(MakeRecipeActivity.this, ViewRecipeActivity.class));
                finish();
            }
        });

/*
        Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
        Map<String, String> ttt=food.getIngredient();
        String temp="";
        Iterator<String> iterator = ttt.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            temp+=ttt.get(key);
        }
        Toast.makeText(this, food.getName().toString() + temp, Toast.LENGTH_SHORT).show();
        */
        //사용방법은 카메라 액티비티 참고
    }
}
