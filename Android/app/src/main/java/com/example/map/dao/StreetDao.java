package com.example.map.dao;

import java.util.List;

public class StreetDao {

    /**
     * code : 1
     * message :
     * data : {"streets":[{"name":"","id":1}]}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<StreetsBean> streets;

        public List<StreetsBean> getStreets() {
            return streets;
        }

        public void setStreets(List<StreetsBean> streets) {
            this.streets = streets;
        }

        public static class StreetsBean {
            /**
             * name :
             * id : 1
             */

            private String name;
            private int id;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
