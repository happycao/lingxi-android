package me.cl.lingxi.common.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.util.Arrays;
import java.util.List;

/**
 * Gson工具类
 * Created by bafsj on 17/3/31.
 */
public class GsonUtil {

    private static Gson gson = new Gson();

    /**
     * 解析json
     * @param json json字符串
     * @param clazz class
     * @return 对象
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     *
     * @param json json字符串
     * @param type Type
     * @return 对象
     */
    public static <T> T toObject(String json, Type type) {
        return gson.fromJson(json, type);
    }

    /**
     * 解析json数组
     * @param json json字符串
     * @param clazz 示例 T[].class
     * @return 集合
     */
    public static <T> List<T> toList(String json, Class<T[]> clazz) {
        T[] array = gson.fromJson(json, clazz);
        return Arrays.asList(array);
    }

    /**
     * 将Object转为json
     * @param src Object
     * @return json字符串
     */
    public static String toJson(Object src) {
        return gson.toJson(src);
    }
}