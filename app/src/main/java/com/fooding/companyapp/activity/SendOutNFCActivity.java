package com.fooding.companyapp.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    @BindView(R.id.my_page)
    Button my_pagebutton;
    @BindView(R.id.filter)
    Button filterbutton;
    @BindView(R.id.viewrecipe)
    Button viewrecipebutton;
    @BindView(R.id.title)
    TextView title;
    NfcAdapter mNfc;
    PendingIntent pIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_out_nfc);
        ButterKnife.bind(this);

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
                Intent intent=new Intent(SendOutNFCActivity.this, MyPageActivity.class);
                //intent.putExtra("date",Integer.parseInt(date.getText().toString().replaceAll("[^0-9]", "")));
                startActivity(intent);
            }
        });

        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutNFCActivity.this, FilterActivity.class));
            }
        });

        viewrecipebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SendOutNFCActivity.this, ViewRecipeActivity.class));
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
        String codeString = FoodingCompanyApplication.getInstance().getCurrentFood().getID();
        Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(), null, codeString.getBytes());
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
