package com.example.map.dao;

import java.util.List;

public class CommunityDao {

    /**
     * code : 1
     * message :
     * data : {"communities":[{"name":"","id":1}]}
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
        private List<CommunitiesBean> communities;

        public List<CommunitiesBean> getCommunities() {
            return communities;
        }

        public void setCommunities(List<CommunitiesBean> communities) {
            this.communities = communities;
        }

        public static class CommunitiesBean {
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
