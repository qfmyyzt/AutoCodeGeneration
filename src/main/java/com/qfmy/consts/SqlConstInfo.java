package com.qfmy.consts;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 要执行的固定的SQL语句
 */
@SuppressWarnings("all")
public class SqlConstInfo {
    //查看数据库表信息的SQL语句
    public static final String SQL_TABLE_INFO = "show table status";
    //查看表里面的字段信息的SQL语句
    public static final String SQL_TABLE_COLUMN_INFO = "show full columns from %s";
    //读取唯一索引的SQL语句
    public static final String SQL_UNIQUE_INDEX = "show index from %s";
}
