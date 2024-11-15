package com.qfmy.builder;

import ch.qos.logback.classic.Logger;
import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
import com.qfmy.consts.FiledInfoConst;
import com.qfmy.consts.JavaTypeConst;
import com.qfmy.consts.PathAndPackageConst;
import com.qfmy.consts.TableConstInfo;
import com.qfmy.utils.CommentUtils;
import com.qfmy.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author: 廖志伟
 * @date: 2024-11-13
 * 创建Mapper的xml文件
 */
@SuppressWarnings("all")
public class BuilderMapperXml {
    //定义一个日志记录器
    private static final Logger log = (Logger) LoggerFactory.getLogger(BuilderMapperXml.class);
    //定义一个ResultMap的id
    private static final String RESULT_MAP_ID = "base_result_map";
    //定义一个通用查询列的id
    private static final String BASE_COLUMN_LIST = "base_column_list";
    //定义一个基础查询语句的id
    private static final String BASE_QUERY_CONDITION = "base_query_condition";
    //定义一个query_condition的id
    private static final String QUERY_CONDITION = "query_condition";
    //定义一个query_condition_extend的id
    private  static final String BASE_QUERY_CONDITION_EXTEND = "base_query_condition_extend";

    /**
     * 生成xml文件
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_XML);
        //判断文件夹是否存在，不存在则创建
        if(!folder.exists())
        {
            folder.mkdirs();
        }

        //创建mapper接口文件
        File file = new File(folder, tableInfo.getBeanName() + TableConstInfo.MAPPER_INTERFACE_SUFFIX+ ".xml" );
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        try {
            os = new FileOutputStream(file);
            ow = new OutputStreamWriter(os, "UTF-8");
            bw = new BufferedWriter(ow);
            //写入子类
            writerChildFile(tableInfo, bw);

        } catch (Exception e) {
            log.error("创建xml文件失败！", e);
        }finally {
            //关闭流
            if(bw != null)
            {
                try {
                    bw.close();
                } catch (IOException e) {
                    log.error("关闭流失败！", e);
                }
            }
            if(ow != null)
            {
                try {
                    ow.close();
                } catch (IOException e) {
                    log.error("关闭流失败！", e);
                }
            }
            if(os != null)
            {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("关闭流失败！", e);
                }
            }
        }
    }

    /**
     * 创建子类文件
     * @param tableInfo
     * @param bw
     */
    private static void writerChildFile(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws Exception {
        //编写xml文件
        bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\n\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
        bw.write("<mapper namespace=\"" + PathAndPackageConst.PACKAGE_MAPPER+ "." + tableInfo.getBeanName() + TableConstInfo.MAPPER_INTERFACE_SUFFIX + "\">\n\n");
        //判断是否要开启缓冲存
        if(PathAndPackageConst.CACHE_OPEN)
        {
            bw.write("\t<!-- 开启缓存 -->\n");
            bw.write("\t<cache/>\n\n");
        }
        //实体类映射
        writerResultMap(tableInfo, bw);
        //通用查询列
        writeSelectColumns(tableInfo, bw);
        //通用查询条件
        writerSelectBase(tableInfo, bw);
        //通用查询条件的引用
        writerSelectCondition(tableInfo, bw);
        //对扩展字段的通用的模糊查询方法
        writerSelectLike(tableInfo, bw);
        //定义一个查询集合
        writerSelectList(tableInfo, bw);
        //定义一个查询集合的总数
        selectCount(tableInfo, bw);
        //定义一个插入方法
        insert(tableInfo, bw);
        //插入或者更新方法
        insertOrUpdate(tableInfo, bw);
        //批量插入
        insertBatch(tableInfo, bw);
        //批量更新或者插入
        insertOrUpdateBatch(tableInfo, bw);
        //基础的增删改查方法
        SelectOrDelete(tableInfo, bw);
        bw.write("</mapper>");
        //刷新缓冲区
        bw.flush();
    }



    /**
     * 生成resultMap
     * @param tableInfo
     * @param bw
     * @throws Exception
     */
    public static void writerResultMap(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws Exception {
        //实体类映射
        bw.write("<!-- 实体类映射 -->\n");
        bw.write("\t<resultMap id=" + "\"" + RESULT_MAP_ID + "\" type=\"" + PathAndPackageConst.PACKAGE_POJO + "." + tableInfo.getBeanName() + "\">\n\n");
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        //定义一个变量用来记录主键的主键
        FieldInfo primaryKey = null;
        //遍历主键获取主键
        for(var entry : keyIndexMap.entrySet())
        {
            if("PRIMARY_KEY".equals(entry.getKey()))
            {
                //获取主键
                primaryKey = entry.getValue().get(0);
                List<FieldInfo> value = entry.getValue();
                if(value.size() == 1)
                {
                    //唯一主键
                    primaryKey = value.get(0);
                }
            }
        }
        //遍历字段
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        for(var fieldInfo : fieldList)
        {
            String key = "";
            //判断是否是id字段
            if(fieldInfo.getPropertyName().equals("id") && primaryKey != null)
            {
                try {
                    bw.write("\t\t<!-- "+fieldInfo.getComment()+" -->\n");
                    bw.write("\t\t<id column=\"" + fieldInfo.getFiledName() + "\" property=\"" + fieldInfo.getPropertyName()  + "\" />\n");
                    key = "id";
                } catch (IOException e) {
                    log.error("写入id字段失败！", e);
                }
            }
            else{
                key = "result";
                try {
                    bw.write("\t\t<!-- "+fieldInfo.getComment()+" -->\n");
                    //判断是否是日期类型
                    if(!fieldInfo.getJavaType().equals(JavaTypeConst.SQL_DATE_TYPES) || fieldInfo.getJavaType().equals(JavaTypeConst.SQL_DATE_TIME_TYPES))
                    {
                        bw.write("\t\t<result column=\"" + fieldInfo.getFiledName() + "\" property=\"" + fieldInfo.getPropertyName()  + "\" jdbcType=" + "\"" + fieldInfo.getSqlType() + "\" />\n");
                        bw.write("\t\t<" + key + " column=\"" + fieldInfo.getFiledName() + "\" property=\"" + fieldInfo.getPropertyName()  + "\" />\n");
                    }
                } catch (IOException e) {
                    log.error("写入字段失败！", e);
                }
            }

        }
        bw.write("</resultMap>\n\n");
    }

    /**
     * 生成通用查询列
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void writeSelectColumns(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("\t<!-- 通用查询列 -->\n");
        bw.write("<sql id=\""+ BASE_COLUMN_LIST +"\">\n");
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        StringBuilder sb = new StringBuilder();
        fieldList.forEach(fieldInfo -> {
            sb.append(fieldInfo.getFiledName()).append(",");
        });
        //进行去除最后一个逗号
        String columnList = sb.substring(0, sb.length() - 1);
        bw.write("\t\t" + columnList + "\n");
        bw.write("</sql>\n\n");
    }

    /**
     * 基础查询语句
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void writerSelectBase(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("\t<!-- 基础查询语句 -->\n");
        bw.write("<sql id=\""+ BASE_QUERY_CONDITION +"\">\n");
        final String query = "query.";
        //获取所有的字段
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        //遍历字段
        fieldList.forEach(fieldInfo -> {
            //对于String类型字段，加上字符串不为""的条件
            if(ArrayUtils.contains(JavaTypeConst.SQL_STRING_TYPES,fieldInfo.getSqlType()))
            {
                try {
                    bw.write("\t\t<if test= \"" + query + fieldInfo.getPropertyName() + " != null and " + query + fieldInfo.getPropertyName() + "!= ''\">\n");
                    bw.write("\t\t\tand " + fieldInfo.getFiledName() + " = #{query." + fieldInfo.getPropertyName() + "}\n");
                    bw.write("\t\t</if>\n");
                } catch (IOException e) {
                    log.error("写入if失败！", e);
                }
            }
           //对于其他类型字段，加上不为null的条件
            try {
                bw.write("\t\t<if test= \""+ query + fieldInfo.getPropertyName() + " != null\">\n");
                bw.write("\t\t\tand " + fieldInfo.getFiledName() + " = #{query." + fieldInfo.getPropertyName() + "}\n");
                bw.write("\t\t</if>\n");
            } catch (IOException e) {
                log.error("写入if失败！", e);
            }
        });
        bw.write("</sql>\n\n");
    }

    /**
     * 生成通用查询条件引用
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void writerSelectCondition(TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("\t<!-- 通用查询条件列 -->\n");
        bw.write("<sql id=\""+ QUERY_CONDITION +"\">\n");
        bw.write("\t<where>\n");
        bw.write("\t\t<include refid=\""+ BASE_QUERY_CONDITION +"\" />\n");
        bw.write("\t\t<include refid= \""+ BASE_QUERY_CONDITION_EXTEND +"\"/>\n");
        bw.write("\t</where>\n");
        bw.write("</sql>\n\n");
    }

    /**
     * 对扩展字段的通用的模糊查询方法
     * @param tableInfo
     * @param bw
     */
    private static void writerSelectLike(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("\t<!-- 扩展字段模糊查询 -->\n");
        bw.write("<sql id=\""+ BASE_QUERY_CONDITION_EXTEND +"\">\n");
        //获取扩展字段
        List<FieldInfo> extendFieldList = tableInfo.getExtendFieldList();
        final String query = "query.";
        //判断是否有扩展字段
        if(extendFieldList != null && extendFieldList.size() > 0) {
            extendFieldList.forEach(fieldInfo -> {
                //判断是否是字符串类型
                if(ArrayUtils.contains(JavaTypeConst.SQL_STRING_TYPES,fieldInfo.getSqlType()))
                {
                    try {
                        bw.write("\t\t<if test= \"" + query + fieldInfo.getPropertyName() + TableConstInfo.PARAM_FUZZY_SUFFIX + " != null and " + query + fieldInfo.getPropertyName() + TableConstInfo.PARAM_FUZZY_SUFFIX + "!= ''\">\n");
                        bw.write("\t\t\tand " + fieldInfo.getFiledName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + TableConstInfo.PARAM_FUZZY_SUFFIX + "}, '%')\n");
                        bw.write("\t\t</if>\n");
                    } catch (IOException e) {
                        log.error("写入if失败！", e);
                    }
                }
                //判断是d对于时间类型
                if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TYPES,fieldInfo.getSqlType()) || ArrayUtils.contains(JavaTypeConst.SQL_DATE_TIME_TYPES,fieldInfo.getSqlType()))
                {
                    try {
                        //开始时间
                        bw.write("\t\t<if test= \"" + query + fieldInfo.getPropertyName() + TableConstInfo.PARAM_START_DATE_SUFFIX + " != null and " + query + fieldInfo.getPropertyName() + TableConstInfo.PARAM_START_DATE_SUFFIX + "!= ''\">\n");
                        bw.write("\t\t\t<![CDATA[ and " + fieldInfo.getFiledName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + TableConstInfo.PARAM_START_DATE_SUFFIX + "}, '%Y-%m-%d %H:%i:%BaseException.txt')]]>\n");
                        bw.write("\t\t</if>\n");
                        //结束时间
                        bw.write("\t\t<if test= \"" + query + fieldInfo.getPropertyName() + TableConstInfo.PARAM_END_DATE_SUFFIX + " != null and " + query + fieldInfo.getPropertyName() + TableConstInfo.PARAM_END_DATE_SUFFIX + "!= ''\">\n");
                        bw.write("\t\t\t<![CDATA[ and " + fieldInfo.getFiledName() + " < date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + TableConstInfo.PARAM_END_DATE_SUFFIX + "}, '%Y-%m-%d %H:%i:%BaseException.txt'), interval -1 day)]]>\n");
                        bw.write("\t\t</if>\n");
                    } catch (IOException e) {
                        log.error("写入if失败！", e);
                    }
                }
            });
        }
        bw.write("</sql>\n\n");
    }

    /**
     * 定义一个查询集合
     * @param tableInfo
     * @param bw
     */
    private static void writerSelectList(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
       bw.write("\t<!-- 查询集合 -->\n");
       bw.write("\t<select id=\"selectList\" resultMap=\"" + RESULT_MAP_ID + "\">\n");
       bw.write("\t\tselect\n");
       bw.write("\t\t<include refid=\""+BASE_COLUMN_LIST+"\" />\n");
       bw.write("\t\tfrom " + tableInfo.getTableName() + "\n");
       bw.write("\t\t<include refid=\""+QUERY_CONDITION+"\" />\n");
       bw.write("\t<!-- 分页查询集合 -->\n");
       bw.write("\t\t<if test=\"query.orderBy != null\">\n");
       bw.write("\t\t\t order by ${query.orderBy}\n");
       bw.write("\t\t</if>\n");
       bw.write("\t\t<if test=\"query.simplePage != null\">\n");
       bw.write("\t\t\tlimit ${query.simplePage.start}, ${query.simplePage.end}\n");
       bw.write("\t\t</if>\n");
       //如果有逻辑删除字段，则加上逻辑删除条件
       if(FiledInfoConst.IS_LOGIC_DELETE)
       {
           bw.write("\t\tand " + FiledInfoConst.LOGIC_DELETE_FIELD_DB   + " = 1\n");
       }
       bw.write("\t</select>\n\n");
    }

    /**
     * 查询集合总数
     * @param bw
     * @throws IOException
     */
    public static void selectCount(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("<select id= \"selectCount\" resultType=\"java.lang.Integer\">\n");
        bw.write("\t\tselect count(1)\n");
        bw.write("\t\tfrom " + tableInfo.getTableName() + "\n");
        bw.write("\t\t<include refid=\""+QUERY_CONDITION+"\" />\n");
        //如果有逻辑删除字段，则加上逻辑删除条件
        if(FiledInfoConst.IS_LOGIC_DELETE)
        {
            bw.write("\t\tand " + FiledInfoConst.LOGIC_DELETE_FIELD_DB   + " = 1\n");
        }
        bw.write("</select>\n\n");
    }

    /**
     * 插入方法
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void insert(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("<!-- 插入 -->\n");
        bw.write("<insert id=\"insert\" parameterType=\""+PathAndPackageConst.PACKAGE_POJO+"." + tableInfo.getBeanName() + "\">\n");
        //遍历字段
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        //定义一个变量用来记录主键的主键
        AtomicReference<FieldInfo> primaryKey = new AtomicReference<>();
        fieldList.forEach(fieldInfo -> {
         if(fieldInfo.isAutoIncrement())
         {
             primaryKey.set(fieldInfo);
         }
        });
        if(primaryKey.get() != null)
        {
            bw.write("\t\t<selectKey keyProperty=\""+primaryKey.get().getFiledName()+"\" order=\"AFTER\" resultType=\"" + primaryKey.get().getJavaType() +"\">\n");
            bw.write("\t\t\tSELECT LAST_INSERT_ID()\n");
            bw.write("\t\t</selectKey>\n\n");
        }
        //进行插入操作
        bw.write("\t\tinsert into "+tableInfo.getTableName()+"\n");
        //去掉最后一个逗号
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        fieldList.forEach(fieldInfo -> {
            final  String bean = "bean.";
            if(!fieldInfo.isAutoIncrement()) {
                try {
                    bw.write("\t\t\t<if test=\"" + bean+ fieldInfo.getPropertyName() + " != null\">\n");
                    bw.write("\t\t\t\t"  + fieldInfo.getFiledName() + ",\n");
                    bw.write("\t\t\t</if>\n");
                } catch (IOException e) {
                    log.error("写入if失败！", e);
                }
            }
        });
        bw.write("\t\t</trim>\n");
        //插入值
        bw.write("\t\tvalues\n");
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        fieldList.forEach(fieldInfo -> {
            final  String bean = "bean.";
            if(!fieldInfo.isAutoIncrement()) {
                try {
                    bw.write("\t\t\t<if test=\"" + bean + fieldInfo.getPropertyName() + " != null\">\n");
                    bw.write("\t\t\t\t#{" + bean + fieldInfo.getPropertyName() + "},\n");
                    bw.write("\t\t\t</if>\n");
                } catch (IOException e) {
                    log.error("写入if失败！", e);
                }
            }
        });
        bw.write("\t\t</trim>\n");
        bw.write("</insert>\n\n");

    }

    /**
     *插入或者更新
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void insertOrUpdate(@NotNull TableInfo tableInfo, BufferedWriter bw) throws IOException {
        //对唯一索引不能更新
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        //定义一个集合用来存放唯一索引
        Map<String, String> uniqueIndexMap = new HashMap<>();
        for (var entry : keyIndexMap.entrySet()) {
            //获取所有的值
            List<FieldInfo> value = entry.getValue();
            value.forEach(fieldInfo -> {
                //将唯一索引放入集合
                uniqueIndexMap.put(fieldInfo.getFiledName(), fieldInfo.getFiledName());
            });
        }
        bw.write("<!-- 插入或者更新 -->\n");
        bw.write("<insert id=\"insertOrUpdate\" parameterType=\""+PathAndPackageConst.PACKAGE_POJO+"." + tableInfo.getBeanName() + "\">\n");
        //遍历字段
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        //定义一个变量用来记录主键的主键
        AtomicReference<FieldInfo> primaryKey = new AtomicReference<>();
        fieldList.forEach(fieldInfo -> {
            //判断是否是主键
            if(fieldInfo.isAutoIncrement())
            {
                primaryKey.set(fieldInfo);
            }
        });
        if(primaryKey.get() != null)
        {
            bw.write("\t\t<selectKey keyProperty=\""+primaryKey.get().getFiledName()+"\" order=\"AFTER\" resultType=\"" + primaryKey.get().getJavaType() +"\">\n");
            bw.write("\t\t\tSELECT LAST_INSERT_ID()\n");
            bw.write("\t\t</selectKey>\n\n");
        }
        //进行插入操作
        bw.write("\t\tinsert into "+tableInfo.getTableName()+"\n");
        //去掉最后一个逗号
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
       for (var fieldInfo : fieldList)
       {
           //判断是否是唯一索引
           if(uniqueIndexMap.get(fieldInfo.getFiledName()) != null)
           {
               //不是唯一索引，则进行更新操作
               continue;
           }
           final  String bean = "bean.";
           if(!fieldInfo.isAutoIncrement()) {
               try {
                   bw.write("\t\t\t<if test=\"" + bean+ fieldInfo.getPropertyName() + " != null\">\n");
                   bw.write("\t\t\t\t"  + fieldInfo.getFiledName() + ",\n");
                   bw.write("\t\t\t</if>\n");
               } catch (IOException e) {
                   log.error("写入if失败！", e);
               }
           }
       }
        bw.write("\t\t</trim>\n");
        //插入值
        bw.write("\t\tvalues\n");
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
       for (var fieldInfo : fieldList)
       {
           //判断是否是唯一索引
           if(uniqueIndexMap.get(fieldInfo.getFiledName()) != null)
           {
               //不是唯一索引，则进行更新操作
               continue;
           }
           final  String bean = "bean.";
           if(!fieldInfo.isAutoIncrement()) {
               try {
                   bw.write("\t\t\t<if test=\"" + bean + fieldInfo.getPropertyName() + " != null\">\n");
                   bw.write("\t\t\t\t#{" + bean + fieldInfo.getPropertyName() + "},\n");
                   bw.write("\t\t\t</if>\n");
               } catch (IOException e) {
                   log.error("写入if失败！", e);
               }
           }
       }
        bw.write("\t\t</trim>\n");
        bw.write("\t\tON DUPLICATE KEY UPDATE\n");
        bw.write("\t\t<trim prefix=\"\" suffix=\"\" suffixOverrides=\",\">\n");
        for(var fieldInfo : fieldList){
            //判断是否是唯一索引
            if(uniqueIndexMap.get(fieldInfo.getFiledName()) != null)
            {
                //不是唯一索引，则进行更新操作
                continue;
            }
            final  String bean = "bean.";
            if(!fieldInfo.isAutoIncrement()) {
                try {
                    bw.write("\t\t\t<if test=\"" + bean + fieldInfo.getPropertyName() + " != null\">\n");
                    bw.write("\t\t\t\t" + fieldInfo.getFiledName() + " = values(" + fieldInfo.getFiledName() + "),\n");
                    bw.write("\t\t\t</if>\n");
                } catch (IOException e) {
                    log.error("写入if失败！", e);
                }
            }
        }
        bw.write("\t\t</trim>\n");
        bw.write("</insert>\n\n");
    }

    /**
     * 批量插入
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void insertBatch(@NotNull TableInfo tableInfo, @NotNull BufferedWriter bw) throws IOException {
        bw.write("<!-- 批量插入 -->\n");
        //自动生成主键Insert
        bw.write("<insert id=\"insertBatch\" parameterType=\""+PathAndPackageConst.PACKAGE_POJO+"." + tableInfo.getBeanName() + "\" useGeneratedKeys= \"true\" keyProperty=\""+tableInfo.getFieldList().get(0).getFiledName()+"\">\n");
        bw.write("\t\tinsert into " + tableInfo.getTableName() + " (\n");
        //遍历字段
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        for (var fieldInfo : fieldList) {
            //判断是否是主键
            if(fieldInfo.isAutoIncrement())
            {
                continue;
            }
            try {
                //去掉最后一个逗号
                if (fieldList.indexOf(fieldInfo) != fieldList.size() - 1) {
                    bw.write("\t\t\t" + fieldInfo.getFiledName() + ",\n");
                }
                else
                {
                    bw.write("\t\t\t" + fieldInfo.getFiledName());
                    bw.newLine();
                }

            } catch (IOException e) {
                log.error("写入字段失败！", e);
            }
        }
        bw.write("\t\t) \n\t\tvalues \n");
        bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">\n");
        bw.write("\t\t\t(\n");
        //遍历字段
       for (var fieldInfo : fieldList) {
           //判断是否是主键
           if(fieldInfo.isAutoIncrement())
           {
               continue;
           }
           try {
               //去掉最后一个逗号
               if (fieldList.indexOf(fieldInfo) != fieldList.size() - 1) {
                   bw.write("\t\t\t\t#{item." + fieldInfo.getPropertyName() + "},\n");
               }
               else
               {
                   bw.write("\t\t\t\t#{item." + fieldInfo.getPropertyName() + "}\n");
               }
           } catch (IOException e) {
               log.error("写入字段失败！", e);
           }
       }
        bw.write("\t\t\t)\n");
        bw.write("\t\t</foreach>\n");
        bw.write("</insert>\n\n");


    }

    /**
     * 批量插入或者更新
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static  void insertOrUpdateBatch(@NotNull TableInfo tableInfo, BufferedWriter bw) throws IOException {
        //对唯一索引不能更新
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        //定义一个集合用来存放唯一索引
        Map<String, String> uniqueIndexMap = new HashMap<>();
        for (var entry : keyIndexMap.entrySet()) {
            //获取所有的值
            List<FieldInfo> value = entry.getValue();
            value.forEach(fieldInfo -> {
                //将唯一索引放入集合
                uniqueIndexMap.put(fieldInfo.getFiledName(), fieldInfo.getFiledName());
            });
        }
        bw.write("<!-- 批量插入或者更新 -->\n");
        //自动生成主键Insert
        bw.write("<insert id=\"insertOrUpdateBatch\" parameterType=\""+PathAndPackageConst.PACKAGE_POJO+"." + tableInfo.getBeanName() + "\" useGeneratedKeys= \"true\" keyProperty=\""+tableInfo.getFieldList().get(0).getFiledName()+"\">\n");
        bw.write("\t\tinsert into " + tableInfo.getTableName() + " (\n");
        //遍历字段
        List<FieldInfo> fieldList = tableInfo.getFieldList();
        for (var fieldInfo : fieldList) {
            //判断是否是主键
            if(fieldInfo.isAutoIncrement())
            {
                continue;
            }
            try {
                //去掉最后一个逗号
                if (fieldList.indexOf(fieldInfo) != fieldList.size() - 1) {
                    bw.write("\t\t\t" + fieldInfo.getFiledName() + ",\n");
                }
                else
                {
                    bw.write("\t\t\t" + fieldInfo.getFiledName());
                    bw.newLine();
                }

            } catch (IOException e) {
                log.error("写入字段失败！", e);
            }
        }
        bw.write("\t\t) \n\t\tvalues \n");
        bw.write("\t\t<foreach collection=\"list\" item=\"item\" separator=\",\">\n");
        bw.write("\t\t\t(\n");
        //遍历字段
        for (var fieldInfo : fieldList) {
            //判断是否是主键
            if(fieldInfo.isAutoIncrement())
            {
                continue;
            }
            try {
                //去掉最后一个逗号
                if (fieldList.indexOf(fieldInfo) != fieldList.size() - 1) {
                    bw.write("\t\t\t\t#{item." + fieldInfo.getPropertyName() + "},\n");
                }
                else
                {
                    bw.write("\t\t\t\t#{item." + fieldInfo.getPropertyName() + "}\n");
                }
            } catch (IOException e) {
                log.error("写入字段失败！", e);
            }
        }
        bw.write("\t\t\t)\n");
        bw.write("\t\t</foreach>\n");
        bw.write("\t\tON DUPLICATE KEY UPDATE\n");
        //定义一个原子变量->用来记录索引的位置
        AtomicInteger indexAtomic = new AtomicInteger(0);
        for(var fieldInfo : fieldList){
            //判断是否是唯一索引
            if(uniqueIndexMap.get(fieldInfo.getFiledName()) != null)
            {
                indexAtomic.getAndIncrement();
                //不是唯一索引，则进行更新操作
                continue;
            }
            if(!fieldInfo.isAutoIncrement()) {
                try {
                    bw.write("\t\t\t\t" + fieldInfo.getFiledName() + " = values(" + fieldInfo.getFiledName() + ")");
                    if(indexAtomic.get() < fieldList.size() - 1)
                    {
                        //插入逗号
                        bw.write(",\n");
                        //对原子变量进行自增
                        indexAtomic.getAndIncrement();
                    }
                } catch (IOException e) {
                    log.error("写入if失败！", e);
                }
            }
        }
        bw.newLine();
        bw.write("</insert>\n\n");
    }

    /**
     * 根据索引查询
     * @param tableInfo
     * @param bw
     * @throws IOException
     */
    public static void SelectOrDelete(@NotNull TableInfo tableInfo, BufferedWriter bw) throws IOException {
        //遍历所有的索引
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        for(var key : keyIndexMap.entrySet())
        {
            //定义一个StringBuilder对象
            StringBuilder sb = new StringBuilder();
            //获取索引名称
            List<FieldInfo> value = key.getValue();
            //定义一个index->用来记录索引的位置
            Integer index = 0;
            for(var fieldInfo : value)
            {
                index++;
                sb.append(fieldInfo.getPropertyName());
                if(index <value.size())
                {
                    sb.append("And");
                }
            }
            //根据主键查询
            bw.write("\t<!-- 根据" + sb + "查询 -->" + "\n");
            bw.write("\t<select id=\"selectBy"  + StringUtils.firstCharToUpperCase(sb.toString()) + "\" resultMap=\"" + RESULT_MAP_ID + "\">" + "\n");
            bw.write("\t\tselect\n");
            bw.write("\t\t\t<include refid=\"" + BASE_COLUMN_LIST + "\"/>\n");
            bw.write("\t\tfrom " + tableInfo.getTableName() + "\n");
            bw.write("\t\twhere\n");
            //定义一个index->用来记录索引的位置
            int i = 1;
            //遍历索引
            for(var fieldInfo : value)
            {
                bw.write("\t\t\t" + fieldInfo.getFiledName() + " = #{" + fieldInfo.getPropertyName() + "}");
                if(i < value.size())
                {
                    bw.write(" and\n");
                }
                i++;
            }
            bw.newLine();
            bw.write("\t</select>" + "\n");

            //根据主键删除
            bw.write("\t<!-- 根据" + sb + "删除 -->" + "\n");
            bw.write("\t<delete id=\"deleteBy"  + StringUtils.firstCharToUpperCase(sb.toString()) + "\">" + "\n");
            bw.write("\t\tdelete \n\t\tfrom " + tableInfo.getTableName() + "\n");
            bw.write("\t\twhere\n");
            //定义一个index->用来记录索引的位置
            int j = 1;
            //遍历索引
            for(var fieldInfo : value)
            {
                bw.write("\t\t\t" + fieldInfo.getFiledName() + " = #{" + fieldInfo.getPropertyName() + "}");
                if(j < value.size())
                {
                    bw.write(" and\n");
                }
                j++;
            }
            bw.newLine();
            bw.write("\t</delete>" + "\n");
            //根据主键修改
            bw.write("\t<!-- 根据" + sb + "修改 -->" + "\n");
            bw.write("\t<update id=\"UpdateBy"  + StringUtils.firstCharToUpperCase(sb.toString()) + "\" parameterType=\""+PathAndPackageConst.PACKAGE_POJO+"." + tableInfo.getBeanName() + "\">" + "\n");
            bw.write("\t\tupdate  " + tableInfo.getTableName() + "\n");
            bw.write("\t\t<set>\n");
            //遍历字段
            List<FieldInfo> fieldList = tableInfo.getFieldList();
            final  String bean = "bean.";
            fieldList.forEach(fieldInfo -> {
                if(!fieldInfo.isAutoIncrement())
                {
                    try {
                        bw.write("\t\t\t<if test=\"" + bean + fieldInfo.getPropertyName() + " != null\">\n");
                        bw.write("\t\t\t" + fieldInfo.getFiledName() + " = #{" +  bean +fieldInfo.getPropertyName() + "},\n");
                        bw.write("\t\t\t</if>\n");
                    } catch (IOException e) {
                        log.error("写入字段失败！", e);
                    }
                }
            });
          bw.write("\t\t</set>\n");
          bw.write("\t\twhere\n");
          //定义一个原子变量->用来记录索引的位置
          AtomicInteger indexAtomic = new AtomicInteger(0);
          //遍历索引
          value.forEach(fieldInfo -> {
              try {
                  bw.write("\t\t" + fieldInfo.getFiledName() + " = #{" + fieldInfo.getPropertyName() + "}");
              } catch (IOException e) {
                  log.error("写入字段失败！", e);
              }
              //对原子变量进行自增
              if(indexAtomic.get() < value.size() - 1)
              {
                  //对原子变量进行自增
                  indexAtomic.getAndIncrement();
                  try {
                      bw.write("\tand ");
                  } catch (IOException e) {
                      log.error("写入字段失败！", e);
                  }
              }
          });
          bw.write("\n");
          bw.write("\t</update>\n");
        }
    }
}


