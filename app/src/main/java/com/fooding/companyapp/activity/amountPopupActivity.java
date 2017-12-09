package com.fooding.companyapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;
import com.fooding.companyapp.data.model.Ingredient;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

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

public class amountPopupActivity extends Activity {
    @BindView(R.id.okay) Button ok;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.minus) ImageButton minus;
    @BindView(R.id.plus) ImageButton plus;
    @BindView(R.id.amountGram) EditText amountGram;
    @BindView(R.id.ingredientName) TextView ingreName;

    private String ingredientName;
    private Integer ingredientAmount;
    private Integer ingredientPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_amount_popup);
        ButterKnife.bind(this);

        /*************************************************************************************************************/
        // font setting
        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        SharedPreferences myPref = app.getMyPref();

        /*final String pathT = myPref.getString("titleFont", "none");
        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
        title.setTypeface(font);*/

        final String pathK = myPref.getString("koreanFont", "none");
        Typeface fontK = Typeface.createFromAsset(getAssets(), pathK);
        final String pathKB = myPref.getString("boldKoreanFont", "none");
        Typeface fontKB = Typeface.createFromAsset(getAssets(), pathKB);

        amountGram.setTypeface(fontKB);
        ok.setTypeface(fontKB);
        cancel.setTypeface(fontKB);
        /*************************************************************************************************************/

        Intent intent = getIntent();
        ingredientName = intent.getStringExtra("ingredientName");
        ingredientPS = Integer.parseInt(intent.getStringExtra("ingredientPS"));
        ingredientAmount = Integer.parseInt(intent.getStringExtra("ingredientAmount"));

        Log.i("name",ingredientName);
        Log.i("ps",Integer.toString(ingredientPS));
        Log.i("amoutn",Integer.toString(ingredientAmount));



        ingreName.setText(ingredientName);
        amountGram.setText(Integer.toString(ingredientAmount));

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountGram.setText(""+Integer.toString(Integer.parseInt(amountGram.getText().toString())-10));
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountGram.setText(""+Integer.toString(Integer.parseInt(amountGram.getText().toString())+10));
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();

                returnIntent.putExtra("setIngredient",Integer.toString(ingredientPS));

                returnIntent.putExtra("setIngredientAmount",amountGram.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amountGram.setText(Integer.toString(ingredientAmount));
            }
        });

    }
}
