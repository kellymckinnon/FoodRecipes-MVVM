package com.codingwithmitch.foodrecipes;

import static com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel.QUERY_EXHAUSTED;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.Resource;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel.ViewState;
import java.util.List;

public class RecipeListActivity extends BaseActivity
    implements RecipeRecyclerAdapter.OnRecipeListener {

  private static final String TAG = "RecipeListActivity";

  private RecipeListViewModel mRecipeListViewModel;
  private RecyclerView mRecyclerView;
  private RecipeRecyclerAdapter mRecipeRecyclerAdapter;
  private SearchView mSearchView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe_list);
    mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
    subscribeObservers();

    initRecyclerView();
    initSearchView();
    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
  }

  private void initRecyclerView() {
    mRecyclerView = findViewById(R.id.recipe_list);
    mRecipeRecyclerAdapter = new RecipeRecyclerAdapter();
    mRecipeRecyclerAdapter.setOnRecipeListener(this);
    mRecipeRecyclerAdapter.setGlideRequestManager(getGlideRequestManager());
    mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
    mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(30));
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addOnScrollListener(
        new OnScrollListener() {
          @Override
          public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (!mRecyclerView.canScrollVertically(1)
                && mRecipeListViewModel.getViewState().getValue() == ViewState.RECIPES) {
              mRecipeListViewModel.searchNextPage();
            }
          }
        });
  }

  private RequestManager getGlideRequestManager() {
    Drawable bg = new ColorDrawable(Color.WHITE);
    RequestOptions options = new RequestOptions().placeholder(bg).error(bg);
    return Glide.with(this).setDefaultRequestOptions(options);
  }

  private void initSearchView() {
    mSearchView = findViewById(R.id.search_view);
    mSearchView.setOnQueryTextListener(
        new OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            //            mRecipeRecyclerAdapter.displayLoading();
            mRecyclerView.smoothScrollToPosition(0);
            mRecipeListViewModel.searchRecipesApi(query, 1);
            mSearchView.clearFocus();
            return false;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            return false;
          }
        });
  }

  private void subscribeObservers() {
    mRecipeListViewModel
        .getViewState()
        .observe(
            this,
            new Observer<ViewState>() {
              @Override
              public void onChanged(ViewState viewState) {
                if (viewState != null) {
                  switch (viewState) {
                    case RECIPES:
                      break; // Recipes will show from another observer
                    case CATEGORIES:
                      displaySearchCategories();
                      break;
                  }
                }
              }
            });
    mRecipeListViewModel
        .getRecipes()
        .observe(
            this,
            new Observer<Resource<List<Recipe>>>() {
              @Override
              public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if (listResource != null) {
                  Log.d(TAG, "onChanged: status: " + listResource.status);

                  if (listResource.data != null) {
                    for (Recipe recipe : listResource.data) {
                      System.out.println(recipe);
                    }
                    switch (listResource.status) {
                      case LOADING:
                        if (mRecipeListViewModel.getPageNumber() > 1) {
                          // Show loading animation at bottom of screen
                          mRecipeRecyclerAdapter.displayLoading(false);
                        } else {
                          // They're querying the first page, so show only loading
                          mRecipeRecyclerAdapter.displayLoading(true);
                        }
                        break;
                      case ERROR:
                        Log.e(TAG, "onChanged: cannot refresh the cache.");
                        Log.e(TAG, "onChanged: ERROR message: " + listResource.message);
                        Log.e(
                            TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size());
                        mRecipeRecyclerAdapter.hideLoading();
                        mRecipeRecyclerAdapter.submitList(listResource.data);
                        Toast.makeText(
                                RecipeListActivity.this, listResource.message, Toast.LENGTH_SHORT)
                            .show();

                        if (listResource.message.equals(QUERY_EXHAUSTED)) {
                          mRecipeRecyclerAdapter.showQueryExhausted();
                        }
                        break;
                      case SUCCESS:
                        Log.d(TAG, "onChanged: cache has been refreshed.");
                        Log.d(
                            TAG,
                            "onChanged: status: SUCCESS, #recipes: " + listResource.data.size());
                        mRecipeRecyclerAdapter.hideLoading();
                        mRecipeRecyclerAdapter.submitList(listResource.data);
                        break;
                    }
                  }
                }
              }
            });
  }

  private void displaySearchCategories() {
    mRecipeRecyclerAdapter.displaySearchCategories();
  }

  @Override
  public void onRecipeClick(int position) {
    List<Recipe> recipes = mRecipeRecyclerAdapter.getCurrentList();
    if (recipes != null && recipes.size() > position) {
      Recipe recipe = recipes.get(position);
      Intent intent = new Intent(this, RecipeActivity.class);
      intent.putExtra(Constants.EXTRA_RECIPE, recipe);
      startActivity(intent);
    }
  }

  @Override
  public void onCategoryClick(String category) {
    //    mRecipeRecyclerAdapter.displayLoading(true);
    mRecipeListViewModel.searchRecipesApi(category, 1);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_categories) {
      displaySearchCategories();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    if (mRecipeListViewModel.getViewState().getValue() == ViewState.CATEGORIES) {
      super.onBackPressed();
    } else {
      mRecipeListViewModel.setIsViewingCategories();
    }
  }
}
