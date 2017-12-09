package com.fooding.companyapp.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;

import java.io.IOException;
import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cglab on 2017-11-12.
 */

public class SendOutNFCActivity extends AppCompatActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.setting)
    ImageButton settingBtn;
    @BindView(R.id.myPage)
    ImageButton myPageBtn;
    @BindView(R.id.logout)
    ImageButton logoutBtn;
    @BindView(R.id.makeMenu)
    ImageButton makeMenuBtn;
    @BindView(R.id.msg)
    TextView msg;
    @BindView(R.id.nfc)
    ImageView nfc;
    NfcAdapter mNfc;
    PendingIntent pIntent;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_out_nfc);
        ButterKnife.bind(this);
        code = getIntent().getStringExtra("code");
//        code = "R"+code;
        Log.i("codeString(NFC)", code);

        /*************************************************************************************************************/
        // font setting
        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        SharedPreferences myPref = app.getMyPref();
        // Toast.makeText(getApplicationContext(), fontSP.getString("titleFont", "none"), Toast.LENGTH_SHORT).show();
        final String path = myPref.getString("titleFont", "none");
        // Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();

        Typeface font = Typeface.createFromAsset(getAssets(), path);
        title.setTypeface(font);
        final String pathK = myPref.getString("koreanFont", "none");
        Typeface fontK = Typeface.createFromAsset(getAssets(), pathK);
        msg.setTypeface(fontK);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.sendOutNFCActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            // change text color
            title.setTextColor(Color.parseColor("#ffffff"));
            msg.setTextColor(getResources().getColor(R.color.myWhite));

            // change buttons
            settingBtn.setImageResource(R.mipmap.settings_white);
            myPageBtn.setImageResource(R.mipmap.user_white);
            logoutBtn.setImageResource(R.mipmap.exit_white);
            makeMenuBtn.setImageResource(R.mipmap.compose_white);

            // change image
            nfc.setImageResource(R.mipmap.noun_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.menu_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /*************************************************************************************************************/

        final ImageView nfc_iv = (ImageView)findViewById(R.id.nfc);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        nfc_iv.startAnimation(anim);
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

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutNFCActivity.this, SettingsActivity.class));
//                finish();
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutNFCActivity.this, MyPageActivity.class));
//                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SendOutNFCActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
                editor.putString("password", null);
                editor.apply();

                startActivity(new Intent(SendOutNFCActivity.this, LoginActivity.class));
                finish();
            }
        });

        makeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutNFCActivity.this, MakeRecipeActivity.class));
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
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(), null, code.getBytes());
        NdefMessage textMessage = new NdefMessage(new NdefRecord[]{textRecord});
        Ndef ndef = Ndef.get(mTag);
        try {
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(getApplicationContext(),"that tag is read_only!",Toast.LENGTH_SHORT).show();
                }
                if (ndef.getMaxSize()<textMessage.toByteArray().length) {
                    Toast.makeText(getApplicationContext(),"oversize message",Toast.LENGTH_SHORT).show();
                }
                ndef.writeNdefMessage(textMessage);
                Toast.makeText(getApplicationContext(),"write done.",Toast.LENGTH_SHORT).show();
            } else {
                NdefFormatable format = NdefFormatable.get(mTag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(textMessage);
                        Toast.makeText(getApplicationContext(),"overwrite done.",Toast.LENGTH_SHORT).show();
                        return;
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"overwrite failed.",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"tag doesn't support NDEF.",Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

}
