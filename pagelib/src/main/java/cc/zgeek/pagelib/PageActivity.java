package cc.zgeek.pagelib;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;

import cc.zgeek.pagelib.Utils.PageUtil;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class PageActivity extends AppCompatActivity {
    IPage rootPage;
    Handler handler;
    boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());


        if (savedInstanceState != null) {
            String clsName = savedInstanceState.getString("rootPage");
            Bundle args = savedInstanceState.getBundle("rootData");
            rootPage = PageUtil.restorePage(this, clsName, args);
        }
        if (rootPage == null)
            rootPage = initRootPage();
//        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
//        ViewGroup decor = findDecorView();
//        decor.removeAllViews();
//        decor.addView(rootPage.getRootView());
//        if (savedInstanceState != null)
        setContentView(rootPage.getRootView());
    }

    ViewGroup findDecorView() {
        View view = findViewById(android.R.id.content);
        ViewParent parent = view.getParent();
        do {
            if (parent == null)
                return (ViewGroup) view;
            else {
                view = (View) parent;
                parent = parent.getParent();
            }
        } while (true);
    }

    protected abstract IPage initRootPage();

    public IPage getRootPage() {
        return rootPage;
    }

    @Override
    public void onBackPressed() {
        if (!rootPage.onBackPressed()) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        rootPage.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        isActive = true;
        rootPage.onShow();
        super.onResume();
        rootPage.onShown();
    }

    @Override
    protected void onPause() {
        rootPage.onHide();
        super.onPause();
        rootPage.onHidden();
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    protected void onDestroy() {
        rootPage.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (rootPage.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (rootPage.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (rootPage.onTouchEvent(event)) {
//            return true;
//        }
//        return super.onTouchEvent(event);
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rootPage.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        rootPage.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = rootPage.onSaveInstanceState(rootPage.isViewInited());
        outState.putBundle("rootData", bundle);
        outState.putString("rootPage", rootPage.getClass().getName());
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        rootPage.onRestoreInstanceState(savedInstanceState);
//    }


    public void postDelayed(Runnable runnable, long delayMillis) {
        if (delayMillis == 0 && Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
            return;
        }
        handler.postDelayed(runnable, delayMillis);
    }

    public void post(Runnable runnable) {
        handler.post(runnable);
    }

    public void removeCallbacks(Runnable runnable) {
        handler.removeCallbacks(runnable);
    }
}
