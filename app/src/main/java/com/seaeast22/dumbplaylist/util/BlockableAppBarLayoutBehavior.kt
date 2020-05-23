package com.seaeast22.dumbplaylist.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout

//allows to block scrolling of AppBarLayout https://stackoverflow.com/a/48086783/878126
class BlockableAppBarLayoutBehavior(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {
    var isShouldScroll = false

    override fun onStartNestedScroll(parent: CoordinatorLayout,
                                     child: AppBarLayout,
                                     directTargetChild: View,
                                     target: View,
                                     nestedScrollAxes: Int,
                                     type: Int) = isShouldScroll

    override fun onTouchEvent(parent: CoordinatorLayout,
                              child: AppBarLayout,
                              ev: MotionEvent) = isShouldScroll && super.onTouchEvent(parent, child, ev)

}

// Usage
// disable changing its size when scrolling
// ((app_bar.layoutParams as CoordinatorLayout.LayoutParams).behavior as BlockableAppBarLayoutBehavior).isShouldScroll = false
// and enable:
// ((app_bar.layoutParams as CoordinatorLayout.LayoutParams).behavior as BlockableAppBarLayoutBehavior).isShouldScroll = true

/*
<android.support.design.widget.AppBarLayout
    android:id="@+id/app_bar_layout"
    android:layout_width="wrap_content"
    android:layout_height="@dimen/activity_main_toolbar_height_extended"
    android:theme="@style/ThemeOverlay.AppCompat.Dark"
    android:fitsSystemWindows="true"
    android:elevation="4dp"
    app:layout_behavior="io.eighttails.mvp.widgets.CustomAppBarLayoutBehavior">
 */