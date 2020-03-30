package com.codingwithmitch.foodrecipes.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * Binds recipes to views. We use ListAdapter to get better performance as it uses DiffUtil under
 * the hood to only make necessary changes when the list is updated, rather than refreshing the
 * whole list.
 */
public class RecipeRecyclerAdapter extends ListAdapter<Recipe, RecyclerView.ViewHolder> {

  private static final int RECIPE_TYPE = 1;
  private static final int LOADING_TYPE = 2;
  private static final int CATEGORY_TYPE = 3;
  private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<Recipe>() {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldRecipe, @NonNull Recipe newRecipe) {
          // Recipe properties may have changed if reloaded from the DB, but ID is fixed
          return oldRecipe.getRecipe_id() != null
              && newRecipe.getRecipe_id() != null
              && oldRecipe.getRecipe_id().equals(newRecipe.getRecipe_id());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldRecipe, @NonNull Recipe newRecipe) {
          // NOTE: if you use equals, your object must properly override Object#equals()
          // Incorrectly returning false here will result in too many animations.
          return oldRecipe.equals(newRecipe);
        }
      };
  private OnRecipeListener mOnRecipeListener;
  private RequestManager requestManager;

  public RecipeRecyclerAdapter() {
    super(DIFF_CALLBACK);
  }

  public void setOnRecipeListener(OnRecipeListener onRecipeListener) {
    mOnRecipeListener = onRecipeListener;
  }

  public void setGlideRequestManager(RequestManager glideRequestManager) {
    requestManager = glideRequestManager;
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    View view;
    switch (viewType) {
      case RECIPE_TYPE:
        view =
            LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_recipe_list_item, viewGroup, false);
        return new RecipeViewHolder(view, mOnRecipeListener, requestManager);
      case LOADING_TYPE:
        view =
            LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_loading_list_item, viewGroup, false);
        return new LoadingViewHolder(view);
      case CATEGORY_TYPE:
        view =
            LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_category_list_item, viewGroup, false);
        return new CategoryViewHolder(view, mOnRecipeListener, requestManager);
      default:
        throw new IllegalArgumentException("Not a valid viewtype");
    }
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    int itemViewType = getItemViewType(i);
    Recipe item = getItem(i);

    if (itemViewType == RECIPE_TYPE) {
      ((RecipeViewHolder) viewHolder).onBind(item);
    } else if (itemViewType == CATEGORY_TYPE) {
      ((CategoryViewHolder) viewHolder).onBind(item);
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (getItem(position).getTitle().equals("LOADING...")) {
      return LOADING_TYPE;
    } else if (getItem(position).getSocial_rank() == -1) {
      return CATEGORY_TYPE;
    } else {
      return RECIPE_TYPE;
    }
  }

  public void displayLoading(boolean onlyShowLoading) {
    List<Recipe> loadingList = onlyShowLoading ? new ArrayList<Recipe>() : getCurrentList();

    if (!isLoading()) {
      Recipe recipe = new Recipe();
      recipe.setTitle("LOADING...");
      loadingList.add(recipe);
      submitList(loadingList);
    }
  }

  public void hideLoading() {
    if (isLoading()) {
      // The loading will either be at the beginning or end
      List<Recipe> currentList = new ArrayList(getCurrentList());
      if (currentList.get(0).getTitle().equals("LOADING...")) {
        currentList.remove(0);
      } else if (currentList.get(currentList.size() - 1).getTitle().equals("LOADING...")) {
        currentList.remove(currentList.size() - 1);
      }
      submitList(currentList);
    }
  }

  public void showQueryExhausted() {
    // TODO
  }

  public void displaySearchCategories() {
    List<Recipe> categories = new ArrayList<>();
    for (int i = 0; i < Constants.DEFAULT_SEARCH_CATEGORIES.length; i++) {
      Recipe recipe = new Recipe();
      recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
      recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
      recipe.setSocial_rank(-1);
      categories.add(recipe);
    }
    submitList(categories);
  }

  private boolean isLoading() {
    List<Recipe> list = getCurrentList();
    return list.size() > 0 && list.get(list.size() - 1).getTitle().equals("LOADING...");
  }

  public interface OnRecipeListener {
    void onRecipeClick(int position);

    void onCategoryClick(String category);
  }

  class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    final TextView title;
    final TextView publisher;
    final TextView socialScore;
    final AppCompatImageView image;
    final OnRecipeListener onRecipeListener;
    final RequestManager requestManager;

    RecipeViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener, RequestManager requestManager) {
      super(itemView);
      this.onRecipeListener = onRecipeListener;
      this.requestManager = requestManager;
      title = itemView.findViewById(R.id.recipe_title);
      publisher = itemView.findViewById(R.id.recipe_publisher);
      socialScore = itemView.findViewById(R.id.recipe_social_score);
      image = itemView.findViewById(R.id.recipe_image);
      itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe) {
      requestManager.load(recipe.getImage_url()).into(image);

      title.setText(recipe.getTitle());
      publisher.setText(recipe.getPublisher());
      socialScore.setText(String.valueOf(Math.round(recipe.getSocial_rank())));
    }

    @Override
    public void onClick(View v) {
      onRecipeListener.onRecipeClick(getAdapterPosition());
    }
  }

  class LoadingViewHolder extends RecyclerView.ViewHolder {

    LoadingViewHolder(@NonNull View itemView) {
      super(itemView);
    }
  }

  class CategoryViewHolder extends ViewHolder implements View.OnClickListener {

    final OnRecipeListener listener;
    final CircleImageView image;
    final TextView categoryTitle;
    final RequestManager requestManager;

    CategoryViewHolder(@NonNull View itemView, OnRecipeListener listener, RequestManager requestManager) {
      super(itemView);
      this.listener = listener;
      this.requestManager = requestManager;
      image = itemView.findViewById(R.id.category_image);
      categoryTitle = itemView.findViewById(R.id.category_title);
      itemView.setOnClickListener(this);
    }

    public void onBind(Recipe recipe) {
      requestManager.load("android.resource://com.codingwithmitch.foodrecipes/drawable/" + recipe.getImage_url()).into(image);

      categoryTitle.setText(recipe.getTitle());
    }

    @Override
    public void onClick(View v) {
      listener.onCategoryClick(categoryTitle.getText().toString());
    }
  }
}
