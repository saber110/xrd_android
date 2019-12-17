package com.example.database;

import java.io.Serializable;

import com.litesuits.orm.db.annotation.Check;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * 所有Model的顶层父类
 *
 * @author NapoleonBai
 *
 */
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    // 设置为主键,自增
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    // 取名为“_id”,如果此处不重新命名,就采用属性名称
    @Column("_id")
    public int id;
//    @Column("_kind")
//    public String pictureKind;
//    @Column("_time")
//    public String collectTime;
//    @Column("_token")
//    public String token;
//    @Column("_image")
//    public String image;

//    // @Check条件检测
//    @Check("description NOT NULL")
//    public String description = "字段描述";
//
//    @Ignore
//    private String ignore = "标记Ignore,并不会出现在数据库中";

//    @Override
//    public String toString() {\n" +
//                "        return \"BaseModel{\" + \"description='" + description + '\'' + '}';
//    }
}