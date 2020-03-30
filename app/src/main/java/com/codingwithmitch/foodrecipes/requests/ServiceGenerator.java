package com.codingwithmitch.foodrecipes.requests;

import static com.codingwithmitch.foodrecipes.util.Constants.BASE_URL;

import androidx.lifecycle.LiveData;
import com.codingwithmitch.foodrecipes.util.LiveDataCallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Holds Retrofit instance and API instance */
public class ServiceGenerator {

  private static final Retrofit.Builder retrofitBuilder =
      new Retrofit.Builder().baseUrl(BASE_URL)
          .addCallAdapterFactory(new LiveDataCallAdapterFactory())
          .addConverterFactory(GsonConverterFactory.create());

  private static final Retrofit retrofit = retrofitBuilder.build();

  private static final RecipeApi recipeApi = retrofit.create(RecipeApi.class);

  public static RecipeApi getRecipeApi() {
    return recipeApi;
  }
}
