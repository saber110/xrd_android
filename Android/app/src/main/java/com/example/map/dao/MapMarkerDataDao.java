package com.example.map.dao;

import java.util.List;

public class MapMarkerDataDao {

    /**
     * code : 0
     * data : {"map_data":[{"gardenId":1,"id":1,"kindId":1,"latitude":39.91692679672596,"longitude":116.4028333590616,"name":"第一栋","userId":1}]}
     * message : 获取地图数据成功
     */

    private int code;
    private DataBean data;
    private String message;

    @Override
    public String toString() {
        return "MapMarkerDataDao{" +
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
                    "map_data=" + map_data +
                    '}';
        }

        private List<MapDataBean> map_data;

        public List<MapDataBean> getMap_data() {
            return map_data;
        }

        public void setMap_data(List<MapDataBean> map_data) {
            this.map_data = map_data;
        }

        public static class MapDataBean {
            @Override
            public String toString() {
                return "MapDataBean{" +
                        "gardenId=" + gardenId +
                        ", id=" + id +
                        ", kindId=" + kindId +
                        ", latitude=" + latitude +
                        ", longitude=" + longitude +
                        ", name='" + name + '\'' +
                        ", userId=" + userId +
                        '}';
            }

            /**
             * gardenId : 1
             * id : 1
             * kindId : 1
             * latitude : 39.91692679672596
             * longitude : 116.4028333590616
             * name : 第一栋
             * userId : 1
             */

            private int gardenId;
            private int id;
            private int kindId;
            private double latitude;
            private double longitude;
            private String name;
            private int userId;

            public int getGardenId() {
                return gardenId;
            }

            public void setGardenId(int gardenId) {
                this.gardenId = gardenId;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getKindId() {
                return kindId;
            }

            public void setKindId(int kindId) {
                this.kindId = kindId;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }
        }
    }
}
