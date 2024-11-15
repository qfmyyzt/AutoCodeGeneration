package com.qfmy.consts;

import com.qfmy.utils.PropertiesUtils;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * @Description: 表常量信息类
 */
@SuppressWarnings("all")
public class TableConstInfo {
    // 忽视表名前缀
    public static final Boolean IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getProperty("ignore.table.predix"));
    //参数bean的后缀
    public static final String PARAM_BEAN_SUFFIX = PropertiesUtils.getProperty("suffix.bean.param");
    //注释的的作者名称
    public static final String AUTHOR_NAME = PropertiesUtils.getProperty("author.name");
    //需要忽略的属性
    public static final String[] IGNORE_FIELDS = PropertiesUtils.getProperty("ignore.bean.toJson.filed").split(",");
    //使用那个注解进行忽视
    public static final String IGNORE_ANNOTATION = PropertiesUtils.getProperty("ignore.bean.toJson.annotation");
    //使用那个注解的包
    public static final String IGNORE_ANNOTATION_PACKAGE = PropertiesUtils.getProperty("ignore.bean.toJson.annotation.package");
    //日期格式化方式
    public static final String DATE_FORMAT = PropertiesUtils.getProperty("date.format");
    //日期格式化方式的包->所需要的包
    public static final String DATE_FORMAT_PACKAGE = PropertiesUtils.getProperty("date.format.package");
    //日期反序列化
    public static final String DATE_DESERIALIZE = PropertiesUtils.getProperty("date.deserializer");
    //日期反序列化方式的包->所需要的包
    public static final String DATE_DESERIALIZE_PACKAGE = PropertiesUtils.getProperty("date.deserializer.package");
    //对前端不显示的字段
    public static final String[] NOT_SHOW_FIELDS = PropertiesUtils.getProperty("ignore.bean.toFront.filed").split(",");
    //参数模糊搜索的后缀名称
    public static final String PARAM_FUZZY_SUFFIX = PropertiesUtils.getProperty("suffix.param.fuzzy");
    //参数的起始日期后缀
    public static final String PARAM_START_DATE_SUFFIX = PropertiesUtils.getProperty("suffix.param.start");
    //参数的结束日期后缀
    public static final String PARAM_END_DATE_SUFFIX = PropertiesUtils.getProperty("suffix.param.end");
    //定义mapper接口的后缀
    public static final String MAPPER_INTERFACE_SUFFIX = PropertiesUtils.getProperty("suffix.mapper");



}
