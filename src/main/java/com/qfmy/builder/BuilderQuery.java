package com.qfmy.builder;

import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
import com.qfmy.consts.JavaTypeConst;
import com.qfmy.consts.PathAndPackageConst;
import com.qfmy.consts.TableConstInfo;
import com.qfmy.utils.CommentUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: 廖志伟
 * @date: 2024-11-13
 * 创建pojo对象
 */
@SuppressWarnings("all")
public class BuilderQuery {
    //定义一个log日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderQuery.class);
    //创建一个扩展字段的集合
    private static final List<FieldInfo> extendFieldList = new ArrayList<>();

    /**
     * 创建query对象 -> 构建表创建对象
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_PARAM);
        //判断文件夹是否存在，不存在则创建
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        //创建query文件对象
        File file = new File(folder, tableInfo.getBeanName() + "query." + PathAndPackageConst.JAVA_NAME);
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
            log.error("创建query文件失败！", e);
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
        //编写子类
        bw.write("package " + PathAndPackageConst.PACKAGE_PARAM + ";" + "\n");
        bw.write("import lombok.Data;" + "\n");
        bw.write("import lombok.AllArgsConstructor;" + "\n");
        bw.write("import lombok.NoArgsConstructor;" + "\n");
        //判断是否有日期或者时间类型
        if(tableInfo.isHaveData() || tableInfo.isHaveDateTime())
        {
           bw.write("import java.util.Date;" + "\n");
        }
        //生成注释
        CommentUtils.CreateClassComment(bw,tableInfo.getComment() + "查询对象");
        bw.write("@Data" + "\n");
        bw.write("@AllArgsConstructor" + "\n");
        bw.write("@NoArgsConstructor" + "\n");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("public class " + tableInfo.getBeanName() + "query extends BaseQuery {" + "\n");
        bw.write("" + "\n");
        CommentUtils.CreateFieldComment(bw,"生成序列化id");
        bw.write("\t\tprivate static final long serialVersionUID = 1L;" + "\n");
        //遍历字段
        tableInfo.getFieldList().forEach(filed -> {
            try {
                //生成注释
                CommentUtils.CreateFieldComment(bw,filed.getComment());
                bw.write("\t\tprivate " + filed.getJavaType() + " " + filed.getPropertyName() + ";" + "\n");
                //对String类型增加模糊查询
                if(ArrayUtils.contains(JavaTypeConst.SQL_STRING_TYPES,filed.getSqlType()))
                {
                    String comment = filed.getComment() + "用于模糊查询";
                    //生成注释
                    CommentUtils.CreateFieldComment(bw,comment);
                    bw.write("\t\tprivate "  + "String " + filed.getPropertyName() + TableConstInfo.PARAM_FUZZY_SUFFIX + ";" + "\n");
                    //往扩展字段集合中添加模糊查询字段
                    extendFieldList.add(filed);
                }
                //对日期类型增加范围查询
                if(ArrayUtils.contains(JavaTypeConst.SQL_DATE_TIME_TYPES,filed.getSqlType()) || ArrayUtils.contains(JavaTypeConst.SQL_DATE_TYPES,filed.getSqlType()))
                {
                    String comment = filed.getComment() + "用于起始时间查询";
                    //生成注释
                    CommentUtils.CreateFieldComment(bw,comment);
                    bw.write("\t\tprivate String " + filed.getPropertyName() + TableConstInfo.PARAM_START_DATE_SUFFIX + ";" + "\n");
                    //生成注释
                    comment = filed.getComment() + "用于结束时间查询";
                    //生成注释
                    CommentUtils.CreateFieldComment(bw,comment);
                    bw.write("\t\tprivate String " + filed.getPropertyName() + TableConstInfo.PARAM_END_DATE_SUFFIX + ";" + "\n");
                    //往扩展字段集合中添加范围查询字段
                    extendFieldList.add(filed);
                }
            } catch (Exception e) {
                log.error("写入字段失败！", e);
            }
        });
        bw.write("}" + "\n");
        tableInfo.setExtendFieldList(extendFieldList);
        //刷新缓冲区
        bw.flush();
    }


}
