package com.lzy.addressselector.bean;

/**
 * Title: TestBean <br>
 * @author LiZhengyu.
 */
public class TestBean implements ISelectAble{
    private String name;
    private String id;
    public TestBean(String name,String id){
        this.name = name;
        this.id = id;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getArg() {
        return this;
    }
}
