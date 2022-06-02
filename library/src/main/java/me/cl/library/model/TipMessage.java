package me.cl.library.model;

import androidx.annotation.StringRes;

/**
 * @author : happyc
 * time    : 2021/05/10
 * desc    :
 * version : 1.0
 */
public class TipMessage {

    private int msgId;
    private String msgStr;
    private boolean res;

    public TipMessage() {
    }

    public TipMessage(int msgId) {
        this.msgId = msgId;
        this.res = true;
    }

    public TipMessage(String msgStr) {
        this.msgStr = msgStr;
        this.res = false;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsgStr() {
        return msgStr;
    }

    public void setMsgStr(String msgStr) {
        this.msgStr = msgStr;
    }

    public boolean isRes() {
        return res;
    }

    public void setRes(boolean res) {
        this.res = res;
    }

    public static TipMessage resId(@StringRes int msgId) {
        return new TipMessage(msgId);
    }

    public static TipMessage str(String msgStr) {
        return new TipMessage(msgStr);
    }
}
