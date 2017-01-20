package cc.zgeek.pagelib;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import static android.support.v4.widget.ViewDragHelper.STATE_SETTLING;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/17 : Create
 */

public class NavigationViewManager {

    private NavigationPage mNavigationPage;
    private NavigationViewManager.NavigationContainerView mNavigationContainerView;

    NavigationViewManager(NavigationPage pageManager) {
        mNavigationPage = pageManager;
    }


    NavigationViewManager.NavigationContainerView createContainerView(Context context) {
        mNavigationContainerView = new NavigationViewManager.NavigationContainerView(context);
        return mNavigationContainerView;
    }


    public class NavigationContainerView extends FrameLayout {

        private float mSwipeToHideThreshold = 0.1f;
        private ViewDragHelper dragHelper;

        private boolean mSwipeToHide;

        public NavigationContainerView(Context context) {
            super(context);
            dragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

                @Override
                public boolean tryCaptureView(View child, int pointerId) {
//                    return mNavigationPage.getChildPageCount() > 1 && mNavigationPage.getTopPage().getRootView() == child;
                    return false;
                }

                @Override
                public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                    if (mNavigationPage.getChildPageCount() > 1 && !mNavigationPage.isAnim()) {
                        dragHelper.captureChildView(mNavigationPage.getTopPage().getRootView(), pointerId);
                        View mPreView = mNavigationPage.getChildPageAt(mNavigationPage.getChildPageCount() - 2).getRootView();
                        mPreView.setVisibility(VISIBLE);
                        MarginLayoutParams mPreViewLayoutParams = (MarginLayoutParams) mPreView.getLayoutParams();
                        mPreViewLayoutParams.setMargins(-getMeasuredWidth() / 2, 0, getMeasuredWidth() / 2, 0);
                        mPreView.setLayoutParams(mPreViewLayoutParams);
                    }
                }


                @Override
                public void onViewReleased(View releasedChild, float xvel, float yvel) {
                    int left = releasedChild.getLeft();
                    int minOffset = (int) (mSwipeToHideThreshold * getMeasuredWidth());
                    if (left < minOffset) {
//                        dragHelper.settleCapturedViewAt(0, 0);
//                        invalidate();
                        cancelSwipeToHide();
                    } else {
                        mNavigationPage.popFromSwip();
                    }
                }

                @Override
                public int clampViewPositionHorizontal(View child, int left, int dx) {
                    if (left > 0)
                        return left;
                    return 0;
                }


                @Override
                public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                    View currentView = mNavigationPage.getTopPage().getRootView();
                    MarginLayoutParams params = (MarginLayoutParams) currentView.getLayoutParams();
                    params.setMargins(left, 0, -left, 0);

                    View mPreView = mNavigationPage.getChildPageAt(mNavigationPage.getChildPageCount() - 2).getRootView();
                    MarginLayoutParams mPreViewLayoutParams = (MarginLayoutParams) mPreView.getLayoutParams();
                    int childLeft = (left - getMeasuredWidth()) / 2;
                    mPreViewLayoutParams.setMargins(childLeft, 0, -childLeft, 0);
                    mPreView.setLayoutParams(mPreViewLayoutParams);
                }

                @Override
                public int getViewHorizontalDragRange(View child) {
                    return getMeasuredWidth();
                }

//                @Override
//                public void onViewDragStateChanged(int state) {
//                    if (state == STATE_SETTLING) {
//                        NavigationContainerView.this.setEnabled(false);
//                    } else {
//                        NavigationContainerView.this.setEnabled(true);
//                    }
//                }

            });
            dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        }

        @Override
        public void computeScroll() {
            if (dragHelper.continueSettling(true)) {
                invalidate();
            }
        }


        public void enableSwipeToHide() {
            if (!mSwipeToHide) {
                mSwipeToHide = true;
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return dragHelper.shouldInterceptTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            dragHelper.processTouchEvent(event);
            return true;
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
