package com.codingwithmitch.foodrecipes.repositories;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.persistence.RecipeDatabase;
import com.codingwithmitch.foodrecipes.requests.ServiceGenerator;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;
import com.codingwithmitch.foodrecipes.util.Resource;
import java.util.List;

public class RecipeRepository {
  private static final String TAG = "RecipeRepository";
  private static RecipeRepository instance;
  private RecipeDao recipeDao;

  private RecipeRepository(Context context) {
    recipeDao = RecipeDatabase.getInstance(context).getRecipeDao();
  }

  public static RecipeRepository getInstance(Context context) {
    if (instance == null) {
      instance = new RecipeRepository(context);
    }

    return instance;
  }

  public LiveData<Resource<List<Recipe>>> searchRecipesApi(
      final String query, final int pageNumber) {
    return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(
        AppExecutors.getInstance()) {

      @Override
      protected void saveCallResult(@NonNull RecipeSearchResponse item) {
        if (item.getRecipes() != null) {
          Recipe[] recipes = new Recipe[item.getRecipes().size()];
          int index = 0;

          for (long rowid : recipeDao.insertRecipes(item.getRecipes().toArray(recipes))) {
            if (rowid == -1) {
              Log.d(TAG, "saveCallResult: CONFLICT... this recipe is already in the cache");
              // if the recipe already exists, I don't want to set the ingredients or timestamp
              // because they will be erased
              Recipe recipe = recipes[index];
              recipeDao.updateRecipe(recipe.getRecipe_id(), recipe.getTitle(), recipe.getPublisher(), recipe.getImage_url(), recipe.getSocial_rank());
            }
          }
        }
      }

      @Override
      protected boolean shouldFetch(@Nullable List<Recipe> data) {
        return true;
      }

      @NonNull
      @Override
      protected LiveData<List<Recipe>> loadFromDb() {
        return recipeDao.searchRecipes(query, pageNumber);
      }

      @NonNull
      @Override
      protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
        return ServiceGenerator.getRecipeApi()
            .searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber));
      }
    }.getAsLiveData();
  }
}
