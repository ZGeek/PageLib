package cc.zgeek.pagelib;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flyop(YZQ) on 2017/1/19.
 */

public class ViewPagerPage2  extends SingleActivePage{

    private PagerAdapter adapter;
    private int currentShowIndex = 0;
    private ArrayList<ViewPager.OnPageChangeListener> mOnPageChangeListeners;

    public ViewPagerPage2(PageActivity pageActivity) {
        super(pageActivity);
        adapter = new ViewPagerPage.IPageAdapterWrapper();
    }


    @NonNull
    @Override
    public View getRootView() {
        if (rootView == null) {
            synchronized (this) {
                if (rootView == null) {
                    rootView = new ViewPager(mContext);
                    onViewInited();
                }
            }
        }
        return rootView;
    }

    @Override
    public void onViewInited() {
        super.onViewInited();
        ViewPager viewPager = (ViewPager) rootView;
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    public void removeOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.remove(listener);
        }
    }


    @Override
    public IPage getActiviePage() {
        if (getChildPageCount() == 0)
            return null;
        return getChildPageAt(currentShowIndex);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListeners == null || mOnPageChangeListeners.size() == 0)
            return;
        for (int i = 0; i < mOnPageChangeListeners.size(); i++) {
            mOnPageChangeListeners.get(i).onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (isAttachToActivity()) {
//            if (currentShowIndex != position) {
//                IPage oldPage = getChildPageAt(currentShowIndex);
//                oldPage.onHide();
//                oldPage.onHidden();
//            }
//            IPage page = getChildPageAt(position);
//            page.onShow();
//            page.onShown();


            IPage oldPage = getChildPageAt(currentShowIndex);
            IPage page = getChildPageAt(position);

            page.onShow();
            oldPage.onHide();
            page.onShown();
            oldPage.onHidden();
        }
        currentShowIndex = position;
        if (mOnPageChangeListeners == null || mOnPageChangeListeners.size() == 0)
            return;
        for (int i = 0; i < mOnPageChangeListeners.size(); i++) {
            mOnPageChangeListeners.get(i).onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListeners == null || mOnPageChangeListeners.size() == 0)
            return;
        for (int i = 0; i < mOnPageChangeListeners.size(); i++) {
            mOnPageChangeListeners.get(i).onPageScrollStateChanged(state);
        }

    }

    public void switchToPage(int index) {
        switchToPage(index, false);
    }

    public void switchToPage(int index, boolean smoothScroll) {
        ((ViewPager) getRootView()).setCurrentItem(index, smoothScroll);
    }

    @Override
    public void addPage(IPage page) {
        super.addPage(page);
        adapter.notifyDataSetChanged();
    }

    public void addPages(List<IPage> pages) {
        int preCount = getChildPageCount();
        for (int i = 0; i < pages.size(); i++) {
            super.addPage(pages.get(i));
        }
        int afterCount = getChildPageCount();
        if (preCount == 0 && afterCount > 0) {
            //此时第一个Page将显示，但并不会调用onPageSelected，所以此时需要对第一个page做处理
            getChildPageAt(0).onShow();
            adapter.notifyDataSetChanged();
            getChildPageAt(0).onShown();
        } else if (pages.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean removePage(IPage page) {
        /***
         * 根据测试结果，VIewPage在页面移除的逻辑是
         * 1：当移除当前页时优先使用后面的一页，后面没有用前页，前页也没有显示空白
         * 2：移除非当前页时保持当前页继续显示给用户
         */

        int targetRemovePageIndex = getChildPageIndex(page);
        if (targetRemovePageIndex < 0)
            return false;
        if (targetRemovePageIndex == currentShowIndex) {
            IPage willShowPage = getWillShowPageIndexWhenRemove(targetRemovePageIndex);
            if (willShowPage != null)
                willShowPage.onShow();
            page.onHide();
            super.removePage(page);
            adapter.notifyDataSetChanged();
            if (willShowPage != null)
                willShowPage.onShown();
            page.onHidden();
            if(page.isViewInited()){
                page.onDestroy();
            }

        } else if (targetRemovePageIndex < currentShowIndex) {
            super.removePage(page);
            if(page.isViewInited()){
                page.onDestroy();
            }
            adapter.notifyDataSetChanged();
            currentShowIndex--;
        } else {
//            index > currentShowIndex
            super.removePage(page);
            adapter.notifyDataSetChanged();
            if(page.isViewInited()){
                page.onDestroy();
            }
        }

        return true;
    }

    public IPage getWillShowPageIndexWhenRemove(int removeIndex) {
        int childCount = getChildPageCount();
        boolean hasPre = (removeIndex == 0);
        boolean hasAfter = (childCount > (removeIndex + 1));
        if (hasAfter) {
            return getChildPageAt(removeIndex + 1);
        } else {
            if (hasPre)
                return getChildPageAt(removeIndex - 1);
            else
                return null;
        }
    }

    class IPageAdapterWrapper extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IPage page = getChildPageAt(position);
            container.addView(page.getRootView());
            return page;
        }

        @Override
        public int getCount() {
            return getChildPageCount();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((IPage) object).getRootView() == view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            IPage page = (IPage) object;
            container.removeView(page.getRootView());
        }

        @Override
        public int getItemPosition(Object object) {
            int index = getChildPageIndex((IPage) object);
            if (index < 0 || index >= getChildPageCount())
                return POSITION_NONE;
            return index;
        }
    }
}