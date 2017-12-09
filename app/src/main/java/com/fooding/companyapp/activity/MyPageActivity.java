package com.fooding.companyapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPageActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView title;
    //    @BindView(R.id.companyName) TextView companyNameText;
    @BindView(R.id.recipeList) ListView recipeList;
    @BindView(R.id.myInfoTitle) Button infoTitle;
    @BindView(R.id.recentRecipeTitle) Button recentTitle;
    @BindView(R.id.edit) Button editBtn;
    @BindView(R.id.text1_1) TextView text1_1;
    @BindView(R.id.text1_2) TextView text1_2;
    @BindView(R.id.text2_1) TextView text2_1;
    @BindView(R.id.text2_2) TextView text2_2;
    @BindView(R.id.text3_1) TextView text3_1;
    @BindView(R.id.text3_2) TextView text3_2;
    @BindView(R.id.text4_1) TextView text4_1;
    @BindView(R.id.text4_2) TextView text4_2;
    @BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.makeMenu) ImageButton makeMenuBtn;
    @BindView(R.id.logout) ImageButton logoutBtn;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        ButterKnife.bind(this);

        /*************************************************************************************************************/
        // font setting
        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        SharedPreferences myPref = app.getMyPref();

        final String pathT = myPref.getString("titleFont", "none");
        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
        title.setTypeface(font);

        final String pathK = myPref.getString("koreanFont", "none");
        Typeface fontK = Typeface.createFromAsset(getAssets(), pathK);
        final String pathKB = myPref.getString("boldKoreanFont", "none");
        Typeface fontKB = Typeface.createFromAsset(getAssets(), pathKB);

        infoTitle.setTypeface(fontKB);
        recentTitle.setTypeface(fontKB);
//        infoTitle.setTypeface(font);
        text1_1.setTypeface(fontK);
        text1_2.setTypeface(fontK);
        text2_1.setTypeface(fontK);
        text2_2.setTypeface(fontK);
        text3_1.setTypeface(fontK);
        text3_2.setTypeface(fontK);
        text4_1.setTypeface(fontK);
        text4_2.setTypeface(fontK);

        editBtn.setTypeface(fontK);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.myPageActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            title.setTextColor(getResources().getColor(R.color.myWhite));
            recentTitle.setTextColor(getResources().getColor(R.color.darkThemeBrightGray));
            ((View)findViewById(R.id.recentRecipeView)).setBackgroundColor(getResources().getColor(R.color.darkThemeBrightGray));
            text1_1.setTextColor(getResources().getColor(R.color.myWhite));
            text1_2.setTextColor(getResources().getColor(R.color.myWhite));
            text2_1.setTextColor(getResources().getColor(R.color.myWhite));
            text2_2.setTextColor(getResources().getColor(R.color.myWhite));
            text3_1.setTextColor(getResources().getColor(R.color.myWhite));
            text3_2.setTextColor(getResources().getColor(R.color.darkThemeBrightGray));
            text4_1.setTextColor(getResources().getColor(R.color.myWhite));
            text4_2.setTextColor(getResources().getColor(R.color.darkThemeBrightGray));

            // change buttons
            settingBtn.setImageResource(R.mipmap.settings_white);
            makeMenuBtn.setImageResource(R.mipmap.compose_white);
            logoutBtn.setImageResource(R.mipmap.exit_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.menu_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /*************************************************************************************************************/

        Retrofit retrofit;
        APIService apiService;
        final Map<String, String> tempRecipes = new LinkedHashMap<String, String>();

//        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
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
                final ArrayAdapter adapter = new ArrayAdapter(MyPageActivity.this, android.R.layout.simple_list_item_1, recipes) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);

                        textView.setTextColor(getResources().getColor(R.color.myBlack));

                        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                        SharedPreferences myPref = app.getMyPref();

                        final String pathT = myPref.getString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
                        textView.setTypeface(font);

                        final Integer fontSize = myPref.getInt("fontSize", 16);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

                        if(myPref.getBoolean("theme", false)) { // dark theme
                            textView.setTextColor(Color.parseColor("#ffffff"));
                        }

                        return view;
                    }
                };
                recipeList.setAdapter(adapter);

                text1_2.setText(user.getName());

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


        Map<String, String> CompanyRecipes = user.getRecipe();

        final ArrayList<String> recipes = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recipes) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(getResources().getColor(R.color.myBlack));

                final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                final String pathT = myPref.getString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                Typeface font = Typeface.createFromAsset(getAssets(), pathT);
                textView.setTypeface(font);

                final Integer fontSize = myPref.getInt("fontSize", 16);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

                if(myPref.getBoolean("theme", false)) { // dark theme
                    textView.setTextColor(Color.parseColor("#ffffff"));
                }

                return view;
            }
        };
        recipeList.setAdapter(adapter);

//        companyNameText.setText(user.getName());
        text1_2.setText(user.getName());        // 사업자명

        myPref = getSharedPreferences("settings", MODE_PRIVATE);

        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("companyName", text1_2.getText().toString());
        editor.apply();

        text2_2.setText(myPref.getString("id", null));  // 아이디
        if(myPref.getString("email", null) != null) {   // 이메일
            text3_2.setText(myPref.getString("email", null));
            text3_2.setTextColor(getResources().getColor(R.color.myBlack));
            if(myPref.getBoolean("theme", false))   // dark theme
                text3_2.setTextColor(getResources().getColor(R.color.myWhite));
        }
        if(myPref.getString("address", null) != null) {   // 주소
            text4_2.setText(myPref.getString("address", null));
            text4_2.setTextColor(getResources().getColor(R.color.myBlack));
            if(myPref.getBoolean("theme", false))   // dark theme
                text4_2.setTextColor(getResources().getColor(R.color.myWhite));
        }

        Map<String, String> ttt= user.getRecipe();

        recipeList.setAdapter(adapter);

        Iterator<String> iterator = CompanyRecipes.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            recipes.add(CompanyRecipes.get(key));
        }
        adapter.notifyDataSetChanged();

        infoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.myInformation)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.recentRecipe)).setVisibility(View.INVISIBLE);

                infoTitle.setTextColor(getResources().getColor(R.color.myBlue));
                ((View)findViewById(R.id.myInfoView)).setBackgroundColor(getResources().getColor(R.color.myBlue));
                recentTitle.setTextColor(getResources().getColor(R.color.gray));
                ((View)findViewById(R.id.recentRecipeView)).setBackgroundColor(getResources().getColor(R.color.gray));

                final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                if(myPref.getBoolean("theme", false)) { // dark theme
                    recentTitle.setTextColor(getResources().getColor(R.color.darkThemeBrightGray));
                    ((View)findViewById(R.id.recentRecipeView)).setBackgroundColor(getResources().getColor(R.color.darkThemeBrightGray));
                }
            }
        });



        recentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LinearLayout)findViewById(R.id.myInformation)).setVisibility(View.INVISIBLE);
                ((LinearLayout)findViewById(R.id.recentRecipe)).setVisibility(View.VISIBLE);

                infoTitle.setTextColor(getResources().getColor(R.color.gray));
                ((View)findViewById(R.id.myInfoView)).setBackgroundColor(getResources().getColor(R.color.gray));
                recentTitle.setTextColor(getResources().getColor(R.color.myBlue));
                ((View)findViewById(R.id.recentRecipeView)).setBackgroundColor(getResources().getColor(R.color.myBlue));

                final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                if(myPref.getBoolean("theme", false)) { // dark theme
                    infoTitle.setTextColor(getResources().getColor(R.color.darkThemeBrightGray));
                    ((View)findViewById(R.id.myInfoView)).setBackgroundColor(getResources().getColor(R.color.darkThemeBrightGray));
                }
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, SettingsActivity.class));
                finish();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, EditActivity.class));
//                finish();
            }
        });
        makeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, MakeRecipeActivity.class));
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(MyPageActivity.this, ViewRecipeActivity.class));
                finish();*/
                Toast.makeText(MyPageActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
//                editor.putString("id", null);
                editor.putString("password", null);
                editor.apply();

                startActivity(new Intent(MyPageActivity.this, LoginActivity.class));
                finish();
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
                final String finalFoodID = foodID;
                comment.enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        Food food=new Food();
                        String temp=foodName;
                        food.setName(temp);
                        temp= "R"+finalFoodID;
                        food.setID(temp);
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
    }
}