package cc.zgeek.pagelib;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/10 : Create
 */

/**
 * Child Page in the Navigation Page，Whether to support sliding back，If do not need to support，
 * Then inherited the interface and returns false，If we do not inherit that support sliding back
 */
public interface SupportSwipToHide {
    boolean canSwipToHide();
}
