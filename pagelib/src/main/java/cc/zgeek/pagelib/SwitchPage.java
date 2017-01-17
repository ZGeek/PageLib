package cc.zgeek.pagelib;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cc.zgeek.pagelib.anim.PageAnimatorProvider;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public abstract class SwitchPage extends Page {

    int showIndex = 0;
    private Runnable mAnimatedTransitionsFinishAction = null;

    public SwitchPage(PageActivity pageActivity) {
        super(pageActivity);
    }


    public void switchToPage(IPage page) {
        switchToPage(page, null);
    }

    public void switchToPage(IPage page, PageAnimatorProvider provider) {
        int index = findPageIndex(page);
        if (index >= 0) {
            switchToPage(index, provider);
        }
    }

    public void switchToPage(int index) {
        switchToPage(index, null);
    }

    public void switchToPage(final int index, PageAnimatorProvider provider) {
        if (index < 0 || index >= getChildPageCount() || index == showIndex) {
            throw new IllegalArgumentException("index is Out Of Bound");
        }

        ensureEndAnimationExecution();
        final boolean isAttach = isAttachToActivity();
        final IPage oldPage = getChildPageAt(showIndex);
        final IPage newPage = getChildPageAt(index);
        if (isAttach) {
            newPage.onShow();
            oldPage.onHide();
        }
        newPage.getRootView().setVisibility(View.VISIBLE);
        long time = (provider == null ? 0L : provider.startPageAnimation(currentContiner(), oldPage.getRootView(), newPage.getRootView()));
        if (time != 0L)
            time = Math.max(time + 1, 0);
        mAnimatedTransitionsFinishAction = new Runnable() {
            @Override
            public void run() {
                doFinalWorkForSwitchPage(isAttach, index, newPage, oldPage);
            }
        };
        mContext.postDelayed(mAnimatedTransitionsFinishAction, time);
    }

    private void ensureEndAnimationExecution() {
        if (mAnimatedTransitionsFinishAction != null) {
            mContext.removeCallbacks(mAnimatedTransitionsFinishAction);
            mAnimatedTransitionsFinishAction.run();
            mAnimatedTransitionsFinishAction = null;
        }
    }

    private void doFinalWorkForSwitchPage(boolean isAttach, int index, IPage newPage, IPage oldPage) {
        if(isAttach){
            newPage.onShow();
            oldPage.onHidden();
            newPage.getRootView().requestFocus();
        }

        showIndex = index;
    }

    @Override
    public void addPage(IPage page) {
        super.addPage(page);
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setMargins(0, 0, 0, 0);
        page.getRootView().setLayoutParams(layout);
        currentContiner().addView(page.getRootView());
    }

    @Override
    public void removePage(IPage targetPage) {
        int removeIndex = getChildPageIndex(targetPage);
        int count = getChildPageCount();

        boolean hasPrePage = removeIndex != 0;
        boolean hasAfterPage = removeIndex != count;

        if (hasAfterPage) {
            switchToPage(removeIndex + 1);
            showIndex = removeIndex;
        } else {
            if (hasPrePage) {
                targetPage.onHide();
                targetPage.onHidden();
                showIndex = -1;
            } else {
                switchToPage(removeIndex - 1);
            }
        }
        super.removePage(targetPage);
        currentContiner().removeView(targetPage.getRootView());
        targetPage.onDestroy();
    }

    public ViewGroup currentContiner() {
        return (FrameLayout) rootView;
    }

    @Override
    public void onShow() {
//        pageState = STATE_SHOWING;
        getChildPageAt(showIndex).onShow();
    }

    @Override
    public void onShown() {
//        pageState = STATE_SHOWN;
        IPage page = getChildPageAt(showIndex);
        page.onShown();
        page.getRootView().requestFocus();
    }

    @Override
    public void onHide() {
//        pageState = STATE_HIDDING;
        getChildPageAt(showIndex).onHide();
    }

    @Override
    public void onHidden() {
//        pageState = STATE_HIDDEN;
        getChildPageAt(showIndex).onHidden();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return getChildPageAt(showIndex).onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onBackPressed() {
        return getChildPageAt(showIndex).onBackPressed() || super.onBackPressed();
    }
}
