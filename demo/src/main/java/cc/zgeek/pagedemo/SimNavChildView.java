package cc.zgeek.pagedemo;

import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop(YZQ) on 2017/1/19.
 */

@PageLayout(R.layout.simple_page)
public class SimNavChildView extends Page implements View.OnClickListener {

    static Random random = new Random();

    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.content)
    FrameLayout frameLayout;

    public SimNavChildView(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited() {
        textView.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        frameLayout.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        textView.setOnClickListener(this);
        textView.setText("" + random.nextInt());
    }


    @Override
    public void onClick(View v) {
        ((NavigationPage)getParentPage()).pushPage(new SimNavChildView(mContext));
    }
}
