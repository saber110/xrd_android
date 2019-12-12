package com.example.collectdata_01;

import com.example.collectdata_01.BaseModel;
import com.litesuits.orm.db.annotation.Check;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;

import java.io.File;
import java.security.PublicKey;

/**
 * 用户信息类
 *
 * @author NapoleonBai
 *
 */
// 创建一个名为user_table的表格
@Table("user_table")
public class Users extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String COLLECTTIOME_COL = "collectTime";
    public static final String ISUPLOADED_COL = "isUploaded";
    public static final String GARDENID_COL = "gardenId";
    public static final String PICTUREKIND_COL = "pictureKind";

    // 非空约束 形同于@Check("userName NOT NULL")
    @NotNull
    @Column(GARDENID_COL)
    private String gardenId;
    @Column(PICTUREKIND_COL)
    private String pictureKind;

    @Column(COLLECTTIOME_COL)
    private String collectTime;
    private String token;
    private String image;
    private String buildingName;
    // false 为未上传
    // true  为已上传
    @Column(ISUPLOADED_COL)
    private Boolean isUploaded;


//    // 性别用 0 - 1 - 2替代,0=男,1=女,2=未知
//    @Check("userSex >= 0 AND userSex < 3")
//    // 设置默认值
//    @Default("2")
//    private int userSex;

    // 唯一键约束
//    @Unique
//    @NotNull
//    private String userPhone;
//
//    private String userEmail;

    /**
     * 构造方法，传入数据
     *
     * @param gardenId
     * @param pictureKind
     * @param collectTime
     * @param image
     */
    // 用作小区数据的持久化
    public Users(String gardenId, String pictureKind, String collectTime, String image) {
        this.gardenId = gardenId;
        this.buildingName = "非楼栋";
        this.pictureKind = pictureKind;
        this.collectTime = collectTime;
        this.image = image;
        this.isUploaded = false;
    }

    // 用作楼栋数据的持久化
    public Users(String buildingName, String pictureKind, String collectTime, String image, String gardenId) {
        this.buildingName = buildingName;
        this.gardenId = gardenId;
        this.pictureKind = pictureKind;
        this.collectTime = collectTime;
        this.image = image;
        this.isUploaded = false;
    }


    public Users() {
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getGardenId() {
        return gardenId;
    }

    public void setGardenId(String gardenId) {
        this.gardenId = gardenId;
    }

    public String getpictureKind(){return pictureKind;}

    public void setpictureKind(String pictureKind){this.pictureKind = pictureKind;}

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {return image;}

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsuploaded(){
        return isUploaded;
    }

    public void setIsuploaded(boolean isUploaded) {
        this.isUploaded = isUploaded;
    }
}