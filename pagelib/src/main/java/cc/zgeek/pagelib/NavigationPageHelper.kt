package cc.zgeek.pagelib

import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/17 : Create
 */

class NavigationPageHelper internal constructor(private val mNavigationPage: NavigationPage) {
    private var mNavigationContainerView: NavigationPageHelper.NavigationContainerView? = null


    internal fun createContainerView(context: Context): NavigationPageHelper.NavigationContainerView {
        mNavigationContainerView = this.NavigationContainerView(context)
        return mNavigationContainerView ?: throw RuntimeException("changed by other thread")
    }


    inner class NavigationContainerView(context: Context) : FrameLayout(context) {

        private val mSwipeToHideThreshold = 0.1f
        private val dragHelper: ViewDragHelper

        private var mSwipeToHide: Boolean = false

        init {
            dragHelper = ViewDragHelper.create(this, object : ViewDragHelper.Callback() {

                override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                    //                    return mNavigationPage.getChildPageCount() > 1 && mNavigationPage.getTopPage().getRootView() == child;
                    return false
                }

                override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
                    if (mNavigationPage.isTopPageCanSwipeToHide && mNavigationPage.childPageCount > 1 && !mNavigationPage.isAnim) {
//                        dragHelper.captureChildView(mNavigationPage.topPage.rootView, pointerId)
                        this@NavigationContainerView.captureChildView(mNavigationPage.topPage!!.rootView, pointerId)
                        val topCopy = mNavigationPage.topPage ?: throw RuntimeException("topPage can not be null")
                        mNavigationPage.prepareForPop(mNavigationPage.getChildPageAt(mNavigationPage.childPageCount - 2), topCopy)
                        val mPreView = mNavigationPage.getChildPageAt(mNavigationPage.childPageCount - 2).rootView
                        mPreView.visibility = View.VISIBLE
                        val mPreViewLayoutParams = mPreView.layoutParams as ViewGroup.MarginLayoutParams
                        mPreViewLayoutParams.setMargins(-measuredWidth / 2, 0, measuredWidth / 2, 0)
                        mPreView.layoutParams = mPreViewLayoutParams
                    }
                }


                override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
                    val left = releasedChild!!.left
                    val minOffset = (mSwipeToHideThreshold * measuredWidth).toInt()
                    if (left < minOffset) {
                        //                        dragHelper.settleCapturedViewAt(0, 0);
                        //                        invalidate();
                        cancelSwipeToHide()
                    } else {
                        mNavigationPage.popFromSwipe()
                    }
                }

                override fun clampViewPositionHorizontal(child: View?, left: Int, dx: Int): Int {
                    if (left > 0)
                        return left
                    return 0
                }


                override fun onViewPositionChanged(changedView: View?, left: Int, top: Int, dx: Int, dy: Int) {
                    val currentView = mNavigationPage.topPage!!.rootView
                    val params = currentView.layoutParams as ViewGroup.MarginLayoutParams
                    params.setMargins(left, 0, -left, 0)

                    val mPreView = mNavigationPage.getChildPageAt(mNavigationPage.childPageCount - 2).rootView
                    val mPreViewLayoutParams = mPreView.layoutParams as ViewGroup.MarginLayoutParams
                    val childLeft = (left - measuredWidth) / 2
                    mPreViewLayoutParams.setMargins(childLeft, 0, -childLeft, 0)
                    mPreView.layoutParams = mPreViewLayoutParams
                }

                override fun getViewHorizontalDragRange(child: View?): Int {
                    return measuredWidth
                }

                //                @Override
                //                public void onViewDragStateChanged(int state) {
                //                    if (state == STATE_SETTLING) {
                //                        NavigationContainerView.this.setEnabled(false);
                //                    } else {
                //                        NavigationContainerView.this.setEnabled(true);
                //                    }
                //                }

            })
            dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
        }
        fun captureChildView(childView:View, activePointerId:Int){
            return dragHelper.captureChildView(childView, activePointerId)
        }

        override fun computeScroll() {
            if (dragHelper.continueSettling(true)) {
                invalidate()
            }
        }


        fun enableSwipeToHide() {
            if (!mSwipeToHide) {
                mSwipeToHide = true
            }
        }

        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            return dragHelper.shouldInterceptTouchEvent(ev)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            dragHelper.processTouchEvent(event)
            return true
        }


        private fun cancelSwipeToHide() {
            mNavigationPage.cancelSwipeToHide()
        }

    }

    //    public static void animateView(View view, int fromXType, float fromXValue, int toXType, float toXValue, long time,
    //                                   Animation.AnimationListener animationListener) {
    //        Animation animation = createAnimation(fromXType, fromXValue, toXType, toXValue, time, animationListener);
    //        view.startAnimation(animation);
    //    }
    //
    //    private static Animation createAnimation(
    //            int fromXType, float fromXValue,
    //            int toXType, float toXValue,
    //            long time, Animation.AnimationListener animationListener) {
    //        Animation animation = new TranslateAnimation(fromXType, fromXValue, toXType,
    //                toXValue, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
    //        animation.setDuration(time);
    //        animation.setInterpolator(new DecelerateInterpolator(2.0f));
    //        animation.setAnimationListener(animationListener);
    //        return animation;
    //    }
}
