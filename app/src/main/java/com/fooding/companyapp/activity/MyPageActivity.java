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
    @BindView(R.id.viewRecipe) ImageButton viewRecipeBtn;
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
                for(int i=0; i< response.body().size();i++){
                    tempRecipes.put(response.body().get(i).getId(), response.body().get(i).getId());
                }
                User user = FoodingCompanyApplication.getInstance().getUser();
                user.setRecipe(tempRecipes);
                FoodingCompanyApplication.getInstance().setUser(user);
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
        text1_2.setText(user.getName());

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
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, SettingsActivity.class));
                finish();
            }
        });

        makeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, MakeRecipeActivity.class));
                finish();
            }
        });

        viewRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyPageActivity.this, ViewRecipeActivity.class));
                finish();
            }
        });
    }
}