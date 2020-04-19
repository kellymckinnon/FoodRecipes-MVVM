package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

public class RecipeViewModel extends AndroidViewModel {

  private final RecipeRepository mRecipeRepository;

  public RecipeViewModel(@NonNull Application application) {
    super(application);
    mRecipeRepository = RecipeRepository.getInstance(application);
  }

  public LiveData<Resource<Recipe>> searchRecipesApi(String recipeId) {
    return mRecipeRepository.searchRecipesApi(recipeId);
  }
}
