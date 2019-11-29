package com.example.dao;

public class AddGradenResult {


    /**
     * code : 0
     * data : {"gardenId":1}
     * message : 添加小区成功
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * gardenId : 1
         */

        private int gardenId;

        public int getGardenId() {
            return gardenId;
        }

        public void setGardenId(int gardenId) {
            this.gardenId = gardenId;
        }
    }
}
