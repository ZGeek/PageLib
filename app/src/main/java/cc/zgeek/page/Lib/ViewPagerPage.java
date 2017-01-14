package cc.zgeek.page.Lib;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/14 : Create
 */

public class ViewPagerPage extends Page {

    public ViewPagerPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @NonNull
    @Override
    protected View initView() {
        ViewPager viewPager = new ViewPager(mContext);
        return viewPager;
    }


    ViewPager currentContainer(){
        return (ViewPager) getRootView();
    }
}
