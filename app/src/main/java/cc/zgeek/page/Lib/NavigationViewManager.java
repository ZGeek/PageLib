package cc.zgeek.page.Lib;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;


import java.util.HashMap;
import java.util.Map;


/**
 * Copyright (c) 2015 neevek <i@neevek.net>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * This is the main view container
 */
class NavigationViewManager {
    private final static int SHADOW_VIEW_WIDTH = 20;                // in DIP
    private final static int SWIPE_TO_HIDE_THRESHOLD = 60;          // in DIP
    private final static int SWIPE_TO_HIDE_EDGE_SLOPE = 10;         // in DIP
    private final static int DEFAULT_ANIMATE_TIME = 300; //ms

    private NavigationPage mNavigationPage;
    private NavigationContainerView mNavigationContainerView;
    private Map<String, Animation> mAnimationCache = new HashMap<String, Animation>();

    NavigationViewManager(NavigationPage pageManager) {
        mNavigationPage = pageManager;
    }


    NavigationContainerView createContainerView(Context context) {
        mNavigationContainerView = new NavigationContainerView(context);
        return mNavigationContainerView;
    }

    void enableSwipeToHide(boolean applyInsetsToShadow) {
        if (mNavigationContainerView != null) {
            mNavigationContainerView.enableSwipeToHide(applyInsetsToShadow);
        }
    }

    public class NavigationContainerView extends FrameLayout {
        //        private ShadowView mShadowView;
        private View mCurrentView;
        private View mPrevView;
        //        private MarginLayoutParams mShadowViewLP;
        private MarginLayoutParams mCurrentViewLP;
        private MarginLayoutParams mPrevViewLP;

        private int mInitialX;
        private boolean mIsDragging;
        private float mTouchSlope;
        private float mEdgeSlope;
        private int mSwipeToHideThreshold;

        private boolean mSwipeToHide;
        private boolean mApplyInsetsToShadow;

        public NavigationContainerView(Context context) {
            super(context);
            mTouchSlope = ViewConfiguration.get(context).getScaledTouchSlop();
            mEdgeSlope = (int) (SWIPE_TO_HIDE_EDGE_SLOPE * getResources().getDisplayMetrics().density);
            mSwipeToHideThreshold = (int) (SWIPE_TO_HIDE_THRESHOLD * getResources().getDisplayMetrics().density);
        }

//        @TargetApi(20)
//        public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
//            if (insets != null) {
//                if (mApplyInsetsToShadow && mShadowView != null) {
//                    MarginLayoutParams lp = (MarginLayoutParams) mShadowView.getLayoutParams();
//                    if (lp.topMargin != insets.getSystemWindowInsetTop()) {
//                        lp.topMargin = insets.getSystemWindowInsetTop();
//                    }
//                }
//
//                final int childCount = getChildCount();
//                for (int i = 0; i < childCount; ++i) {
//                    getChildAt(i).dispatchApplyWindowInsets(insets);
//                }
//            }
//            return insets;
//        }

        public void enableSwipeToHide(boolean applyInsetsToShadow) {
            if (!mSwipeToHide) {
                mSwipeToHide = true;
                mApplyInsetsToShadow = applyInsetsToShadow;
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!mSwipeToHide || mNavigationPage.getPageCount() < 2 || !mNavigationPage.isTopPageCanSwipeToHide()) {
                return super.onInterceptTouchEvent(ev);
            }

            final int x = (int) ev.getRawX();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mInitialX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!mIsDragging &&
                            mNavigationPage.getTopPage().getRootView().getAnimation() == null &&
                            mInitialX <= mEdgeSlope && x - mInitialX > mTouchSlope) {
                        mIsDragging = true;

                        IPage topPage = mNavigationPage.getTopPage();
                        mCurrentView = topPage.getRootView();
                        int index = mNavigationPage.getPageCount() - 2;
                        if (index >= 0) {
                            mPrevView = mNavigationPage.getPageAt(index).getRootView();
                            mPrevView.setVisibility(VISIBLE);
                            mPrevViewLP = (MarginLayoutParams) mPrevView.getLayoutParams();
                        }
                        mCurrentViewLP = (MarginLayoutParams) mCurrentView.getLayoutParams();
                        mInitialX = x;
                        return true;
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!mSwipeToHide) {
                return super.onTouchEvent(event);
            }

            int x = (int) event.getRawX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (mIsDragging) {
                        int delta = x - mInitialX;
                        if (delta < 0) {
                            delta = 0;
                        }

                        if (mPrevView != null) {
                            mPrevViewLP.leftMargin = -(int) (getWidth() * 0.5f - (delta * 0.5f));
                            mPrevViewLP.rightMargin = -(-(int) (getWidth() * 0.5f - (delta * 0.5f)));
                        }
                        if (mCurrentView != null) {
                            mCurrentViewLP.leftMargin = delta;
                            mCurrentViewLP.rightMargin = -delta;
//                            mShadowViewLP.leftMargin = delta - mShadowView.getWidth();
                        }

                        mNavigationContainerView.requestLayout();

                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mIsDragging) {
                        mIsDragging = false;
                        int currentViewLeft = mCurrentView.getLeft();

                        if (currentViewLeft > mSwipeToHideThreshold) {
                            mNavigationPage.popFromSwip();
                        } else if (currentViewLeft > 0) {
                            cancelSwipeToHide();
                        } else {
//                            mShadowView.setVisibility(INVISIBLE);
                        }

                        return true;
                    }
                    break;

            }

            return super.onTouchEvent(event);
        }

        private void cancelSwipeToHide() {
            mNavigationPage.cancelSwipeToHide();
        }

    }

    public static void animateView(View view, int fromXType, float fromXValue, int toXType, float toXValue, long time,
                                   Animation.AnimationListener animationListener) {
        Animation animation = createAnimation(fromXType, fromXValue, toXType, toXValue, time, animationListener);
        view.startAnimation(animation);
    }

    private static Animation createAnimation(
            int fromXType, float fromXValue,
            int toXType, float toXValue,
            long time, Animation.AnimationListener animationListener) {
        Animation animation = new TranslateAnimation(fromXType, fromXValue, toXType,
                toXValue, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(time);
        animation.setInterpolator(new DecelerateInterpolator(2.0f));
        animation.setAnimationListener(animationListener);
        return animation;
    }
}
