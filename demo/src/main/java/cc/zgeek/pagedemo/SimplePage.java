package cc.zgeek.pagedemo;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Random;

import cc.zgeek.pagedemo.util.NavHelper;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;


/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/13 : Create
 */

@PageLayout(R.layout.simple_page)
public class SimplePage extends Page implements View.OnClickListener {
    static Random random = new Random();

    final  String index;
    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.content)
    FrameLayout frameLayout;
    public SimplePage(PageActivity pageActivity) {
        super(pageActivity);
        index = random.nextInt()+"";
    }



    @Override
    public void onViewInited() {

        textView.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        frameLayout.setBackgroundColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255) ));
        textView.setText(getName());
        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        NavigationPage page = NavHelper.findFirstNav(this);
        if(page != null){
            page.pushPage(new SimplePage(getContext()));
        }
//        ((NavigationPage) getContext().getRootPage()).pushPage(new SimplePage(getContext()));
    }

    @Override
    public void onShow() {
        super.onShow();
        Log.d(SimplePage.class.getSimpleName(), getName()+"-->onShow");
    }

    @Override
    public void onShown() {
        super.onShown();
        Log.d(SimplePage.class.getSimpleName(), getName()+"-->onShown");
    }

    @Override
    public void onHide() {
        super.onHide();
        Log.d(SimplePage.class.getSimpleName(), getName()+"-->onHide");
    }

    @Override
    public void onHidden() {
        super.onHidden();
        Log.d(SimplePage.class.getSimpleName(), getName()+"-->onHidden");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SimplePage.class.getSimpleName(), getName()+"-->onDestroy");
    }

    @Override
    public String getName() {
        String name =  super.getName();
        if(TextUtils.isEmpty(name))
            return index;
        return name;
    }
}
