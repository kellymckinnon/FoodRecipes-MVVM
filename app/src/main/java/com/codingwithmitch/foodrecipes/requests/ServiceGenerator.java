package com.codingwithmitch.foodrecipes.requests;

import static com.codingwithmitch.foodrecipes.util.Constants.BASE_URL;
import static com.codingwithmitch.foodrecipes.util.Constants.CONNECTION_TIMEOUT;
import static com.codingwithmitch.foodrecipes.util.Constants.READ_TIMEOUT;
import static com.codingwithmitch.foodrecipes.util.Constants.WRITE_TIMEOUT;

import androidx.lifecycle.LiveData;
import com.codingwithmitch.foodrecipes.util.LiveDataCallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Holds Retrofit instance and API instance */
public class ServiceGenerator {

  private static OkHttpClient client = new OkHttpClient.Builder()
      // time it takes for app to establish connection to server
      .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
      // time between each byte read from the server
      .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
      // time between each byte sent to server
      .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
      .retryOnConnectionFailure(false)
      .build();

  private static final Retrofit.Builder retrofitBuilder =
      new Retrofit.Builder().baseUrl(BASE_URL)
          .client(client)
          .addCallAdapterFactory(new LiveDataCallAdapterFactory())
          .addConverterFactory(GsonConverterFactory.create());

  private static final Retrofit retrofit = retrofitBuilder.build();

  private static final RecipeApi recipeApi = retrofit.create(RecipeApi.class);

  public static RecipeApi getRecipeApi() {
    return recipeApi;
  }
}
