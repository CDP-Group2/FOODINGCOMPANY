package com.fooding.companyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fooding.companyapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewRecipeActivity extends AppCompatActivity {
    @BindView(R.id.sendout)
    Button sendoutbutton;
    @BindView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ButterKnife.bind(this);

        sendoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, SendOutQRActivity.class));
                finish();
            }
        });
    }
}