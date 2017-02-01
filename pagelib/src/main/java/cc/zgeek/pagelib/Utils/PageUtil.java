package cc.zgeek.pagelib.Utils;

import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cc.zgeek.pagelib.IPage;
import cc.zgeek.pagelib.PageActivity;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/27 : Create
 */

public class PageUtil {

    /***
     * Page是否是被激活的状态，依赖于isChildPageActive和PageActivity的状态
     *
     * @param page 测试的page
     * @return 是否激活 true 激活  false 没激活
     */
    public static boolean isPageActive(IPage page) {
        if (!page.getContext().isActive()) {
            return false;
        }
        IPage root = page.getContext().getRootPage();
        IPage child = page;
        if (child == root)
            return true;
        IPage parent = page.getParentPage();

        while (parent != null) {
            boolean isActive = parent.isChildPageActive(child);
            if (!isActive)
                return false;
            if (parent == root)
                return true;
            child = parent;
            parent = parent.getParentPage();
        }
        return false;
    }

    public static IPage restorePage(PageActivity activity, String clsName, Bundle args) {

        try {
            Class cls = Class.forName(clsName);
            final Constructor ctor = cls.getDeclaredConstructor(PageActivity.class);
            ctor.setAccessible(true);
            final IPage p = (IPage) ctor.newInstance(activity);
            p.setArgs(args);
            return p;

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No <init>(PageActivity) constructor in Page: " +
                    clsName +
                    ", which is required for page restore/recovery to work.", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Can not reflective Page:" + clsName, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Can not reflective Page:" + clsName, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Can not reflective Page:" + clsName, e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can not reflective Page:" + clsName, e);
        }
    }
    public static Bundle setSaveInsanceFlag(Bundle bundle){
        if(bundle != null){
            bundle.putBoolean("sava_instance_flag", true);
        }
        return bundle;
    }
    public static boolean isBundleFromSaveInstance(Bundle bundle){
        return bundle != null && bundle.getBoolean("sava_instance_flag", false);
    }
}
