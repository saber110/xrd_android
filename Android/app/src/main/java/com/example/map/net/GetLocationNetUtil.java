package com.example.map.net;

import com.example.net.ProcessInterface;

public class GetLocationNetUtil {

    public static class GetProvinceNetUtil implements ProcessInterface {
        @Override
        public Object call() {
            return GetLoctionIdUtil.getProvinceId();
        }
    }

    public static class GetCityNetUtil implements ProcessInterface {

        private Integer provinceId;

        public GetCityNetUtil(Integer provinceId) {
            this.provinceId = provinceId;
        }

        @Override
        public Object call() {
            return GetLoctionIdUtil.getCityId(provinceId);
        }
    }

    public static class GetDistrictNetUtil implements ProcessInterface {
        private Integer cityId;

        public GetDistrictNetUtil(Integer cityId) {
            this.cityId = cityId;
        }

        @Override
        public Object call() {

            return GetLoctionIdUtil.getDistrictDao(cityId);
        }
    }

    public static class GetStreetNetUtil implements ProcessInterface {
        private Integer districtId;

        public GetStreetNetUtil(Integer districtId) {
            this.districtId = districtId;
        }

        @Override
        public Object call() {
            return GetLoctionIdUtil.getStreetDao(districtId);
        }
    }

    public static class GetCommunityNetUtil implements ProcessInterface {
        private Integer streetId;

        public GetCommunityNetUtil(Integer streetId) {
            this.streetId = streetId;
        }

        @Override
        public Object call() {
            return GetLoctionIdUtil.getCommunityDao(streetId);
        }
    }
}
