package com.codingwithmitch.foodrecipes;

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
    subscribeObservers();
  }

  private void getIncomingIntent() {
    if (getIntent().hasExtra(Constants.EXTRA_RECIPE)) {
      Recipe recipe = getIntent().getParcelableExtra(Constants.EXTRA_RECIPE);
      Log.d(TAG, "getIncomingIntent: " + recipe.getTitle());
      mRecipeViewModel.searchRecipeById(recipe.getRecipeId());
    }
  }

  private void subscribeObservers() {
    mRecipeViewModel.getRecipe().observe(this, new Observer<Recipe>() {
      @Override
      public void onChanged(Recipe recipe) {
        if (recipe != null) {
          if (recipe.getRecipeId().equals(mRecipeViewModel.getRecipeId())) {
            setRecipeProperties(recipe);
          }
        }
      }
    });
  }

  private void setRecipeProperties(Recipe recipe) {
    if (recipe != null) {
      RequestOptions requestOptions = new RequestOptions()
          .placeholder(R.drawable.ic_launcher_background);

      Glide.with(this)
          .setDefaultRequestOptions(requestOptions)
          .load(recipe.getImageUrl())
          .into(mRecipeImage);

      mRecipeTitle.setText(recipe.getTitle());
      mRecipeRank.setText(String.valueOf(Math.round(recipe.getSocialRank())));

      mRecipeIngredientsContainer.removeAllViews();
      for (String ingredient : recipe.getIngredients()) {
        TextView textView = new TextView(this);
        textView.setText(ingredient);
        textView.setTextSize(16);
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRecipeIngredientsContainer.addView(textView);
      }

      showParent();
      showProgressBar(false);
    }
  }

  private void showParent() {
    mScrollView.setVisibility(View.VISIBLE);
  }
}
