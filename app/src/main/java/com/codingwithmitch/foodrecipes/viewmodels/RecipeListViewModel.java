package com.codingwithmitch.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import java.util.List;

/** Responsible for retrieving, holding, and displaying recipes */
public class RecipeListViewModel extends ViewModel {

  private final RecipeRepository mRecipeRepository;
  private boolean mIsViewingRecipes;

  public RecipeListViewModel() {
    mRecipeRepository = RecipeRepository.getInstance();
  }

  public LiveData<List<Recipe>> getRecipes() {
    return mRecipeRepository.getRecipes();
  }

  public void searchRecipesApi(String query, int pageNumber) {
    mIsViewingRecipes = true;
    mRecipeRepository.searchRecipesApi(query, pageNumber);
  }

  public void searchNextPage() {
    if (mIsViewingRecipes && !mRecipeRepository.isQueryExhausted().getValue()) {
      mRecipeRepository.searchNextPage();
    }
  }

  public boolean isViewingRecipes() {
    return mIsViewingRecipes;
  }

  public void setIsViewingRecipes(boolean isViewingRecipes) {
    mIsViewingRecipes = isViewingRecipes;
  }

  public boolean onBackPressed() {
    if (mIsViewingRecipes) {
      mIsViewingRecipes = false;
      return false;
    }

    return true;
  }
}
