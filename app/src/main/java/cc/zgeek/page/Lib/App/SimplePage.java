package cc.zgeek.page.Lib.App;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

import cc.zgeek.page.Lib.NavigationPage;
import cc.zgeek.page.Lib.Page;
import cc.zgeek.page.Lib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/13 : Create
 */

public class SimplePage extends Page implements View.OnClickListener {
    static Random random = new Random();

    public SimplePage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    protected View initView() {
        FrameLayout layout = new FrameLayout(mContext);

        TextView textView = new TextView(mContext);
        textView.setText(new Random().nextLong() + "");
        textView.setOnClickListener(this);
        textView.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        textView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));

        layout.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));


        layout.addView(textView);

        return layout;
    }

    @Override
    public void onClick(View v) {
        ((NavigationPage) mContext.getRootPage()).pushPage(new SimplePage(mContext));
    }

    @Override
    public void onShow() {
        super.onShow();
        Log.d(SimplePage.class.getSimpleName(), this+"-->onShow");
    }

    @Override
    public void onShown() {
        super.onShown();
        Log.d(SimplePage.class.getSimpleName(), this+"-->onShown");
    }

    @Override
    public void onHide() {
        super.onHide();
        Log.d(SimplePage.class.getSimpleName(), this+"-->onHide");
    }

    @Override
    public void onHidden() {
        super.onHidden();
        Log.d(SimplePage.class.getSimpleName(), this+"-->onHidden");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SimplePage.class.getSimpleName(), this+"-->onDestroy");
    }
}
