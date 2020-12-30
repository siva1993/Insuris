package com.siva.insuris.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class MyItemDecoration extends RecyclerView.ItemDecoration{

    private Paint paint;
    private final static int OFFSET = 8;

    public MyItemDecoration(Paint paint){
        this.paint = paint;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(OFFSET, OFFSET, OFFSET, OFFSET);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        for(int i=0; i<parent.getChildCount(); i++){
            final View child = parent.getChildAt(i);
            c.drawRoundRect(layoutManager.getDecoratedLeft(child) + OFFSET,
                    layoutManager.getDecoratedTop(child) + OFFSET,
                    layoutManager.getDecoratedRight(child) - OFFSET,
                    layoutManager.getDecoratedBottom(child) - OFFSET, 2, 2, paint);
        }
    }
}