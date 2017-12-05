package com.fooding.companyapp.activity;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class MakeRecipeActivity extends AppCompatActivity {
    @BindView(R.id.title) TextView title;
    @BindView(R.id.addButton) ImageButton addBtn;
    @BindView(R.id.deleteButton) ImageButton deleteBtn;
    @BindView(R.id.makeButton) ImageButton makeBtn;
    @BindView(R.id.ingredientList) ListView ingredientList;
    @BindView(R.id.recipeName) EditText recipeNameText;
    @BindView(R.id.text1) TextView text1;
    @BindView(R.id.clearBtn) Button clearBtn;
    @BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.myPage) ImageButton myPageBtn;
    @BindView(R.id.logout) ImageButton logoutBtn;
    final Integer ADD_INGREDIENT = 1;
    public ArrayList<String> ingredients;
    public ArrayList<String> ingredientsID;
    public ArrayAdapter adapter;
    private Food food;
    private FoodingCompanyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_recipe);
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

        text1.setTypeface(fontK);
        recipeNameText.setTypeface(fontK);
        clearBtn.setTypeface(fontK);
        /*************************************************************************************************************/

        ingredients = new ArrayList<String>();
        ingredientsID = new ArrayList<String>();
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
                            textView.setTextColor(getResources().getColor(R.color.yellowAccent));
                    }
                }

                return view;
            }
        };
        ingredientList.setAdapter(adapter);

        ingredientList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ingredientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                int selectedCount = 0;
//                SparseBooleanArray sparse = ingredientList.getCheckedItemPositions();
//                final int length = filter.getUserListName().size();

                /*for(int j = 0; j < length; j++) {
                    if(sparse.valueAt(j)) {
                        Log.i("Selected Item", filter.getUserListName().get(i));

                        selectedCount++;
                    }
                }

                Log.i("# of Selected Items", Integer.toString(selectedCount));*/

                adapter.notifyDataSetChanged();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MakeRecipeActivity.this, CameraActivity.class),ADD_INGREDIENT);

            }
        });

        /*deleteBtn.setOnClickListener(new View.OnClickListener() {
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
        });*/
        deleteBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray checkedItems = ingredientList.getCheckedItemPositions();
                int count = adapter.getCount();

                int selectedCount = 0;

                for(int i = count - 1; i >= 0; i--) {
                    if(checkedItems.get(i)) {
                        ingredients.remove(i);
                        selectedCount++;
                    }
                }

                if(selectedCount == 0)
                    Toast.makeText(MakeRecipeActivity.this, "0 Items Selected", Toast.LENGTH_SHORT).show();

                ingredientList.clearChoices();
                adapter.notifyDataSetChanged();
            }
        });

        makeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                app = FoodingCompanyApplication.getInstance();
                food=new Food();
                food.setName(recipeNameText.getText().toString());
                Map<String, String> ttt=new LinkedHashMap<String, String>();
                int count;
                count=adapter.getCount();
                for(int i=0;i<count;i++){
                    ttt.put(ingredientsID.get(i),ingredients.get(i));
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
                    Log.i("key",ingredients.get(key));
                }

                Log.i("lenof tmp", Integer.toString(tmp.size()));

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

//                startActivity(new Intent(MakeRecipeActivity.this, ViewRecipeActivity.class));
                Intent intent = new Intent(MakeRecipeActivity.this, ViewRecipeActivity.class);
                intent.putExtra("recipeName", recipeNameText.getText().toString());
                startActivity(intent);
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
//                startActivity(new Intent(MakeRecipeActivity.this, ViewRecipeActivity.class));
                Toast.makeText(MakeRecipeActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
                editor.putString("id", null);
                editor.putString("password", null);
                editor.apply();
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingredientList.clearChoices();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String addIngredient=data.getStringExtra("addIngredient");
                String addIngredientID=data.getStringExtra("addIngredientID");
                ingredients.add(addIngredient);
                ingredientsID.add(addIngredientID);
                adapter.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //만약 반환값이 없을 경우의 코드를 여기에 작성하세요.
            }
        }
    }//onActivityResult
}
