package com.lzy.addressselector.bean;

import java.util.List;

/**
 * Title: ProvinceBean <br>
 * @author LiZhengyu.
 */
public class ProvinceBean extends PinYinSort implements ISelectAble {
    private String id;
    private String name;
    private List<CityBean> cityList;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getArg() {
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityBean> cityList) {
        this.cityList = cityList;
    }
}
