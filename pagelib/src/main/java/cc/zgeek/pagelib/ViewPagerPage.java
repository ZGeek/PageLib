package cc.zgeek.pagelib;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public class ViewPagerPage extends Page implements ViewPager.OnPageChangeListener {

    private ViewPageAdapter adapter;
    private int currentShowIndex = 0;

    public ViewPagerPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @NonNull
    @Override
    protected View initView() {
        ViewPager viewPager = new ViewPager(mContext);
        adapter = new ViewPageAdapter();
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(adapter);
        return viewPager;
    }

    @Override
    public void addPage(IPage page) {
        super.addPage(page);
        adapter.setData(getSubChildPages(0, getChildPageCount()));
    }

    @Override
    public void removePage(IPage page) {
        super.removePage(page);
        adapter.setData(getSubChildPages(0, getChildPageCount()));
    }

    @Override
    public void onShow() {
//        pageState = STATE_SHOWING;
        getChildPageAt(currentShowIndex).onShow();
    }

    @Override
    public void onShown() {
//        pageState = STATE_SHOWN;
        getChildPageAt(currentShowIndex).onShown();
    }

    @Override
    public void onHide() {
//        pageState = STATE_HIDDING;
        getChildPageAt(currentShowIndex).onHide();
    }

    @Override
    public void onHidden() {
//        pageState = STATE_HIDDEN;
        getChildPageAt(currentShowIndex).onHidden();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentShowIndex = position;
        IPage page = getChildPageAt(position);
        page.onShow();
        page.onShown();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class ViewPageAdapter extends PagerAdapter{

        private List<IPage> data;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            IPage page = data.get(position);
            container.addView(page.getRootView());
            return page;
        }

        @Override
        public int getCount() {
            return data == null ? 0 : data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((IPage)object).getRootView() == view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(((IPage)object).getRootView());
        }

        @Override
        public int getItemPosition(Object object) {
            int index = data.indexOf(object);
            if(index < 0)
                return POSITION_NONE;
            else
                return index;
        }

        public void setData(List<IPage> pages) {
            this.data = pages;
            notifyDataSetChanged();
        }
    }
}
