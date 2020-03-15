package com.codingwithmitch.foodrecipes.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import java.util.List;

/** Responsible for retrieving, holding, and displaying recipes */
public class RecipeListViewModel extends ViewModel {

  private final RecipeRepository mRecipeRepository;
  private MutableLiveData<ViewState> mViewState;

  public enum ViewState {
    CATEGORIES,
    RECIPES
  }

  public RecipeListViewModel() {
    mRecipeRepository = RecipeRepository.getInstance();

    if (mViewState == null) {
      mViewState = new MutableLiveData<>();
      mViewState.setValue(ViewState.CATEGORIES);
    }
  }

  public LiveData<ViewState> getViewState() {
    return mViewState;
  }

  public LiveData<List<Recipe>> getRecipes() {
    return mRecipeRepository.getRecipes();
  }

  public void searchRecipesApi(String query, int pageNumber) {
    mRecipeRepository.searchRecipesApi(query, pageNumber);
  }

  public void searchNextPage() {
    if (!mRecipeRepository.isQueryExhausted().getValue()) {
      mRecipeRepository.searchNextPage();
    }
  }
}
