## FlipLayoutManager

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## 介绍

自定义水平、垂直翻页``RecycleView.LayoutManager``。因为需要实现页面翻折效果，需要自定义相关页面``RootView ``。使用时``Page``中``RootView``必须实现``FlipLayout``接口。

### 效果图

![blockchain](screenshot.gif)

### 原理
 
...

### 使用

设置``RecyclerView``的``LayoutManager``，并配置翻页模式

```xml
<androidx.recyclerview.widget.RecyclerView
        ...
        app:layoutManager="com.sclimin.recycler.flip.FlipLayoutManager"
        // 翻页模式
        android:orientation="vertical"
        或
        android:orientation="horizontal"
        ... />
```

``Page``中``RootView``必须使用实现``FlipLayout``接口的``Layout``

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.sclimin.recycler.flip.FlipSampleLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    ...
    
</com.sclimin.recycler.flip.FlipSampleLayout>
```

自定义``FlipLayout``：

```Java
public class FlipSampleLayout extends FrameLayout implements FlipLayout {

    private FlipLayoutHelper mHelper;

    ...

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mHelper.sizeChanged(w, h);
    }

    @Override
    public void draw(Canvas canvas) {
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
```

## License

```
   Copyright 2020 sclimin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```