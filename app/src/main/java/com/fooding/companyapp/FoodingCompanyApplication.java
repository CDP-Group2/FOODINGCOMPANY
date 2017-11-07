package com.fooding.companyapp;

import android.app.Application;

import com.fooding.companyapp.data.Food;
import com.fooding.companyapp.data.User;


public class FoodingCompanyApplication extends Application {
    private static FoodingCompanyApplication instance;
    private User user;
    private Food currentFood;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static FoodingCompanyApplication getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Food getCurrentFood() {
        return currentFood;
    }

    public void setCurrentFood(Food currentFood) {
        this.currentFood = currentFood;
    }
}
