package com.codingwithmitch.foodrecipes.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.codingwithmitch.foodrecipes.models.Recipe;
import java.util.List;

@Dao
public interface RecipeDao {

  /**
   * Insert multiple recipes
   *
   * @param recipe recipes to insert
   * @return array of which insertions were successful For example, all successful will be { id1,
   *     id2, id3... etc} Conflicts will be marked as -1 in the array
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  long[] insertRecipes(Recipe... recipe);

  /**
   * Insert a single repository
   *
   * @param recipe recipe to insert
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertRecipe(Recipe recipe);

  /**
   * Update a recipe
   *
   * @param recipeId ID of the recipe to update
   */
  @Query(
      "UPDATE recipes SET title = :title, publisher = :publisher, image_url = :imageUrl, social_rank = :socialRank "
          + "WHERE recipe_id = :recipeId")
  void updateRecipe(
      String recipeId, String title, String publisher, String imageUrl, float socialRank);

  /**
   * Search for recipes
   *
   * @param query query to search for in title and ingredients
   * @param pageNumber page number of search results we want
   * @return list of recipes that match the query
   */
  @Query(
      "SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' "
          + "ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
  LiveData<List<Recipe>> searchRecipes(String query, int pageNumber);
}
