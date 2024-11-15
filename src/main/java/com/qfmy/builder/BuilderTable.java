package com.qfmy.builder;

import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
import com.qfmy.consts.JavaTypeConst;
import com.qfmy.consts.SqlConstInfo;
import com.qfmy.consts.TableConstInfo;
import com.qfmy.utils.MysqlUtils;
import com.qfmy.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 创建表
 */
@SuppressWarnings("all")
public class BuilderTable {
    //创建日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderTable.class);
    //创建一个集合，存放表信息
    private static List<TableInfo> tableInfos = new ArrayList<>();
    //创建一个集合，存放字段信息
    private static List<FieldInfo> fieldInfos = new ArrayList<>();

    /**
     * 创建表
     */
    public static List<TableInfo> getTables() {
        // 创建PreparedStatement对象
        PreparedStatement ps = null;
        // 创建结果集
        ResultSet rs = null;
        //执行SQL语句
        try {
            ps = MysqlUtils.getConn().prepareStatement(SqlConstInfo.SQL_TABLE_INFO);
            // 执行查询 ->并且获取结果集对象
            rs = ps.executeQuery();
            // 循环遍历结果集
            while(rs.next())
            {
                //获取表名
                String tableName = rs.getString("name");
                //获取表注释
                String tableComment = rs.getString("comment");
                //把表名映射到实体类
               String beanName = tableName;
               //判断是否要忽略表的前缀
                if(TableConstInfo.IGNORE_TABLE_PREFIX)
                {
                    beanName = tableName.substring(tableName.indexOf("_") + 1);
                }
                //对实体类名进行转换
                beanName = ProcessTableOrFiled(beanName, true);
                //创建TableInfo对象
                TableInfo tableInfo = new TableInfo();
                //设置表名
                tableInfo.setTableName(tableName);
                //设置实体类名
                tableInfo.setBeanName(beanName);
                //设置表注释
                tableInfo.setComment(tableComment);
                //设置表的参数名称
                tableInfo.setBeanParamName(beanName + TableConstInfo.PARAM_BEAN_SUFFIX);
                //获取字段信息
                fieldInfos = ReadisFields(tableInfo);
                //设置字段信息
                tableInfo.setFieldList(fieldInfos);
                //读取索引信息
                readIndexInfo(tableInfo);
                //把TableInfo对象添加到集合中
                tableInfos.add(tableInfo);
            }
        } catch (SQLException e) {
            // 打印异常信息
            log.error("获取表信息失败", e);
        }
        finally {
            // 关闭连接
           MysqlUtils.close(ps, rs);
        }
        return tableInfos;
    }

    private static @NotNull List<FieldInfo> ReadisFields(@NotNull TableInfo tableInfo)
    {
        //定义一个Boolean变量，用来判断是否有BigDecimal类型字段
        boolean isBigDecimal = false;
        //定义一个Boolean变量，用来判断是否有日期类型字段
        boolean isDate = false;
        //定义一个Boolean变量，用来判断是否有日期时间类型字段
        boolean isDateTime = false;

        //创建一个字段信息集合
        List<FieldInfo> fieldListInfos = new ArrayList<>();
        // 创建PreparedStatement对象
        PreparedStatement ps = null;
        // 创建结果集
        ResultSet rs = null;
        //执行SQL语句
        try {
            ps = MysqlUtils.getConn().prepareStatement(String.format(SqlConstInfo.SQL_TABLE_COLUMN_INFO, tableInfo.getTableName()));
            // 执行查询 ->并且获取结果集对象
            rs = ps.executeQuery();
            // 遍历结果集
            while(rs.next())
            {
               //获取字段名
                String fieldName = rs.getString("field");
                //获取字段注释
                String fieldComment = rs.getString("comment");
                //获取字段类型
                String fieldType = rs.getString("type");
                //获取是否自增
                boolean isAutoIncrement = rs.getString("extra").equals("auto_increment");
                //创建字段信息对象
                FieldInfo fieldInfo = new FieldInfo();
                //设置字段名
                fieldInfo.setFiledName(fieldName);
                //将字段名称转为驼峰式
                fieldName = ProcessTableOrFiled(fieldName, false);
                //设置字段所对应的bean名称
                fieldInfo.setPropertyName(fieldName);
                //设置字段注释
                fieldInfo.setComment(fieldComment);
                //把字段类型()去掉
                if(fieldType.indexOf("(") > 0)
                {
                    fieldType = fieldType.substring(0, fieldType.indexOf("("));
                }
                //设置字段类型
                fieldInfo.setSqlType(fieldType);
                //把字段类型转换为java类型
                String javaType = processJavaType(fieldType);
                //设置参数类型
                fieldInfo.setJavaType(javaType);
                //设置是否自增
                fieldInfo.setAutoIncrement(isAutoIncrement);
                //把字段信息添加到集合中
                fieldListInfos.add(fieldInfo);
                //判断是否有有日期时间类型字段
                if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TIME_TYPES, fieldType))
                {
                   isDateTime = true;
                }
                //判日期类型字段
                if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TYPES, fieldType)) {
                    isDate = true;
                }
                //判断是否有BigDecimal类型字段
                if(ArrayUtils.contains(JavaTypeConst.SQL_FLOAT_TYPES, fieldType))
                {
                    isBigDecimal = true;
                }
            }
            //设置日期类型字段
            tableInfo.setHaveData(isDate);
            //设置日期时间类型字段
            tableInfo.setHaveDateTime(isDateTime);
            //设置BigDecimal类型字段
            tableInfo.setHaveBigDecimal(isBigDecimal);
        } catch (SQLException e) {
            log.error("创建字段失败", e);
        }
        finally{
            MysqlUtils.close( ps, rs);
        }
        //返回字段信息
        return fieldListInfos;
    }

    /**
     * 读取索引信息
     * @param tableInfo 表信息
     */
    public static void readIndexInfo(TableInfo tableInfo)
    {
        //定义一个缓冲区
        Map<String, FieldInfo> listMap = new HashMap<>();
        //使用流式编程，遍历字段信息
       tableInfo.getFieldList().stream().forEach(fieldInfo -> {
           //获取字段名
           String fieldName = fieldInfo.getFiledName();
           //获取字段信息
           listMap.put(fieldName, fieldInfo);
       });;
        // 创建PreparedStatement对象
        PreparedStatement ps = null;
        // 创建结果集
        ResultSet rs = null;
        //执行SQL语句
        try {
            ps = MysqlUtils.getConn().prepareStatement(String.format(SqlConstInfo.SQL_UNIQUE_INDEX, tableInfo.getTableName()));
            // 执行查询 ->并且获取结果集对象
            rs = ps.executeQuery();
            // 遍历结果集
            while(rs.next())
            {
                // 获取索引名
                String indexName = rs.getString("key_name");
                //获取唯一索引
                Integer nonUnique = rs.getInt("non_unique");
                //获取索引字段
                String column = rs.getString("column_name");
                //判断是否是唯一索引
                if(nonUnique == 1)
                {
                    //跳过主键索引
                    continue;
                }
                //获取索引字段信息
                List<FieldInfo> filedList = tableInfo.getKeyIndexMap().get(indexName);
                //判断是否有索引字段信息
                if(null == filedList)
                {
                    //创建一个集合，存放索引字段信息
                    filedList = new ArrayList<>();
                }
                filedList.add(listMap.get(column));
                //将索引字段信息存放到集合中
                tableInfo.getKeyIndexMap().put(indexName, filedList);


            }
        } catch (SQLException e) {
            log.error("创建索引失败", e);
        }
        finally{
            // 关闭连接
            MysqlUtils.close( ps, rs);
        }
    }

    /**
     * 根据数据库类型，返回对应的java类型
     * @param sqlType 数据库类型
     * @return java类型
     */
    public static String processJavaType(String sqlType)
    {
        //根据数据库类型，返回对应的java类型
        if(ArrayUtils.contains(JavaTypeConst.SQL_INTEGER_TYPES, sqlType))
        {
            return "Integer";
        }
        else if(ArrayUtils.contains(JavaTypeConst.SQL_FLOAT_TYPES, sqlType))
        {
            return "Double";
        }
        else if(ArrayUtils.contains(JavaTypeConst.SQL_STRING_TYPES, sqlType))
        {
            return "String";
        }
        else if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TYPES, sqlType) || ArrayUtils.contains(JavaTypeConst.SQL_DATE_TIME_TYPES, sqlType))
        {
            return "Date";
        }
       else if(ArrayUtils.contains(JavaTypeConst.SQL_LONG_TYPES, sqlType))
        {
            return "Long";
        }
       else
        {
//            throw new RuntimeException("不支持的数据库类型：" + sqlType);
            return "Object";
        }
    }


    /**
     * 处理表名或者字段名
     * @param name 表名或者字段名
     * @param isToUpperCase 是否转换为大写
     * @return 处理后的表名或者字段名
     */
    public static String ProcessTableOrFiled(String name, boolean isToLowerCase)
    {
        //创建一个StringBuilder对象
        StringBuilder sb = new StringBuilder();
        //对表名或者字段名进行处理
        String[] processName = name.split("_");
        //判断是否要转换为大写
        if(isToLowerCase)
        {
            //将表名或者字段名转换为大写
            String newName = StringUtils.firstCharToUpperCase(processName[0]);
            //把表名或者字段名添加到StringBuilder对象中
            sb.append(newName);
        }
        else
        {
            //将表名或者字段名转换为小写
            String newName = StringUtils.firstCharToLowerCase(processName[0]);
            //把表名或者字段名添加到StringBuilder对象中
            sb.append(newName);
        }
        //循环遍历processName数组
        for (int i = 1; i <processName.length; i++) {
            //将表名或者字段名转换为大写
            String newName = StringUtils.firstCharToUpperCase(processName[i]);
            //把表名或者字段名添加到StringBuilder对象中
            sb.append(newName);
        }
        //返回处理后的表名或者字段名
        return sb.toString();

    }
}
