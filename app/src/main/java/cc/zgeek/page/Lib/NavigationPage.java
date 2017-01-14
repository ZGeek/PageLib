package cc.zgeek.page.Lib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.List;

import cc.zgeek.page.Lib.Utils.ListUtil;
import cc.zgeek.page.Lib.anim.PageAnimatorProvider;
import cc.zgeek.page.R;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 *
 * 导航Page，采用回退栈机制对page进行管理，至少保留一个Page，每次只显示一个Page
 */
public class NavigationPage extends Page {

    private static final String TAG = NavigationPage.class.getName();
    private final static int DEFAULT_ANIMATE_TIME = 500; //ms
    //    private View mViewTransparentMask;
    private NavigationViewManager mNavigationViewManager;
    private boolean mEnableDebug;
    //    private boolean mUseSwipePageTransitionEffect;
    //View的转场动画结束时的action，用于提前结束View的转场动画时提前做操作
    private Runnable mAnimatedTransitionsFinishAction = null;


    public NavigationPage(PageActivity pageActivity, IPage rootPage) {
        super(pageActivity);
        addPage(rootPage);
    }

    @Override
    protected View initView() {
        mNavigationViewManager = new NavigationViewManager(this);
        View view = mNavigationViewManager.createContainerView(mContext);
        mNavigationViewManager.enableSwipeToHide(true);
        return view;
    }

    public void pushPage(final IPage newPage) {
        pushPage(newPage, true);
    }

    public void pushPage(final IPage newPage, boolean anim) {
        pushPage(newPage, anim ? getDefaultPushAnimator() : null);
    }

    public void pushPage(final IPage newPage, PageAnimatorProvider provider) {

        ensureEndAnimationExecution();

        final IPage oldPage = getTopPage();

        newPage.onShow();
        if (oldPage != null) {
            oldPage.getRootView().bringToFront();
            oldPage.onHide();
        }
        addPage(newPage);
        long time = 0;
        if (provider != null) {
            time = provider.startPageAnimation(currentContiner(), oldPage == null ? null : oldPage.getRootView(), newPage.getRootView());
        }

        if (mEnableDebug) {
            Log.d(TAG, String.format(">>>> pushPage, pageStack=%d, %s", pageList.size(), newPage));
        }
        if (time != 0)
            time = Math.max(time + 1, 0);
        mAnimatedTransitionsFinishAction = new Runnable() {
            @Override
            public void run() {
                doFinalWorkForPushPage(oldPage, newPage);
            }
        };
        mContext.postDelayed(mAnimatedTransitionsFinishAction, time);
    }

    private synchronized void ensureEndAnimationExecution() {
        if (mAnimatedTransitionsFinishAction != null) {
            mContext.removeCallbacks(mAnimatedTransitionsFinishAction);
            mAnimatedTransitionsFinishAction.run();
            mAnimatedTransitionsFinishAction = null;
        }
    }


    private void doFinalWorkForPushPage(IPage oldPage, IPage newPage) {
        if (newPage == getTopPage()) {
            newPage.getRootView().bringToFront();
            newPage.getRootView().requestFocus();
            newPage.onShown();
        }

        if (oldPage != null) {
            oldPage.getRootView().setVisibility(View.GONE);
            oldPage.onHidden();
        }
        mAnimatedTransitionsFinishAction = null;
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
        for (int i = getPageCount() - 1; i >= 0; i--) {
            if (pageList.get(i) == page)
                currentIndex = i;
        }
        if (currentIndex <= 0)
            return;
        int N = getPageCount() - currentIndex - 1;
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
        for (int i = getPageCount() - 1; i >= 0; i--) {
            if (pageList.get(i).getClass() == clazz)
                currentIndex = i;
        }
        if (currentIndex <= 0)
            return;
        int N = getPageCount() - currentIndex - 1;
        popTopNPages(N, provider);
    }

    public void popToRootPage() {
        popToRootPage(true);
    }

    public void popToRootPage(boolean anim) {
        popToRootPage(anim ? getDefaultPopAnimator() : null);
    }

    public void popToRootPage(PageAnimatorProvider provider) {
        popTopNPages(getPageCount() - 1);
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
        if (n <= 0 || n > getPageCount() - 1 || getPageCount() <= 1)//数据不合法
            return;

        if (n <= 0 || pageList.size() <= 0) {
            return;
        }
        ensureEndAnimationExecution();

        IPage topPage = pageList.getLast();
        final List<IPage> willRemovePages = ListUtil.subList(pageList, pageList.size() - n, n);
        final IPage willShowPage = pageList.get(pageList.size() - n - 1);

        topPage.onHide();
        willShowPage.onShow();
        willShowPage.getRootView().setVisibility(View.VISIBLE);
        long time = 0;
        if (isFromSwip) {
            provider = getSwipPopAnimator();
        }
        if (provider != null) {
            time = provider.startPageAnimation(currentContiner(), topPage.getRootView(), willShowPage.getRootView());
        }
        if (time != 0)
            time = Math.max(time + 1, 0);
        mAnimatedTransitionsFinishAction = new Runnable() {
            @Override
            public void run() {
                doFinalWorkForPopPage(willRemovePages, willShowPage);
            }
        };
        mContext.postDelayed(mAnimatedTransitionsFinishAction, time);
    }


    void popFromSwip() {
        popTopNPages(1, null, true);
    }

    private void doFinalWorkForPopPage(List<IPage> willRemovePages, IPage willShowPage) {
        IPage topPage = willRemovePages.get(willRemovePages.size() - 1);
        for (int i = willRemovePages.size() - 1; i >= 0; i--) {
            IPage cPage = willRemovePages.get(i);
            currentContiner().removeView(cPage.getRootView());
            if (cPage == topPage) {
                cPage.onHidden();
            }
            pageList.remove(cPage);
            if (mEnableDebug) {
                Log.d(TAG, String.format(">>>> popPage, pageStack=%d, %s", pageList.size(), cPage));
            }
        }
        willShowPage.getRootView().requestFocus();
        willShowPage.onShown();
        for (int i = willRemovePages.size() - 1; i >= 0; i--) {
            IPage cPage = willRemovePages.get(i);
            cPage.onDestroy();
        }
        mAnimatedTransitionsFinishAction = null;
    }

    public int getPageCount() {
        return pageList.size();
    }

    public IPage getTopPage() {
        return pageList.isEmpty() ? null : pageList.getLast();
    }

    public IPage getPageAt(int index) {
        return pageList.get(index);
    }

    public boolean isTopPageCanSwipeToHide() {
        IPage page = getTopPage();
        if (page instanceof CanSwipToHide && !((CanSwipToHide) page).canSwipToHide())
            return false;
        return true;
    }

    public NavigationViewManager.NavigationContainerView currentContiner() {
        return (NavigationViewManager.NavigationContainerView) rootView;
    }

    private PageAnimatorProvider getSwipPopAnimator() {
        return new PageAnimatorProvider() {
            @Override
            public long startPageAnimation(ViewGroup container, @Nullable View fromView, View toView) {
                int width = container.getWidth();
                long t1 = 0, t2 = 0;
                if (fromView != null) {
                    int left = fromView.getLeft();
                    t1 = Math.abs(DEFAULT_ANIMATE_TIME * (width - left) / width);
//                    t1 = 10000;
                    resetMargin(fromView);
                    NavigationViewManager.animateView(fromView,
                            Animation.ABSOLUTE, left,
                            Animation.RELATIVE_TO_PARENT, 1.0f,
                            t1, null);
                }
                if (toView != null) {
                    int left = toView.getLeft();
                    t2 = Math.abs(DEFAULT_ANIMATE_TIME * left / width*2);
//                    t2 = 10000;
                    resetMargin(toView);
                    NavigationViewManager.animateView(toView,
                            Animation.ABSOLUTE, left,
                            Animation.RELATIVE_TO_PARENT, 0.0f,
                            t2, null);
                }
                return Math.max(t1, t2);
            }
        };
    }

    private PageAnimatorProvider getDefaultPopAnimator() {
        return new PageAnimatorProvider() {
            @Override
            public long startPageAnimation(ViewGroup container, @Nullable View fromView, View toView) {
                long t1 = 0, t2 = 0;
                if (fromView != null) {
                    Animation fromAnim = AnimationUtils.loadAnimation(mContext, R.anim.pop_hide_anim);
//                    fromAnim.setDuration(10000);
                    t1 = fromAnim.getDuration();
                    fromView.startAnimation(fromAnim);
                }
                if (toView != null) {
                    Animation toAnim = AnimationUtils.loadAnimation(mContext, R.anim.pop_show_anim);
//                    toAnim.setDuration(10000);
                    t2 = toAnim.getDuration();
                    toView.startAnimation(toAnim);
                }
                return Math.max(t1, t2);
            }
        };
    }

    private PageAnimatorProvider getDefaultPushAnimator() {
        return new PageAnimatorProvider() {
            @Override
            public long startPageAnimation(ViewGroup container, @Nullable View fromView, View toView) {
                long t1 = 0, t2 = 0;
                if (fromView != null) {
                    Animation fromAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_hide_anim);
//                    fromAnim.setDuration(10000);
                    t1 = fromAnim.getDuration();
                    fromView.startAnimation(fromAnim);
                }
                if (toView != null) {
                    Animation toAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_show_anim);
//                    toAnim.setDuration(10000);
                    t2 = toAnim.getDuration();
                    toView.startAnimation(toAnim);
                }
                return Math.max(t1, t2);
            }
        };
    }

    void cancelSwipeToHide() {
        View mCurrentView = pageList.getLast().getRootView();
        View mPrevView = getPageCount() > 1 ? getPageAt(getPageCount() - 2).getRootView() : null;
        int width = currentContiner().getWidth();
        if (mPrevView != null) {
            resetMargin(mPrevView);
            int t1 = DEFAULT_ANIMATE_TIME * (width/2 - Math.abs(mPrevView.getLeft()))/(width/2);
            NavigationViewManager.animateView(mPrevView,
                    Animation.ABSOLUTE, mPrevView.getLeft(),
                    Animation.RELATIVE_TO_PARENT, -0.5f,
                    t1, null);
        }
        resetMargin(mCurrentView);
        int t2 = DEFAULT_ANIMATE_TIME * Math.abs(mCurrentView.getLeft())/(width);
        NavigationViewManager.animateView(mCurrentView,
                Animation.ABSOLUTE, mCurrentView.getLeft(),
                Animation.RELATIVE_TO_PARENT, 0,
                t2, null);
    }

    private void resetMargin(View view) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.leftMargin = 0;
        lp.rightMargin = 0;
        view.requestLayout();
    }

    public void setmEnableDebug(boolean mEnableDebug) {
        this.mEnableDebug = mEnableDebug;
    }

    @Override
    protected void addPage(IPage page) {
        super.addPage(page);
        View view = page.getRootView();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);
        currentContiner().addView(page.getRootView());
    }

    //Life Cycle

    @Override
    public void onShow() {
        ensureEndAnimationExecution();
        if (getPageCount() > 0) {
            pageList.getLast().onShow();
        }
    }

    @Override
    public void onShown() {
        ensureEndAnimationExecution();
        if (getPageCount() > 0) {
            pageList.getLast().onShown();
        }
    }

    @Override
    public void onHide() {
        ensureEndAnimationExecution();
        if (getPageCount() > 0) {
            pageList.getLast().onHide();
        }
    }

    @Override
    public void onHidden() {
        ensureEndAnimationExecution();
        if (getPageCount() > 0) {
            pageList.getLast().onHidden();
        }
    }

    @Override
    public void onDestroy() {
        ensureEndAnimationExecution();
        for (int i = getPageCount() - 1; i >= 0; i--) {
            getPageAt(i).onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getPageCount() > 0) {
            if (getTopPage().onKeyDown(keyCode, event))
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onMenuPressed() {
        if (getPageCount() > 0) {
            if (getTopPage().onMenuPressed())
                return true;
        }
        return super.onMenuPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (getPageCount() > 0) {
            if (getTopPage().onKeyUp(keyCode, event))
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onBackPressed() {
        if (getPageCount() > 0) {
            if (getTopPage().onBackPressed())
                return true;
        }
        if (getPageCount() > 1) {
            popPage();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPageCount() > 0) {
            getTopPage().onActivityResult(requestCode, resultCode, data);
        }
    }
}
