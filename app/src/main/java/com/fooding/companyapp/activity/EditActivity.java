package com.fooding.companyapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cglab on 2017-12-05.
 */

public class EditActivity extends Activity {
    @BindView(R.id.submit_button)
    Button submit_button;
    @BindView(R.id.login_id_edittext)
    EditText id_text;
    @BindView(R.id.login_pw_edittext) EditText pw_text;
    @BindView(R.id.companyname_edittext) EditText cName_text;
    @BindView(R.id.companyname_english) EditText cName_english_text;
    @BindView(R.id.login_pw_check) EditText pw_check_text;
    @BindView(R.id.email) EditText email_text;
    @BindView(R.id.address) EditText address_text;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.text1) TextView text1;
    @BindView(R.id.text2) TextView text2;
    @BindView(R.id.text3) TextView text3;
    @BindView(R.id.text4) TextView text4;
    @BindView(R.id.text5) TextView text5;
    @BindView(R.id.text6) TextView text6;
    @BindView(R.id.text7) TextView text7;


    private String id;
    private String pw;
    private String cname;
    private String email;
    private String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        text1.setText("아이디");
        text2.setTypeface(fontK);
        text3.setTypeface(fontK);
        text4.setTypeface(fontK);
        text5.setTypeface(fontK);
        text6.setTypeface(fontK);
        text7.setTypeface(fontK);

        id_text.setTypeface(fontK);
        pw_text.setTypeface(fontK);
        cName_text.setTypeface(fontK);
        cName_english_text.setTypeface(fontK);
        pw_check_text.setTypeface(fontK);
        email_text.setTypeface(fontK);
        address_text.setTypeface(fontK);
        //기존 정보
        SharedPreferences setPref = getSharedPreferences("settings", MODE_PRIVATE);
        id_text.setText(setPref.getString("id",""));
        id_text.setFocusable(false);
        email_text.setText(setPref.getString("email",""));
        cName_text.setText(setPref.getString("companyName",""));
        cName_english_text.setText(setPref.getString("companyEnglishName",""));
        address_text.setText(setPref.getString("address",""));


        submit_button.setTypeface(fontKB);
        submit_button.setText("정 보 수 정");
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.registerActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            // change text color
            title.setTextColor(Color.parseColor("#ffffff"));
            text1.setTextColor(Color.parseColor("#ffffff"));
            text2.setTextColor(Color.parseColor("#ffffff"));
            text3.setTextColor(Color.parseColor("#ffffff"));
            text4.setTextColor(Color.parseColor("#ffffff"));
            text5.setTextColor(Color.parseColor("#ffffff"));
            text6.setTextColor(Color.parseColor("#ffffff"));
            text7.setTextColor(Color.parseColor("#ffffff"));
            id_text.setTextColor(Color.parseColor("#ffffff"));
            pw_text.setTextColor(Color.parseColor("#ffffff"));
            cName_text.setTextColor(Color.parseColor("#ffffff"));
            cName_english_text.setTextColor(Color.parseColor("#ffffff"));
            pw_check_text.setTextColor(Color.parseColor("#ffffff"));
            email_text.setTextColor(Color.parseColor("#ffffff"));
            address_text.setTextColor(Color.parseColor("#ffffff"));

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /*************************************************************************************************************/

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cname = cName_text.getText().toString();
                id = id_text.getText().toString();
                pw = pw_text.getText().toString();
                email = email_text.getText().toString();
                address = address_text.getText().toString();
                //로그인 체크
                if(submitCheck(cname,id,pw,email,address) == 0)
                    finish();
            }
        });

    }
    private int submitCheck(final String cname, String id, String pw, String email, String address) {
        if(id.trim().length() == 0) {
            Toast.makeText(EditActivity.this, R.string.need_id, Toast.LENGTH_SHORT).show();
            return -1;
        } /*else if(pw.trim().length() == 0) {
            Toast.makeText(this, R.string.need_password, Toast.LENGTH_SHORT).show();
            return -1;
        }*/ else if(cname.trim().length() == 0) {
            Toast.makeText(EditActivity.this, R.string.need_cname, Toast.LENGTH_SHORT).show();
            return -1;
        } else if(!pw_text.getText().toString().equals(pw_check_text.getText().toString())) {
            Toast.makeText(EditActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
            return -1;
        }

        SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();

        editor.putString("companyName", cName_text.getText().toString());
        editor.putString("companyEnglishName", cName_english_text.getText().toString());
        if(email_text.getText().toString().length() == 0)
            editor.putString("email", null);
        else
            editor.putString("email", email_text.getText().toString());
        if(address_text.getText().toString().length() == 0)
            editor.putString("address", null);
        else
            editor.putString("address", address_text.getText().toString());
        editor.apply();

        Retrofit retrofit;
        APIService apiService;

        retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(APIService.class);

        Call<ResponseBody> comment = apiService.doRegister(cname,id,pw,email,address);
        Log.i("id/pw : ",id+"  "+pw);
        comment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("response",response.body().toString());
                if(response.isSuccessful()){
                    ResponseBody res = response.body();
                    if(res.toString()=="fail"){
                        Toast.makeText(EditActivity.this, R.string.modify_failed, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(EditActivity.this, R.string.modify_success, Toast.LENGTH_SHORT).show();
                        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
                        app.getUser().setName(cname);
                        startActivity(new Intent(EditActivity.this, MyPageActivity.class));
                        finish();
                    }
                } else{
                    Log.i("Test1", "fail");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Test1", "onfailure");
                t.printStackTrace();
                Toast.makeText(EditActivity.this, R.string.modify_failed, Toast.LENGTH_SHORT).show();
            }
        });

        return 0;

    }
}
