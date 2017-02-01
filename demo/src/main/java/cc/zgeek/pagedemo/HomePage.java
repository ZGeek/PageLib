package cc.zgeek.pagedemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cc.zgeek.pagedemo.util.ToolbarHelper;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;
import cc.zgeek.pagelib.anim.PageAnimatorProvider;
import cc.zgeek.pagelib.anim.SimpleAnimListener;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/21 : Create
 */

@PageLayout(R.layout.page_home)
public class HomePage extends Page implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @InjectView(R.id.drawer_layout)
    private DrawerLayout mDrawerLayout;
    @InjectView(R.id.tb_header_bar)
    private Toolbar mTbHeaderBar;
    @InjectView(R.id.navigation_view)
    private NavigationView mNavigationView;
    @InjectView(R.id.tv_text)
    private TextView mTvText;
    @InjectView(R.id.viewPagerBtn)
    private Button viewPagerBtn;
    @InjectView(R.id.btn_simple_tab_page)
    private Button switchPage;
    @InjectView(R.id.list_page)
    private Button listPage;
    @InjectView(R.id.btn_cta_page)
    private Button cus_btn;
    @InjectView(R.id.btn_sub_nav_page)
    private Button sub_nav;

    public HomePage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited(boolean isRestore, Bundle args) {
        super.onViewInited(isRestore, args);
        setupToolbar();
        mTvText.setText(R.string.page_demo_about);
        viewPagerBtn.setOnClickListener(this);
        switchPage.setOnClickListener(this);
        listPage.setOnClickListener(this);
        cus_btn.setOnClickListener(this);
        sub_nav.setOnClickListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public Bundle onSaveInstanceState(boolean isViewInited) {
        return null;
    }

    private void setupToolbar() {
        mTbHeaderBar.setTitle("PageLib · MainPage");
        setupDrawerToggle();
        ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_main,
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("PageLib")
                                .setMessage(R.string.page_demo_about)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), "OK clicked",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                        return true;
                    }
                });
    }

    private void setupDrawerToggle() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                getContext(),
                mDrawerLayout,
                mTbHeaderBar,
                R.string.app_name,
                R.string.app_name
        );

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onClick(View v) {
        actionWithId(v.getId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawers();
        actionWithId(item.getItemId());
        return true;
    }

    private void actionWithId(int id) {
        switch (id) {
            case R.id.viewPagerBtn:
                ((NavigationPage) getContext().getRootPage()).pushPage(new SwipableTabPage(getContext()));
                break;
            case R.id.btn_simple_tab_page:
                ((NavigationPage) getContext().getRootPage()).pushPage(new SwitchTabPage(getContext()));
                break;
            case R.id.list_page:
                ((NavigationPage) getContext().getRootPage()).pushPage(new ListViewPage(getContext()));
                break;
            case R.id.btn_cta_page:
                ((NavigationPage) getContext().getRootPage()).pushPage(SimplePage.newInstance(getContext()), new PageAnimatorProvider() {
                    @Override
                    public ValueAnimator getPageAnimation(ViewGroup container, @Nullable View fromView, View toView) {
                        return getAnim(container, fromView, toView);
                    }
                });
                break;
            case R.id.btn_sub_nav_page:
                ((NavigationPage) getContext().getRootPage()).pushPage(new InnerNavPage(getContext()));
                break;
        }
    }

    private ValueAnimator getAnim(ViewGroup container, @Nullable final View fromView, final View toView) {
        toView.setVisibility(View.INVISIBLE);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(500).setFloatValues(0, 90, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean isFirstHalf = true;
            float lastValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();

                if (isFirstHalf && lastValue > value) {
                    isFirstHalf = false;
                    if (fromView != null)
                        fromView.setVisibility(View.INVISIBLE);
                    toView.setVisibility(View.VISIBLE);

                }

                if (isFirstHalf) {
                    if (fromView != null)
                        fromView.setRotationY(value);
                } else {
                    toView.setRotationY(360-value);
                }
                lastValue = value;
            }
        });
        valueAnimator.addListener(new SimpleAnimListener(){
            @Override
            public void onAnimationStart(Animator animation) {
//                getRootView().setBackgroundColor(0xFF000000);
                toView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                getRootView().setBackgroundColor(0x00000000);
                if (fromView != null){
                    fromView.setVisibility(View.VISIBLE);
                    fromView.setRotationY(0);
                }
                toView.setRotationY(0);

            }
        });
        return valueAnimator;
    }
}