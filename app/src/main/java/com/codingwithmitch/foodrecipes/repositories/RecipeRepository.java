package com.codingwithmitch.foodrecipes.repositories;

import androidx.lifecycle.LiveData;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.RecipeApiClient;
import java.util.List;

public class RecipeRepository {
  private static RecipeRepository instance;
  private final RecipeApiClient mRecipeApiClient;
  private String mQuery;
  private int mPageNumber;

  private RecipeRepository() {
    mRecipeApiClient = RecipeApiClient.getInstance();
  }

  public static RecipeRepository getInstance() {
    if (instance == null) {
      instance = new RecipeRepository();
    }

    return instance;
  }

  public LiveData<List<Recipe>> getRecipes() {
    return mRecipeApiClient.getRecipes();
  }

  public void searchRecipesApi(String query, int pageNumber) {
    if (pageNumber == 0) {
      pageNumber = 1;
    }
    mQuery = query;
    mPageNumber = pageNumber;
    mRecipeApiClient.searchRecipesApi(query, pageNumber);
  }

  public void searchNextPage() {
    searchRecipesApi(mQuery, mPageNumber + 1);
  }
}
