package com.lzy.addressselector.bean;

/**
 * Title: AreaBean <br>
 * @author LiZhengyu.
 */
public class AreaBean extends PinYinSort implements ISelectAble {
    private String id;
    private String name;

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
}
