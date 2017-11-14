package com.fooding.companyapp.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hansanghoon on 2017. 11. 14..
 */

public class Recipe {

    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("NAME")
    @Expose
    private String name;

    public String getId() { return id;}

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}