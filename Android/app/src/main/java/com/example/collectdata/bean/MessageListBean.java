package com.example.collectdata.bean;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.collectdata.exception.PageInitException;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata.tools.HttpTools;
import com.example.collectdata.tools.JsonTools;
import com.example.login.login;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 数据收集页列表
 */
public class MessageListBean {

    List<CommonItemBean> list = new ArrayList<>();
//    private static final String ROOT_URL = "http://kms.yinaoxiong.cn:8888/api/v1/get_data/";
    private static final String ROOT_URL = "http://kms.yinaoxiong.cn:8888/api/v1/get_data/";
    //四个实例
    private static MessageListBean xqgk = null;
    private static MessageListBean lzdc = null;
    private static MessageListBean lzxx = null;
    private static MessageListBean xqxx = null;
    static OkHttpClient client = new OkHttpClient();
    public static MessageListBean getInstance(int type) throws PageInitException, IOException {
        switch (type){
            case ConstTools.XIAOQUGAIKUANG:
                if (xqgk == null) {
                    xqgk =  new MessageListBean(ConstTools.XIAOQUGAIKUANG) ;
                }
                return xqgk;
            case ConstTools.LOUZHUANGDIAOCHA:
                if (lzdc == null) {
                    lzdc =  new MessageListBean(ConstTools.LOUZHUANGDIAOCHA) ;
                }
                return lzdc;
            case ConstTools.LOUZHUANGXINXI:
                if (lzxx == null) {
                    lzxx =  new MessageListBean(ConstTools.LOUZHUANGXINXI) ;
                }
                return lzxx;
            case ConstTools.XIAOQUXINXI:
                if (xqxx == null) {
                    xqxx =  new MessageListBean(ConstTools.XIAOQUXINXI) ;
                }
                return xqxx;
        }
        return null;
    }

    public static MessageListBean getXqgk() {
        return xqgk;
    }

    public static MessageListBean getLzdc() {
        return lzdc;
    }

    public static MessageListBean getLzxx() {
        return lzxx;
    }

    public static MessageListBean getXqxx() {
        return xqxx;
    }

    private MessageListBean(int listType) throws PageInitException, IOException {
        switch (listType){
            case ConstTools.XIAOQUGAIKUANG:
                initXiaoQuGaiKuang();
                break;
            case ConstTools.LOUZHUANGDIAOCHA:
                initLouZhuangDiaoCha();
                break;
            case ConstTools.LOUZHUANGXINXI:
                initLouzhuangXinxi();
                break;
            case ConstTools.XIAOQUXINXI:
                initXiaoQiXinXi();
        }
    }

    public List<CommonItemBean> getList() {
        return list;
    }

    public CommonItemBean getItemBean(int position){
        return list.get(position);
    }

    /**
     * 初始化小区概况表
     */
    private void initXiaoQuGaiKuang() throws PageInitException, IOException {
        //TODO 获取采集人员
        Log.i("MessageListBean","请求搂幢调查表的内容");
        Map map = new HashMap<String, String>();
        map.put("token",login.token);
        post(ROOT_URL+"building_base_info", (HashMap<String, Object>) map);

//        //获取当前日期
//        Calendar calendar = Calendar.getInstance();
//        String dataStr = calendar.get(Calendar.YEAR) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DAY_OF_MONTH);
//        //获取采集人员
//        String caijirenyuan = "张三";
//
//
//        //初始化区域位置
//        String[] quyuweizhi = {"城区中心","城东","城西","城南","城北","近郊组团及乡镇","远郊组团及乡镇"};
//        //楼盘状态
//        String[] loupanzhuangtai = {"存量","在建","在售","灭失"};
//        //小区类型
//        String[] xiaoquleixing = {"普通商品房","老式住宅区","经济适用房","中档商品房","高档商品房","自建房","零星住宅区","农村拆迁房","城市拆迁房","人才专项房"};
//        //建筑类型
//        String[] jianzhuleixing = {"住宅(电梯房)","住宅(楼梯房)","住宅(洋房)","单身公寓(住宅)","单身公寓(非住宅)","办公写字楼","别墅(独栋)","别墅(联排)","别墅(双拼)","叠墅","自建民房","其他类型",""};
//        //房屋性质
//        String[] fangwuxingzhi = {"商品房","房改房","经济适用房","集资房","私房","非商品房","公租房","安置房"};
//        //建筑结构
//        String[] jianzhujiegou = {"钢混结构","混合结构","钢及钢混结构","砖混结构","砖木结构","钢结构","木结构","简易结构"};
//        //土地性质
//        String[] tudixingzhi = {"国有土地","集体土地"};
//        //使用权
//        String[] shiyongquan = {"出让","划拨","集体"};
//        //土地等级
//        String[] tudidengji = {"一级","二级","三级","四级","五级","六级","七级","八级","九级","十级","十一级"};
//        //是否封闭
//        String[] shifoufengbi = {"是","否"};
//        //物管分类
//        String[] wuguanfenlei = {"专业物业管理","社区保洁","社区准物业","无物业管理"};
//
//
//        //初始化列表
//        list.add(new CommonItemBean("采集人员",caijirenyuan));
//        list.add(new CommonItemBean("采集日期",dataStr));
//        list.add(new CommonItemBean("市",""));
//        list.add(new CommonItemBean("区县",""));
//        list.add(new CommonItemBean("街道",""));
//        list.add(new CommonItemBean("社区名称",""));
//        list.add(new CommonItemBean("社区别名",""));
//        list.add(new CommonItemBean("小区名称",""));
//        list.add(new CommonItemBean("小区别名",""));
//        list.add(new CommonItemBean("小区别名2",""));
//        list.add(new CommonItemBean("小区坐落",""));
//        list.add(new CommonItemBean("小区东至",""));
//        list.add(new CommonItemBean("小区西至",""));
//        list.add(new CommonItemBean("小区南至",""));
//        list.add(new CommonItemBean("小区北至",""));
//        list.add(new SelectorItemBean("区域位置",quyuweizhi,true));
//        list.add(new SelectorItemBean("楼盘状态",loupanzhuangtai,true));
//        list.add(new SelectorItemBean("小区类型",xiaoquleixing,true));
//        list.add(new SelectorItemBean("建筑类型",jianzhuleixing,true));
//        list.add(new SelectorItemBean("房屋性质",fangwuxingzhi,true));
//        list.add(new SelectorItemBean("建筑结构",jianzhujiegou,true));
//        list.add(new CommonItemBean("住宅幢数",""));
//        list.add(new CommonItemBean("非住宅幢数",""));
//        list.add(new CommonItemBean("幢数描述",""));
//        list.add(new YearItemBean("建成年份"));
//        list.add(new YearItemBean("设定年份"));
//        list.add(new SelectorItemBean("土地性质",tudixingzhi,true));
//        list.add(new SelectorItemBean("使用权",shiyongquan,true));
//        list.add(new SelectorItemBean("土地等级",tudidengji,true));
//        list.add(new CommonItemBean("询价记录",""));
//        list.add(new SelectorItemBean("是否封闭",shifoufengbi,true));
//        list.add(new SelectorItemBean("物管分类",wuguanfenlei,true));
//        list.add(new CommonItemBean("价格初判",""));
//        list.add(new CommonItemBean("其他信息备注",""));
//        //从地图获取的信息

    }

    /**
     * 初始化楼幢调查
     */
    private void initLouZhuangDiaoCha() throws PageInitException, IOException {
        Log.i("MessageListBean","请求搂幢调查表的内容");
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("token",login.token);
        map.put("gardenId",1);
        post(ROOT_URL+"garden_base_info", (HashMap<String, Object>) map);
//       HttpTools httpTools = new HttpTools();
//        String response = httpTools.request("garden_base_info",null);
//
//        try {
//            JsonTools.jsonParasForMessageList(response,this.list);
//            Log.i("ListButtonListener","list转换结果为:" + list);
//        } catch (JSONException e) {
//            Log.i("MessageListBean","请求结果数据格式异常");
//            e.printStackTrace();
//            throw PageInitException.getExceptionInstance();
//        }
    }

    /**
     * 初始化楼幢信息
     */
    private void initLouzhuangXinxi() throws PageInitException{
        Log.i("MessageListBean","请求搂幢信息的内容");
        HttpTools httpTools = new HttpTools();
        String response = httpTools.request("building_base_info",null);
        Log.i("ListButtonListener","测试结果为:" + response);
        try {
            JsonTools.jsonParasForMessageList(response,this.list);
            Log.i("ListButtonListener","list转换结果为:" + list);
        } catch (JSONException e) {
            Log.i("MessageListBean","请求结果数据格式异常");
            e.printStackTrace();
            throw PageInitException.getExceptionInstance();
        }
    }

    /**
     * 初始化小区信息
     */
    private void initXiaoQiXinXi(){

    }

    //TODO 判断是否为InnerItem
    public static void setCurrentSelect(int position,String content){
        try {
            List<CommonItemBean> beans = null;
            try {
                beans = MessageListBean.getInstance(CacheTools.pageType).getList();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SelectorItemBean bean = (SelectorItemBean)beans.get(position);
            bean.setCurrentSelect(content);
        } catch (PageInitException e) {
            e.printStackTrace();
        }


    }

    //TODO 判断是否为InnerItem
    public static String getCurrentSelect(int position) {
        try {
            List<CommonItemBean> beans = null;
            try {
                beans = MessageListBean.getInstance(CacheTools.pageType).getList();
            } catch (IOException e) {
                e.printStackTrace();
            }
            SelectorItemBean bean = (SelectorItemBean)beans.get(position);
            return bean.getCurrentSelect();
        } catch (PageInitException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void post(final String url, final HashMap<String,Object> map) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("http2", login.token);
                RequestBody body = RequestBody.create(JSON.toJSONString(map), login.JSONDATA);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i("http", "onFailure: ");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
//                            Log.i("http2", "onResponse: " + response.body().string());
                            String result = response.body().string();
                            JsonTools.funPrint("http2", result);
                            JsonTools.jsonParasForMessageList(result,list);

                            Log.i("ListButtonListener","list转换结果为:" + list);
                        }catch (JSONException e) {
                            Log.i("MessageListBean","请求结果数据格式异常");
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.i("error","未知错误");
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}
