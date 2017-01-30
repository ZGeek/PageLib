package cc.zgeek.pagedemo;

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
    public void onViewInited() {
        NavigationPage nav = new NavigationPage(getContext(), new SimplePage(getContext()));
        frameLayout.addView(nav.getRootView());
        addPage(nav);
    }
}
