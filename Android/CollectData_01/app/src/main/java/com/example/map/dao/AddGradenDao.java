package com.example.map.dao;

public class AddGradenDao {

    /**
     * token :
     * districtId : 1
     * streetId : 1
     * communityId : 1
     * gardenName : 测试小区
     */

    private String token;
    private int districtId;
    private int streetId;
    private int communityId;
    private String gardenName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public int getStreetId() {
        return streetId;
    }

    public void setStreetId(int streetId) {
        this.streetId = streetId;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public String getGardenName() {
        return gardenName;
    }

    public void setGardenName(String gardenName) {
        this.gardenName = gardenName;
    }
}
