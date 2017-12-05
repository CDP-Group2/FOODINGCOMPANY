package com.fooding.companyapp.data;


import java.util.LinkedHashMap;
import java.util.Map;

public class Food {
    private String name="";
    private String id="";
    private Map<String, String> ingredient=new LinkedHashMap<String, String>();

    public String getName() {
        return name;
    }
    public String getID() {return id;}
    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {this.id =id;}
    public Map<String, String> getIngredient(){ return ingredient; }

    public void setIngredient(Map<String, String> nIngredient){
        this.ingredient=new LinkedHashMap<String,String>();
        this.ingredient.putAll(nIngredient);
    }

    public void addIngredient(String key, String name) {
        this.ingredient.put(key, name);
    }

    //여따가 서버연결 코드
}