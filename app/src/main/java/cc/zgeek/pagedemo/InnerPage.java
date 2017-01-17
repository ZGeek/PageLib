package cc.zgeek.pagedemo;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/17 : Create
 */

public class InnerPage extends Page {

    @InjectView(R.id.contentView)
    FrameLayout frameLayout;

    public InnerPage(PageActivity pageActivity) {
        super(pageActivity);
        NavigationPage navigationPage = new NavigationPage(pageActivity, new SimplePage(pageActivity));
        addPage(navigationPage);
        frameLayout.addView(navigationPage.getRootView());
    }

    @NonNull
    @Override
    protected View initView() {
        return mContext.getLayoutInflater().inflate(R.layout.inner_scroll_test, null);
    }
}
