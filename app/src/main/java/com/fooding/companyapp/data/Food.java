package com.fooding.companyapp.data;


import java.util.LinkedHashMap;
import java.util.Map;

public class Food {
    private String name="";
    private Map<String, String> ingredient=new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getIngredient(){ return ingredient; }

    public void setIngredient(Map<String, String> nIngredient){
        this.ingredient.putAll(nIngredient);
    }

    public void addIngredient(String key, String name) {
        this.ingredient.put(key, name);
    }

    //여따가 서버연결 코드
}