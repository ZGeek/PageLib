package cc.zgeek.pagelib.Utils

import android.os.Bundle
import android.util.Log

import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

import cc.zgeek.pagelib.IPage
import cc.zgeek.pagelib.PageActivity

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/27 : Create
 */


/***
 * Page是否是被激活的状态，依赖于isChildPageActive和PageActivity的状态

 * @param page 测试的page
 * *
 * @return 是否激活 true 激活  false 没激活
 */

fun PageActivity.restorePage(clsName: String, args: Bundle): IPage {

    try {
        val cls = Class.forName(clsName)
        val ctor = cls.getDeclaredConstructor(PageActivity::class.java)
        ctor.isAccessible = true
        val p = ctor.newInstance(this) as IPage
        p.args = args
        return p

    } catch (e: NoSuchMethodException) {
        throw RuntimeException("No <init>(PageActivity) constructor in Page: " +
                clsName +
                ", which is required for page restore/recovery to work.", e)
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
        throw RuntimeException("Can not reflective Page:" + clsName, e)
    } catch (e: InstantiationException) {
        throw RuntimeException("Can not reflective Page:" + clsName, e)
    } catch (e: InvocationTargetException) {
        throw RuntimeException("Can not reflective Page:" + clsName, e)
    } catch (e: ClassNotFoundException) {
        throw RuntimeException("Can not reflective Page:" + clsName, e)
    }

}

object PageUtil {
    fun setSaveInsanceFlag(bundle: Bundle): Bundle {
        bundle.putBoolean("sava_instance_flag", true)
        return bundle
    }

    fun isBundleFromSaveInstance(bundle: Bundle?): Boolean {
        return bundle != null && bundle.getBoolean("sava_instance_flag", false)
    }

    fun printPageTree(page: IPage): String {
        val buffer = StringBuilder(10240)
        buffer.append(page.toString())
        buffer.append("\n")
        realPrintPageTree(1, page, buffer)
        return buffer.toString()
    }

    private fun realPrintPageTree(deep: Int, page: IPage, buffer: StringBuilder) {

        //print all page
        for (childPage in page.getChildPages()) {
            var deepCopy = deep
            while (deepCopy-- > 0) {
                buffer.append("\t")
            }
            buffer.append(childPage.toString())
            buffer.append("\n")
            realPrintPageTree(deep + 1, childPage, buffer)
        }
    }
}
