package com.fooding.companyapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fooding.companyapp.FoodingCompanyApplication;
import com.fooding.companyapp.R;
import com.fooding.companyapp.data.Food;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewRecipeActivity extends AppCompatActivity {
    @BindView(R.id.toSendOutButton) Button toSendOutBtn;
//    @BindView(R.id.toHomeButton) Button toHomeBtn;
    @BindView(R.id.title) TextView titleText;
//    @BindView(R.id.recipeName) TextView recipeNameText;
    @BindView(R.id.ingredientList) ListView ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ButterKnife.bind(this);

        FoodingCompanyApplication app = FoodingCompanyApplication.getInstance();
        Food food = app.getCurrentFood();
        String rName=food.getName();
        Map<String, String> FoodIngredients = food.getIngredient();

        final ArrayList<String> ingredients = new ArrayList<String>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredients) ;
        ingredientList.setAdapter(adapter);

//        recipeNameText.setText(rName);
        Log.i("beforeSetText",app.getCurrentFood().getName());
        titleText.setText(rName);
        Log.i("afterSetText",app.getCurrentFood().getName());

        Iterator<String> iterator = FoodIngredients.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            ingredients.add(FoodIngredients.get(key));
        }
        adapter.notifyDataSetChanged();

        toSendOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> ListItems = new ArrayList<>();
                ListItems.add("QR code");
                ListItems.add("NFC tag");
                final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("AlertDialog Title");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int pos) {
                        switch (pos){
                            case 0:
                                Intent QRintent = new Intent(ViewRecipeActivity.this, SendOutQRActivity.class);
                                Log.i("intent to qr",FoodingCompanyApplication.getInstance().getCurrentFood().getName());
                                startActivity(QRintent);
                                finish();
                                break;
                            case 1:
                                Intent NFCintent = new Intent(ViewRecipeActivity.this, SendOutNFCActivity.class);
                                Log.i("intent to nfc",FoodingCompanyApplication.getInstance().getCurrentFood().getName());
                                startActivity(NFCintent);
                                finish();
                                break;
                        }

                    }
                });
                builder.show();
            }
        });
        /*toHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewRecipeActivity.this, MainActivity.class));
                finish();
            }
        });*/
    }
}