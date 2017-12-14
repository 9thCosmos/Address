package com.lzy.addressselector;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


import com.lzy.addressselector.bean.AreaBean;
import com.lzy.addressselector.bean.CityBean;
import com.lzy.addressselector.bean.ProvinceBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Title: ChinaAddressHelper <br>
 * @author LiZhengyu.
 */
public class ChinaAddressHelper {
    private List<ProvinceBean> allProvinceList;
    private List<CityBean> allCityList;

    private PinYinComparator pinYinComparator;
    public static ChinaAddressHelper getInstance() {
        return ChinaAddressHelperBuilder.instance;
    }

    public static class ChinaAddressHelperBuilder {
        private static ChinaAddressHelper instance = new ChinaAddressHelper();
    }

    public void initData(Context applicationContext){
        allProvinceList = new ArrayList<>();
        allCityList = new ArrayList<>();
        pinYinComparator = new PinYinComparator();

        AssetManager asset = applicationContext.getAssets();
        try {
            InputStream input = asset.open("citydict.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(input,"UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
            reader.close();
            input.close();

            JSONObject jsonObject = new JSONObject(builder.toString());

            PinYinHelper.getInstance().init(applicationContext);

            //省
            JSONObject provincesJson = jsonObject.optJSONObject("province");
            Iterator provinceKeys = provincesJson.keys(); //省首字母缩写集合
            while (provinceKeys.hasNext()) {
                String key = (String) provinceKeys.next();
                JSONObject provinceValues = provincesJson.optJSONObject(key);//省集合
                Iterator ids = provinceValues.keys();
                while (ids.hasNext()){
                    ProvinceBean bean = new ProvinceBean();
                    String id = (String)ids.next();
                    bean.setId(id);
                    bean.setName(provinceValues.getString(id));
                    bean.setSortLetters(bean.getName());
                    bean.setPinyin(PinYinHelper.getInstance().getPinyin(bean.getName()));
                    allProvinceList.add(bean);
                }
            }

            //对省进行拼音排序
            Collections.sort(allProvinceList, pinYinComparator);

            //市
            JSONObject citiesJson = jsonObject.optJSONObject("city");
            for(int i=0;i<allProvinceList.size();i++){
                JSONObject cityJson = citiesJson.optJSONObject(allProvinceList.get(i).getId());
                if(cityJson != null){
                    List<CityBean> cityList = new ArrayList<>();
                    Iterator ids = cityJson.keys();
                    while (ids.hasNext()){
                        CityBean bean = new CityBean();
                        String id = (String)ids.next();
                        bean.setId(id);
                        bean.setName(cityJson.getString(id));
                        bean.setSortLetters(bean.getName());
                        bean.setPinyin(PinYinHelper.getInstance().getPinyin(bean.getName()));
                        cityList.add(bean);
                        allCityList.add(bean);
                    }
                    //对市进行拼音排序
                    Collections.sort(cityList, pinYinComparator);
                    allProvinceList.get(i).setCityList(cityList);
                }
            }

            //区
            JSONObject areasJson = jsonObject.optJSONObject("country");
            for(int i=0;i<allCityList.size();i++){
                JSONObject areaJson = areasJson.optJSONObject(allCityList.get(i).getId());
                if(areaJson != null){
                    List<AreaBean> areaList = new ArrayList<>();
                    Iterator ids = areaJson.keys();
                    while (ids.hasNext()){
                        AreaBean bean = new AreaBean();
                        String id = (String)ids.next();
                        bean.setId(id);
                        bean.setName(areaJson.getString(id));
                        bean.setSortLetters(bean.getName());
                        bean.setPinyin(PinYinHelper.getInstance().getPinyin(bean.getName()));
                        areaList.add(bean);
                    }
                    //对区进行拼音排序
                    Collections.sort(areaList, pinYinComparator);
                    allCityList.get(i).setAreaList(areaList);
                }
            }
        }catch (Exception e){
            Log.e("ChinaAddressHelper",e.getMessage());
        }
    }

    public List<ProvinceBean> getAllProvinceList() {
        return allProvinceList;
    }

    public List<CityBean> getAllCityList() {
        return allCityList;
    }
}
