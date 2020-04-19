package com.codingwithmitch.foodrecipes;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.Resource;
import com.codingwithmitch.foodrecipes.viewmodels.RecipeViewModel;

/** Displays a single recipe and its ingredients. */
public class RecipeActivity extends BaseActivity {

  private static final String TAG = "RecipeActivity";

  private AppCompatImageView mRecipeImage;
  private TextView mRecipeTitle, mRecipeRank;
  private LinearLayout mRecipeIngredientsContainer;
  private ScrollView mScrollView;

  private RecipeViewModel mRecipeViewModel;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe);
    mRecipeImage = findViewById(R.id.recipe_image);
    mRecipeTitle = findViewById(R.id.recipe_title);
    mRecipeRank = findViewById(R.id.recipe_social_score);
    mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
    mScrollView = findViewById(R.id.parent);

    mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

    showProgressBar(true);
    getIncomingIntent();
  }

  private void getIncomingIntent() {
    if (getIntent().hasExtra(Constants.EXTRA_RECIPE)) {
      Recipe recipe = getIntent().getParcelableExtra(Constants.EXTRA_RECIPE);
      Log.d(TAG, "getIncomingIntent: " + recipe.getTitle());
      subscribeObservers(recipe.getRecipe_id());
    }
  }

  private void subscribeObservers(final String recipeId) {
    mRecipeViewModel
        .searchRecipesApi(recipeId)
        .observe(
            this,
            new Observer<Resource<Recipe>>() {
              @Override
              public void onChanged(Resource<Recipe> recipeResource) {
                if (recipeResource != null && recipeResource.data != null) {
                  switch (recipeResource.status) {
                    case ERROR:
                      Log.e(TAG, "onChanged: status: ERROR, recipe: " + recipeResource.data);
                      Log.e(TAG, "onChanged: ERROR message: " + recipeResource.message);
                      setRecipeProperties(recipeResource.data);
                      showParent();
                      showProgressBar(false);
                      break;
                    case LOADING:
                      showProgressBar(true);
                      break;
                    case SUCCESS:
                      Log.d(TAG, "onChanged: Cache has been refreshed");
                      Log.d(
                          TAG,
                          "onChanged: status: SUCCESS, recipe: " + recipeResource.data.getTitle());
                      setRecipeProperties(recipeResource.data);
                      showParent();
                      showProgressBar(false);
                      break;
                  }
                }
              }
            });
  }

  private void setRecipeProperties(Recipe recipe) {
    if (recipe != null) {
      RequestOptions requestOptions =
          new RequestOptions().placeholder(new ColorDrawable(Color.WHITE));

      Glide.with(this)
          .setDefaultRequestOptions(requestOptions)
          .load(recipe.getImage_url())
          .into(mRecipeImage);

      mRecipeTitle.setText(recipe.getTitle());
      mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocial_rank())));

      mRecipeIngredientsContainer.removeAllViews();
      if (recipe.getIngredients() != null) {
        for (String ingredient : recipe.getIngredients()) {
          TextView textView = new TextView(this);
          textView.setText(ingredient);
          textView.setTextSize(16);
          textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
          mRecipeIngredientsContainer.addView(textView);
        }
      } else {
        TextView textView = new TextView(this);
        textView.setText("Error retrieving ingredients");
        textView.setTextSize(16);
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRecipeIngredientsContainer.addView(textView);
      }
    }
  }

  private void showParent() {
    mScrollView.setVisibility(View.VISIBLE);
  }
}
