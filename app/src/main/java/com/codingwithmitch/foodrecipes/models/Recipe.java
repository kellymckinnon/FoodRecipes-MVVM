package com.codingwithmitch.foodrecipes.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.Objects;

public class Recipe implements Parcelable {
  public static final Parcelable.Creator<Recipe> CREATOR =
      new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
          return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
          return new Recipe[size];
        }
      };
  private String title;
  private String publisher;
  private String[] ingredients;
  private String recipe_id;
  private String image_url;
  private float social_rank;

  public Recipe(
      String title,
      String publisher,
      String[] ingredients,
      String recipe_id,
      String image_url,
      float social_rank) {
    this.title = title;
    this.publisher = publisher;
    this.ingredients = ingredients;
    this.recipe_id = recipe_id;
    this.image_url = image_url;
    this.social_rank = social_rank;
  }

  public Recipe() {}

  private Recipe(Parcel in) {
    this.title = in.readString();
    this.publisher = in.readString();
    this.ingredients = in.createStringArray();
    this.recipe_id = in.readString();
    this.image_url = in.readString();
    this.social_rank = in.readFloat();
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public String[] getIngredients() {
    return ingredients;
  }

  public void setIngredients(String[] ingredients) {
    this.ingredients = ingredients;
  }

  public String getRecipeId() {
    return recipe_id;
  }

  public void setRecipeId(String recipe_id) {
    this.recipe_id = recipe_id;
  }

  public String getImageUrl() {
    return image_url;
  }

  public void setImageUrl(String image_url) {
    this.image_url = image_url;
  }

  public float getSocialRank() {
    return social_rank;
  }

  public void setSocialRank(float social_rank) {
    this.social_rank = social_rank;
  }

  @Override
  public String toString() {
    return "Recipe{"
        + "title='"
        + title
        + '\''
        + ", publisher='"
        + publisher
        + '\''
        + ", ingredients="
        + Arrays.toString(ingredients)
        + ", recipe_id='"
        + recipe_id
        + '\''
        + ", image_url='"
        + image_url
        + '\''
        + ", social_rank="
        + social_rank
        + '}';
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.title);
    dest.writeString(this.publisher);
    dest.writeStringArray(this.ingredients);
    dest.writeString(this.recipe_id);
    dest.writeString(this.image_url);
    dest.writeFloat(this.social_rank);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Recipe recipe = (Recipe) o;
    return Float.compare(recipe.social_rank, social_rank) == 0
        && Objects.equals(title, recipe.title)
        && Objects.equals(publisher, recipe.publisher)
        && Arrays.equals(ingredients, recipe.ingredients)
        && Objects.equals(recipe_id, recipe.recipe_id)
        && Objects.equals(image_url, recipe.image_url);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(title, publisher, recipe_id, image_url, social_rank);
    result = 31 * result + Arrays.hashCode(ingredients);
    return result;
  }
}
