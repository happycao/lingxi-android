package me.cl.lingxi.entity;

import java.util.List;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/04/22
 * desc   : 分页信息
 * version: 1.0
 */
public class PageInfo<E> {

    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private List<E> list;
    private Integer size;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<E> getList() {
        return list;
    }

    public void setList(List<E> list) {
        this.list = list;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
