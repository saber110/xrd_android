package com.example.map.dao;

import java.util.List;

public class SearchGardenResultDao {

    /**
     * code : 0
     * data : {"gardens":[{"cityName":"长沙市","communityName":"识字里社区","districtName":"芙蓉区","gardenId":16,"gardenName":"测试小区1","provinceName":"湖南省","streetName":"文艺路街道"}]}
     * message : 查找小区成功
     */

    private int code;
    private DataBean data;
    private String message;

    @Override
    public String toString() {
        return "SearchGardenResultDao{" +
                "code=" + code +
                ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

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
        @Override
        public String toString() {
            return "DataBean{" +
                    "gardens=" + gardens +
                    '}';
        }

        private List<GardensBean> gardens;

        public List<GardensBean> getGardens() {
            return gardens;
        }

        public void setGardens(List<GardensBean> gardens) {
            this.gardens = gardens;
        }

        public static class GardensBean {
            @Override
            public String toString() {
                return "GardensBean{" +
                        "cityName='" + cityName + '\'' +
                        ", communityName='" + communityName + '\'' +
                        ", districtName='" + districtName + '\'' +
                        ", gardenId='" + gardenId + '\'' +
                        ", gardenName='" + gardenName + '\'' +
                        ", provinceName='" + provinceName + '\'' +
                        ", streetName='" + streetName + '\'' +
                        '}';
            }

            /**
             * cityName : 长沙市
             * communityName : 识字里社区
             * districtName : 芙蓉区
             * gardenName : 测试小区1
             * provinceName : 湖南省
             * streetName : 文艺路街道
             */

            private String cityName;
            private String communityName;
            private String districtName;
            private int gardenId;
            private String gardenName;
            private String provinceName;
            private String streetName;

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

            public int getGardenId() {
                return gardenId;
            }

            public void setGardenId(int gardenId) {
                this.gardenId = gardenId;
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
        }
    }
}
