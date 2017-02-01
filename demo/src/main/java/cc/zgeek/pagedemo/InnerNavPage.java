package cc.zgeek.pagedemo;

import android.os.Bundle;
import android.widget.FrameLayout;

import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/30 : Create
 */

@PageLayout(R.layout.combin_page)
public class InnerNavPage extends Page {
    @InjectView(R.id.content)
    FrameLayout frameLayout;

    public InnerNavPage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        super.onViewInited(isRestore, args);
        if(!isRestore){
            NavigationPage nav = new NavigationPage(getContext());
            nav.pushPage(SimplePage.newInstance(getContext()));
            addPage(nav);
        }
        frameLayout.addView(getChildPageAt(0).getRootView());
    }

//    @Override
//    public Bundle onSaveInstanceState(boolean isViewInited) {
//        return null;
//    }
}
