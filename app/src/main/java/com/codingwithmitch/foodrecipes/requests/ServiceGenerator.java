package com.codingwithmitch.foodrecipes.requests;

import static com.codingwithmitch.foodrecipes.util.Constants.BASE_URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Holds Retrofit instance and API instance */
class ServiceGenerator {

  private static final Retrofit.Builder retrofitBuilder =
      new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());

  private static final Retrofit retrofit = retrofitBuilder.build();

  private static final RecipeApi recipeApi = retrofit.create(RecipeApi.class);

  public static RecipeApi getRecipeApi() {
    return recipeApi;
  }
}
