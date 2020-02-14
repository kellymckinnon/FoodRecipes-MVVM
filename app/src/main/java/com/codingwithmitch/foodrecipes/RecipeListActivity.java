package com.codingwithmitch.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeListViewModel;
import java.util.List;

public class RecipeListActivity extends BaseActivity
    implements RecipeRecyclerAdapter.OnRecipeListener {

  private static final String TAG = "RecipeListActivity";

  private RecipeListViewModel mRecipeListViewModel;
  private RecyclerView mRecyclerView;
  private RecipeRecyclerAdapter mRecipeRecyclerAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe_list);
    mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
    subscribeObservers();
    initRecyclerView();
    initSearchView();

    if (!mRecipeListViewModel.isViewingRecipes()) {
      displaySearchCategories();
    }

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
  }

  private void initRecyclerView() {
    mRecyclerView = findViewById(R.id.recipe_list);
    mRecipeRecyclerAdapter = new RecipeRecyclerAdapter();
    mRecipeRecyclerAdapter.setOnRecipeListener(this);
    mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
    mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(30));
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.addOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (!mRecyclerView.canScrollVertically(1)) {
          mRecipeListViewModel.searchNextPage();
        }
      }
    });
  }

  private void initSearchView() {
    final SearchView searchView = findViewById(R.id.search_view);
    searchView.setOnQueryTextListener(
        new OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            mRecipeRecyclerAdapter.displayLoading();
            mRecipeListViewModel.searchRecipesApi(query, 1);
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
        .getRecipes()
        .observe(
            this,
            new Observer<List<Recipe>>() {
              @Override
              public void onChanged(@Nullable List<Recipe> recipes) {
                if (recipes != null) {
                  for (Recipe recipe : recipes) {
                    if (mRecipeListViewModel.isViewingRecipes()) {
                      Log.d(TAG, "onChanged: " + recipe.getTitle());
                      mRecipeRecyclerAdapter.submitList(recipes);
                    }
                  }
                }
              }
            });
  }

  private void displaySearchCategories() {
    mRecipeListViewModel.setIsViewingRecipes(false);
    mRecipeRecyclerAdapter.displaySearchCategories();
  }

  @Override
  public void onRecipeClick(int position) {}

  @Override
  public void onCategoryClick(String category) {
    mRecipeRecyclerAdapter.displayLoading();
    mRecipeListViewModel.searchRecipesApi(category, 1);
  }

  @Override
  public void onBackPressed() {
    if (mRecipeListViewModel.onBackPressed()) {
      super.onBackPressed();
    } else {
      displaySearchCategories();
    }
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
}
