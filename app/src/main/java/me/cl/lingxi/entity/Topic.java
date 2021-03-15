package me.cl.lingxi.entity;

import java.io.Serializable;

/**
 * @author : happyc
 * time    : 2020/11/09
 * desc    :
 * version : 1.0
 */
public class Topic implements Serializable {

    private String id;
    private String topicName;
    private String avatar;
    private boolean selected;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
