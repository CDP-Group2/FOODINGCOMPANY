package com.fooding.companyapp;


import com.fooding.companyapp.data.model.Ingredient;
import com.fooding.companyapp.data.model.Recipe;
import com.fooding.companyapp.data.model.UserLogin;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    public static final String API_URL = "http://poerty.co.kr/fooding/";

    //음식 key 가지고 해당 음식 재료 정보 가져오기
    @GET("getIngredient.php")
    Call<List<Ingredient>>getIngredient(@Query("key") String key);

    @GET("Ingredientsid.php")
    Call<Ingredient>getIngredientInfo(@Query("key") String key);

    //사업자명과 음식 이름과 재료들 가지고 레시피 생성하기
    @POST("makeRecipee.php")
    Call<ResponseBody> makeRecipe(@Query("companyID") String companyID, @Query("recipeName") String recipeName, @Query("ingredientList[]") ArrayList<String> ingredientList);

    //사업자번호로 해당 사업자 레시피 리스트 가져오기
    @GET("getRecipe2.php")
    Call<List<Recipe>>getRecipe(@Query("companyID") String companyID);

    //로그인
    //추후
    @GET("doLogin.php")
    Call<UserLogin>doLogin(@Query("ID") String ID, @Query("password") String password);

    @GET("doRegister.php")
    Call<ResponseBody>doRegister(@Query("CNAME") String CNAME, @Query("ID") String ID, @Query("password") String password);

    //auto complete search
    @GET("searchIngredient.php")
    Call<List<Ingredient>>searchIngredient(@Query("searchText") String searchText);
}
