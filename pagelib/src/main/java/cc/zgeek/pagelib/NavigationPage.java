package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.List;

import cc.zgeek.pagelib.anim.PageAnimatorProvider;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 * <p>
 * 导航Page，采用回退栈机制对page进行管理，至少保留一个Page，每次只显示一个Page
 */
public class NavigationPage extends SingleActivePage {

    private static final String TAG = NavigationPage.class.getName();
    private final static int DEFAULT_ANIMATE_TIME = 500; //ms
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
    public void onViewInited() {
        super.onViewInited();
        ((NavigationViewManager.NavigationContainerView) rootView).enableSwipeToHide();
    }

    @NonNull
    @Override
    public View getRootView() {
        if (rootView == null) {
            synchronized (this) {
                if (rootView == null) {
                    mNavigationViewManager = new NavigationViewManager(this);
                    rootView = mNavigationViewManager.createContainerView(mContext);
                    onViewInited();
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

    public void pushPage(final IPage newPage, PageAnimatorProvider provider) {

        final boolean isAttach = isAttachToActivity();
        ensureEndAnimationExecution();

        final IPage oldPage = getTopPage();

        if (isAttach) {
            newPage.onShow();
            if (oldPage != null) {
                oldPage.getRootView().bringToFront();
                oldPage.onHide();
            }
        }

        addPage(newPage);
        long time = 0;
        if (isAttach && provider != null) {
            time = provider.startPageAnimation(currentContiner(), oldPage == null ? null : oldPage.getRootView(), newPage.getRootView());
        }

        if (mEnableDebug) {
            Log.d(TAG, String.format(">>>> pushPage, pageStack=%d, %s", getChildPageCount(), newPage));
        }
        if (time != 0)
            time = Math.max(time + 1, 0);
        mAnimatedTransitionsFinishAction = new Runnable() {
            @Override
            public void run() {
                doFinalWorkForPushPage(oldPage, newPage, isAttach);
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


    private void doFinalWorkForPushPage(IPage oldPage, IPage newPage, boolean isAttach) {
        if (isAttach) {
            if (newPage == getTopPage()) {
                newPage.getRootView().bringToFront();
                newPage.getRootView().requestFocus();
                newPage.onShown();
            }
        }

        if (oldPage != null) {
            oldPage.getRootView().setVisibility(View.GONE);
            if (isAttach) {
                oldPage.onHidden();
            }
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

        final boolean isAttach = isAttachToActivity();
        if (getChildPageCount() == 1)
            return;

        IPage topPage = getChildPageAt(getChildPageCount() - 1);
        final List<IPage> willRemovePages = getSubChildPages(getChildPageCount() - n, n);
        final IPage willShowPage = getChildPageAt(getChildPageCount() - n - 1);

        if (isAttach) {
            topPage.onHide();
            willShowPage.onShow();
        }
        willShowPage.getRootView().setVisibility(View.VISIBLE);
//        if(!isFromSwip){
//            currentContiner().addView(willShowPage.getRootView());
//            topPage.getRootView().bringToFront();
//        }
        long time = 0;
        if (isAttach) {
            if (isFromSwip) {
                provider = getSwipPopAnimator();
            }
            if (provider != null) {
                time = provider.startPageAnimation(currentContiner(), topPage.getRootView(), willShowPage.getRootView());
            }
        }

        if (time != 0)
            time = Math.max(time + 1, 0);
        mAnimatedTransitionsFinishAction = new Runnable() {
            @Override
            public void run() {
                doFinalWorkForPopPage(willRemovePages, willShowPage, isAttach);
            }
        };
        mContext.postDelayed(mAnimatedTransitionsFinishAction, time);
    }


    void popFromSwip() {
        popTopNPages(1, null, true);
    }

    private void doFinalWorkForPopPage(List<IPage> willRemovePages, IPage willShowPage, boolean isAttach) {
        IPage topPage = willRemovePages.get(willRemovePages.size() - 1);
        for (int i = willRemovePages.size() - 1; i >= 0; i--) {
            IPage cPage = willRemovePages.get(i);
            currentContiner().removeView(cPage.getRootView());
            if (isAttach && cPage == topPage) {
                cPage.onHidden();
            }
            removePage(cPage);
            if (mEnableDebug) {
                Log.d(TAG, String.format(">>>> popPage, pageStack=%d, %s", getChildPageCount(), cPage));
            }
        }
        if (isAttach) {
            willShowPage.getRootView().bringToFront();
            willShowPage.getRootView().requestFocus();
            willShowPage.onShown();
        }
        for (int i = willRemovePages.size() - 1; i >= 0; i--) {
            IPage cPage = willRemovePages.get(i);
            cPage.onDestroy();
        }
//        mViewTransparentMask.bringToFront();
        mAnimatedTransitionsFinishAction = null;
    }


    public IPage getTopPage() {
        return getChildPageCount() == 0 ? null : getChildPageAt(getChildPageCount() - 1);
    }

    public boolean isTopPageCanSwipeToHide() {
        IPage page = getTopPage();
        if (page instanceof CanSwipToHide && !((CanSwipToHide) page).canSwipToHide())
            return false;
        return true;
    }

    
    public ViewGroup currentContiner() {
        return (ViewGroup) getRootView();
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
                    t2 = Math.abs(DEFAULT_ANIMATE_TIME * left / width * 2);
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
        View mCurrentView = getChildPageAt(getChildPageCount() - 1).getRootView();
        View mPrevView = getChildPageCount() > 1 ? getChildPageAt(getChildPageCount() - 2).getRootView() : null;
        int width = currentContiner().getWidth();
        if (mPrevView != null) {
            resetMargin(mPrevView);
            int t1 = DEFAULT_ANIMATE_TIME * (width / 2 - Math.abs(mPrevView.getLeft())) / (width / 2);
            NavigationViewManager.animateView(mPrevView,
                    Animation.ABSOLUTE, mPrevView.getLeft(),
                    Animation.RELATIVE_TO_PARENT, -0.5f,
                    t1, null);
        }
        resetMargin(mCurrentView);
        int t2 = DEFAULT_ANIMATE_TIME * Math.abs(mCurrentView.getLeft()) / (width);
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
    public void addPage(IPage page) {
        super.addPage(page);
        View view = page.getRootView();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);
        currentContiner().addView(page.getRootView());
//        mViewTransparentMask.bringToFront();
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
}
