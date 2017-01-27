package cc.zgeek.pagedemo;

import android.content.DialogInterface;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cc.zgeek.pagedemo.util.ToolbarHelper;
import cc.zgeek.pagelib.Annotation.InjectView;
import cc.zgeek.pagelib.Annotation.PageLayout;
import cc.zgeek.pagelib.NavigationPage;
import cc.zgeek.pagelib.Page;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/21 : Create
 */

@PageLayout(R.layout.page_home)
public class HomePage extends Page implements View.OnClickListener {

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

    public HomePage(PageActivity pageActivity) {
        super(pageActivity);
    }

    @Override
    public void onViewInited() {
        super.onViewInited();
        setupToolbar();
        viewPagerBtn.setOnClickListener(this);
    }

    private void setupToolbar() {
        mTbHeaderBar.setTitle("Paginize · MainPage");
        setupDrawerToggle();
        ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_main,
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        new AlertDialog.Builder(mContext)
                                .setTitle("PageLib")
                                .setMessage("Paginize is a light-weight application framework " +
                                        "for Android.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext, "OK clicked",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(mContext, "Cancel clicked", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();
                        return true;
                    }
                });
    }

    private void setupDrawerToggle() {
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                mContext,
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
        switch (v.getId()){
            case R.id.viewPagerBtn:
                ((NavigationPage)mContext.getRootPage()).pushPage(new SwipableTabPage(mContext));
                break;
        }
    }
}