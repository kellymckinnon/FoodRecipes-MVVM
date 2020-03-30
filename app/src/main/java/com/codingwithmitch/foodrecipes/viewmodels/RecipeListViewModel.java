package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;
import java.util.List;

/** Responsible for retrieving, holding, and displaying recipes */
public class RecipeListViewModel extends AndroidViewModel {

  private final RecipeRepository recipeRepository;
  private MutableLiveData<ViewState> viewState;
  private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

  public enum ViewState {
    CATEGORIES,
    RECIPES
  }

  public RecipeListViewModel(@NonNull Application application) {
    super(application);
    recipeRepository = RecipeRepository.getInstance(application);

    if (viewState == null) {
      viewState = new MutableLiveData<>();
      viewState.setValue(ViewState.CATEGORIES);
    }
  }

  public LiveData<ViewState> getViewState() {
    return viewState;
  }

  public LiveData<Resource<List<Recipe>>> getRecipes() {
    return recipes;
  }

  public void searchRecipesApi(String query, int pageNumber) {
    final LiveData<Resource<List<Recipe>>> repositorySource =
        recipeRepository.searchRecipesApi(query, pageNumber);

    recipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>> () {

      @Override
      public void onChanged(Resource<List<Recipe>> listResource) {
        recipes.setValue(listResource);
      }
    });
  }
}
