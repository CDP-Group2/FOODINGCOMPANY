package com.fooding.companyapp.data;


import java.util.LinkedHashMap;
import java.util.Map;

public class Food {
    private String name="";
    private Map<String, String> ingredient=new LinkedHashMap<String, String>();
    private Map<String, Integer> ingredientAmount=new LinkedHashMap<String, Integer>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getIngredient(){ return ingredient; }
    public Map<String, Integer> getIngredientAmount(){ return ingredientAmount; }

    public void setIngredient(Map<String, String> nIngredient){
        this.ingredient=new LinkedHashMap<String,String>();
        this.ingredient.putAll(nIngredient);
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