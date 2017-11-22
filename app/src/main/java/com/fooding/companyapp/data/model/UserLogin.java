package com.fooding.companyapp.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hansanghoon on 2017. 11. 14..
 */

public class UserLogin {

    @SerializedName("CID")
    @Expose
    private String companyID;
    @SerializedName("CNAME")
    @Expose
    private String companyName;
    @SerializedName("SUCCESS")
    @Expose
    private String loginSuccess;

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(String loginSuccess) {
        this.loginSuccess = loginSuccess;
    }
}