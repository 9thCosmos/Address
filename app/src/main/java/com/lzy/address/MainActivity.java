package com.lzy.address;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.addressselector.AddressSelectorDialog;
import com.lzy.addressselector.Constants;
import com.lzy.addressselector.DefaultDataProvider;
import com.lzy.addressselector.SelectedListener;
import com.lzy.addressselector.Selector;
import com.lzy.addressselector.bean.ISelectAble;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private AddressSelectorDialog mAddressSelectorDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAddress = (Button) findViewById(R.id.btn_address);
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAddress();
            }
        });

    }


    private void testAddress(){
        Selector selector = new Selector(this, 3, Constants.LAYOUT_GRID); //层级：3表示3级点击
        selector.setDataProvider(new DefaultDataProvider(this));
        selector.setSelectedListener(new SelectedListener() {
            @Override
            public void onAddressSelected(ArrayList<ISelectAble> selectAbles) {
                Toast.makeText(MainActivity.this,selectAbles.get(0).getName()+"-"+selectAbles.get(1).getName()+"-"+selectAbles.get(2).getName(),Toast.LENGTH_SHORT).show();
                mAddressSelectorDialog.dismiss();
            }
        });

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        selector.setTabWidth(metric.widthPixels/3);

        TextView view = new TextView(this);
        view.setText("无数据");
        selector.setNoResultView(view);

        mAddressSelectorDialog = new AddressSelectorDialog(this);
        mAddressSelectorDialog.init(this,selector);
        mAddressSelectorDialog.show();

        int screenHeight = DisplayUtils.getScreenMetrics(this).y;
        int height = screenHeight > 400 ? 400 : (screenHeight-80);
        mAddressSelectorDialog.setSize(WindowManager.LayoutParams.MATCH_PARENT,DisplayUtils.dip2px(this,height));
    }
}
