package com.example.collectdata.tools;

import android.util.Log;

import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.ListItemBean;
import com.example.collectdata.bean.SelectorItemBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonTools {
    private static final int SUCCESS = 1;

    public static void jsonParasForMessageList(String jsonStr, List<CommonItemBean> list) throws JSONException {
//        list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonStr);
        int code = jsonObject.getInt("code");
        Log.i("JsonTools","Code = " + code);
        if (code != SUCCESS ){
            return ;
        }
        JSONObject dataJsonObj = (JSONObject)jsonObject.get("data");

        JSONArray formList = (JSONArray)dataJsonObj.get("gardenInfoList");

        funPrint("dataJsonObj" ,dataJsonObj.toString());
        int formListLength = formList.length();
        CommonItemBean commonItemBean = null;
        for (int i = 0 ; i < formListLength ; i ++) {
            Log.i("JsonTools","第" + i + "次转换");
            JSONObject lineJson = formList.getJSONObject(i);
            String type = lineJson.getString("type");
            String title = lineJson.getString("label");
            if (type.equals("text")) {
                //文字输入框
                commonItemBean = jsonParasForCommonItem(title,lineJson);
            } else if (type.equals("radio") || type.equals("multiple") ) {
                //单选，多选框
                commonItemBean = jsonParasForSelectItem(type,title,lineJson);
            } else if (type.equals("list")) {
                Log.i("JsonTools","初始化一级列表:" + i);
                ListItemBean listItemBean = jsonParasForLineTitleItem(i,formList,title,lineJson);
                i += listItemBean.getLength();
                commonItemBean = listItemBean;
            }
            Log.i("JsonTools","添加一个ItemBean" + commonItemBean);
            list.add(commonItemBean);
        }
    }

    private static CommonItemBean jsonParasForCommonItem(String title, JSONObject lineJson) throws JSONException{
        //文字输入框
        String content = lineJson.getString("value");
        return new CommonItemBean(title,content);
    }

    private static CommonItemBean jsonParasForSelectItem(String type, String title, JSONObject lineJson) throws JSONException{
        //单选，多选框
        JSONArray contentJsonArray = lineJson.getJSONArray("option");
        ArrayList<String> contentList = new ArrayList<>();
        for (int j = 0 ; j < contentJsonArray.length() ; j ++) {
            String optionStr = contentJsonArray.getString(j);
            contentList.add(optionStr);
        }
        if (type.equals("radio")) {
            //单选框
            String value = lineJson.getString("value");
            return new SelectorItemBean(title,contentList,value);
        } else {
            //多选框
            JSONArray values = lineJson.getJSONArray("value");
            ArrayList<String> currentSelectList = new ArrayList<>();
            for (int j = 0 ; j < values.length() ; j ++ ) {
                String currentSelect = values.getString(j);
                currentSelectList.add(currentSelect);
            }
            return new SelectorItemBean(title,contentList,currentSelectList);
        }
    }

    private static ListItemBean jsonParasForLineTitleItem(int i, JSONArray formList, String title, JSONObject lineJson) throws JSONException{
        //一级菜单列表
        int length = lineJson.getInt("length");
        Log.i("JsonTools","获取到的length = " + length);
        ListItemBean listItemBean = new ListItemBean(title,length);
        for (int j = 1; j <= length ; j ++) {
            //获取内置行的信息
            lineJson = formList.getJSONObject(i + j);
            Log.i("JsonTools","初始化第" + j + "个json:" + lineJson);
            String innerItemType = lineJson.getString("type");
            String innerItemTitle = lineJson.getString("label");
            CommonItemBean innerItemBean = null;
            if (innerItemType.equals("text")) {
                //文字输入框
                innerItemBean = jsonParasForCommonItem(innerItemTitle,lineJson);
            } else if (innerItemType.equals("radio") || innerItemType.equals("multiple") ) {
                //单选，多选框
                innerItemBean = jsonParasForSelectItem(innerItemType,innerItemTitle,lineJson);
            }
            listItemBean.addInnerItem(innerItemBean);
        }
        return listItemBean;
    }

    public static void funPrint(String title, String xml){
        if(xml.length() > 4000) {
            for(int i=0;i<xml.length();i+=4000){
                if(i+4000<xml.length())
                    Log.i(title+i,xml.substring(i, i+4000));
                else
                    Log.i(title+i,xml.substring(i, xml.length()));
            }
        } else
            Log.i(title,xml);
    }
}
