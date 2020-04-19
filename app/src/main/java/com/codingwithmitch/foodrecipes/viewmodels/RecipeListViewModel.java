package com.codingwithmitch.foodrecipes.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;
import com.codingwithmitch.foodrecipes.util.Resource.Status;
import java.util.List;

/** Responsible for retrieving, holding, and displaying recipes */
public class RecipeListViewModel extends AndroidViewModel {

  private static final String TAG = "RecipeListViewModel";
  public static final String QUERY_EXHAUSTED = "No more results";

  public enum ViewState {
    CATEGORIES,
    RECIPES
  }

  private final RecipeRepository recipeRepository;
  private MutableLiveData<ViewState> viewState;
  private final MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

  // Query extras
  private boolean isQueryExhausted;
  private boolean isPerformingQuery;
  private int pageNumber;
  private String query;
  private boolean cancelRequest;
  private long requestStartTime;

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

  public int getPageNumber() {
    return pageNumber;
  }

  public void setIsViewingCategories() {
    viewState.setValue(ViewState.CATEGORIES);
  }

  public void searchRecipesApi(String query, int pageNumber) {
    if (!isPerformingQuery) {
      if (pageNumber == 0) {
        pageNumber = 1;
      }
      this.pageNumber = pageNumber;
      this.query = query;
      isQueryExhausted = false;
      executeSearch();
    }
  }

  public void searchNextPage() {
    if (!isQueryExhausted && !isPerformingQuery) {
      pageNumber++;
      executeSearch();
    }
  }

  private void executeSearch() {
    requestStartTime = System.currentTimeMillis();
    cancelRequest = false;
    isPerformingQuery = true;
    viewState.setValue(ViewState.RECIPES);
    final LiveData<Resource<List<Recipe>>> repositorySource =
        recipeRepository.searchRecipesApi(query, pageNumber);

    recipes.addSource(
        repositorySource,
        new Observer<Resource<List<Recipe>>>() {

          @Override
          public void onChanged(Resource<List<Recipe>> listResource) {
            if (cancelRequest) {
              recipes.removeSource(repositorySource);
              return;
            }

            if (listResource != null) {
              recipes.setValue(listResource);
              if (listResource.status == Status.SUCCESS) {
                Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000);
                isPerformingQuery = false;
                if (listResource.data != null) {
                  if (listResource.data.size() == 0) {
                    Log.d(TAG, "onChanged: query is exhausted...");
                    recipes.setValue(
                        new Resource<>(
                            Status.ERROR, listResource.data, QUERY_EXHAUSTED));
                  }
                }
                recipes.removeSource(repositorySource);
              } else if (listResource.status == Status.ERROR) {
                isPerformingQuery = false;
                recipes.removeSource(repositorySource);
              }
            } else {
              /* Remove the source or else the observer will continue observing and
              you'll get duplicates printed out to the onChanged method */
              recipes.removeSource(repositorySource);
            }
          }
        });
  }

  public void cancelSearchRequest() {
    if (isPerformingQuery) {
      Log.d(TAG, "cancelSearchRequest: canceling the search request");
      cancelRequest = true;
      isPerformingQuery = false;
      pageNumber = 1;
    }
  }
}
