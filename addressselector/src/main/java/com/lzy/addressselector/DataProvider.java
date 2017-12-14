package com.lzy.addressselector;


import com.lzy.addressselector.bean.ISelectAble;

import java.util.List;

/**
 * Created by dun on 17/2/9.
 */

public interface DataProvider {
    void provideData(int currentDeep, ISelectAble preData, DataReceiver receiver);


    interface DataReceiver {
        void send(List<ISelectAble> data);
    }
}
