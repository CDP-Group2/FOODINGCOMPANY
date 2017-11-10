package com.fooding.companyapp.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.nio.charset.Charset;

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

         mNfc.getDefaultAdapter(this) ;
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
        Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (mTag != null)
            Toast.makeText(this,mTag.toString(),Toast.LENGTH_SHORT);
    }

    public void setReadTagData(NdefMessage ndefmsg) {
        if (ndefmsg == null) {
            return;
        }
        String msgs = "";
        msgs += ndefmsg.toString() + "\n";
        NdefRecord[] records = ndefmsg.getRecords();
        for (NdefRecord rec : records) {
            byte[] payload = rec.getPayload();
            String textEncoding = "UTF-8";
            if (payload.length > 0)
                textEncoding = (payload[0] & 0200) == 0 ? "UTF-8" : "UTF-16";

            Short tnf = rec.getTnf();
            String type = String.valueOf(rec.getType());
            String payloadStr = new String(rec.getPayload(), Charset.forName(textEncoding));
        }
    }
}
