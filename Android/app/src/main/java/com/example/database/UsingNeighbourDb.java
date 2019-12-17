package com.example.database;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

@Table("usingNeighbour_table")
public class UsingNeighbourDb extends BaseModel {
    public static final String GARDENID_COL = "gardenId";
    public static final String GARDENNAME_COL = "gardenName";

    @Column(GARDENID_COL)
    private String gardenId;
    @Column(GARDENNAME_COL)
    private String gardenName;

    public UsingNeighbourDb(String gardenId, String gardenName){
        setGardenId(gardenId);
        setGardenName(gardenName);
    }

    public String getGardenId() {
        return gardenId;
    }

    public void setGardenId(String gardenId) {
        this.gardenId = gardenId;
    }

    public String getGardenName() {
        return gardenName;
    }

    public void setGardenName(String gardenName) {
        this.gardenName = gardenName;
    }
}
