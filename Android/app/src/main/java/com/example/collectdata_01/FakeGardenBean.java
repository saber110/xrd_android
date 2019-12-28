package com.example.collectdata_01;

import com.google.gson.Gson;

import java.util.List;

public class FakeGardenBean {

    /**
     * code : 0
     * data : {"gardenList":[{"cityName":"长沙市","communityName":"识字里社区","districtName":"芙蓉区","gardenName":"测试小区1","provinceName":"湖南省","streetName":"文艺路街道","gardenId":1,"userId":1}]}
     * message : 获取虚拟小区成功
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
        private List<GardenListBean> gardenList;

        public List<GardenListBean> getGardenList() {
            return gardenList;
        }

        public void setGardenList(List<GardenListBean> gardenList) {
            this.gardenList = gardenList;
        }

        public static class GardenListBean {
            /**
             * cityName : 长沙市
             * communityName : 识字里社区
             * districtName : 芙蓉区
             * gardenName : 测试小区1
             * provinceName : 湖南省
             * streetName : 文艺路街道
             * gardenId : 1
             * userId : 1
             */

            private String cityName;
            private String communityName;
            private String districtName;
            private String gardenName;
            private String provinceName;
            private String streetName;
            private int gardenId;
            private int userId;

            public String getCityName() {
                return cityName;
            }

            public void setCityName(String cityName) {
                this.cityName = cityName;
            }

            public String getCommunityName() {
                return communityName;
            }

            public void setCommunityName(String communityName) {
                this.communityName = communityName;
            }

            public String getDistrictName() {
                return districtName;
            }

            public void setDistrictName(String districtName) {
                this.districtName = districtName;
            }

            public String getGardenName() {
                return gardenName;
            }

            public void setGardenName(String gardenName) {
                this.gardenName = gardenName;
            }

            public String getProvinceName() {
                return provinceName;
            }

            public void setProvinceName(String provinceName) {
                this.provinceName = provinceName;
            }

            public String getStreetName() {
                return streetName;
            }

            public void setStreetName(String streetName) {
                this.streetName = streetName;
            }

            public int getGardenId() {
                return gardenId;
            }

            public void setGardenId(int gardenId) {
                this.gardenId = gardenId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }
        }
    }


    public static FakeGardenBean objectFromData(String str) {
        return new Gson().fromJson(str, FakeGardenBean.class);
    }
}
