package com.fooding.companyapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.User;
import com.fooding.companyapp.data.model.UserLogin;

import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends Activity {
    @BindView(R.id.login_button) Button login_button;
    @BindView(R.id.register_button) Button register_button;
    @BindView(R.id.login_id_edittext) EditText id_text;
    @BindView(R.id.login_pw_edittext) EditText pw_text;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.save_id) CheckBox save_id;
    @BindView(R.id.auto_login) CheckBox auto_login;
    @BindView(R.id.tmp1) ImageView tmp1;
    @BindView(R.id.tmp2) ImageView tmp2;
    @BindView(R.id.tmp3) TextView tmp3;
    private String id;
    private String pw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        id_text.setTypeface(fontK);
        pw_text.setTypeface(fontK);
        tmp3.setTypeface(fontK);
        /*((TextInputLayout) findViewById(R.id.login_id)).setTypeface(fontK);
        ((TextInputLayout) findViewById(R.id.login_pw)).setTypeface(fontK);*/
        save_id.setTypeface(fontK);
        auto_login.setTypeface(fontK);
        login_button.setTypeface(fontKB);
        register_button.setTypeface(fontKB);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.loginActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            title.setTextColor(Color.parseColor("#ffffff"));
            login_button.setTextColor(getResources().getColor(R.color.myBlack));
            id_text.setTextColor(getResources().getColor(R.color.myWhite));
            pw_text.setTextColor(getResources().getColor(R.color.myWhite));
            save_id.setTextColor(getResources().getColor(R.color.myWhite));
            auto_login.setTextColor(getResources().getColor(R.color.myWhite));
            tmp3.setTextColor(getResources().getColor(R.color.myWhite));

            tmp1.setImageResource(R.mipmap.user_2_white);
            tmp2.setImageResource(R.mipmap.password_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));

            if(myPref.getBoolean("save_id_login", false))
                save_id.setTextColor(getResources().getColor(R.color.myBlue));
            else
                save_id.setTextColor(getResources().getColor(R.color.myWhite));

            if(myPref.getBoolean("auto_login", false))
                auto_login.setTextColor(getResources().getColor(R.color.myBlue));
            else
                auto_login.setTextColor(getResources().getColor(R.color.myWhite));
        }
        /*************************************************************************************************************/

        save_id.setChecked(myPref.getBoolean("save_id_login", false));
        auto_login.setChecked(myPref.getBoolean("auto_login", false));

        SharedPreferences loginPref = getSharedPreferences("settings", MODE_PRIVATE);
        if(loginPref.getBoolean("auto_login", false)) {
            id = loginPref.getString("id", null);
            pw = loginPref.getString("password", null);

            loginCheck(id, pw);
        }

        if(loginPref.getBoolean("save_id_login", false)) {
            id_text.setText(loginPref.getString("id", null));
            save_id.setChecked(true);
        }

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = id_text.getText().toString();
                pw = pw_text.getText().toString();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putString("id", id);
                editor.putString("password", pw);
                editor.apply();

                //로그인 체크
                loginCheck(id, pw);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        save_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                /*editor.putBoolean("save_id_login", b);
                editor.apply();*/

                Log.i("save_id", Boolean.toString(b));

                if(myPref.getBoolean("theme", false)) { // dark theme
                    if(b)
                        save_id.setTextColor(getResources().getColor(R.color.myBlue));
                    else
                        save_id.setTextColor(getResources().getColor(R.color.myWhite));
                }
            }
        });

        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                /*editor.putBoolean("auto_login", b);
                editor.apply();*/

                save_id.setChecked(b);

                if(myPref.getBoolean("theme", false)) { // dark theme
                    if(b)
                        auto_login.setTextColor(getResources().getColor(R.color.myBlue));
                    else
                        auto_login.setTextColor(getResources().getColor(R.color.myWhite));
                }
            }
        });
    }

    //인터넷 연결해서 로그인 체크
    private void loginCheck(String id, String pw) {
        if(id.trim().length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.need_id, Toast.LENGTH_SHORT).show();
            return;
        } else if(pw.trim().length() == 0) {
            Toast.makeText(LoginActivity.this, R.string.need_password, Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit;
        APIService apiService;

        retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

        Call<UserLogin> comment = apiService.doLogin(id,pw);
        Log.i("id/pw : ",id+"  "+pw);
        comment.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                if(response.isSuccessful()){
                    UserLogin res = response.body();
                    if(res.toString()=="fail"){
                        Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        User user = new User();
                        user.setKey(res.getCompanyID());
                        user.setName(res.getCompanyName());
                        Map<String, String> recipeList= new LinkedHashMap<String, String>();
                        recipeList.put("d23","ketchap");
                        recipeList.put("d11","hamburger");
                        user.setRecipe(recipeList);
                        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                        app.setUser(user);


                        // set preferences for auto-login or id save
                        SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPref.edit();

                        editor.putBoolean("save_id_login", save_id.isChecked());
                        editor.putBoolean("auto_login", auto_login.isChecked());
                        editor.putString("companyName", res.getCompanyName());
                        editor.putString("CID",res.getCompanyID());
                        editor.putString("email", res.getEmail());
                        editor.putString("address", res.getAddress());

                        editor.apply();

//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        startActivity(new Intent(LoginActivity.this, MyPageActivity.class));
                        finish();
                    }
                } else{
                    Log.i("Test1", "fail");
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Log.i("Test1", "onfailure");
                t.printStackTrace();
                Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
