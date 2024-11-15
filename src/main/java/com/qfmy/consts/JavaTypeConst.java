package com.qfmy.consts;

/**
 * @author: 廖志伟
 * @date: 2024-11-11
 * 数据库字段类型->映射Java类型
 */
@SuppressWarnings("all")
public class JavaTypeConst {
    //日期时间类型
    public static final String[] SQL_DATE_TIME_TYPES = new String[] {"datetime","timestamp"};
    //日期类型
    public static final String[] SQL_DATE_TYPES = new String[] {"date"};
    //整型类型
    public static final String[] SQL_INTEGER_TYPES = new String[] {"tinyint","smallint","mediumint","int","integer","unsigned int","signed int"};
    //浮点型类型
    public static final String[] SQL_FLOAT_TYPES = new String[] {"float","double","decimal"};
    //字符串类型
    public static final String[] SQL_STRING_TYPES = new String[] {"char","varchar","text","tinytext","mediumtext","longtext"};
    //Long类型
    public static final String[] SQL_LONG_TYPES = new String[] {"bigint"};


}
