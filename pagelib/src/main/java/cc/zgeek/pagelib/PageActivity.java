package cc.zgeek.pagelib;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

public abstract class PageActivity extends AppCompatActivity {
    IPage rootPage;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handler = new Handler(Looper.getMainLooper());
//        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);

        rootPage = initRootPage();
        setContentView(rootPage.getRootView());
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
        rootPage.onShow();
        super.onResume();
        rootPage.onShown();
    }

    @Override
    protected void onPause() {
        rootPage.onHide();
        super.onPause();
        rootPage.onHidden();
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

    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        rootPage.onSaveInstanceState(outState);
//    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        rootPage.onRestoreInstanceState(savedInstanceState);
//    }


    public void postDelayed(Runnable runnable, long delayMillis) {
        if(delayMillis == 0 && Looper.myLooper() == Looper.getMainLooper()){
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
