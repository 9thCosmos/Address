package com.lzy.addressselector.bean;

import java.util.List;

/**
 * Title: CityBean <br>
 * @author LiZhengyu.
 */
public class CityBean extends PinYinSort implements ISelectAble {
    private String id;
    private String name;

    private List<AreaBean> areaList;

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

    public List<AreaBean> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaBean> areaList) {
        this.areaList = areaList;
    }
}
