package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public interface IPage {
    /***
     * 当返回键按下时回调此函数
     * @return 是否消费此次点及，若消费则不继续往下传递
     */
    boolean onBackPressed();

    /***
     *同Activity的onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean onKeyDown(int keyCode, KeyEvent event);

    boolean onKeyUp(int keyCode, KeyEvent event);

//    boolean onTouchEvent(MotionEvent event);

    void onConfigurationChanged(Configuration newConfig);

    /***
     * 得到当前page的父page，即其附加在其上的page
     * @return
     */
    IPage getParentPage();

    /***
     * 设置当前的page
     * @param parentPage
     */
    void setParentPage(IPage parentPage);

//    /***
//     * 是否已经附加到Activity上了
//     * @return
//     */
//    boolean isAttachToActivity();


    PageActivity getContext();
    boolean isChildPageActive(IPage child);

    /***
     * 得到此Page的根视图
     * @return
     */
    @NonNull View getRootView();

//    void onSaveInstanceState(Bundle outState);
//
//    void onRestoreInstanceState(Bundle savedInstanceState);



//    void addPage(IPage page);
//
//    boolean removePage(IPage page);

    /***
     * getChildPageAt 得到在index位置处的子page
     */
    IPage getChildPageAt(int index);
    int getChildPageIndex(@NonNull IPage page);
    List<IPage> getSubChildPages(int beginIndex, int count);
    int getChildPageCount();

    /***
     * 当视图被创建后调用，每个page只会被调用一次
     */
    void onViewInited();

    /***
     * 用于判断视图是否被初始化，
     * @return
     */
    boolean isViewInited();


    /***
     * Life Cycle Relation
     * 将要显示时的回调，对于NavigationPage，其调用时机是在转场动画开始时调用，
     * 对于viewpagerpage，其和onshow的调用时机几乎一致，同样的道理同onHide与onHidden
     * 对于Page的显示与隐藏，其调用的顺序为：
     * willShowPage.onShow()->willHidePage.onHide()->willShowPage.onShown()->willHidePage.onHidden()-> willHidePage.onDestroy()
     * 其中willHidePage.onDestroy()的调用取决于page的view是否初始化，并且page是否真正的从childPageList中移除，而非简单的隐藏
     */
    void onShow();
    //view已经显示时的回调
    void onShown();
    //将要隐藏时的回调
    void onHide();
    //已经隐藏时的回调
    void onHidden();
    //page将被销毁时调用，如果此page的isViewInited返回false，则不会调用onDestroy,在调用onDestroy时，根据getParent不一定能找到其父Page
    void onDestroy();

    boolean onMenuPressed();

    //收到系统的内存警告时调用
    void onLowMemory();

    String getName();

    void setName(String name);
}
