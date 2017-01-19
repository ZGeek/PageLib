package cc.zgeek.pagedemo;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;


/**
 * Created by flyop.
 * Change History:
 * 2017/1/13 : Create
 */

@PageLayout(R.layout.simple_page)
public class SimplePage extends Page {
    static Random random = new Random();
    private final int index;

    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.content)
    FrameLayout frameLayout;
    public SimplePage(PageActivity pageActivity, int index) {
        super(pageActivity);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public void onViewInited() {

        textView.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        frameLayout.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        textView.setText(index + "");
    }
//
//    @Override
//    public void onClick(View v) {
//        ((NavigationPage) getParentPage()).pushPage(new SimplePage(mContext));
//    }

    @Override
    public void onShow() {
        super.onShow();
        Log.d(SimplePage.class.getSimpleName(), index+"-->onShow");
    }

    @Override
    public void onShown() {
        super.onShown();
        Log.d(SimplePage.class.getSimpleName(), index+"-->onShown");
    }

    @Override
    public void onHide() {
        super.onHide();
        Log.d(SimplePage.class.getSimpleName(), index+"-->onHide");
    }

    @Override
    public void onHidden() {
        super.onHidden();
        Log.d(SimplePage.class.getSimpleName(), index+"-->onHidden");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SimplePage.class.getSimpleName(), index+"-->onDestroy");
    }
}
