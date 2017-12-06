package com.fooding.companyapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Range;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.github.channguyen.rsv.RangeSliderView;

import java.lang.reflect.Type;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    private RangeSliderView textBoldness;
    private RangeSliderView textSize;
    private LinearLayout textBoldnessCaption;
    private LinearLayout textSizeCaption;
    @BindView(R.id.autoLoginCaption) TextView autoLoginCaption;
    @BindView(R.id.themeCaption) TextView themeCaption;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.textBoldnessTitle) TextView textBoldnessTitle;
    @BindView(R.id.textSizeTitle) TextView textSizeTitle;
    @BindView(R.id.etcTitle) TextView etcTitle;
    @BindView(R.id.makeMenu) ImageButton makeMenuBtn;
    @BindView(R.id.logout) ImageButton logoutBtn;
    @BindView(R.id.myPage) ImageButton myPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        textBoldnessCaption = (LinearLayout)findViewById(R.id.textBoldnessCaption);
        textSizeCaption = (LinearLayout)findViewById(R.id.textSizeCaption);

        /*************************************************************************************************************/
        // font setting
        final FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        final SharedPreferences myPref = app.getMyPref();

        final String pathT = myPref.getString("titleFont", "none");
        Log.i("pathT", pathT);
        Typeface font = Typeface.createFromAsset(getAssets(), pathT);
        Log.i("foo", font.toString());
        title.setTypeface(font);

        final String pathK = myPref.getString("koreanFont", "none");
        Typeface fontK = Typeface.createFromAsset(getAssets(), pathK);
        TextView tv;
        for(int i = 0; i < textBoldnessCaption.getChildCount(); i++) {
            View view = textBoldnessCaption.getChildAt(i);
            if(view instanceof TextView) {
                tv = (TextView)view;
                tv.setTypeface(fontK);
            }
        }
        for(int i = 0; i < textSizeCaption.getChildCount(); i++) {
            View view = textSizeCaption.getChildAt(i);
            if(view instanceof TextView) {
                tv = (TextView)view;
                tv.setTypeface(fontK);
            }
        }
        autoLoginCaption.setTypeface(fontK);
        themeCaption.setTypeface(fontK);

        final String pathKB = myPref.getString("boldKoreanFont", "none");
        Typeface fontKB = Typeface.createFromAsset(getAssets(), pathKB);
        textBoldnessTitle.setTypeface(fontKB);
        textSizeTitle.setTypeface(fontKB);
        etcTitle.setTypeface(fontKB);
        /*************************************************************************************************************/

        /*************************************************************************************************************/
        // theme setting
        if(myPref.getBoolean("theme", false)) { // dark theme
            // change background
            final View root = findViewById(R.id.settingsActivity).getRootView();
//            root.setBackgroundColor(Color.parseColor("#000000"));
            root.setBackgroundResource(R.drawable.dark_theme_background);

            // change text color
            title.setTextColor(Color.parseColor("#ffffff"));
            for(int i = 0; i < textBoldnessCaption.getChildCount(); i++) {
                View view = textBoldnessCaption.getChildAt(i);
                if(view instanceof TextView) {
                    tv = (TextView)view;
                    tv.setTextColor(Color.parseColor("#ffffff"));
                }
            }
            for(int i = 0; i < textSizeCaption.getChildCount(); i++) {
                View view = textSizeCaption.getChildAt(i);
                if(view instanceof TextView) {
                    tv = (TextView)view;
                    tv.setTextColor(Color.parseColor("#ffffff"));
                }
            }
            autoLoginCaption.setTextColor(Color.parseColor("#ffffff"));
            themeCaption.setTextColor(Color.parseColor("#ffffff"));
            textBoldnessTitle.setTextColor(Color.parseColor("#ffffff"));
            textSizeTitle.setTextColor(Color.parseColor("#ffffff"));
            etcTitle.setTextColor(Color.parseColor("#ffffff"));

            // change buttons
            makeMenuBtn.setImageResource(R.mipmap.compose_white);
            logoutBtn.setImageResource(R.mipmap.exit_white);
            myPageBtn.setImageResource(R.mipmap.user_white);

            // change dividing lines
            View tmp = findViewById(R.id.title_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.menu_bar);
            tmp.setBackgroundColor(Color.parseColor("#ffffff"));
            tmp = findViewById(R.id.tmp1);
            tmp.setBackgroundColor(Color.parseColor("#ececec"));
            tmp = findViewById(R.id.tmp2);
            tmp.setBackgroundColor(Color.parseColor("#ececec"));
        }
        /*************************************************************************************************************/

        textBoldness = (RangeSliderView)findViewById(R.id.textBoldness);
        final int userBoldness = myPref.getInt("fontBoldness", 1);
        textBoldness.setInitialIndex(userBoldness);

        textBoldness.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                Log.i("Font Boldness", Integer.toString(index));

                SharedPreferences.Editor editor = myPref.edit();
                editor.putInt("fontBoldness", index);

                switch (index) {
                    case 0:
                        editor.putString("listViewFont", "fonts/NanumSquareRoundOTFL.otf");
                        break;
                    case 1:
                        editor.putString("listViewFont", "fonts/NanumSquareRoundOTFR.otf");
                        break;
                    case 2:
                        editor.putString("listViewFont", "fonts/NanumSquareRoundOTFB.otf");
                        break;
                    case 3:
                        editor.putString("listViewFont", "fonts/NanumSquareRoundOTFEB.otf");
                        break;
                }

                editor.apply();
                /*final int temp = myPref.getInt("fontBoldness", 5);
                Toast.makeText(getApplicationContext(), Integer.toString(temp), Toast.LENGTH_SHORT).show();*/
            }
        });

        textSize = (RangeSliderView)findViewById(R.id.textSize);
        final int userSize = myPref.getInt("fontSize", 16);
        textSize.setInitialIndex((userSize - 12) / 2);

        textSize.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                Log.i("Text Size", Integer.toString(index));

                SharedPreferences.Editor editor = myPref.edit();
                editor.putInt("fontSize", index * 2 + 12);
                editor.apply();

                /*final int temp = myPref.getInt("fontSize", 5);
                Toast.makeText(getApplicationContext(), Integer.toString(temp), Toast.LENGTH_SHORT).show();*/
            }
        });

        SwitchCompat themeSwitch = (SwitchCompat)findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(myPref.getBoolean("theme", false));
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };
        int[] trackColors = new int[] {
                getResources().getColor(R.color.gray),
                getResources().getColor(R.color.myBlueAlpha),
        };
        themeSwitch.setTrackTintList(new ColorStateList(states, trackColors));

        themeSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("theme", b);
                editor.apply();

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

//                Toast.makeText(getApplicationContext(), Boolean.toString(myPref.getBoolean("theme", false)), Toast.LENGTH_SHORT).show();
            }
        });

        final SwitchCompat autoLoginSwitch = (SwitchCompat) findViewById(R.id.autoLoginSwitch);
        autoLoginSwitch.setChecked(myPref.getBoolean("auto_login", false));
        autoLoginSwitch.setTrackTintList(new ColorStateList(states, trackColors));

        autoLoginSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", b);
                editor.apply();
            }
        });

        makeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MakeRecipeActivity.class));
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(SettingsActivity.this, ViewRecipeActivity.class));
                finish();*/
                Toast.makeText(SettingsActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
                /*editor.putString("id", null);
                editor.putString("password", null);*/
                editor.apply();

                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, MyPageActivity.class));
                finish();
            }
        });
    }
}
