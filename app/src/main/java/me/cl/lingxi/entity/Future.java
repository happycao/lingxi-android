package me.cl.lingxi.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2020/05/14
 * desc   : 写给未来
 * version: 1.0
 */
public class Future implements Serializable {

    private String id;
    private String userId;
    private String toMail;
    private String username;
    private String futureInfo;
    private Integer days;
    private Date createTime;
    private Date showTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToMail() {
        return toMail;
    }

    public void setToMail(String toMail) {
        this.toMail = toMail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFutureInfo() {
        return futureInfo;
    }

    public void setFutureInfo(String futureInfo) {
        this.futureInfo = futureInfo;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }
}
