package com.example.test;

import android.util.Log;

import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.ListItemBean;
import com.example.collectdata.bean.SelectorItemBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JTools {
    public static void jsonParasForMessageList(String jsonStr, List<CommonItemBean> list, List<Integer> radius, String mode) throws JSONException {
        radius.clear();
        JSONObject jsonObject = new JSONObject(jsonStr);
        int code = jsonObject.getInt("code");
        Log.i("JsonTools","Code = " + code);
        if (code != 0 ){
            return ;
        }
        JSONObject dataJsonObj = (JSONObject)jsonObject.get("data");
        JSONArray formList = null;
        if (mode.equals("base"))
            formList = (JSONArray)dataJsonObj.get("gardenInfoList");
        else if (mode.equals("extra"))
            formList = (JSONArray)dataJsonObj.get("gardenImportInfoList");

        int formListLength = formList.length();
        CommonItemBean commonItemBean = null;
        for (int i = 0 ; i < formListLength ; i ++) {
            JSONObject lineJson = formList.getJSONObject(i);
            Log.i("JsonTools","第" + i + "次转换 " + lineJson.toString());
            String type = lineJson.getString("type");
            String title = lineJson.getString("label");
            boolean require = lineJson.getBoolean("required");
            if (type.equals("text")) {
                //文字输入框
                commonItemBean = jsonParasForCommonItem(title,lineJson);
                commonItemBean.setType(0);
            } else if (type.equals("radio") || type.equals("multiple") ) {
                //单选，多选框
                Log.i("JsonTools","初始化选择: " + i + title + lineJson.toString());
                commonItemBean = jsonParasForSelectItem(type,title,lineJson);
                commonItemBean.setType(1);
            } else if (type.equals("list")) {
                Log.i("JsonTools","初始化一级列表:" + i);
                ListItemBean listItemBean = jsonParasForLineTitleItem(i,formList,title,lineJson);
                i += listItemBean.getLength();
                commonItemBean = listItemBean;
                commonItemBean.setType(0);
            } else if(type.equals("number")){
                //数字输入框
                commonItemBean = jsonParasForCommonItem(title,lineJson);
                commonItemBean.setType(0);
            }else if (type.equals("map")){
                commonItemBean = jsonParasForCommonItem(title,lineJson);
                commonItemBean.setType(0);
                radius.add(lineJson.getInt("radius"));
            }
            if(!type.equals("list"))
                commonItemBean.setKey(lineJson.getString("key"));
            commonItemBean.setRequire(require);
            commonItemBean.setRequireType(type);

            if(!type.equals("map")){
                Log.i("JsonTools","添加一个ItemBean" + commonItemBean);
                list.add(commonItemBean);
            }

        }
    }

    private static CommonItemBean jsonParasForCommonItem(String title, JSONObject lineJson) throws JSONException {
        //文字输入框
        String content = lineJson.getString("value");
        return new CommonItemBean(title,content);
    }

    // 3.09 OK
    private static CommonItemBean jsonParasForSelectItem(String type, String title, JSONObject lineJson) throws JSONException {
        //单选，多选框
        String TAG = "hhhhjjjj";
        JSONArray contentJsonArray = lineJson.getJSONArray("option");
        ArrayList<String> contentList = new ArrayList<>();
        Log.i(TAG, "jsonParasForSelectItem: " + contentJsonArray.length());
        Log.i(TAG, "jsonParasForSelectItem: " + type);
        for (int j = 0; j < contentJsonArray.length(); j++) {
            String optionStr = contentJsonArray.getString(j);
            Log.i(TAG, "jsonParasForSelectItem: " + optionStr);
            contentList.add(optionStr);
        }
        if (type.equals("radio")) {
            //单选框
            Log.i(TAG, "jsonParasForSelectItem: radio " + lineJson.getString("label"));
            String value = lineJson.getString("value");
            return new SelectorItemBean(title, contentList, value);
        } else {
            //多选框
            Log.i(TAG, "jsonParasForSelectItem: multiple " + lineJson.getString("label"));

            JSONArray values = lineJson.getJSONArray("value");
            ArrayList<String> currentSelectList = new ArrayList<>();
            for (int j = 0; j < values.length(); j++) {
                String currentSelect = values.getString(j);
                currentSelectList.add(currentSelect);
            }
            return new SelectorItemBean(title, contentList, currentSelectList);
        }
    }
    private static ListItemBean jsonParasForLineTitleItem(int i, JSONArray formList, String title, JSONObject lineJson) throws JSONException {
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
            }else if(innerItemType.equals("number")){
                //数字输入框
                innerItemBean = jsonParasForCommonItem(innerItemTitle,lineJson);
            }
            listItemBean.addInnerItem(innerItemBean);
        }
        return listItemBean;
    }

}
