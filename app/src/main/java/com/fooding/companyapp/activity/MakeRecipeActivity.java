package com.fooding.companyapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.travijuu.numberpicker.library.NumberPicker;

public class MakeRecipeActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView title;
    @BindView(R.id.addButton) ImageButton addBtn;
    @BindView(R.id.deleteButton) ImageButton deleteBtn;
    @BindView(R.id.makeButton) ImageButton makeBtn;
    @BindView(R.id.ingredientList) ListView ingredientList;
    @BindView(R.id.ingredientAmountList) ListView ingredientAmountList;
    @BindView(R.id.recipeName) EditText recipeNameText;
    @BindView(R.id.text1) TextView text1;
    @BindView(R.id.clearBtn) Button clearBtn;
    @BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.myPage) ImageButton myPageBtn;
    @BindView(R.id.logout) ImageButton logoutBtn;
    @BindView(R.id.addButtonCaption) TextView addButtonCaption;
    @BindView(R.id.deleteButtonCaption) TextView deleteButtonCaption;
    @BindView(R.id.makeButtonCaption) TextView makeButtonCaption;
    @BindView(R.id.ingredientCaption) TextView ingredientCaption;
    @BindView(R.id.ingredientAmountCaption) TextView ingredientAmountCaption;
    final Integer ADD_INGREDIENT = 1;
    final Integer SET_AMOUNT = 2;
    public ArrayList<String> ingredients;
    public ArrayList<String> ingredientsID;
    public ArrayList<Integer> ingredientsAmount;
    public ArrayAdapter adapter;
    public ArrayAdapter adapter2;
    private Food food;
    private FoodingCompanyApplication app;
    private Boolean amountFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_recipe);
        ButterKnife.bind(this);

        amountFlag=false;


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

        text1.setTypeface(fontK);
        addButtonCaption.setTypeface(fontK);
        deleteButtonCaption.setTypeface(fontK);
        makeButtonCaption.setTypeface(fontK);
        ingredientCaption.setTypeface(fontK);
        ingredientAmountCaption.setTypeface(fontK);
        recipeNameText.setTypeface(fontKB);
        clearBtn.setTypeface(fontK);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.makeRecipeActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            title.setTextColor(Color.parseColor("#ffffff"));

            text1.setTextColor(Color.parseColor("#ffffff"));
//            recipeNameText.setTextColor(Color.parseColor("#ffffff"));
            clearBtn.setTextColor(Color.parseColor("#ececec"));
            addButtonCaption.setTextColor(getResources().getColor(R.color.myWhite));
            deleteButtonCaption.setTextColor(getResources().getColor(R.color.myWhite));
            makeButtonCaption.setTextColor(getResources().getColor(R.color.myWhite));
            ingredientCaption.setTextColor(getResources().getColor(R.color.myWhite));
            ingredientAmountCaption.setTextColor(getResources().getColor(R.color.myWhite));

            // change buttons
            addBtn.setImageResource(R.mipmap.camera_white);
            deleteBtn.setImageResource(R.mipmap.dustbin_white);
            makeBtn.setImageResource(R.mipmap.upload_white);
            settingBtn.setImageResource(R.mipmap.settings_white);
            myPageBtn.setImageResource(R.mipmap.user_white);
            logoutBtn.setImageResource(R.mipmap.exit_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.menu_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /*************************************************************************************************************/

        ingredients = new ArrayList<String>();
        ingredientsID = new ArrayList<String>();
        ingredientsAmount = new ArrayList<Integer>();
//        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients) ;
        adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredientsAmount) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);

                textView.setTextColor(getResources().getColor(R.color.myBlue));
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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

                return view;
            }
        };
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, ingredients) {
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

                    // 선택된 항목 텍스트 색 변화 (바탕이 검은색이라 체크 항목이 안 보임)
                    SparseBooleanArray checked = ingredientList.getCheckedItemPositions();
                    for(int i = 0; i < checked.size(); i++) {
                        int key = checked.keyAt(i);
                        boolean value = checked.get(key);
                        if(value && position == key)
                            textView.setTextColor(getResources().getColor(R.color.myBlue));
                    }
                }

                return view;
            }
        };
        ingredientList.setAdapter(adapter);
        ingredientAmountList.setAdapter(adapter2);

        ingredientList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Intent intent = getIntent();
        String booedit = intent.getStringExtra("editRecipe");
        if(booedit!=null){
            if(booedit.equals("true")){
                recipeNameText.setText(app.getCurrentFood().getName());
                Map<String, String> temp = app.getCurrentFood().getIngredient();
                Map<String, Integer> temp2 = app.getCurrentFood().getIngredientAmount();

                Integer flag=0;
                Iterator<String> iterator2 = temp2.keySet().iterator();
                while(iterator2.hasNext()){
                    String key=iterator2.next();
                    Log.i("key",key);
                    ingredientsAmount.add(temp2.get(key));
                    flag=1;
                }


                Iterator<String> iterator = temp.keySet().iterator();
                while(iterator.hasNext()){
                    String key=iterator.next();
                    ingredients.add(temp.get(key));
                    ingredientsID.add(key);
                    if(flag==0) ingredientsAmount.add(0);
                }


                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        }



        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MakeRecipeActivity.this, CameraActivity.class),ADD_INGREDIENT);

            }
        });


        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ingredientAmountList.setItemChecked(position,true);

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
                else{
                }

                adapter.notifyDataSetChanged();
            }
        });

        ingredientAmountList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MakeRecipeActivity.this, amountPopupActivity.class);
                intent.putExtra("ingredientName",ingredients.get(position).toString());
                intent.putExtra("ingredientPS",Integer.toString(position));
                intent.putExtra("ingredientAmount",ingredientsAmount.get(position).toString());

                Log.i("name",ingredients.get(position).toString());
                Log.i("id",ingredientsID.get(position).toString());
                Log.i("amoutn",ingredientsAmount.get(position).toString());

                startActivityForResult(intent,SET_AMOUNT);
            }
        });
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checkedItems = ingredientList.getCheckedItemPositions();
                int count = adapter.getCount();

                int selectedCount = 0;

                for(int i = count - 1; i >= 0; i--) {
                    if(checkedItems.get(i)) {
                        ingredients.remove(i);
                        ingredientAmountList.clearChoices();
                        ingredientsAmount.remove(i);
                        selectedCount++;
                    }
                }

                if(count < 1)
                    Toast.makeText(MakeRecipeActivity.this, "지울 항목이 없습니다.", Toast.LENGTH_SHORT).show();
                else if(selectedCount == 0)
                    Toast.makeText(MakeRecipeActivity.this, "지울 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();

                ingredientList.clearChoices();
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }
        });

        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                food=new Food();
                food.setName(recipeNameText.getText().toString());
                Map<String, String> ttt=new LinkedHashMap<String, String>();
                Map<String, Integer> ttta=new LinkedHashMap<String, Integer>();
                int count;
                count=adapter2.getCount();
                int flag=0,flag2=0;
                for(int i=0;i<count;i++){
                    if(ingredientsAmount.get(i)!=0) flag=1;
                    if(ingredientsAmount.get(i)==0) flag2=1;
                }
                if(flag==1 && flag2==1) {
                    Toast.makeText(MakeRecipeActivity.this, "모든 재료의 함량을 기입해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int i=0;i<count;i++){
                    ttt.put(ingredientsID.get(i),ingredients.get(i));
                    ttta.put(ingredientsID.get(i),ingredientsAmount.get(i));
                }


                if(count == 0 && recipeNameText.getText().length() == 0) {
                    Toast.makeText(MakeRecipeActivity.this, "레시피를 작성해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(count == 0) {
                    Toast.makeText(MakeRecipeActivity.this, "재료를 추가해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(recipeNameText.getText().length() == 0) {
                    Toast.makeText(MakeRecipeActivity.this, "레시피 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(flag==1)food.setIngredient(ttt,ttta);
                else food.setIngredient(ttt);

                app.setCurrentFood(food);



                final ArrayList<String> tmp = new ArrayList<String>();
                final ArrayList<Integer> tmp2 = new ArrayList<Integer>();
                food = app.getCurrentFood();
                Map<String, String> ingredients= food.getIngredient();
                Map<String, Integer> ingredientsAmount = food.getIngredientAmount();

                Iterator<String> iterator = ingredients.keySet().iterator();
                while(iterator.hasNext()){
                    String key=iterator.next();
                    tmp.add(key);
                    tmp2.add(ingredientsAmount.get(key));
                    Log.i("key",ingredients.get(key));
                }

                Log.i("lenof tmp", Integer.toString(tmp.size()));

                Retrofit retrofit;
                APIService apiService;


                retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).build();
                apiService = retrofit.create(APIService.class);
                Call<ResponseBody> comment = apiService.makeRecipe("1", recipeNameText.getText().toString(), tmp, tmp2);
                comment.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try{
                            Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
                            food.setID(response.body().string());
                            FoodingCompanyApplication.getInstance().setCurrentFood(food);
                        }
                        catch (IOException e) {}

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("Test1", "onfailure");
                        t.printStackTrace();
                    }
                });

                Log.i("intent to viewrecipe",app.getCurrentFood().getName());
                startActivity(new Intent(MakeRecipeActivity.this, ViewRecipeActivity.class));
                finish();
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MakeRecipeActivity.this, SettingsActivity.class));
//                finish();
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MakeRecipeActivity.this, MyPageActivity.class));
//                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(MyPageActivity.this, ViewRecipeActivity.class));
                finish();*/
                Toast.makeText(MakeRecipeActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
//                editor.putString("id", null);
                editor.putString("password", null);
                editor.apply();

                startActivity(new Intent(MakeRecipeActivity.this, LoginActivity.class));
                finish();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.clearChoices();
                adapter.notifyDataSetChanged();
            }
        });

        addButtonCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBtn.performClick();
            }
        });

        deleteButtonCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBtn.performClick();
            }
        });

        makeButtonCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeBtn.performClick();
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
                ingredientsAmount.add(0);
                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
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
                adapter2.notifyDataSetChanged();
            }
        }
    }//onActivityResult
}
