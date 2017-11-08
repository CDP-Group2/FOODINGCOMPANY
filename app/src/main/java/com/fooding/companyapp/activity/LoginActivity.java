package com.fooding.companyapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;
import com.fooding.companyapp.data.User;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends Activity {
    @BindView(R.id.login_button) Button login_button;
    @BindView(R.id.login_id_edittext) EditText id_text;
    @BindView(R.id.login_pw_edittext) EditText pw_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = id_text.getText().toString();
                String pw = pw_text.getText().toString();
                //로그인 체크
                loginCheck(id, pw);
            }
        });
    }

    //인터넷 연결해서 로그인 체크
    private void loginCheck(String id, String pw) {
        if(id.trim().length() == 0) {
            Toast.makeText(this, R.string.need_id, Toast.LENGTH_SHORT).show();
            return;
        } else if(pw.trim().length() == 0) {
            Toast.makeText(this, R.string.need_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if(id.equals("test"))
            Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            //로그인 성공

            User user = new User();
            user.setKey(123);
            user.setName("한상훈");
            Map<String, String> recipeList= new LinkedHashMap<String, String>();
            recipeList.put("d23","ketchap");
            recipeList.put("d11","hamburger");
            user.setRecipe(recipeList);
            FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
            app.setUser(user);

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
