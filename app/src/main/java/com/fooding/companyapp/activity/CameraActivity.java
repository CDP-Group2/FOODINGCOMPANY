package com.fooding.companyapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class CameraActivity extends AppCompatActivity {
    @BindView(R.id.NFC) Button nfcbutton;
    @BindView(R.id.toSearchButton) Button toSearchBtn;
    @BindView(R.id.title) TextView title;

    private DecoratedBarcodeView barcodeView;
    private String lastText;

    public String results;
    public String resultsID;

    //callback when barcode scanned


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        //set barcode instant****************
        // camera permission for marshmellow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (this.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    this.requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            1);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if(result.getText() == null || result.getText().equals(lastText)) {
                    // Prevent duplicate scans
                    return;
                }

                lastText = result.getText();

                Retrofit retrofit;
                APIService apiService;

                retrofit = new Retrofit.Builder().baseUrl(APIService.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
                apiService = retrofit.create(APIService.class);

                // String serialNumber = result.getText();
                String serialNumber = lastText;
                Call<Ingredient> comment = apiService.getIngredientInfo(serialNumber);
                comment.enqueue(new Callback<Ingredient>() {
                    @Override
                    public void onResponse(Call<Ingredient> call, Response<Ingredient> response) {
                        if(response.isSuccessful()) {
                            // results = "";
                            Log.i("foo","foo");
                            Ingredient temp = response.body();
                            results = temp.getName();
                            resultsID = temp.getId();

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

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
            }
        });

        nfcbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraActivity.this, NFCActivity.class));
                finish();
            }
        });


        toSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraActivity.this,
                        SearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT));
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        resumeScanner();
    }

    protected void resumeScanner() {
        if (!barcodeView.isActivated())
            barcodeView.resume();
    }

    protected void pauseScanner() {
        barcodeView.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseScanner();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
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
