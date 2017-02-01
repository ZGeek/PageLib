package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

public interface IPage {
    /***
     * When the return key press callback
     * @return Whether consume this  point ，若消费则不继续往下传递
     */
    boolean onBackPressed();

    /***
     * same as Activity#onActivityResult(int, int, Intent)
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
     * Get the parent page of  current page
     * @return
     */
    IPage getParentPage();

    void setParentPage(IPage parentPage);


    PageActivity getContext();
    boolean isChildPageActive(IPage child);

    /***
     * Get the root view of this Page
     * @return
     */
    @NonNull View getRootView();

    Bundle onSaveInstanceState(boolean isViewInited);

    void setArgs(Bundle args);
    Bundle getArgs();


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
    void onViewInited(boolean isRestore, Bundle args);

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

    IPage setName(String name);
}
