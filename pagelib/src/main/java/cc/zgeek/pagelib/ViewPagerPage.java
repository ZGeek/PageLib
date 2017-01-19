package cc.zgeek.pagelib;


import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public class ViewPagerPage extends SingleActivePage implements ViewPager.OnPageChangeListener {


    private IPageAdapterWrapper adapterWrapper;
    private int currentShowIndex = 0;
    private ArrayList<ViewPager.OnPageChangeListener> mOnPageChangeListeners;

    public ViewPagerPage(PageActivity pageActivity) {
        super(pageActivity);
        adapterWrapper = new IPageAdapterWrapper();
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
        viewPager.setAdapter(adapterWrapper);
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
            if (currentShowIndex != position) {
                IPage oldPage = getChildPageAt(currentShowIndex);
                oldPage.onHide();
                oldPage.onHidden();
            }
            IPage page = getChildPageAt(position);
            page.onShow();
            page.onShown();
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
        adapterWrapper.notifyDataSetChanged();
    }

    public void addPages(List<IPage> pages) {
        for (int i=0; i< pages.size(); i ++){
            super.addPage(pages.get(i));
        }
        adapterWrapper.notifyDataSetChanged();
    }

    @Override
    public void removePage(IPage page) {
        super.removePage(page);
        adapterWrapper.notifyDataSetChanged();
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
            int index = getChildPageIndex(page);
            if (index < 0)
                page.onDestroy();
        }

        @Override
        public int getItemPosition(Object object) {
            int index = getChildPageIndex((IPage) object);
            if (index < 0 || index >=getChildPageCount())
                return POSITION_NONE;
            return index;
        }
    }

}
