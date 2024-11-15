package com.qfmy.utils;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 处理字符串相关的工具类
 */
@SuppressWarnings("all")
public class StringUtils {

    /**
     * 将字符串的第一个字符转为大写
     * @param name
     * @return
     */
    public static String firstCharToUpperCase(String name){
       //判断字符串是否为空
        if(org.apache.commons.lang3.StringUtils.isEmpty(name))
        {
            return name;
        }
        //将字符串的第一个字符转为大写
        String newName = name.substring(0, 1).toUpperCase() + name.substring(1);
        return newName;
    }

    /**
     * 将字符串的第一个字符转为小写
     * @param name
     * @return
     */
    public static String firstCharToLowerCase(String name){
        //判断字符串是否为空
        if(org.apache.commons.lang3.StringUtils.isEmpty(name))
        {
            return name;
        }
        //将字符串的第一个字符转为大写
        String newName = name.substring(0, 1).toLowerCase() + name.substring(1);
        return newName;
    }

}
