package com.qfmy.utils;

import com.alibaba.fastjson.JSON;

/**
 * @author: 廖志伟
 * @date: 2024-11-11
 * json工具类 -> 将一个对象转换为json字符串
 */
@SuppressWarnings("all")
public class JsonUtils {
    /**
     * 将一个对象转换为json字符串
     * @param object
     * @return
     */
    public static String convertObjectJson(Object object)
    {
        // 如果对象为空，则返回null
        if(object == null) {
            return null;
        }
        //将对象转换为json字符串
        return JSON.toJSONString(object);

    }
}
