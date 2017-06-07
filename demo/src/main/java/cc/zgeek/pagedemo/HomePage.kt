package cc.zgeek.pagedemo

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import cc.zgeek.pagedemo.util.ToolbarHelper
import cc.zgeek.pagelib.Annotation.InjectView
import cc.zgeek.pagelib.Annotation.PageLayout
import cc.zgeek.pagelib.NavigationPage
import cc.zgeek.pagelib.Page
import cc.zgeek.pagelib.PageActivity
import cc.zgeek.pagelib.anim.PageAnimatorProvider
import cc.zgeek.pagelib.anim.SimpleAnimListener

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/21 : Create
 */

@PageLayout(R.layout.page_home)
class HomePage(pageActivity: PageActivity) : Page(pageActivity), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @InjectView(R.id.drawer_layout)
    private lateinit  var mDrawerLayout: DrawerLayout
    @InjectView(R.id.tb_header_bar)
    private lateinit var mTbHeaderBar: Toolbar
    @InjectView(R.id.navigation_view)
    private lateinit var mNavigationView: NavigationView
    @InjectView(R.id.tv_text)
    private lateinit var mTvText: TextView
    @InjectView(R.id.viewPagerBtn)
    private lateinit var viewPagerBtn: Button
    @InjectView(R.id.btn_simple_tab_page)
    private lateinit var switchPage: Button
    @InjectView(R.id.list_page)
    private lateinit var listPage: Button
    @InjectView(R.id.btn_cta_page)
    private lateinit var cus_btn: Button
    @InjectView(R.id.btn_sub_nav_page)
    private lateinit var sub_nav: Button

    override fun onViewInited(isRestore: Boolean, args: Bundle) {
        super.onViewInited(isRestore, args)
        setupToolbar()
        mTvText.setText(R.string.page_demo_about)
        viewPagerBtn.setOnClickListener(this)
        switchPage.setOnClickListener(this)
        listPage.setOnClickListener(this)
        cus_btn.setOnClickListener(this)
        sub_nav.setOnClickListener(this)
        mNavigationView.setNavigationItemSelectedListener(this)
    }

    override fun onSaveInstanceState(isViewInited: Boolean): Bundle {
        return Bundle()
    }

    private fun setupToolbar() {
        mTbHeaderBar.title = "PageLib · MainPage"
        setupDrawerToggle()
        ToolbarHelper.setupMenu(mTbHeaderBar, R.menu.menu_main,
                Toolbar.OnMenuItemClickListener {
                    AlertDialog.Builder(context)
                            .setTitle("PageLib")
                            .setMessage(R.string.page_demo_about)
                            .setPositiveButton("OK") { dialog, which ->
                                Toast.makeText(context, "OK clicked",
                                        Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Cancel") { dialog, which -> Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show() }
                            .show()
                    true
                })
    }

    private fun setupDrawerToggle() {
        val mDrawerToggle = ActionBarDrawerToggle(
                context,
                mDrawerLayout,
                mTbHeaderBar,
                R.string.app_name,
                R.string.app_name
        )

        mDrawerToggle.isDrawerIndicatorEnabled = true
        mDrawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
    }

    override fun onClick(v: View) {
        actionWithId(v.id)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDrawerLayout.closeDrawers()
        actionWithId(item.itemId)
        return true
    }

    private fun actionWithId(id: Int) {
        when (id) {
            R.id.viewPagerBtn -> (context.rootPage as NavigationPage).pushPage(SwipeableTabPage(context))
            R.id.btn_simple_tab_page -> (context.rootPage as NavigationPage).pushPage(SwitchTabPage(context))
            R.id.list_page -> (context.rootPage as NavigationPage).pushPage(ListViewPage(context))
            R.id.btn_cta_page -> (context.rootPage as NavigationPage).pushPage(SimplePage.newInstance(context), object : PageAnimatorProvider {
                override fun getPageAnimation(container: ViewGroup, fromView: View?, toView: View): ValueAnimator {
                    return getAnim(container, fromView, toView)
                }
            })
            R.id.btn_sub_nav_page -> (context.rootPage as NavigationPage).pushPage(InnerNavPage(context))
        }
    }

    private fun getAnim(container: ViewGroup, fromView: View?, toView: View): ValueAnimator {
        toView.visibility = View.INVISIBLE
        val valueAnimator = ValueAnimator()
        valueAnimator.setDuration(500).setFloatValues(0f, 90f, 0f)
        valueAnimator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            internal var isFirstHalf = true
            internal var lastValue: Float = 0.toFloat()

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedValue as Float

                if (isFirstHalf && lastValue > value) {
                    isFirstHalf = false
                    if (fromView != null)
                        fromView.visibility = View.INVISIBLE
                    toView.visibility = View.VISIBLE

                }

                if (isFirstHalf) {
                    if (fromView != null)
                        fromView.rotationY = value
                } else {
                    toView.rotationY = 360 - value
                }
                lastValue = value
            }
        })
        valueAnimator.addListener(object : SimpleAnimListener() {
            override fun onAnimationStart(animation: Animator) {
                //                getRootView().setBackgroundColor(0xFF000000);
                toView.visibility = View.INVISIBLE
            }

            override fun onAnimationEnd(animation: Animator) {
                //                getRootView().setBackgroundColor(0x00000000);
                if (fromView != null) {
                    fromView.visibility = View.VISIBLE
                    fromView.rotationY = 0f
                }
                toView.rotationY = 0f

            }
        })
        return valueAnimator
    }
}