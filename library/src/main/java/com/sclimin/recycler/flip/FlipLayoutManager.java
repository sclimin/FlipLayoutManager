/*
 * Copyright 2020, sclimin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sclimin.recycler.flip;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class FlipLayoutManager extends RecyclerView.LayoutManager implements
        RecyclerView.SmoothScroller.ScrollVectorProvider {

    public static final int HORIZONTAL = RecyclerView.HORIZONTAL;
    public static final int VERTICAL = RecyclerView.VERTICAL;

    @RecyclerView.Orientation
    private final int mOrientation;

    private int mItemLength;
    private int mOffset;
    private int mPage;

    private final int[] mPositionSet = new int[3];

    public FlipLayoutManager(Context context) {
        this(context, null, 0, 0);
    }

    public FlipLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlipLayoutManager,
                defStyleAttr, defStyleRes);
        mOrientation = ta.getInt(R.styleable.FlipLayoutManager_android_orientation, VERTICAL);
        ta.recycle();
    }

    public int getPage() {
        return mPage;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(Context c, AttributeSet attrs) {
        return new LayoutParams(c, attrs);
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        return lp instanceof LayoutParams;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        new FlipSnapHelper().attachToRecyclerView(view);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int itemCount = state.getItemCount();

        mItemLength = mOrientation == RecyclerView.VERTICAL ? getHeight() : getWidth();
        detachAndScrapAttachedViews(recycler);
        if (itemCount == 0) {
            mPage = 0;
            mOffset = 0;
        } else {
            if (mPage >= itemCount) {
                mPage = itemCount - 1;
                mOffset = 0;
            }
            fill(recycler, state);
        }
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return mOrientation == HORIZONTAL;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dx, recycler, state);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollBy(dy, recycler, state);
    }

    private int scrollBy(int delta, RecyclerView.Recycler recycler, RecyclerView.State state) {

        final int itemCount = state.getItemCount();
        final int itemLength = mItemLength;

        final int oldOffset = mOffset;
        final int oldPage = mPage;

        int offset = oldOffset;
        int page = oldPage;

        int logicMaxOffset = itemLength * (itemCount - 1);
        int logicOffset = page * itemLength + offset;

        int newLogicOffset = delta + logicOffset;
        if (newLogicOffset < 0) {
            newLogicOffset = 0;
        }
        else if (newLogicOffset > logicMaxOffset) {
            newLogicOffset = logicMaxOffset;
        }

        final int consumed = newLogicOffset - logicOffset;

        if (consumed != 0) {
            page = newLogicOffset / itemLength;
            offset = newLogicOffset % itemLength;

            if (Math.abs(offset) >= (itemLength >> 1)) {
                if (offset > 0) {
                    offset -= itemLength;
                    page += 1;
                }
                else {
                    offset += itemLength;
                    page -= 1;
                }
            }

            mOffset = offset;
            mPage = page;

            if (oldPage != page) {
                fill(recycler, state);
            }
            else {
                updateViewByPosition(page);
            }
        }
        return consumed;
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        final int[] positionSet = mPositionSet;

        positionSet[0] = mPage - 1;
        positionSet[1] = mPage + 1;
        positionSet[2] = mPage;

        int validChildrenCount = 0;

        for (int position : positionSet) {
            if (position >= 0 && position < state.getItemCount()) {
                validChildrenCount++;
            }

            View layout = fromRecycler(recycler, state, position);
            if (layout == null) {
                continue;
            }

            layoutChild(layout);
        }

        int invalidChildrenCount = getChildCount() - validChildrenCount;
        for (int i = 0; i < invalidChildrenCount; i++) {
            View c = getChildAt(0);
            if (c == null) {
                break;
            }
            removeAndRecycleView(c, recycler);
        }
    }

    private void layoutChild(View layout) {
        addView(layout);
        measureChildWithMargins(layout, 0, 0);
        layoutDecoratedWithMargins(layout, getPaddingStart(), getPaddingTop(),
                getWidth() - getPaddingEnd(),
                getHeight() - getPaddingEnd());
    }

    private void updateViewByPosition(int position) {
        View c = findViewByPosition(position);
        if (c != null) {
            updateLayoutParams(c, position);
            c.invalidate();
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == null) {
                continue;
            }

            int cp = getPosition(child);
            if (cp < position) {
                child.setVisibility(mOffset < 0 ? View.VISIBLE : View.GONE);
            }
            else if (cp > position) {
                child.setVisibility(mOffset > 0 ? View.VISIBLE : View.GONE);
            }
        }
    }

    private View fromRecycler(RecyclerView.Recycler recycler, RecyclerView.State state, int position) {

        if (position < 0 || position >= state.getItemCount() ||
                Math.abs(position - mPage) > 1) {
            return null;
        }

        View view = recycler.getViewForPosition(position);
        if (view instanceof FlipLayout) {

            updateLayoutParams(view, position);

            if (mOffset == 0) {
                view.setVisibility(position == mPage ? View.VISIBLE : View.GONE);
            }
            else if (mOffset > 0) {
                view.setVisibility(position == mPage - 1 ? View.GONE : View.VISIBLE);
            }
            else {
                view.setVisibility(position == mPage + 1 ? View.GONE : View.VISIBLE);
            }
            return view;
        }
        else {
            throw new RuntimeException("FlipLayoutManager child must be FlipLayout");
        }
    }

    private void updateLayoutParams(View view, int position) {
        final LayoutParams lp = (LayoutParams) view.getLayoutParams();
        lp.mOrientation = mOrientation;
        lp.mDegree = position == mPage ? (int) (((float) mOffset / mItemLength) * 180.f) : 0;
    }

    @Override
    public void scrollToPosition(int position) {
        mPage = position;
        mOffset = 0;
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        FlipSmoothScroller flipSmoothScroller = new FlipSmoothScroller(recyclerView.getContext());
        flipSmoothScroller.setTargetPosition(position);
        startSmoothScroll(flipSmoothScroller);
    }

    final int[] calculateDistanceToFinalSnap(View targetView) {
        System.out.println("HW::calculateDistanceToFinalSnap");
        int[] out = new int[2];

        int targetPosition = getPosition(targetView);
        int distance = (targetPosition - mPage) * mItemLength - mOffset;
        if (canScrollHorizontally()) {
            out[0] = distance;
        }
        else if (canScrollVertically()) {
            out[1] = distance;
        }
        return out;
    }

    final View findSnapView() {
        System.out.println("HW::findSnapView");
        if (mPage >= 0 && mPage < getItemCount()) {
            return findViewByPosition(mPage);
        }
        return null;
    }

    final int findTargetSnapPosition(int[] scrollDistance) {
        System.out.println("HW::findTargetSnapPosition");

        final int itemCount = getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        final int newOffset = mOffset + scrollDistance[mOrientation];

        int newPage = mPage;
        if (Math.abs(newOffset) > (mItemLength >> 1)) {
            if (newOffset > 0 && newPage < (itemCount - 1)) {
                newPage += 1;
            }
            else if (newOffset < 0 && newPage > 0) {
                newPage -= 1;
            }
        }
        return newPage;
    }

    @Nullable
    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        final int itemCount = getItemCount();
        if (itemCount == 0) {
            return null;
        }

        int distance = (targetPosition - mPage) * mItemLength - mOffset;
        if (mOrientation == HORIZONTAL) {
            return new PointF(distance, 0);
        } else {
            return new PointF(0, distance);
        }
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {

        int mDegree;
        int mOrientation;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            init();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            init();
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            init();
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            init();
        }

        public LayoutParams(RecyclerView.LayoutParams source) {
            super(source);
            init();
        }

        private void init() {
            width = MATCH_PARENT;
            height = MATCH_PARENT;
        }
    }
}
