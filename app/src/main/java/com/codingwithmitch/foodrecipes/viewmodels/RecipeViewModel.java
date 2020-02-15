package com.codingwithmitch.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel {

  private RecipeRepository mRecipeRepository;

  public RecipeViewModel() {
    mRecipeRepository = RecipeRepository.getInstance();
  }

  public LiveData<Recipe> getRecipe() {
    return mRecipeRepository.getRecipe();
  }

  public void searchRecipeById(String recipeId) {
    mRecipeRepository.searchRecipeById(recipeId);
  }
}
