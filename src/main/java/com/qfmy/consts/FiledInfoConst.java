package com.qfmy.consts;

import com.qfmy.utils.PropertiesUtils;

/**
 * @author: 廖志伟
 * @date: 2024-11-14
 * @Description: 字段常量信息类
 */
@SuppressWarnings("all")
public class FiledInfoConst {
    //是否使用逻辑删除字段
    public static final boolean IS_LOGIC_DELETE = Boolean.parseBoolean(PropertiesUtils.getProperty("logic.delete"));
    //逻辑删除字段名称
    public static final String LOGIC_DELETE_FIELD = PropertiesUtils.getProperty("logic.delete.field");
    //数据库里面逻辑删除字段名称
    public static final String LOGIC_DELETE_FIELD_DB = PropertiesUtils.getProperty("logic.delete.sql_filed");
}
