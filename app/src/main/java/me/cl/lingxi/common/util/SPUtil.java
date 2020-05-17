package me.cl.lingxi.common.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import me.cl.lingxi.module.LxApplication;

/**
 * SharedPreferences 工具类
 * 需要在Application中注册{@link LxApplication#onCreate()}
 */
public class SPUtil {

    private SharedPreferences mSp;
    private SharedPreferences.Editor mEditor;

    private static SPUtil mInstance;

    public static SPUtil newInstance(){
        if (mInstance == null)
            synchronized (SPUtil.class) {
                if (mInstance == null) {
                    mInstance = new SPUtil();
                }
            }
        return mInstance;
    }

    public SPUtil init(Application context) {
        mSp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        mEditor = mSp.edit();
        mEditor.apply();
        return this;
    }

    public SharedPreferences getSp() {
        return mSp;
    }

    public SharedPreferences.Editor getEditor() {
        return mEditor;
    }

    public static Builder build() {
        return new Builder();
    }

    public static class Builder {

        private SPUtil mSPUtil;
        private SharedPreferences mSp;
        private SharedPreferences.Editor mEditor;

        public Builder() {
            mSPUtil = SPUtil.newInstance();
            mSp = mSPUtil.getSp();
            mEditor = mSPUtil.getEditor();
        }

        /**
         * SP中写入String类型value
         */
        public Builder putString(String key, String value) {
            mEditor.putString(key, value).apply();
            return this;
        }

        /**
         * SP中读取String
         */
        public String getString(String key) {
            return getString(key, "");
        }

        /**
         * SP中读取String
         */
        public String getString(String key, String defaultValue) {
            return mSp.getString(key, defaultValue);
        }

        /**
         * SP中写入int类型value
         */
        public void putInt(String key, int value) {
            mEditor.putInt(key, value).apply();
        }

        /**
         * SP中读取int
         */
        public int getInt(String key) {
            return getInt(key, -1);
        }

        /**
         * SP中读取int
         */
        public int getInt(String key, int defaultValue) {
            return mSp.getInt(key, defaultValue);
        }

        /**
         * SP中写入long类型value
         */
        public void putLong(String key, long value) {
            mEditor.putLong(key, value).apply();
        }

        /**
         * SP中读取long
         */
        public long getLong(String key) {
            return getLong(key, -1L);
        }

        /**
         * SP中读取long
         */
        public long getLong(String key, long defaultValue) {
            return mSp.getLong(key, defaultValue);
        }

        /**
         */
        public void putFloat(String key, float value) {
            mEditor.putFloat(key, value).apply();
        }

        /**
         * SP中读取float
         */
        public float getFloat(String key) {
            return getFloat(key, -1f);
        }

        /**
         * SP中读取float
         */
        public float getFloat(String key, float defaultValue) {
            return mSp.getFloat(key, defaultValue);
        }

        /**
         * SP中写入boolean类型value
         */
        public void putBoolean(String key, boolean value) {
            mEditor.putBoolean(key, value).apply();
        }

        /**
         * SP中读取boolean
         */
        public boolean getBoolean(String key) {
            return getBoolean(key, false);
        }

        /**
         * SP中读取boolean
         */
        public boolean getBoolean(String key, boolean defaultValue) {
            return mSp.getBoolean(key, defaultValue);
        }

        /**
         * 获取SP中所有键值对
         */
        public Map<String, ?> getAll() {
            return mSp.getAll();
        }

        /**
         * 从SP中移除该key
         */
        public void remove(String key) {
            mEditor.remove(key).apply();
        }

        /**
         * 判断SP中是否存在该key
         */
        public boolean contains(String key) {
            return mSp.contains(key);
        }

        /**
         * 清除SP中所有数据
         */
        public void clear() {
            mEditor.clear().apply();
        }
    }

}
