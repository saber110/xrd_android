package com.example.collectdata.tools;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class HttpTools implements Runnable {

//    private static final String ROOT_URL = "http://139.199.8.103:8888/api/v1/get_data/";

    private static final String ROOT_URL = "http://rap2api.taobao.org/app/mock/234350/api/v1/get_data/";
//    http://rap2api.taobao.org/app/mock/234350/api/v1/get_data/garden_base_info
    private URL url;
    private String urlStr;
    private HttpURLConnection conn;
    private Map<String,String> param;
    private String response;

    //请求状态
    private static int requestStatus ;

    private static final int REQUEST_SUCCESS = 1;
    private static final int REQUEST_FAIL = 2;
    private static final int REQUEST_PROCESSING = 0;

    @Override
    public void run() {
        try {
            url = new URL(ROOT_URL+urlStr);
            conn = (HttpURLConnection)url.openConnection();

            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
//            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            conn.connect();

            OutputStream os = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            //发送参数
            String content = toUrl(param);
            System.out.println(content);
            byte[] buf = content.getBytes();
            dos.write(buf);
            dos.flush();
            dos.close();

            Log.i("HttpTools",conn.getResponseCode() + "");
            //读取数据
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1){
                baos.write(buf,0,len);
            }

            baos.close();
            is.close();

            byte[] data = baos.toByteArray();
            response = new String(data);
            conn.disconnect();
            requestStatus = REQUEST_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            requestStatus = REQUEST_FAIL;
        }
    }


    public String request(String url,Map<String,String> param) {
        this.urlStr = url;
        this.param = param;

        Thread thread = new Thread(this);
        thread.start();

        //查看请求结果
        int requestTime = 0;
        // 5000ms内请求到数据就退出
        while (requestTime <= 5000) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (requestStatus == REQUEST_PROCESSING) {
                requestTime += 200;
                continue;
            } else {
                break;
            }
        }
        Log.i("HttpTools",response == null ? "暂无数据" : response);
        if (requestStatus == REQUEST_SUCCESS){
            return response;
        } else {
            Log.i("HttpTools","数据请求失败");
            return null;
        }
    }

    private String toUrl(Map<String,String> param){
        String url = "";
        if (param == null || param.isEmpty())
            return url;
        Set<String> keys = param.keySet();
        for (String str :
                keys) {
            String value = param.get(str);
            url += (str+"="+value+"&");
        }
        return url.substring(0,url.length() - 1);
    }
}
