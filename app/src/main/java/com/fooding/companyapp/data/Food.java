package com.fooding.companyapp.data;


import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class Food {
    private String name="";
    private String id ="";

    private Map<String, String> ingredient=new LinkedHashMap<String, String>();
    private Map<String, Integer> ingredientAmount=new LinkedHashMap<String, Integer>();
    private Map<String, String> ingredientName = new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        Log.i("setName",name);
    }
    public void setID(String id){
        this.id = id;
    }
    public String getID(){ return id; }

    public Map<String, String> getIngredient(){ return ingredient; }
    public Map<String, Integer> getIngredientAmount(){ return ingredientAmount; }

    public void setIngredient(Map<String, String> nIngredient){
        this.ingredient=new LinkedHashMap<String,String>();
        this.ingredient.putAll(nIngredient);
    }

    public void setIngredientName(Map<String, String> nIngredient){
        this.ingredientName=new LinkedHashMap<String,String>();
        this.ingredientName.putAll(nIngredient);
    }

    public Map<String, String> getIngredientName() {
        return ingredientName;
    }

    public void setIngredient(Map<String, String> nIngredient, Map<String, Integer> nIngredientAmount){
        this.ingredient=new LinkedHashMap<String,String>();
        this.ingredient.putAll(nIngredient);

        this.ingredientAmount=new LinkedHashMap<String,Integer>();
        this.ingredientAmount.putAll(nIngredientAmount);
    }

    public void addIngredient(String key, String name) {
        this.ingredient.put(key, name);
    }

    //여따가 서버연결 코드
}