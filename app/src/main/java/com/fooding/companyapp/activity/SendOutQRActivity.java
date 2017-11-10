package com.fooding.companyapp.activity;

import android.content.Intent;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

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
    @BindView(R.id.QRView) ImageView QRimage;
    @BindView(R.id.toHomeButton) Button toHomeBtn;
    @OnClick(R.id.share_button) void QRcode_Share() {
        if(bitmap!=null) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/png");
            share.addCategory(Intent.CATEGORY_DEFAULT);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, "image", null);
            Uri imageUri =  Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
            startActivity(Intent.createChooser(share, "Share"));

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
        setContentView(R.layout.activity_send_out_qr);
        ButterKnife.bind(this);
        //intent로 원문 받아오기 - food로 받아와도 됨
        String codeString = getIntent().getStringExtra("Code");
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
        toHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutQRActivity.this, MainActivity.class));
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
