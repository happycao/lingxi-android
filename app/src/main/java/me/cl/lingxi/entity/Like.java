package me.cl.lingxi.entity;

import java.io.Serializable;

// 点赞
public class Like implements Serializable {

    private String userId;
    private String username;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
