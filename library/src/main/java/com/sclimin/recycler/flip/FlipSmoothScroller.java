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
import android.graphics.PointF;
import android.view.View;

import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

final class FlipSmoothScroller extends LinearSmoothScroller {

    public FlipSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {
        if (getChildCount() == 0) {
            stop();
            return;
        }

        PointF scrollVector = computeScrollVectorForPosition(getTargetPosition());
        if (scrollVector == null || (scrollVector.x == 0 && scrollVector.y == 0)) {
            final int target = getTargetPosition();
            action.jumpTo(target);
            stop();
            return;
        }

        mTargetVector = scrollVector;
        mInterimTargetDx = (int) scrollVector.x;
        mInterimTargetDy = (int) scrollVector.y;

        updateActionForInterimTarget(action, mInterimTargetDx, mInterimTargetDy);
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        if (!(getLayoutManager() instanceof FlipLayoutManager)) {
            return;
        }

        int[] snapDistances = ((FlipLayoutManager) getLayoutManager())
                .calculateDistanceToFinalSnap(targetView);
        updateActionForInterimTarget(action, snapDistances[0], snapDistances[1]);
    }

    private void updateActionForInterimTarget(Action action, int dx, int dy) {
        final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
        if (time > 0) {
            action.update(dx, dy, time, mDecelerateInterpolator);
        }
    }
}
