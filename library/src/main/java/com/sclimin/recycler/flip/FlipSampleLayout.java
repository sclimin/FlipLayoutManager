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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FlipSampleLayout extends FrameLayout implements FlipLayout {

    private FlipLayoutHelper mHelper;

    public FlipSampleLayout(@NonNull Context context) {
        super(context);
    }

    public FlipSampleLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipSampleLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        ensureFlipLayoutHelper();
        mHelper.sizeChanged(w, h);
    }

    private void ensureFlipLayoutHelper() {
        if (mHelper == null) {
            mHelper = new FlipLayoutHelper(this);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        ensureFlipLayoutHelper();
        if (mHelper.draw(canvas)) {
            return;
        }
        super.draw(canvas);
    }

    @Override
    public void drawSuper(Canvas canvas) {
        super.draw(canvas);
    }
}
