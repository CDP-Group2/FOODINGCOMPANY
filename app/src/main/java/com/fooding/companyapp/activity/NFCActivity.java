package com.fooding.companyapp.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.APIService;
import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;
import com.fooding.companyapp.data.model.Ingredient;

import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    NfcAdapter mNfc;
    PendingIntent pIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        ButterKnife.bind(this);

        /****************
         *******************************
         ********************/
        //camera 찍어서 아래처럼 Food 저장 data-Food dir 참고

        /*FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        Food food=new Food();
        String temp="오뚜기 케챱";
        food.setName(temp);
        app.setCurrentFood(food);*/
        //위처럼 food 정보 저장한다음 서버로 ㄲ 하는 작업 시
        //Food food = FoodingCompanyApplication.getInstance().getCurrentFood();
        //처럼 food 정보 가져올 수 있다

         mNfc=NfcAdapter.getDefaultAdapter(this) ;
        if (mNfc == null) {
            // NFC 미지원단말
            Toast.makeText(getApplicationContext(), "No NFC on your Device", Toast.LENGTH_SHORT).show();
            finish();
        }
        Intent intent =new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pIntent = PendingIntent.getActivity(this,0,intent,0);


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
    @Override
    protected void onResume() {
        super.onResume();
        if(mNfc!=null)
            mNfc.enableForegroundDispatch(this,pIntent,null,null);
    }

    protected void onPause() {
        super.onPause();
        if (mNfc != null)
            mNfc.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null)
            return;
        Parcelable msg[] = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (msg != null) {
            Log.d("d", "hi");
            String dMsg = setReadTagData((NdefMessage) msg[0]);
            Toast.makeText(this, dMsg, Toast.LENGTH_SHORT).show();

            Retrofit retrofit;
            APIService apiService;

            retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = retrofit.create(APIService.class);

            Call<Ingredient> comment = apiService.getIngredientInfo(dMsg);
            comment.enqueue(new Callback<Ingredient>() {
                @Override
                public void onResponse(Call<Ingredient> call, Response<Ingredient> response) {
                    if(response.isSuccessful()) {
                        // results = "";
                        Log.i("foo","foo");
                        Ingredient temp = response.body();
                        String results = temp.getName();
                        String resultsID = temp.getId();

                        Intent returnIntent = new Intent();
                        // returnIntent.putExtra("addIngredient","fromCamera : " +lastText);
                        returnIntent.putExtra("addIngredient",results.toString());
                        returnIntent.putExtra("addIngredientID",resultsID.toString());
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    } else {
                        Log.i("Get Ingredient Info", "Fail");
                    }
                }

                @Override
                public void onFailure(Call<Ingredient> call, Throwable t) {
                    Log.i("Get Ingredient Info", "On Failure");
                    t.printStackTrace();
                }
            });
        }
    }

    public String setReadTagData(NdefMessage ndefmsg) {
        String strRec =null;
        if (ndefmsg == null) {
            return strRec;
        }
        NdefRecord[] records = ndefmsg.getRecords();
        for (NdefRecord rec : records) {
            byte[] payload = rec.getPayload();
            // 버퍼 데이터를 인코딩 변환
            strRec = byteDecoding(payload);
        }
        return strRec;
    }
    public String byteDecoding(byte[] buf) {
        String strText="";
        String textEncoding = ((buf[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        int langCodeLen = buf[0] & 0077;

        try {
            strText = new String(buf, langCodeLen + 1,
                    buf.length - langCodeLen - 1, textEncoding);
        } catch(Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }
}
