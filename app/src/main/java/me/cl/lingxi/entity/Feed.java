package me.cl.lingxi.entity;

import java.io.Serializable;
import java.util.List;

// 动态
public class Feed implements Serializable {

    private String id;
    // 用户信息
    private User user;
    private String feedInfo;
    private Integer viewNum;
    // 评论数
    private Integer commentNum;
    // 当前用户是否点赞
    private boolean like;
    // 点赞列表
    private List<Like> likeList;
    // 相册
    private List<String> photoList;
    private String createTime;
    private String updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFeedInfo() {
        return feedInfo;
    }

    public void setFeedInfo(String feedInfo) {
        this.feedInfo = feedInfo;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public List<Like> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<Like> likeList) {
        this.likeList = likeList;
    }

    public List<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<String> photoList) {
        this.photoList = photoList;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", feedInfo='" + feedInfo + '\'' +
                ", viewNum=" + viewNum +
                ", commentNum=" + commentNum +
                ", like=" + like +
                ", likeList=" + likeList +
                ", photoList=" + photoList +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
