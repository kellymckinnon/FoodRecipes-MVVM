package com.codingwithmitch.foodrecipes.requests;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Responsible for making network requests */
public class RecipeApiClient {

  private static final String TAG = "RecipeApiClient";
  private static RecipeApiClient instance;

  private final MutableLiveData<List<Recipe>> mRecipes;
  private final MutableLiveData<Recipe> mRecipe;

  public static RecipeApiClient getInstance() {
    if (instance == null) {
      instance = new RecipeApiClient();
    }

    return instance;
  }

  private RecipeApiClient() {
    mRecipes = new MutableLiveData<>();
    mRecipe = new MutableLiveData<>();
  }

  public LiveData<List<Recipe>> getRecipes() {
    return mRecipes;
  }

  public LiveData<Recipe> getRecipe() {
    return mRecipe;
  }

  public void searchRecipesApi(String query, final int pageNumber) {
//    ServiceGenerator.getRecipeApi()
//        .searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber))
//        .enqueue(
//            new Callback<RecipeSearchResponse>() {
//              @Override
//              public void onResponse(
//                  Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
//                if (response.code() == 200) {
//                  List<Recipe> list = new ArrayList<>(response.body().getRecipes());
//                  if (pageNumber == 1) {
//                    mRecipes.postValue(list);
//                  } else {
//                    List<Recipe> currentRecipes = mRecipes.getValue();
//                    currentRecipes.addAll(list);
//                    mRecipes.postValue(currentRecipes);
//                  }
//                } else {
//                  String error = null;
//                  try {
//                    error = response.errorBody().string();
//                  } catch (IOException e) {
//                    e.printStackTrace();
//                  }
//                  Log.d(TAG, "onResponse: " + error);
//                  mRecipes.postValue(null);
//                }
//              }
//
//              @Override
//              public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//              }
//            });
  }

  public void searchRecipeById(String recipeId) {
//    ServiceGenerator.getRecipeApi()
//        .getRecipe(Constants.API_KEY, recipeId)
//        .enqueue(
//            new Callback<RecipeResponse>() {
//              @Override
//              public void onResponse(
//                  Call<RecipeResponse> call, Response<RecipeResponse> response) {
//                if (response.code() == 200) {
//                  Recipe recipe = response.body().getRecipe();
//                  mRecipe.postValue(recipe);
//                } else {
//                  String error = null;
//                  try {
//                    error = response.errorBody().string();
//                  } catch (IOException e) {
//                    e.printStackTrace();
//                  }
//                  Log.d(TAG, "onResponse: " + error);
//                  mRecipe.postValue(null);
//                }
//              }
//
//              @Override
//              public void onFailure(Call<RecipeResponse> call, Throwable t) {
//                Log.e(TAG, "onFailure: ", t);
//              }
//            });
  }
}
