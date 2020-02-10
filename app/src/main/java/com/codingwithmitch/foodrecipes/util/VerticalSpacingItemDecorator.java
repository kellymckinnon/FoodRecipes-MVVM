package com.codingwithmitch.foodrecipes.util;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;

public class VerticalSpacingItemDecorator extends ItemDecoration {
  private final int mVerticalSpaceHeight;

  public VerticalSpacingItemDecorator(int verticalSpaceHeight) {
    this.mVerticalSpaceHeight = verticalSpaceHeight;
  }

  @Override
  public void getItemOffsets(
      @NonNull Rect outRect,
      @NonNull View view,
      @NonNull RecyclerView parent,
      @NonNull State state) {
    outRect.top = mVerticalSpaceHeight;
  }
}
