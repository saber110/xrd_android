package com.example.collectdata.bean;

import com.example.collectdata.tools.ConstTools;

/**
 * 信息详情页普通行
 */
public class CommonItemBean {

    public final int type;

    //标题
    private String title;
    //text内容
    private String content;

    public CommonItemBean(String title, String content ,int type) {
        this.title = title;
        this.content = content;
        this.type = type;
    }


    public CommonItemBean(String title, String content) {
        this.title = title;
        this.content = content;
        this.type = ConstTools.MESSAGE_BEANTYPE_COMMON;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CommonItemBean{" +
                "type=" + type +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}
