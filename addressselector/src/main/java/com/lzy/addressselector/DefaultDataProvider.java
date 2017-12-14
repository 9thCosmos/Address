package com.lzy.addressselector;

import android.content.Context;


import com.lzy.addressselector.bean.CityBean;
import com.lzy.addressselector.bean.ISelectAble;
import com.lzy.addressselector.bean.ProvinceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: DefaultDataProvider <br>
 * @author LiZhengyu.
 */
public class DefaultDataProvider implements DataProvider{
    private Context mContext;
    private List<ProvinceBean> allProvinceList;

    public DefaultDataProvider(Context context){
        mContext = context;
        initData();
    }

    public void initData(){
        if(ChinaAddressHelper.getInstance().getAllProvinceList() == null){
            ChinaAddressHelper.getInstance().initData(mContext.getApplicationContext());
        }
        allProvinceList = ChinaAddressHelper.getInstance().getAllProvinceList();
    }

    @Override
    public void provideData(int currentDeep, ISelectAble preData, DataReceiver receiver) {
        if(currentDeep == 1){
            ArrayList<ISelectAble> data = new ArrayList<>();
            data.addAll(allProvinceList);
            receiver.send(data);
        }else if(currentDeep == 2){
            ArrayList<ISelectAble> data = new ArrayList<>();
            if(preData instanceof ProvinceBean){
                if(((ProvinceBean)preData).getCityList() != null){
                    data.addAll(((ProvinceBean)preData).getCityList());
                }
            }
            receiver.send(data);
        }else{
            ArrayList<ISelectAble> data = new ArrayList<>();
            if(preData instanceof CityBean){
                if(((CityBean)preData).getAreaList() != null){
                    data.addAll(((CityBean)preData).getAreaList());
                }
            }
            receiver.send(data);
        }
    }
}
