package com.qfmy.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 表的信息
 */
@SuppressWarnings("all")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {
    //表名
    private String tableName;
    //bean名称 ->表所对应的实体类名称
    private String beanName;
    //参数名称
    private String beanParamName;
    //表注释
    private String comment;
    //字段信息
    private List<FieldInfo> fieldList;
    //创建一个扩展字段信息
    private List<FieldInfo> extendFieldList;
    //唯一索引集合
    private Map<String, List<FieldInfo>> keyIndexMap = new LinkedHashMap();
    //是否有data类型 -> 用来特殊处理
    private boolean haveData;
    //是否有时间类型 -> 用来特殊处理
    private boolean haveDateTime;
    //是否有BigDecimal类型 -> 用来特殊处理
    private boolean haveBigDecimal;
    //是否有自增长id -> 用来特殊处理
    private boolean haveAutoIncrementId;


}
