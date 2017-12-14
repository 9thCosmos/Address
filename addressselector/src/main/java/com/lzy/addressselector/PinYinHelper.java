package com.lzy.addressselector;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Title: PinYinHelper <br>
 * @author LiZhengyu.
 */
public class PinYinHelper {
    private HashMap<String,String> duoYinZiMap;

    public static PinYinHelper getInstance() {
        return PinYinHelperBuilder.instance;
    }

    public static class PinYinHelperBuilder {
        private static PinYinHelper instance = new PinYinHelper();
    }

    public void init(Context context){
        duoYinZiMap = new HashMap<>();
        AssetManager asset = context.getAssets();
        InputStream input = null;
        BufferedReader reader = null;
        try {
            input = asset.open("duoyinzi_dic.txt");
            reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] arr = line.split("#");
                if(arr!= null && arr.length > 1 && !TextUtils.isEmpty(arr[1])){
                    String[] sems = arr[1].split(" ");
                    for (String sem : sems) {
                        if(!TextUtils.isEmpty(sem)){
                            duoYinZiMap.put(arr[0],sem);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String[] getPinyin(char ch) {
        try{
            HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            if(ch>=32 && ch<=125){    //ASCII >=33 ASCII<=125的直接返回 ,ASCII码表：http://www.asciitable.com/
                return new String[]{String.valueOf(ch)};
            }
            return PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
            return null;
        }

    }


    public String getPinyin(String chinese) {
        if(TextUtils.isEmpty(chinese)){
            return null;
        }

        //chinese = chinese.replaceAll("[\\.,\\,！·\\!？\\?；\\;\\(\\)（）\\[\\]\\:： ]+", " ").trim();

        StringBuilder py_sb = new StringBuilder(32);
        char[] chs = chinese.toCharArray();
        for(int i=0;i<chs.length;i++){
            String[] py_arr = getPinyin(chs[i]);
            if(py_arr==null || py_arr.length<1){
                return null;
            }
            if(py_arr.length==1){
                py_sb.append(convertInitialToUpperCase(py_arr[0]));
            }else if(py_arr.length==2 && py_arr[0].equals(py_arr[1])){
                py_sb.append(convertInitialToUpperCase(py_arr[0]));
            }else{
                String resultPy = null, defaultPy = null;;
                for (String py : py_arr) {
                    String left = null; //向左多取一个字,例如 银[行]
                    if(i>=1 && i+1<=chinese.length()){
                        left = chinese.substring(i-1,i+1);
                        if(duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(left)){
                            resultPy = py;
                            break;
                        }
                    }

                    String right = null;    //向右多取一个字,例如 [长]沙
                    if(i<=chinese.length()-2){
                        right = chinese.substring(i,i+2);
                        if(duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(right)){
                            resultPy = py;
                            break;
                        }
                    }

                    String middle = null;   //左右各多取一个字,例如 龙[爪]槐
                    if(i>=1 && i+2<=chinese.length()){
                        middle = chinese.substring(i-1,i+2);
                        if(duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(middle)){
                            resultPy = py;
                            break;
                        }
                    }
                    String left3 = null;    //向左多取2个字,如 芈月[传],列车长
                    if(i>=2 && i+1<=chinese.length()){
                        left3 = chinese.substring(i-2,i+1);
                        if(duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(left3)){
                            resultPy = py;
                            break;
                        }
                    }

                    String right3 = null;   //向右多取2个字,如 [长]孙无忌
                    if(i<=chinese.length()-3){
                        right3 = chinese.substring(i,i+3);
                        if(duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(right3)){
                            resultPy = py;
                            break;
                        }
                    }

                    if(duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(String.valueOf(chs[i]))){    //默认拼音
                        defaultPy = py;
                    }
                }
                if(TextUtils.isEmpty(resultPy)){
                    if(!TextUtils.isEmpty(defaultPy)){
                        resultPy = defaultPy;
                    }else{
                        resultPy = py_arr[0];
                    }
                }
                py_sb.append(convertInitialToUpperCase(resultPy));
            }
        }
        return py_sb.toString();
    }

    private String convertInitialToUpperCase(String str) {
        if (str == null || str.length()==0) {
            return "";
        }
        return str.substring(0, 1).toUpperCase()+str.substring(1);
    }
}
