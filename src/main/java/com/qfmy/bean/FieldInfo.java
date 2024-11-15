package com.qfmy.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 字段的信息
 */
@SuppressWarnings("all")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldInfo {
    //字段名称
    private String FiledName;
    //字段所对应的bean名称
    private String propertyName;
    //字段所对应的数据库类型
    private String sqlType;
    //字段所对应的java类的类型
    private String javaType;
    //字段注释
    private String comment;
    //字段是否自增
    private boolean isAutoIncrement;
}
