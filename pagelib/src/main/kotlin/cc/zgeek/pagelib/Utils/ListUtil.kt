package cc.zgeek.pagelib.Utils

import java.util.LinkedList

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/11 : Create
 */

object ListUtil {

    fun <T> subList(source: List<T>, index: Int, count: Int): List<T> {
        val subList = LinkedList<T>()
        for (i in 0..count - 1) {
            subList.add(source[index + i])
        }
        return subList
    }

    fun <T> copy(source: List<T>): List<T> {
        val tmp = LinkedList<T>()
        for (item in source) {
            tmp.add(item)
        }
        return tmp
    }
}
