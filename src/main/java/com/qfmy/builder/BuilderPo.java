package com.qfmy.builder;

import com.qfmy.bean.TableInfo;
import com.qfmy.consts.*;
import com.qfmy.utils.CommentUtils;
import com.qfmy.utils.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Arrays;

/**
 * @author: 廖志伟
 * @date: 2024-11-12
 * 创建pojo对象
 */
@SuppressWarnings("all")
public class BuilderPo {
    //定义一个log日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderPo.class);
    //定义一个变量控制判断父类生成
    private static boolean isHaveBase = false;

    /**
     * 创建pojo对象 -> 构建表创建对象
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_PO);
        //判断文件夹是否存在，不存在则创建
       if (!folder.exists()) {
           folder.mkdirs();
       }
        //创建pojo文件对象
        File file = new File(folder, tableInfo.getBeanName() + "." + PathAndPackageConst.JAVA_NAME);
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        try {
            os = new FileOutputStream(file);
            ow = new OutputStreamWriter(os, "UTF-8");
            bw = new BufferedWriter(ow);
            //判断是否写入父类
           if(tableInfo.isHaveDateTime() || tableInfo.isHaveBigDecimal()|| tableInfo.isHaveData() && !isHaveBase)
           {
               //写入父类
               writerParentFile(tableInfo,TestConst.IS_TEST);
               //写入子类
               writerChildFile(tableInfo, bw, TestConst.IS_TEST,true);
               //设置父类生成标志
               isHaveBase = true;
           }
           else
           {
               //没有父类
               writerChildFile(tableInfo, bw, TestConst.IS_TEST,false);
           }

        } catch (Exception e) {
            log.error("创建pojo文件失败！", e);
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
    private static void writerChildFile(TableInfo tableInfo, BufferedWriter bw,boolean isUintTest,boolean isHaveBase) throws Exception {

        //判断是否有测试类
        if(isUintTest)
       {
           //采用swagger测试框架
          if(TestConst.SWAGGER_TEST != null)
          {
              //编写子类
             bw.write("package " + PathAndPackageConst.PACKAGE_POJO + ";" + "\n");
             bw.write("import lombok.Data;" + "\n");
             bw.write("import lombok.AllArgsConstructor;" + "\n");
             bw.write("import lombok.NoArgsConstructor;" + "\n");
             //判断是否需要忽略注解包
             if(TableConstInfo.IGNORE_ANNOTATION_PACKAGE !=null)
             {
                 bw.write(TableConstInfo.IGNORE_ANNOTATION_PACKAGE + "\n");
             }
             bw.write("import io.swagger.v3.oas.annotations.media.Schema;" + "\n\n");
             //生成注释
              CommentUtils.CreateClassComment(bw,tableInfo.getComment());
             bw.write("@Data" + "\n");
             bw.write("@AllArgsConstructor" + "\n");
             bw.write("@NoArgsConstructor" + "\n");
             bw.write("@SuppressWarnings(\"all\")");
             bw.write("@Schema(description = \""+ tableInfo.getComment() +"\")\n");
             //判断是否有父类
            if(isHaveBase)
            {
                bw.write("public class " + tableInfo.getBeanName() + " extends Base"+ tableInfo.getBeanName() +"{" + "\n");
            }
            else
            {
                bw.write("public class " + tableInfo.getBeanName() + "{" + "\n");
            }
             bw.write("" + "\n");
             CommentUtils.CreateFieldComment(bw,"生成序列化id");
             bw.write("\t\tprivate static final long serialVersionUID = 1L;" + "\n");
             //遍历字段
              tableInfo.getFieldList().forEach(filed -> {
                  //判断是否是主日期字段
                  if(!filed.getJavaType().equals("Date"))
                  {
                      try {
                          //生成注释
                          CommentUtils.CreateFieldComment(bw,filed.getComment());
                          bw.write("\t\t@Schema(description = " + "\"" + filed.getComment() + "\")" + "\n");
                          //遍历忽视字段的列表
                          Arrays.stream(TableConstInfo.IGNORE_FIELDS).forEach(fileds ->{
                              if(filed.getPropertyName().equals(fileds))
                              {
                                  try {
                                      bw.write("\t\t"+ TableConstInfo.IGNORE_ANNOTATION + "\n");
                                  } catch (IOException e) {
                                      log.error("忽视注解写入失败！", e);
                                  }
                              }
                          });
                          bw.write("\t\tprivate " + filed.getJavaType() + " " + filed.getPropertyName() + ";" + "\n");
                      } catch (Exception e) {
                         log.error("字段写入失败！", e);
                      }
                  }
              });
             bw.write("}" + "\n");
             //刷新缓冲区
             bw.flush();
          }
       }
       else
        {
            //编写子类
            bw.write("package " + PathAndPackageConst.PACKAGE_POJO + ";" + "\n");
            bw.write("import lombok.Data;" + "\n");
            bw.write("import lombok.AllArgsConstructor;" + "\n");
            bw.write("import lombok.NoArgsConstructor;" + "\n");
            bw.write(TableConstInfo.IGNORE_ANNOTATION_PACKAGE + "\n");
            bw.write("import java.math.BigDecimal;" + "\n\n");
            //生成注释
            CommentUtils.CreateClassComment(bw,tableInfo.getComment());
            bw.write("@Data" + "\n");
            bw.write("@AllArgsConstructor" + "\n");
            bw.write("@NoArgsConstructor" + "\n");
            bw.write("@SuppressWarnings(\"all\")\n");
            //判断是否有父类
            if(isHaveBase)
            {
                bw.write("public class " + tableInfo.getBeanName() + " extends Base"+ tableInfo.getBeanName() +"{" + "\n");
            }
            else
            {
                bw.write("public class " + tableInfo.getBeanName() + "{" + "\n");
            }
            bw.write("" + "\n");
            CommentUtils.CreateFieldComment(bw,"生成序列化id");
            bw.write("\t\tprivate static final long serialVersionUID = 1L;" + "\n");
            //遍历字段
            tableInfo.getFieldList().forEach(filed -> {
                //判断是否是主日期字段
                if(!filed.getJavaType().equals("Date"))
                {
                    try {
                        //生成注释
                        CommentUtils.CreateFieldComment(bw,filed.getComment());
                        //遍历忽视字段的列表
                        Arrays.stream(TableConstInfo.IGNORE_FIELDS).forEach(fileds ->{
                            if(filed.getPropertyName().equals(fileds))
                            {
                                try {
                                    bw.write("\t\t"+ TableConstInfo.IGNORE_ANNOTATION + "\n");
                                } catch (IOException e) {
                                    log.error("忽视注解写入失败！", e);
                                }
                            }
                        });
                        //不对前端显示的字段
                        Arrays.stream(TableConstInfo.NOT_SHOW_FIELDS).forEach(notShowFields ->{
                            if(filed.getPropertyName().equals(notShowFields))
                            {
                                try {
                                    bw.write("\t\t@TableField(value = " + filed.getPropertyName() + ",select = false\n");
                                } catch (IOException e) {
                                    log.error("忽视注解写入失败！", e);
                                }
                            }
                        });
                        bw.write("\t\tprivate " + filed.getJavaType() + " " + filed.getPropertyName() + ";" + "\n");
                    } catch (Exception e) {
                        log.error("字段写入失败！", e);
                    }
                }
            });
            bw.write("}" + "\n");
            //刷新缓冲区
            bw.flush();
        }
    }

    /**
     * 创建父类文件
     * @param tableInfo
     */
    public static void writerParentFile(TableInfo tableInfo,boolean isUintTest)
    {
        //创建一个父文件
        File file = new File(PathAndPackageConst.PATH_PO + "/base");
        //判断文件夹是否存在，不存在则创建
        if(!file.exists())
        {
            file.mkdirs();
        }
        //创建一个父文件对象
       File parentFile = new File(file, "Base" + tableInfo.getBeanName() + "." + PathAndPackageConst.JAVA_NAME);

        //创建输出流
         OutputStream os = null;
         OutputStreamWriter ow = null;
         BufferedWriter bw = null;
        try {
            os = new FileOutputStream(parentFile);
            ow = new OutputStreamWriter(os, "UTF-8");
            bw = new BufferedWriter(ow);
            //写入父类
            bw.write("package " + PathAndPackageConst.PACKAGE_POJO + ";" + "\n");
            bw.write("import lombok.Data;" + "\n");
            bw.write("import java.io.Serializable;" + "\n");
            bw.write("import java.util.Date;" + "\n");
            bw.write("import lombok.AllArgsConstructor;" + "\n");
            bw.write("import com.baomidou.mybatisplus.annotation.*;\n");
            bw.write(TableConstInfo.IGNORE_ANNOTATION_PACKAGE + "\n\n");
            if(isUintTest && TestConst.SWAGGER_TEST != null)
            {
                bw.write("import io.swagger.v3.oas.annotations.media.Schema;" + "\n");
                bw.write("import nonapi.io.github.classgraph.json.Id;\n");
            }
            //判断是否用的是jackson的日期格式化
            if(TableConstInfo.DATE_FORMAT_PACKAGE  != null)
            {
                //序列化包
                bw.write("import com.fasterxml.jackson.annotation.JsonFormat;\n");
                //反序列化包
                bw.write("import org.springframework.format.annotation.DateTimeFormat;\n");
            }
            bw.write("import lombok.NoArgsConstructor;" + "\n\n");
            //生成注释
            CommentUtils.CreateClassComment(bw,tableInfo.getComment() +"的父类");
            bw.write("@Data" + "\n");
            bw.write("@SuppressWarnings(\"all\")" + "\n");
            bw.write("@AllArgsConstructor" + "\n");
            bw.write("@NoArgsConstructor" + "\n");
            bw.write("public class Base" + tableInfo.getBeanName() + " implements Serializable {" + "\n");
            bw.newLine();
            //判断是否有id
            boolean id = tableInfo.getFieldList().get(0).getFiledName().equals("id");
            //循环遍历字段
            if(id)
            {
                //生成注释
                CommentUtils.CreateFieldComment(bw,tableInfo.getFieldList().get(0).getComment());
               if(isUintTest && TestConst.SWAGGER_TEST != null)
               {
                   bw.write("\t\t@Id" + "\n");
                   bw.write("\t\t@Schema(description = "+" \"" + tableInfo.getFieldList().get(0).getComment() + "\"" + ")\n");
               }
                bw.write("\t\tpublic Integer id;" + "\n");
            }
            //判断是否有Data类型字段
            if(tableInfo.isHaveData() || tableInfo.isHaveDateTime())
            {
                //循环遍历Data类型字段
                for(var filed : tableInfo.getFieldList())
                {
                    //判断是否Data类型
                    if(filed.getJavaType().equals("Date"))
                    {
                        //生成注释
                        CommentUtils.CreateFieldComment(bw,filed.getComment());
                       if(isUintTest && TestConst.SWAGGER_TEST != null)
                       {
                           bw.write("\t\t@Schema(description = "+" \"" + filed.getComment() + "\"" + ")\n");
                       }
                       //判断是否是日期时间类型
                       if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TIME_TYPES,filed.getSqlType()))
                       {
                           //序列化日期格式化
                           bw.write("\t\t" + String.format(TableConstInfo.DATE_FORMAT, DateUtils.YYYY_MM_DD_HH_MM_SS_PATTERN) + "\n");
                            //反序列化日期格式化
                           bw.write("\t\t" + String.format(TableConstInfo.DATE_DESERIALIZE , DateUtils.YYYY_MM_DD_HH_MM_SS_PATTERN) + "\n");
                       }
                       //判断是否是日期类型
                        if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TYPES,filed.getSqlType()))
                        {
                            //序列化日期格式化
                            bw.write("\t\t" + String.format(TableConstInfo.DATE_FORMAT, DateUtils.YYYY_MM_DD_PATTERN) + "\n");
                            //反序列化日期格式化
                            bw.write("\t\t" + String.format(TableConstInfo.DATE_DESERIALIZE , DateUtils.YYYY_MM_DD_PATTERN) + "\n");
                        }
                        //遍历忽视字段的列表
                        for(var ignoreFields : TableConstInfo.IGNORE_FIELDS)
                        {
                            if(filed.getPropertyName().equals(ignoreFields))
                            {
                                bw.write("\t\t"+ TableConstInfo.IGNORE_ANNOTATION + "\n");
                            }
                        }
                        //自动填充时间
                        bw.write("\t\t@TableField(value = \""+filed.getFiledName()+"\" ,fill = FieldFill.INSERT)\n");
                        bw.write("\t\tDate " + filed.getPropertyName() + ";" + "\n");
                    }
                }
            }
            //判断是否采用逻辑删除
           if(FiledInfoConst.IS_LOGIC_DELETE)
           {
               //定义一个逻辑删除字段
               CommentUtils.CreateFieldComment(bw, "逻辑删除,0:未删除,1:已删除");
               bw.write("\t\t" + TableConstInfo.IGNORE_ANNOTATION + "\n");
               bw.write("\t\tpublic Byte " + FiledInfoConst.LOGIC_DELETE_FIELD +";" + "\n");
           }
            bw.write("}" + "\n");
            //刷新缓冲区
            bw.flush();
        } catch (Exception e) {
            log.error("创建父文件失败！", e);
        }
        finally {
        }
    }

}
