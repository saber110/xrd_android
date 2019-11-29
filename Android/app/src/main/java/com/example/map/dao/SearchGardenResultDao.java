package com.example.map.dao;

import java.util.List;

public class SearchGardenResultDao {

    /**
     * code :
     * message :
     * data : {"buildingKinds":[{"kindName":1,"id":""}]}
     */

    private String code;
    private String message;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
        private List<BuildingKindsBean> buildingKinds;

        public List<BuildingKindsBean> getBuildingKinds() {
            return buildingKinds;
        }

        public void setBuildingKinds(List<BuildingKindsBean> buildingKinds) {
            this.buildingKinds = buildingKinds;
        }

        public static class BuildingKindsBean {
            /**
             * kindName : 1
             * id :
             */

            private int kindName;
            private String id;

            public int getKindName() {
                return kindName;
            }

            public void setKindName(int kindName) {
                this.kindName = kindName;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
