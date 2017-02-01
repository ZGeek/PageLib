package cc.zgeek.pagedemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public static SimplePage newInstance(PageActivity activity) {
        Bundle args = new Bundle();
        args.putInt("tvBg", Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        args.putInt("frBg", Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));

        SimplePage simplePage = new SimplePage(activity);
        simplePage.setArgs(args);
        return simplePage;
    }

    final String index;
    @InjectView(R.id.text)
    TextView textView;
    @InjectView(R.id.content)
    FrameLayout frameLayout;
    private int tvBg;
    private int frBg;

    private SimplePage(PageActivity pageActivity) {
        super(pageActivity);
        index = random.nextInt() + "";
    }


    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        super.onViewInited(isRestore, args);
//        if (isRestore) {
////            setName(args.getString("name"));
        tvBg = args.getInt("tvBg");
//            if (tvBg == 0)
//                tvBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
        frBg = args.getInt("frBg");
//            if (frBg == 0)
//                frBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
//        } else {
//            tvBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
//            frBg = Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255));
//        }
        textView.setBackgroundColor(tvBg);
        frameLayout.setBackgroundColor(frBg);
        textView.setText(getName());
        textView.setOnClickListener(this);
    }

    @Override
    public Bundle onSaveInstanceState(boolean isViewInited) {
        return super.onSaveInstanceState(false);
//        bundle.putString("name", getName());

//        bundle.putInt("tvBg", getArgs().getInt("tvBg"));
//        bundle.putInt("frBg", getArgs().getInt("frBg"));
//        return bundle;
    }

    @Override
    public void onClick(View v) {
        NavigationPage page = NavHelper.findFirstNav(this);
        if (page != null) {
            page.pushPage(SimplePage.newInstance(getContext()));
        }
//        ((NavigationPage) getContext().getRootPage()).pushPage(new SimplePage(getContext()));
    }

    @Override
    public void onShow() {
        super.onShow();
        Log.d(SimplePage.class.getSimpleName(), getName() + "-->onShow");
    }

    @Override
    public void onShown() {
        super.onShown();
        Log.d(SimplePage.class.getSimpleName(), getName() + "-->onShown");
    }

    @Override
    public void onHide() {
        super.onHide();
        Log.d(SimplePage.class.getSimpleName(), getName() + "-->onHide");
    }

    @Override
    public void onHidden() {
        super.onHidden();
        Log.d(SimplePage.class.getSimpleName(), getName() + "-->onHidden");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(SimplePage.class.getSimpleName(), getName() + "-->onDestroy");
    }

    @Override
    public String getName() {
        String name = super.getName();
        if (TextUtils.isEmpty(name))
            return index;
        return name;
    }
}
