package com.fooding.companyapp.activity;

import android.content.Intent;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.HttpConnection;
import com.fooding.companyapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SendOutQRActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private HttpConnection httpConn = HttpConnection.getInstance();
    public String responseString="";
    private Bitmap bitmap=null;
    @BindView(R.id.setting) ImageButton settingBtn;
    @BindView(R.id.makeMenu) ImageButton makeMenuBtn;
    @BindView(R.id.logout) ImageButton logoutBtn;
    @BindView(R.id.myPage) ImageButton myPageBtn;
    @BindView(R.id.QRView) ImageView QRimage;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.downloadCaption) TextView downloadCaption;
    @BindView(R.id.shareCaption) TextView shareCaption;
    @BindView(R.id.recipeName) TextView recipeName;
//    @BindView(R.id.toHomeButton) Button toHomeBtn;
    @OnClick(R.id.share_button) void QRcode_Share() {
        if(bitmap!=null) {
            try {

                File cachePath = new File(getCacheDir(), "images");
                cachePath.mkdirs(); // don't forget to make the directory
                FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            File imagePath = new File(getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(this, "com.fooding.companyapp.fileprovider", newFile);
            if (contentUri != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
            }
        }
    }
    @OnClick(R.id.save_button) void QRcode_Save() {
        try {
            if (bitmap != null) {
                FileOutputStream stream;
                long now;
                Date date;
                now = System.currentTimeMillis();
                date = new Date(now);
                SimpleDateFormat sdfnow = new SimpleDateFormat("yyyyMMddHHmmss");
                String strnow = sdfnow.format(date);
                String folder = Environment.getExternalStorageDirectory().getAbsolutePath()
                     + "/DCIM/FOODING";
                String path = folder + File.separator + strnow + ".png";
                File FolderPath = new File(folder);
                if (!FolderPath.exists()) {
                    FolderPath.mkdirs();
                    Log.d("MKDIR", folder);
                }
                stream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                stream.close();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+path)));
                Toast.makeText(this,"Saved.",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_out_qr);
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

        recipeName.setTypeface(fontKB);
        downloadCaption.setTypeface(fontK);
        shareCaption.setTypeface(fontK);
        /*************************************************************************************************************/

        String recipeNameText= app.getCurrentFood().getName();
        recipeName.setText(recipeNameText);


        //intent로 원문 받아오기 - food로 받아와도 됨
        String codeString = getIntent().getStringExtra("Code");
        codeString = FoodingCompanyApplication.getInstance().getCurrentFood().getID();
        codeString = "R"+codeString;
        /////////
        //writer 셋업
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        try {
            //QRcode 생성
            BitMatrix bitMatrix = barcodeWriter.encode(codeString, BarcodeFormat.QR_CODE,1000,1000);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(bitMatrix);
            QRimage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (this.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    this.requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
        /*toHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutQRActivity.this, MainActivity.class));
                finish();
            }
        });*/

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutQRActivity.this, SettingsActivity.class));
                finish();
            }
        });

        makeMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutQRActivity.this, MakeRecipeActivity.class));
                finish();
            }
        });

        myPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutQRActivity.this, MyPageActivity.class));
                finish();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*startActivity(new Intent(MyPageActivity.this, ViewRecipeActivity.class));
                finish();*/
                Toast.makeText(SendOutQRActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();

                SharedPreferences myPref = getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();

                editor.putBoolean("auto_login", false);
                editor.putString("id", null);
                editor.putString("password", null);
                editor.apply();

                startActivity(new Intent(SendOutQRActivity.this, LoginActivity.class));
                finish();
            }
        });
    }


   public void onRequestPermissionsResult(int requestCode,
                                          String permissions[], int[] grantResults) {
       switch (requestCode) {
           case 1: {
               // If request is cancelled, the result arrays are empty.
               if (grantResults.length > 0
                       && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // permission granted
               } else {
                   // permission denied, boo! Disable the
                   // functionality that depends on this permission.
               }
               return;
           }
           // other 'case' lines to check for other
           // permissions this app might request
       }
   }

}
