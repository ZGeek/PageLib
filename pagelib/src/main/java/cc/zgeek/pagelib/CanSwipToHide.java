package cc.zgeek.pagelib;

/**
 * Created by flyop.
 * Change History:
 * 2017/1/10 : Create
 */

//加入NavigationPage的View中，是否支持滑动后退，若不支持，则继承此接口并返回fase，若不继承则认为支持滑动返回
public interface CanSwipToHide {
    public boolean canSwipToHide();
}
