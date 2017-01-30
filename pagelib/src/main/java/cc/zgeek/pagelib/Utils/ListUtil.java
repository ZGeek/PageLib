package cc.zgeek.pagelib.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZGeek.
 * Change History:
 * 2017/1/11 : Create
 */

public class ListUtil {

    public static <T> List<T> subList(List<T> source, int index, int count){
        List<T>  subList = new LinkedList<>();
        for(int i =0; i< count; i++){
            subList.add(source.get(index+i));
        }
        return subList;
    }
    public static <T> List<T> copy(List<T> source){
        LinkedList<T> tmp = new LinkedList<>();
        for (T item : source ){
            tmp.add(item);
        }
        return tmp;
    }
}
