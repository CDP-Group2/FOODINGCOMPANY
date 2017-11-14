package com.fooding.companyapp;


import com.fooding.companyapp.data.model.Ingredient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    public static final String API_URL = "http://poerty.co.kr/fooding/";

    //음식 key 가지고 해당 음식 재료 정보 가져오기
    @GET("getIngredient.php")
    Call<List<Ingredient>>getIngredient(@Query("key") String key);

    @GET("getIngredientInfo.php")
    Call<Ingredient>getIngredientInfo(@Query("key") String key);

    //사업자명과 음식 이름과 재료들 가지고 레시피 생성하기
    @POST("makeRecipe.php")
    Call<ResponseBody> makeRecipe(@Query("companyID") String companyID, @Query("recipeName") String recipeName, @Query("ingredientList[]") ArrayList<String> ingredientList);

    //사업자번호로 해당 사업자 레시피 리스트 가져오기
    @GET("getRecipe.php")
    Call<ResponseBody>getRecipe(@Query("companyID") String companyID);

    //로그인
    //추후
    @GET("dogs/{name}")
    Call<ResponseBody>getName(@Path("name") String testpath, @Query("query") String testquery);

    //auto complete search
    @GET("searchIngredient.php")
    Call<List<Ingredient>>searchIngredient(@Query("searchText") String searchText);
}
