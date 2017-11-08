package com.fooding.companyapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.fooding.companyapp.HttpConnection;
import com.fooding.companyapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    @OnClick(R.id.share_button) void QRcode_Share() {
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
        else
        {
            if(bitmap!=null) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                share.addCategory(Intent.CATEGORY_DEFAULT);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap, "image", null);
                Uri imageUri =  Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(share, "Share"));
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_out_qr);
        ButterKnife.bind(this);
        //intent로 원문 받아오기 - food로 받아와도 됨
        String codeString = getIntent().getStringExtra("Code");
        /////////
        //writer 셋업
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        try {
            //QRcode 생성
            BitMatrix bitMatrix = barcodeWriter.encode(codeString, BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(bitMatrix);
            QRimage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

   /* private void sendData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                httpConn.requestWebServer("데이터1","데이터2", callback);
            }
        });
    }

    private final Callback callback = new Callback() {
        private String body;
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "콜백오류:"+e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            body = response.body().string();
            Log.d(TAG, "서버에서 응답한 Body:"+body);
        }
    };*/
   public void onRequestPermissionsResult(int requestCode,
                                          String permissions[], int[] grantResults) {
       switch (requestCode) {
           case 1: {
               // If request is cancelled, the result arrays are empty.
               if (grantResults.length > 0
                       && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   if(bitmap!=null) {
                       Intent share = new Intent(Intent.ACTION_SEND);
                       share.setType("image/png");
                       share.addCategory(Intent.CATEGORY_DEFAULT);
                       String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                               bitmap, "image", null);
                       Uri imageUri =  Uri.parse(path);
                       share.putExtra(Intent.EXTRA_STREAM, imageUri);
                       startActivity(Intent.createChooser(share, "Share"));
                   }
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
