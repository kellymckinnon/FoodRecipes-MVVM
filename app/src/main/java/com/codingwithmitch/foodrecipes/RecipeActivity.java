package com.codingwithmitch.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;

/** Displays a single recipe and its ingredients. */
public class RecipeActivity extends BaseActivity {

  private static final String TAG = "RecipeActivity";

  private AppCompatImageView mRecipeImage;
  private TextView mRecipeTitle, mRecipeRank;
  private LinearLayout mRecipeIngredientsContainer;
  private ScrollView mScrollView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recipe);
    mRecipeImage = findViewById(R.id.recipe_image);
    mRecipeTitle = findViewById(R.id.recipe_title);
    mRecipeRank = findViewById(R.id.recipe_social_score);
    mRecipeIngredientsContainer = findViewById(R.id.ingredients_container);
    mScrollView = findViewById(R.id.parent);

    getIncomingIntent();
  }

  private void getIncomingIntent() {
    if (getIntent().hasExtra(Constants.EXTRA_RECIPE)) {
      Recipe recipe = getIntent().getParcelableExtra(Constants.EXTRA_RECIPE);
      Log.d(TAG, "getIncomingIntent: " + recipe.getTitle());
    }
  }
}
