package com.example.database;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

@Table("user_table")
public class StatusDb extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String USERNAME_COL = "username";

    @Column(USERNAME_COL)
    private String username;

    public StatusDb(String username){
        setUsername(username);
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return username;
    }
}


