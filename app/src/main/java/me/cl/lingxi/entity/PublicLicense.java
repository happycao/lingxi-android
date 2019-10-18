package me.cl.lingxi.entity;

import java.io.Serializable;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/07
 * desc   : 开源相关
 * version: 1.0
 */
public class PublicLicense implements Serializable{

    private String name;
    private String author;
    private String desc;
    private String url;

    public PublicLicense(String name, String author, String desc, String url) {
        this.name = name;
        this.author = author;
        this.desc = desc;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
