package com.fooding.companyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NFCActivity extends AppCompatActivity {
    @BindView(R.id.my_page)
    Button my_pagebutton;
    @BindView(R.id.filter)
    Button filterbutton;
    @BindView(R.id.Camera)
    Button camerabutton;
    @BindView(R.id.viewrecipe)
    Button viewrecipebutton;
    @BindView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        ButterKnife.bind(this);

        /****************
         *******************************
         ********************/
        //camera 찍어서 아래처럼 Food 저장 data-Food dir 참고

        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        Food food=new Food();
        String temp="오뚜기 케챱";
        food.setName(temp);
        app.setCurrentFood(food);

        //위처럼 food 정보 저장한다음 서버로 ㄲ 하는 작업 시
        //Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
        //처럼 food 정보 가져올 수 있다

        my_pagebutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent=new Intent(NFCActivity.this, MyPageActivity.class);
                //intent.putExtra("date",Integer.parseInt(date.getText().toString().replaceAll("[^0-9]", "")));
                startActivity(intent);
            }
        });

        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NFCActivity.this, FilterActivity.class));
            }
        });

        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NFCActivity.this, CameraActivity.class));
                finish();
            }
        });

        viewrecipebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NFCActivity.this, ViewRecipeActivity.class));
                finish();
            }
        });
    }
}
