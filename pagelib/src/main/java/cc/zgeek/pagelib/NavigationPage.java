package cc.zgeek.pagelib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.util.List;

import cc.zgeek.pagelib.Utils.PageLibDebugUtis;
import cc.zgeek.pagelib.Utils.PageUtil;
import cc.zgeek.pagelib.anim.PageAnimatorProvider;
import cc.zgeek.pagelib.anim.SimpleAnimListener;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 * <p>
 * NavigationPage，The back stack mechanism for management page，Keep at least one Page，Can only display a Page at same time
 */
public class NavigationPage extends SingleActivePage {

    private static final String TAG = NavigationPage.class.getName();
    private final static int DEFAULT_ANIMATE_TIME = 300; //ms
    private NavigationPageHelper mNavigationPageHelper;
    private ValueAnimator mAnimatedTransitions = null;

    public NavigationPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        super.onViewInited(isRestore, args);
        if (isRestore)
            addViewToContiner(getActiviePage());
        ((NavigationPageHelper.NavigationContainerView) rootView).enableSwipeToHide();
    }

    @NonNull
    @Override
    public View getRootView() {
        if (rootView == null) {
            synchronized (this) {
                if (rootView == null) {
                    mNavigationPageHelper = new NavigationPageHelper(this);
                    rootView = mNavigationPageHelper.createContainerView(getContext());
                    onViewInited(PageUtil.isBundleFromSaveInstance(getArgs()), getArgs());
                }
            }
        }
        return rootView;
    }


    public void pushPage(final IPage newPage) {
        pushPage(newPage, true);
    }

    public void pushPage(final IPage newPage, boolean anim) {
        pushPage(newPage, anim ? getDefaultPushAnimator() : null);
    }

    public void pushPage(final IPage showPage, PageAnimatorProvider provider) {
        if (showPage == null) {
            throw new NullPointerException("the showView can not be NULL");
        }

        final boolean active = PageUtil.isPageActive(this);
        ensureEndAnimationExecution();

        final IPage hidePage = getTopPage();

        addPage(showPage);
        addViewToContiner(showPage);
//        currentContiner().addView(showPage.getRootView());

        if (active) {
            showPage.onShow();
            if (hidePage != null) {
//                hidePage.getRootView().bringToFront();
                hidePage.onHide();
            }
        }


        if (active && provider != null) {
            mAnimatedTransitions = provider.getPageAnimation(currentContiner(), hidePage == null ? null : hidePage.getRootView(), showPage.getRootView());
            mAnimatedTransitions.addListener(new SimpleAnimListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    doFinalWorkForPushPage(hidePage, showPage, active);
                }
            });
            mAnimatedTransitions.start();
        } else {
            doFinalWorkForPushPage(hidePage, showPage, active);
        }

        if (PageLibDebugUtis.isDebug()) {
            Log.d(PageLibDebugUtis.TAG, String.format(">>>> pushPage, pageStack=%d, %s", getChildPageCount(), showPage));
        }
    }

    private void addViewToContiner(IPage page) {
        View view = page.getRootView();
        if (view.getLayoutParams() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, 0, 0);
            view.setLayoutParams(layoutParams);
        }
        currentContiner().addView(view);
    }


    private synchronized void ensureEndAnimationExecution() {
        if (mAnimatedTransitions != null) {
            mAnimatedTransitions.end();
            mAnimatedTransitions = null;
        }
    }


    private void doFinalWorkForPushPage(IPage oldPage, IPage newPage, boolean isAttach) {
        if (isAttach) {
            if (newPage == getTopPage()) {
                newPage.getRootView().bringToFront();
                newPage.getRootView().requestFocus();
                newPage.onShown();
            }
        }

        if (oldPage != null) {
            currentContiner().removeView(oldPage.getRootView());
            if (isAttach) {
                oldPage.onHidden();
            }
        }

        mAnimatedTransitions = null;
    }

    public void popPage() {
        popPage(true);
    }


    public void popPage(boolean anim) {
        popPage(anim ? getDefaultPopAnimator() : null);
    }

    public void popPage(PageAnimatorProvider provider) {
        popTopNPages(1, provider);
    }

    public void popToTargetPage(IPage page) {
        popToTargetPage(page, true);
    }

    public void popToTargetPage(IPage page, boolean anim) {
        popToTargetPage(page, anim ? getDefaultPopAnimator() : null);
    }

    public void popToTargetPage(IPage page, PageAnimatorProvider provider) {

        int currentIndex = -1;
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            if (getChildPageAt(i) == page)
                currentIndex = i;
        }
        if (currentIndex <= 0)
            return;
        int N = getChildPageCount() - currentIndex - 1;
        popTopNPages(N, provider);
    }

    public void popToTargetPage(Class<? extends IPage> clazz) {
        popToTargetPage(clazz, true);
    }

    public void popToTargetPage(Class<? extends IPage> clazz, boolean anim) {
        popToTargetPage(clazz, anim ? getDefaultPopAnimator() : null);
    }

    public void popToTargetPage(Class<? extends IPage> clazz, PageAnimatorProvider provider) {
        int currentIndex = -1;
        for (int i = getChildPageCount() - 1; i >= 0; i--) {
            if (getChildPageAt(i).getClass() == clazz)
                currentIndex = i;
        }
        if (currentIndex <= 0)
            return;
        int N = getChildPageCount() - currentIndex - 1;
        popTopNPages(N, provider);
    }

    public void popToRootPage() {
        popToRootPage(true);
    }

    public void popToRootPage(boolean anim) {
        popToRootPage(anim ? getDefaultPopAnimator() : null);
    }

    public void popToRootPage(PageAnimatorProvider provider) {
        popTopNPages(getChildPageCount() - 1);
    }

    public void popTopNPages(int n) {
        popTopNPages(n, true);
    }

    public void popTopNPages(int n, boolean animated) {
        popTopNPages(n, animated ? getDefaultPopAnimator() : null);
    }

    public void popTopNPages(int n, PageAnimatorProvider provider) {
        popTopNPages(n, provider, false);
    }

    private void popTopNPages(int n, PageAnimatorProvider provider, boolean isFromSwip) {
        ensureEndAnimationExecution();
        if (n <= 0 || n > getChildPageCount() - 1 || getChildPageCount() <= 1)//数据不合法
            return;

        if (n <= 0 || getChildPageCount() <= 0) {
            return;
        }

        final boolean active = PageUtil.isPageActive(this);
        if (getChildPageCount() == 1)
            return;
        getRootView().setEnabled(false);
        IPage topPage = getTopPage();
        final List<IPage> willRemovePages = getSubChildPages(getChildPageCount() - n, n);
        final IPage willShowPage = getChildPageAt(getChildPageCount() - n - 1);
        if (!isFromSwip) {
            prepareForPop(willShowPage, topPage);
        }
        if (active) {
            willShowPage.onShow();
            topPage.onHide();
        }


        if (isFromSwip) {
            provider = getDefaultPopAnimator();
        }
        if (provider != null && active) {
            mAnimatedTransitions = provider.getPageAnimation(currentContiner(), topPage.getRootView(), willShowPage.getRootView());
            mAnimatedTransitions.addListener(new SimpleAnimListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    doFinalWorkForPopPage(willRemovePages, willShowPage, active);
                }
            });
            resetMargin(topPage.getRootView());
            resetMargin(willShowPage.getRootView());
            mAnimatedTransitions.start();
        } else {
            doFinalWorkForPopPage(willRemovePages, willShowPage, active);
        }
    }


    void popFromSwip() {
        popTopNPages(1, null, true);
    }

    private void doFinalWorkForPopPage(List<IPage> willRemovePages, IPage willShowPage, boolean active) {
        if (willShowPage.getRootView().getAnimation() != null) {
            Log.d("Animation Pop End", "" + willShowPage.getRootView().getAnimation().hasEnded());
        }
        if (active) {
            willShowPage.getRootView().bringToFront();
            willShowPage.getRootView().requestFocus();
            willShowPage.onShown();
        }
        IPage topPage = willRemovePages.get(willRemovePages.size() - 1);
        for (int i = willRemovePages.size() - 1; i >= 0; i--) {
            IPage cPage = willRemovePages.get(i);
            if (cPage == topPage) {
                currentContiner().removeView(cPage.getRootView());
                if (active) {
                    cPage.onHidden();
                }
            }
        }

        for (int i = willRemovePages.size() - 1; i >= 0; i--) {
            IPage cPage = willRemovePages.get(i);
            if (cPage.isViewInited()) {
                cPage.onDestroy();
            }
            removePage(cPage);
            if (PageLibDebugUtis.isDebug()) {
                Log.d(PageLibDebugUtis.TAG, String.format("<<<< popPage, pageStack=%d, %s", getChildPageCount(), cPage));
            }
        }
//        mViewTransparentMask.bringToFront();
        getRootView().setEnabled(true);
        mAnimatedTransitions = null;
    }

    public boolean deletePage(int index) {
        ensureEndAnimationExecution();
        if (index <= 0 || index >= getChildPageCount()) {
            return false;
        }
        if (index == getChildPageCount() - 1) {
            popPage(false);
            return true;
        }
        final IPage removedPage = getChildPageAt(index);

        if (removedPage.isViewInited())
            removedPage.onDestroy();
        removePage(removedPage);
        if (PageLibDebugUtis.isDebug()) {
            Log.d(PageLibDebugUtis.TAG, String.format(">>>> deletePage(%d), pageStack=%d, %s", index, getChildPageCount(), getTopPage()));
        }
        return true;
    }


    public IPage getTopPage() {
        return getChildPageCount() == 0 ? null : getChildPageAt(getChildPageCount() - 1);
    }

    public boolean isTopPageCanSwipeToHide() {
        IPage page = getTopPage();
        if (page == null)
            return false;
        if (page instanceof SupportSwipToHide && !((SupportSwipToHide) page).canSwipToHide())
            return false;
        return true;
    }


    public ViewGroup currentContiner() {
        return (ViewGroup) getRootView();
    }

    private PageAnimatorProvider getDefaultPopAnimator() {
        return new PageAnimatorProvider() {
            @Override
            public ValueAnimator getPageAnimation(ViewGroup container, @Nullable final View fromView, final View toView) {
                final int totleWidth = container.getWidth();
                int left = fromView.getLeft();
                int time = Math.abs(DEFAULT_ANIMATE_TIME * (totleWidth - left) / totleWidth);

                ValueAnimator animator = new ValueAnimator();
                animator.setDuration(time).setIntValues(left, totleWidth);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        fromView.setTranslationX((Integer) animation.getAnimatedValue());
                        toView.setTranslationX((Integer) animation.getAnimatedValue() / 2 - totleWidth / 2);
                    }
                });
                animator.addListener(new SimpleAnimListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fromView.setTranslationX(0f);
                        toView.setTranslationX(0f);
                    }
                });
                return animator;

            }
        };
    }

    private PageAnimatorProvider getDefaultPushAnimator() {
        return new PageAnimatorProvider() {
            @Override
            public ValueAnimator getPageAnimation(ViewGroup container, @Nullable final View fromView, final View toView) {
                final int totleWidth = container.getWidth();

                int left = toView.getLeft();
                if (left == 0)
                    left = totleWidth;
                int time = Math.abs(DEFAULT_ANIMATE_TIME * left / totleWidth);


                ValueAnimator animator = new ValueAnimator();
                animator.setDuration(time).setIntValues(left, 0);
//                animator.setDuration(10000);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (Integer) animation.getAnimatedValue();
                        fromView.setTranslationX(value / 2 - totleWidth / 2);
                        toView.setTranslationX(value);
                    }
                });
                animator.addListener(new SimpleAnimListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fromView.setTranslationX(0f);
                        toView.setTranslationX(0f);
                    }
                });
                return animator;
            }
        };
    }

    void cancelSwipeToHide() {
        IPage topPage = getTopPage();
        final IPage prePage = getChildPageAt(getChildPageCount() - 2);
        PageAnimatorProvider provider = getDefaultPushAnimator();

        ensureEndAnimationExecution();
        if (topPage.getRootView().getLeft() > 0) {
            mAnimatedTransitions = provider.getPageAnimation(currentContiner(), prePage.getRootView(), topPage.getRootView());
            resetMargin(prePage.getRootView());
            resetMargin(topPage.getRootView());
            mAnimatedTransitions.addListener(new SimpleAnimListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentContiner().removeView(prePage.getRootView());
                    mAnimatedTransitions = null;

                }
            });

            mAnimatedTransitions.start();
        } else {
            resetMargin(prePage.getRootView());
            resetMargin(topPage.getRootView());
            currentContiner().removeView(prePage.getRootView());
        }


//        mAnimatedTransitions =  getDefaultPushAnimator();
    }

    private void resetMargin(View view) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.leftMargin = 0;
        lp.rightMargin = 0;
        view.setLayoutParams(lp);
        view.requestLayout();
    }


    void prepareForPop(IPage willShowPage, IPage willHidePage) {
        View willShowView = willShowPage.getRootView();
        if (willShowView.getLayoutParams() == null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 0, 0, 0);
            willShowView.setLayoutParams(layoutParams);
        }
        currentContiner().addView(willShowView);
        willHidePage.getRootView().bringToFront();
    }

    //Life Cycle

    @Override
    public IPage getActiviePage() {
        return getTopPage();
    }

    @Override
    public void onShow() {
        ensureEndAnimationExecution();
        super.onShow();
    }

    @Override
    public void onShown() {
        ensureEndAnimationExecution();
        super.onShown();
    }

    @Override
    public void onHide() {
        ensureEndAnimationExecution();
        super.onHide();
    }

    @Override
    public void onHidden() {
        ensureEndAnimationExecution();
        super.onHidden();
    }

    @Override
    public void onDestroy() {
        ensureEndAnimationExecution();
        super.onDestroy();
    }


    @Override
    public boolean onBackPressed() {
        if (super.onBackPressed()) {
            return true;
        }
        if (getChildPageCount() > 1) {
            popPage();
            return true;
        }
        return super.onBackPressed();
    }

    boolean isAnim() {
        return mAnimatedTransitions != null && mAnimatedTransitions.isRunning();
    }
}
