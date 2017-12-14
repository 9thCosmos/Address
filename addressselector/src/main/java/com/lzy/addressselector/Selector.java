package com.lzy.addressselector;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lzy.addressselector.bean.ISelectAble;

import java.util.ArrayList;
import java.util.List;


public class Selector implements AddressAdapter.OnAddressItemClickListener {

    public static final String INDEX_INVALID = ""; //没有选择项时存储字段
    private final Context context;
    private SelectedListener listener;
    private View view;   //根view
    private View indicator; //选择指示器
    private LinearLayout ll_tabLayout;  //顶部tab
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private LinearLayout llayoutSearchWrapper; //搜索界面容器
    private RelativeLayout rlayoutSearch;
    private LinearLayout imgSearch;
    private EditText etSearch;  //搜索内容
    private LinearLayout llayoutNoResult;

    private int tabIndex = 1; //当前选择的tab,从1开始
    private List<List<ISelectAble>> showDatas = new ArrayList<>();//列表实际显示数据
    private List<List<ISelectAble>> allDatas = new ArrayList<>();//所有数据
    /* 每个tab的adapter */
    private AddressAdapter[] adapters;
    /*选择的深度*/
    private int selectDeep;
    private String[] selectedId; //tab页选中的ID集合
    private boolean[] isShowSearch; //tab页是否显示搜索界面集合
    private DataProvider dataProvider;
    private boolean defaultLoadingEnable = true;// 是否显示自带的加载框

    private int layoutType = 0;

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
        getNextData(null);
    }

    public Selector(Context context, int deep) {
        this(context,deep,Constants.LAYOUT_LINEAR);
    }

    public Selector(Context context, int deep,int layoutType){
        this.context = context;
        this.layoutType = layoutType;
        this.showDatas = new ArrayList<>(deep);
        this.allDatas = new ArrayList<>(deep);
        selectedId = new String[deep];
        isShowSearch = new boolean[deep];
        this.selectDeep = deep;
        for (int i = 0; i < deep; i++) {
            showDatas.add(new ArrayList<ISelectAble>());
            allDatas.add(new ArrayList<ISelectAble>());
            isShowSearch[i] = false; //默认不显示
        }
        initAdapters();
        initViews();
    }

    private void initAdapters() {
        adapters = new AddressAdapter[showDatas.size()];
        for (int i = 0; i < selectDeep; i++) {
            adapters[i] = new AddressAdapter(context,layoutType,showDatas.get(i));
            adapters[i].setOnAddressItemClickListener(this);
        }
    }

    private TextView[] tabs;

    private void initViews() {
        view = LayoutInflater.from(context).inflate(R.layout.address_selector, null);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        indicator = view.findViewById(R.id.indicator);
        llayoutNoResult = (LinearLayout) view.findViewById(R.id.llayout_no_result);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        if(layoutType == Constants.LAYOUT_GRID){
            recyclerView.setLayoutManager(new GridLayoutManager(context,Constants.LAYOUT_GRID_SPANCOUNT));
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        initTabsView();
        updateIndicator(tabIndex);
        initSearchView();
    }

    private void initTabsView(){
        ll_tabLayout = (LinearLayout) view.findViewById(R.id.layout_tab);
        tabs = new TextView[showDatas.size()];
        for (int i = 0; i < showDatas.size(); i++) {
            //动态新增TextView
            TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.address_selector_simple_text_view, ll_tabLayout, false);
            ll_tabLayout.addView(textView);

            //绑定TextView的点击事件
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置tab 下标
                    tabIndex = finalI + 1;
                    //更新搜索
                    showSearchView();
                    etSearch.setText("");
                    //更新adapter
                    showDatas.get(tabIndex-1).clear();
                    showDatas.get(tabIndex-1).addAll(allDatas.get(tabIndex-1));
                    recyclerView.setAdapter(adapters[finalI]);
                    updateTabsVisibility(tabIndex-1);
                    updateIndicator(tabIndex - 1);
                    if(showDatas.get(tabIndex-1).size() == 0){
                        llayoutNoResult.setVisibility(View.VISIBLE);
                    }else{
                        llayoutNoResult.setVisibility(View.GONE);
                    }
                }
            });
            tabs[i] = textView;
        }
    }

    private void initSearchView(){
        llayoutSearchWrapper =(LinearLayout) view.findViewById(R.id.llayout_search_wrapper);
        etSearch = (EditText) view.findViewById(R.id.et_search);
        imgSearch = (LinearLayout) view.findViewById(R.id.img_search);
        rlayoutSearch = (RelativeLayout)view.findViewById(R.id.rlayout_search);
        rlayoutSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgSearch.setVisibility(View.GONE);
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                rlayoutSearch.setOnClickListener(null);
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String content = v.getText().toString().trim();
                    showDatas.get(tabIndex-1).clear();
                    showDatas.get(tabIndex-1).addAll(getSearchResult(content));
                    adapters[tabIndex-1].notifyDataSetChanged();
                }
                return false;
            }
        });

        showSearchView();
    }

    /**
     * 获取搜索结果数据
     * @param searchContent
     * @return
     */
    private List<ISelectAble> getSearchResult(String searchContent){
        List<ISelectAble> resultList = new ArrayList<>();
        for(int i=0;i<allDatas.get(tabIndex-1).size();i++){
            if(allDatas.get(tabIndex-1).get(i).getName().contains(searchContent)){
                resultList.add(allDatas.get(tabIndex-1).get(i));
            }
        }
        return  resultList;
    }

    public View getView() {
        return view;
    }


    public void setNoResultView(View view){
        llayoutNoResult.removeAllViews();
        llayoutNoResult.addView(view);
    }

    /**
     * 设置tab宽度
     * @param tabWidth
     */
    public void setTabWidth(int tabWidth) {
        for(int i=0;i<tabs.length;i++){
            tabs[i].setWidth(tabWidth);
        }
    }
    /**
     * 设置tab字体颜色
     */
    public void setTabTextColor(int color){
        for(int i=0;i<tabs.length;i++){
            tabs[i].setTextColor(color);
        }
    }

    /**
     * 设置indicator的背景
     * @param resourceId
     */
    public void setIndicatorBackground(int resourceId){
        indicator.setBackgroundResource(resourceId);
    }


    /**
     * 设置是否显示搜索栏
     * @param tabIndex
     * @param isShow
     */
    public void setSearchEnable(int tabIndex,boolean isShow){
        isShowSearch[tabIndex-1] = isShow;
    }

    /**
     * 指示器动画
     */
    private void updateIndicator(final int tabIndex) {
        view.post(new Runnable() {
            @Override
            public void run() {
                buildIndicatorAnimatorTowards(tabs[tabIndex]).start();
            }
        });
    }


    private AnimatorSet buildIndicatorAnimatorTowards(TextView tab) {
        ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator.getX(), tab.getX());

        final ViewGroup.LayoutParams params = indicator.getLayoutParams();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, tab.getMeasuredWidth());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.width = (int) animation.getAnimatedValue();
                indicator.setLayoutParams(params);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new FastOutSlowInInterpolator());
        set.playTogether(xAnimator, widthAnimator);

        return set;
    }

    /**
     * 更新tab显示情况
     * @param index
     */
    private void updateTabsVisibility(int index) {
        for (int i = 0; i < tabs.length; i++) {
            TextView tv = tabs[i];
            //当前tab，不管又没有数据，都需要显示
            if(i+1 == tabIndex){
                tv.setVisibility(View.VISIBLE);
            }else{
                tv.setVisibility(showDatas.get(i).size() != 0 ? View.VISIBLE : View.GONE);
            }
            tv.setEnabled(index != i);
        }
    }

    /**
     * 显示搜索界面
     */
    private void showSearchView(){
        if(isShowSearch[tabIndex-1]){
            llayoutSearchWrapper.setVisibility(View.VISIBLE);
        }else{
            llayoutSearchWrapper.setVisibility(View.GONE);
        }
    }

    /**
     * 根据当前集合选择的id，向用户获取下一级子集的数据
     */
    private void getNextData(ISelectAble preData) {
        if (dataProvider == null) {
            return;
        }
        showLoading();
        dataProvider.provideData(tabIndex, preData, new DataProvider.DataReceiver() {
            @Override
            public void send(List<ISelectAble> data) {
                sendData(data);
            }
        });
    }

    /**
     * 是否使用selector自带的加载框
     * @param defaultLoadingEnable
     */
    public void setDefaultLoadingEnable(boolean defaultLoadingEnable){
        this.defaultLoadingEnable = defaultLoadingEnable;
    }

    /**
     * 设置数据
     * @param data
     */
    public void sendData(List<ISelectAble> data){
        hideLoading();
        if (data != null && data.size() > 0) {
            //更新当前tab下标
            allDatas.get(tabIndex-1).clear();
            allDatas.get(tabIndex-1).addAll(data);
            showDatas.get(tabIndex-1).clear();
            showDatas.get(tabIndex-1).addAll(data);
            adapters[tabIndex-1].notifyDataSetChanged();
            showSearchView();
            recyclerView.setAdapter(adapters[tabIndex-1]);
            llayoutNoResult.setVisibility(View.GONE);
        } else {
            allDatas.get(tabIndex-1).clear();
            showDatas.get(tabIndex-1).clear();
            adapters[tabIndex-1].notifyDataSetChanged();
            recyclerView.setAdapter(adapters[tabIndex-1]);
            llayoutNoResult.setVisibility(View.VISIBLE);
        }
        updateTabsVisibility(tabIndex-1);
        updateIndicator(tabIndex-1);
    }

    public void setError(){
        hideLoading();
    }

    private void showLoading(){
        if(defaultLoadingEnable){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    private void hideLoading(){
        progressBar.setVisibility(View.GONE);
    }

    private void callbackInternal() {
        if (listener != null) {
            ArrayList<ISelectAble> result = new ArrayList<>(showDatas.size());
            for (int i = 0; i < selectDeep; i++) {
                if(showDatas.get(i) != null){
                    for(int j=0;j<showDatas.get(i).size();j++){
                        if(selectedId[i].equals(showDatas.get(i).get(j).getId())){
                            result.add(showDatas.get(i).get(j));
                            break;
                        }
                    }
                }
            }
            listener.onAddressSelected(result);
        }
    }

    public void setSelectedListener(SelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(int position) {
        this.selectedId[tabIndex - 1] = showDatas.get(tabIndex-1).get(position).getId();
        ISelectAble selectAble = showDatas.get(tabIndex - 1).get(position);
        tabs[tabIndex-1].setText(selectAble.getName());
        for (int i = tabIndex; i < this.showDatas.size(); i++) {
            tabs[i].setText("请选择");
            showDatas.get(i).clear();
            adapters[i].setSelectedId(INDEX_INVALID);
            adapters[i].notifyDataSetChanged();
            this.selectedId[i] = INDEX_INVALID;
        }
        this.adapters[tabIndex - 1].setSelectedId(showDatas.get(tabIndex-1).get(position).getId());
        this.adapters[tabIndex - 1].notifyDataSetChanged();
        if (tabIndex == selectDeep) {
            callbackInternal();
            return;
        }
        updateTabsVisibility(tabIndex -1);
        updateIndicator(tabIndex);
        tabIndex = tabIndex + 1 >= selectDeep ? selectDeep : tabIndex + 1;
        getNextData(selectAble);
    }
}
