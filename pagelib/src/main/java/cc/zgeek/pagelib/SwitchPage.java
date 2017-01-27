package cc.zgeek.pagelib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cc.zgeek.pagelib.anim.PageAnimatorProvider;
import cc.zgeek.pagelib.anim.SimpleAnimListener;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public abstract class SwitchPage extends SingleActivePage {

    int showIndex = 0;
    private ValueAnimator mAnimate = null;

    public SwitchPage(PageActivity pageActivity) {
        super(pageActivity);
    }


    public void switchToPage(IPage page) {
        switchToPage(page, null);
    }

    public void switchToPage(IPage page, PageAnimatorProvider provider) {
        int index = getChildPageIndex(page);
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
        if(showIndex == index)
            return;

        ensureEndAnimationExecution();
        final boolean isAttach = isAttachToActivity();
        final IPage oldPage = getChildPageAt(showIndex);
        final IPage newPage = getChildPageAt(index);
        prepareForSwitch(newPage, oldPage);
        if (isAttach) {
            newPage.onShow();
            oldPage.onHide();
        }
        if (provider != null && isAttach) {
            mAnimate = provider.getPageAnimation(currentContiner(), oldPage.getRootView(), newPage.getRootView());
            mAnimate.addListener(new SimpleAnimListener(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    doFinalWorkForSwitchPage(isAttach, index, newPage, oldPage);
                }
            });
            mAnimate.start();
        } else {
            doFinalWorkForSwitchPage(isAttach, index, newPage, oldPage);
        }
    }

    private void ensureEndAnimationExecution() {
        if (mAnimate != null && mAnimate.isRunning()) {
            mAnimate.end();
            mAnimate = null;
        }
    }

    private void doFinalWorkForSwitchPage(boolean isAttach, int index, IPage newPage, IPage oldPage) {
        if (isAttach) {
            newPage.onShow();
            oldPage.onHidden();
        }
        currentContiner().removeView(oldPage.getRootView());
        if(isAttach){
            newPage.getRootView().requestFocus();
        }

        showIndex = index;
    }

    void prepareForSwitch(IPage willShowPage, IPage willHidePage) {
        View willShowView = willShowPage.getRootView();
        if (willShowView.getLayoutParams() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, 0, 0);
            willShowView.setLayoutParams(layoutParams);
        }
        currentContiner().addView(willShowView);
        willHidePage.getRootView().bringToFront();
    }

    @Override
    public void addPage(IPage page) {
        super.addPage(page);
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setMargins(0, 0, 0, 0);
        page.getRootView().setLayoutParams(layout);
//        currentContiner().addView(page.getRootView());
    }

    @Override
    public boolean removePage(IPage targetPage) {
        int removeIndex = getChildPageIndex(targetPage);
        int count = getChildPageCount();
        if(removeIndex == -1){
            return false;
        }
        if(count == 0)
            return false;

        if(showIndex != removeIndex){//如果要显示的page不是当前要显示的page，则直接移除，并调用移除page的onDestroy方法，随后对方法showIndex进行修正
            if(targetPage.isViewInited()){
                targetPage.onDestroy();
            }
            IPage removePage = super.removePage(removeIndex);
            if(removeIndex < showIndex)
                showIndex--;
            return removePage == targetPage;
        }else {//如果要移除的page就是目前显示的page
            boolean hasPrePage = removeIndex != 0;
            boolean hasAfterPage = removeIndex != count;
            int willShowIndex = hasAfterPage ? (showIndex+1) : (hasPrePage ? showIndex-1 : -1);
            if(willShowIndex >= 0){
                switchToPage(willShowIndex);
                targetPage.onDestroy();
                return super.removePage(targetPage);
            }else {
                if(isAttachToActivity()){
                    targetPage.onHide();
                    targetPage.onHidden();
                }
                if(targetPage.isViewInited()){
                    targetPage.onDestroy();
                }
                showIndex = -1;
                return super.removePage(targetPage);
            }
        }

    }

    public ViewGroup currentContiner() {
        return (FrameLayout) rootView;
    }

    @Override
    public IPage getActiviePage() {
        return getChildPageAt(showIndex);
    }
}
