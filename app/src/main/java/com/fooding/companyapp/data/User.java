package com.fooding.companyapp.data;


import java.util.LinkedHashMap;
import java.util.Map;

public class User {
    private String key;

    private String name;
    private Map<String, String> recipe = new LinkedHashMap<String, String>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getRecipe(){ return recipe; }

    public void setRecipe(Map<String, String> nRecipe){
        this.recipe=new LinkedHashMap<String,String>();
        this.recipe.putAll(nRecipe);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
