package com.lzy.addressselector;


import com.lzy.addressselector.bean.PinYinSort;

import java.util.Comparator;

/**
 * Title: PinYinComparator <br>
 * @author LiZhengyu.
 */
public class PinYinComparator implements Comparator<PinYinSort> {
    @Override
    public int compare(PinYinSort o1, PinYinSort o2) {
        if ("@".equals(o1.getSortLetters()) || "#".equals(o2.getSortLetters())) {
            return -1;
        } else if ("#".equals(o1.getSortLetters()) || "@".equals(o2.getSortLetters())) {
            return 1;
        } else {
            return o1.getPinyin().compareTo(o2.getPinyin());
        }
    }
}
