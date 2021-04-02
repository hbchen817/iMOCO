package com.rexense.imoco.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int mColumn;
    private int mSpace;

    public GridItemDecoration(int column, int space) {
        this.mColumn = column;
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildLayoutPosition(view);
        if (pos % mColumn == 0) {
            outRect.left = mSpace;
            outRect.top = mSpace;
            outRect.right = mSpace / 2;
            outRect.bottom = 0;
        } else if (pos % mColumn == mColumn - 1) {
            outRect.left = mSpace / 2;
            outRect.top = mSpace;
            outRect.right = mSpace;
            outRect.bottom = 0;
        } else {
            outRect.left = mSpace;
            outRect.top = mSpace;
            outRect.right = mSpace;
            outRect.bottom = 0;
        }
    }
}
