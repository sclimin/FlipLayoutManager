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

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public final class FlipLayoutHelper {
    private final Rect mBounds = new Rect();
    private final Rect mRect = new Rect();

    private final Camera mCamera = new Camera();
    private final Matrix mMatrix = new Matrix();

    private final Paint mPaint = new Paint();

    private final FlipLayout mFlipLayout;

    final static int FLIP_START = 0;
    final static int FLIP_END = 1;
    final static int FLIP_TOP = 2;
    final static int FLIP_BOTTOM = 3;

    public FlipLayoutHelper(FlipLayout flipLayout) {
        if (!(flipLayout instanceof View)) {
            throw new RuntimeException("");
        }
        mFlipLayout = flipLayout;
    }

    private View getView() {
        return (View) mFlipLayout;
    }

    private int getDegree() {
        return ((FlipLayoutManager.LayoutParams) getView().getLayoutParams()).mDegree;
    }

    private int getOrientation() {
        return ((FlipLayoutManager.LayoutParams) getView().getLayoutParams()).mOrientation;
    }

    public final boolean draw(Canvas canvas) {

        final int degree = getDegree();
        final int orientation = getOrientation();

        if (degree == 0) {
            return false;
        }

        int flip;
        if (orientation == RecyclerView.VERTICAL) {
            flip = degree < 0 ? FLIP_TOP : FLIP_BOTTOM;
        }
        else {
            flip = degree < 0 ? FLIP_START : FLIP_END;
        }

        flipPart(canvas, degree, flip);
        clipPart(canvas, flip);
        return true;
    }


    public final void sizeChanged(int w, int h) {
        mBounds.set(getView().getPaddingStart(), getView().getPaddingTop(),
                w - getView().getPaddingEnd(),
                h - getView().getPaddingBottom());
    }

    private Matrix computeFlipMatrix(int degree) {
        final Camera camera = mCamera;
        final Matrix matrix = mMatrix;
        final int orientation = getOrientation();
        final Rect bounds = mBounds;

        matrix.reset();

        camera.save();
        camera.setLocation(0, 0, -40);
        if (orientation == RecyclerView.VERTICAL) {
            camera.rotateX(degree);
        }
        else {
            camera.rotateY(-degree);
        }
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-bounds.centerX(), -bounds.centerY());
        matrix.postTranslate(bounds.centerX(), bounds.centerY());

        return matrix;
    }

    private void flipPart(Canvas canvas, int degree, int flip) {
        canvas.save();

        final Rect rect = mRect;
        final Rect bounds = mBounds;
        final Paint paint = mPaint;

        canvas.getClipBounds(rect);
        switch (flip) {
            case FLIP_START:
                rect.right = bounds.centerX();
                break;
            case FLIP_END:
                rect.left = bounds.centerX();
                break;
            case FLIP_TOP:
                rect.bottom =  bounds.centerY();
                break;
            case FLIP_BOTTOM:
            default:
                rect.top = bounds.centerY();
                break;
        }

        canvas.clipRect(rect);
        canvas.concat(computeFlipMatrix(degree));

        mFlipLayout.drawSuper(canvas);

        paint.setColor(Color.argb((int) (0x99 * Math.abs(degree) / 90.f), 0, 0, 0));
        canvas.drawRect(bounds, paint);

        canvas.restore();
    }

    private void clipPart(Canvas canvas, int flip) {
        canvas.save();

        final Rect rect = mRect;
        final Rect bounds = mBounds;

        canvas.getClipBounds(rect);
        switch (flip) {
            case FLIP_START:
                rect.left = bounds.centerX();
                break;
            case FLIP_END:
                rect.right = bounds.centerX();
                break;
            case FLIP_TOP:
                rect.top =  bounds.centerY();
                break;
            case FLIP_BOTTOM:
            default:
                rect.bottom = bounds.centerY();
                break;
        }
        canvas.clipRect(rect);

        mFlipLayout.drawSuper(canvas);

        canvas.restore();
    }
}
