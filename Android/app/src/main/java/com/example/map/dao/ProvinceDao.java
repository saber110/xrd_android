package com.example.map.dao;

import java.util.List;

public class ProvinceDao {

    @Override
    public String toString() {
        return "ProvinceDao{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * code : 0
     * data : {"provinces":[{"id":1,"name":"湖南省"}]}
     * message : 获取省份数据成功
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
        private List<ProvincesBean> provinces;

        public List<ProvincesBean> getProvinces() {
            return provinces;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "provinces=" + provinces +
                    '}';
        }

        public void setProvinces(List<ProvincesBean> provinces) {
            this.provinces = provinces;
        }

        public static class ProvincesBean {
            /**
             * id : 1
             * name : 湖南省
             */

            private int id;
            private String name;

            @Override
            public String toString() {
                return "ProvincesBean{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
