package com.lzy.addressselector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.lzy.addressselector.bean.ISelectAble;
import com.lzy.addressselector.bean.TestBean;

import java.util.ArrayList;

/**
 * Title: TestAddressSelectorActivity <br>
 * @author LiZhengyu.
 */
public class TestAddressSelectorActivity extends AppCompatActivity{
    Selector selector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_selector_test);
        Button btnTest = (Button)findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        selector = new Selector(this, 3,Constants.LAYOUT_GRID); //层级：3表示3级点击

        /*selector.setDataProvider(new DataProvider() {
            @Override
            public void provideData(final int currentDeep, final ISelectAble preData, DataReceiver receiver) {
                //根据tab的深度和前一项选择的id，获取下一级菜单项

                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        ArrayList<ISelectAble> data = getDatas(currentDeep,preData);
                        selector.sendData(data);
                    }
                }, 2000);
            }
        });*/
        selector.setDataProvider(new DefaultDataProvider(this));
        selector.setSelectedListener(new SelectedListener() {
            @Override
            public void onAddressSelected(ArrayList<ISelectAble> selectAbles) {
                String result = "";
                for (ISelectAble selectAble : selectAbles) {
                    result += selectAble.getName()+" ";
                }
                Toast.makeText(TestAddressSelectorActivity.this,result,Toast.LENGTH_SHORT).show();
            }
        });

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        selector.setTabWidth(metric.widthPixels/3);
        selector.setSearchEnable(3,true);

        TextView view = new TextView(this);
        view.setText("木有数据'");
        selector.setNoResultView(view);

        AddressSelectorDialog dialog = new AddressSelectorDialog(this);
        dialog.init(this,selector);
        dialog.show();
    }

    private ArrayList<ISelectAble> getDatas(int currentDeep, ISelectAble preData){
        if(currentDeep == 1){
            ArrayList<ISelectAble> data = new ArrayList<>();
            data.add(new TestBean("广西","1"));
            data.add(new TestBean("北京","2"));
            data.add(new TestBean("浙江","3"));
            data.add(new TestBean("西藏","4"));
            return data;
        }else if(currentDeep == 2){
            if(preData.getId().equals("1")){
                ArrayList<ISelectAble> data = new ArrayList<>();
                data.add(new TestBean("桂林","11"));
                data.add(new TestBean("南宁","12"));
                data.add(new TestBean("玉林","13"));
                return data;
            }else if(preData.getId().equals("2")){
                ArrayList<ISelectAble> data = new ArrayList<>();
                data.add(new TestBean("北京","21"));
                return data;
            }else if(preData.getId().equals("3")){
                ArrayList<ISelectAble> data = new ArrayList<>();
                data.add(new TestBean("杭州","31"));
                data.add(new TestBean("宁波","32"));
                data.add(new TestBean("温州","33"));
                data.add(new TestBean("绍兴","34"));
                return data;
            }else{
                return null;
            }
        }else{
            ArrayList<ISelectAble> data = new ArrayList<>();
            data.add(new TestBean(preData.getId()+"---支行1",preData.getId()+"111"));
            data.add(new TestBean(preData.getId()+"---支行2",preData.getId()+"222"));
            data.add(new TestBean(preData.getId()+"---支行3",preData.getId()+"333"));
            data.add(new TestBean(preData.getId()+"---支行4",preData.getId()+"444"));
            data.add(new TestBean(preData.getId()+"---支行5",preData.getId()+"555"));
            data.add(new TestBean(preData.getId()+"---支行6",preData.getId()+"666"));
            data.add(new TestBean(preData.getId()+"---支行7",preData.getId()+"777"));
            data.add(new TestBean(preData.getId()+"---支行8",preData.getId()+"888"));
            data.add(new TestBean(preData.getId()+"---支行9",preData.getId()+"999"));
            return data;
        }
    }
}
