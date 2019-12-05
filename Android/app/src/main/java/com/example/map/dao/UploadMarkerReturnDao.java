package com.example.map.dao;

public class UploadMarkerReturnDao {

    /**
     * code : 0
     * data : {"mapDataId":89}
     * message : 添加数据成功
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
         * mapDataId : 89
         */

        private int mapDataId;

        public int getMapDataId() {
            return mapDataId;
        }

        public void setMapDataId(int mapDataId) {
            this.mapDataId = mapDataId;
        }
    }
}
