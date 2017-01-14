package cc.zgeek.page.Lib;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cc.zgeek.page.Lib.anim.PageAnimatorProvider;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public abstract class ContainerPage extends Page {

    int showIndex = 0;
    private Runnable mAnimatedTransitionsFinishAction = null;

    public ContainerPage(PageActivity pageActivity) {
        super(pageActivity);
    }


    public void switchToPage(IPage page){
        switchToPage(page, null);
    }
    public void switchToPage(IPage page, PageAnimatorProvider provider) {
        int index = findPageIndex(page);
        if (index >= 0) {
            switchToPage(index, provider);
        }
    }

    public void switchToPage(int index){
        switchToPage(index, null);
    }

    public void switchToPage(int index, PageAnimatorProvider provider) {
        if (index < 0 || index >= pageList.size() || index == showIndex)
            return;
        ensureEndAnimationExecution();
        final IPage oldPage = pageList.get(showIndex);
        final IPage newPage = pageList.get(index);
        newPage.onShow();
        oldPage.onHide();
        newPage.getRootView().setVisibility(View.VISIBLE);
        long time = (provider == null ? 0L : provider.startPageAnimation(currentContiner(), oldPage.getRootView(), newPage.getRootView()));
        if (time != 0)
            time = Math.max(time + 1, 0);
        mAnimatedTransitionsFinishAction = new Runnable() {
            @Override
            public void run() {
                doFinalWorkForSwitchPage(newPage, oldPage);
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

    private void doFinalWorkForSwitchPage(IPage newPage, IPage oldPage) {
        newPage.onShow();
        oldPage.onHidden();
        newPage.getRootView().requestFocus();
    }

    @Override
    protected void addPage(IPage page) {
        super.addPage(page);
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setMargins(0, 0, 0, 0);
        page.getRootView().setLayoutParams(layout);
        currentContiner().addView(page.getRootView());
    }

    @Override
    protected boolean removePage(IPage page) {
        boolean result = super.removePage(page);
        currentContiner().removeView(page.getRootView());
        return result;
    }

    public ViewGroup currentContiner() {
        return (FrameLayout) rootView;
    }

    @Override
    public void onShow() {
        pageList.get(showIndex).onShow();
    }

    @Override
    public void onShown() {
        pageList.get(showIndex).onShown();
    }

    @Override
    public void onHide() {
        pageList.get(showIndex).onHide();
    }

    @Override
    public void onHidden() {
        pageList.get(showIndex).onHidden();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(pageList.get(showIndex).onTouchEvent(event))
            return true;
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onBackPressed() {
        if(pageList.get(showIndex).onBackPressed())
            return true;
        return super.onBackPressed();
    }
}
