package com.fooding.companyapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.travijuu.numberpicker.library.NumberPicker;

public class MakeRecipeActivity extends AppCompatActivity{
    @BindView(R.id.addButton) Button addBtn;
    @BindView(R.id.deleteButton) Button deleteBtn;
    @BindView(R.id.makeButton) Button makeBtn;
    @BindView(R.id.ingredientList) ListView ingredientList;
    @BindView(R.id.recipeName) EditText recipeNameText;
    final Integer ADD_INGREDIENT = 1;
    final Integer SET_AMOUNT = 2;
    public ArrayList<String> ingredients;
    public ArrayList<String> ingredientsID;
    public ArrayList<Integer> ingredientsAmount;
    public ArrayAdapter adapter;
    private Food food;
    private FoodingCompanyApplication app;
    private Boolean amountFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_recipe);
        ButterKnife.bind(this);

        amountFlag=false;


        ingredients = new ArrayList<String>();
        ingredientsID = new ArrayList<String>();
        ingredientsAmount = new ArrayList<Integer>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, ingredients) ;
        ingredientList.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MakeRecipeActivity.this, CameraActivity.class),ADD_INGREDIENT);

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
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

        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(amountFlag){
                    Intent intent = new Intent(MakeRecipeActivity.this, amountPopupActivity.class);
                    intent.putExtra("ingredientName",ingredients.get(position).toString());
                    intent.putExtra("ingredientPS",Integer.toString(position));
                    intent.putExtra("ingredientAmount",ingredientsAmount.get(position).toString());

                    Log.i("name",ingredients.get(position).toString());
                    Log.i("id",ingredientsID.get(position).toString());
                    Log.i("amoutn",ingredientsAmount.get(position).toString());

                    startActivityForResult(intent,SET_AMOUNT);
                }
            }
        });

        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app = FoodingCompanyApplication.getInstance();
                food=new Food();
                food.setName(recipeNameText.getText().toString());
                Map<String, String> ttt=new LinkedHashMap<String, String>();
                int count;
                count=adapter.getCount();
                for(int i=0;i<count;i++){
                    ttt.put(ingredientsID.get(i),ingredients.get(i));
                }

                food.setIngredient(ttt);

                app.setCurrentFood(food);



                final ArrayList<String> tmp = new ArrayList<String>();
                food = app.getCurrentFood();
                Map<String, String> ingredients= food.getIngredient();
                Iterator<String> iterator = ingredients.keySet().iterator();
                while(iterator.hasNext()){
                    String key=iterator.next();
                    tmp.add(key);
                    Log.i("key",ingredients.get(key));
                }

                Log.i("lenof tmp", Integer.toString(tmp.size()));
                AlertDialog.Builder builder = new AlertDialog.Builder(MakeRecipeActivity.this);
                builder.setCancelable(true);
                builder.setMessage("영양정보를 추가하시겠습니까?");
                builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Retrofit retrofit;
                        APIService apiService;

                        retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).build();
                        apiService = retrofit.create(APIService.class);
                        Call<ResponseBody> comment = apiService.makeRecipe("1", recipeNameText.getText().toString(), tmp);
                        comment.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try{
                                    String temp = response.body().string();
                                    Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
                                    Log.i("string temp",temp);
                                    food.setName(temp);
                                    FoodingCompanyApplication.getInstance().setCurrentFood(food);
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
                builder.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        amountFlag=true;
                    }
                });

                if(amountFlag){
                    Retrofit retrofit;
                    APIService apiService;

                    retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).build();
                    apiService = retrofit.create(APIService.class);
                    Call<ResponseBody> comment = apiService.makeRecipe("1", recipeNameText.getText().toString(), tmp);
                    comment.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try{
                                String temp = response.body().string();
                                Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
                                Log.i("string temp",temp);
                                food.setName(temp);
                                FoodingCompanyApplication.getInstance().setCurrentFood(food);
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
                else{
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_INGREDIENT) {
            if(resultCode == Activity.RESULT_OK){
                String addIngredient=data.getStringExtra("addIngredient");
                String addIngredientID=data.getStringExtra("addIngredientID");
                ingredients.add(addIngredient);
                ingredientsID.add(addIngredientID);
                ingredientsAmount.add(10);
                adapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //만약 반환값이 없을 경우의 코드를 여기에 작성하세요.
            }
        }

        if(requestCode == SET_AMOUNT){
            if(resultCode==Activity.RESULT_OK){
                Integer setIngredient = Integer.parseInt(data.getStringExtra("setIngredient"));
                Integer setIngredientAmount = Integer.parseInt(data.getStringExtra("setIngredientAmount"));
                ingredientsAmount.set(setIngredient,setIngredientAmount);
            }
        }
    }//onActivityResult
}
