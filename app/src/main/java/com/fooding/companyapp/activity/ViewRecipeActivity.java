package com.fooding.companyapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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
import com.fooding.companyapp.data.model.Ingredient;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewRecipeActivity extends AppCompatActivity {
    @BindView(R.id.toSendOutButton) Button toSendOutBtn;
    @BindView(R.id.editBtn) Button editBtn;
//    @BindView(R.id.toHomeButton) Button toHomeBtn;
    @BindView(R.id.title) TextView titleText;
//    @BindView(R.id.recipeName) TextView recipeNameText;
    @BindView(R.id.ingredientList) ListView ingredientList;
    @BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.myPage) ImageButton myPageBtn;
    @BindView(R.id.logout) ImageButton logoutBtn;
    @BindView(R.id.makeMenu) ImageButton makeMenuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ButterKnife.bind(this);

        /*************************************************************************************************************/
        // font setting
        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        SharedPreferences myPref = app.getMyPref();

        final String pathT = myPref.getString("titleFont", "none");
        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
        final String pathTK = myPref.getString("titleFontk", "none");
        Typeface fontTK = Typeface.createFromAsset(getAssets(), pathTK);
        titleText.setTypeface(font);

        final String pathK = myPref.getString("koreanFont", "none");
        Typeface fontK = Typeface.createFromAsset(getAssets(), pathK);
        final String pathKB = myPref.getString("boldKoreanFont", "none");
        Typeface fontKB = Typeface.createFromAsset(getAssets(), pathKB);

        toSendOutBtn.setTypeface(fontKB);
        editBtn.setTypeface(fontKB);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.viewRecipeActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            titleText.setTextColor(Color.parseColor("#ffffff"));
            editBtn.setTextColor(getResources().getColor(R.color.myBlack));

            // change buttons
            settingBtn.setImageResource(R.mipmap.settings_white);
            myPageBtn.setImageResource(R.mipmap.user_white);
            logoutBtn.setImageResource(R.mipmap.exit_white);
            makeMenuBtn.setImageResource(R.mipmap.compose_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.menu_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /*************************************************************************************************************/

//        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        Food food = app.getCurrentFood();
        String rName=food.getName();
        Map<String, String> FoodIngredients = food.getIngredient();

        final ArrayList<String> ingredients = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(getResources().getColor(R.color.myBlack));
//                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                SharedPreferences myPref = app.getMyPref();

                final String pathT = myPref.getString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                Typeface font = Typeface.createFromAsset(getAssets(), pathT);
                textView.setTypeface(font);

                final Integer fontSize = myPref.getInt("fontSize", 16);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);

                /*if(myPref.getBoolean("theme", false)) { // dark theme
                    textView.setTextColor(Color.parseColor("#ffffff"));

                    // 선택된 항목 텍스트 색 변화 (바탕이 검은색이라 체크 항목이 안 보임)
                    SparseBooleanArray checked = ingredientList.getCheckedItemPositions();
                    for(int i = 0; i < checked.size(); i++) {
                        int key = checked.keyAt(i);
                        boolean value = checked.get(key);
                        if(value && position == key)
                            textView.setTextColor(getResources().getColor(R.color.myBlue));
                    }
                }*/

                if(myPref.getBoolean("theme", false)) { // dark theme
                    textView.setTextColor(Color.parseColor("#ffffff"));

                    // 선택된 항목 텍스트 색 변화 (바탕이 검은색이라 체크 항목이 안 보임)
//                    Toast.makeText(getApplicationContext(), Integer.toString(position) + " / " + Integer.toString(mSelectedItem), Toast.LENGTH_SHORT).show();
                    if(position == ingredientList.getCheckedItemPosition())
                        textView.setTextColor(getResources().getColor(R.color.myBlue));
                }

                return view;
            }
        };
        ingredientList.setAdapter(adapter);

//        recipeNameText.setText(rName);
        Log.i("beforeSetText",app.getCurrentFood().getName());
        titleText.setText(rName);
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        boolean hasSpecialChar = p.matcher(rName).find();
        if(hasSpecialChar) {
            Log.i("title korean", "true");
            titleText.setTypeface(fontTK);
        }
        Log.i("afterSetText",app.getCurrentFood().getName());

        Iterator<String> iterator = FoodIngredients.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            ingredients.add(FoodIngredients.get(key));
        }
        adapter.notifyDataSetChanged();

        toSendOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> ListItems = new ArrayList<>();
                ListItems.add("QR code");
                ListItems.add("NFC tag");
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewRecipeActivity.this);
                builder.setTitle("레시피 내보내기");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
                        switch (pos){
                            case 0:
                                Intent QRintent = new Intent(ViewRecipeActivity.this, SendOutQRActivity.class);

                                Log.i("intent to qr",FoodingCompanyApplication.getInstance().getCurrentFood().getName());
                                QRintent.putExtra("code",food.getID());
                                startActivity(QRintent);
                                break;
                            case 1:
                                Intent NFCintent = new Intent(ViewRecipeActivity.this, SendOutNFCActivity.class);
                                Log.i("intent to nfc",FoodingCompanyApplication.getInstance().getCurrentFood().getName());
                                NFCintent.putExtra("code",food.getID());
                                startActivity(NFCintent);
                                break;
                        }

                    }
                });
                builder.show();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Retrofit retrofit;
                APIService apiService;
                retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                apiService = retrofit.create(APIService.class);

                String foodID = app.getCurrentFood().getID();

                Call<List<Ingredient>> comment = apiService.getOwnIngredient(foodID);
                final String finalFoodID = foodID;
                comment.enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        Food food=new Food();
                        String temp=app.getCurrentFood().getName();
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
                        Intent intent = new Intent(ViewRecipeActivity.this, MakeRecipeActivity.class);
                        intent.putExtra("editRecipe","true");
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        Log.i("Test1", "onfailure");
                        t.printStackTrace();
                    }
                });

            }
        });
        /*toHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, MainActivity.class));
                finish();
            }
        });*/

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, SettingsActivity.class));
//                finish();
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, MyPageActivity.class));
//                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MakeRecipeActivity.this, ViewRecipeActivity.class));
                Toast.makeText(ViewRecipeActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
//                editor.putString("id", null);
                editor.putString("password", null);
                editor.apply();

                startActivity(new Intent(ViewRecipeActivity.this, LoginActivity.class));
                finish();
            }
        });

        makeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, MakeRecipeActivity.class));
                finish();
            }
        });
    }
}