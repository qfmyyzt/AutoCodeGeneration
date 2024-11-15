package com.qfmy.builder;

import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
import com.qfmy.consts.PathAndPackageConst;
import com.qfmy.consts.TableConstInfo;
import com.qfmy.utils.CommentUtils;
import com.qfmy.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author: 廖志伟
 * @date: 2024-11-13
 * 创建Mapper接口
 */
@SuppressWarnings("all")
public class BuilderMapper {
    //定义一个log日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderMapper.class);

    /**
     *创建Mapper接口
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_MAPPER);
        //判断文件夹是否存在，不存在则创建
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        //创建mapper接口文件
        File file = new File(folder, tableInfo.getBeanName() + TableConstInfo.MAPPER_INTERFACE_SUFFIX+ "." + PathAndPackageConst.JAVA_NAME);
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
            log.error("创建mapper接口文件失败！", e);
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
        bw.write("package " + PathAndPackageConst.PACKAGE_MAPPER + ";" + "\n");
        bw.write("import org.apache.ibatis.annotations.Mapper;" + "\n");
        bw.write("import org.apache.ibatis.annotations.Param;" + "\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,tableInfo.getComment() + "接口");
        bw.write("@SuppressWarnings(\"all\")" + "\n");
        bw.write("@Mapper\n");
        bw.write("public interface " + tableInfo.getBeanName() + TableConstInfo.MAPPER_INTERFACE_SUFFIX + "<T,P> extends BaseMapper<T,P> {" + "\n");
        //遍历所有的索引->生成Mapper方法
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        for(var key : keyIndexMap.entrySet())
        {
            //定义一个StringBuilder对象->用来拼接Mapper方法
            StringBuilder sb = new StringBuilder();
            //定义一个StringBuilder对象->用来拼接Mapper方法参数
            StringBuilder sbParam = new StringBuilder();
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
            sbParam.append("@Param(\"" + fieldInfo.getPropertyName() + "\") " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
               if(index <value.size())
               {
                   sbParam.append(", ");
               }
           }
           //查询Mapper方法
           //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "查询数据",bw);
           //生成mapper方法
            bw.write("\t\tT selectBy" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString() +");" + "\n\n");
            //删除Mapper方法
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "删除数据",bw);
            //生成mapper方法
            bw.write("\t\tInteger deleteBy" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString() + ");" + "\n\n");
            //更新Mapper方法
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "更新数据",bw);
            //生成mapper方法
            bw.write("\t\tInteger UpdateBy" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ "@Param(\"bean\") T t, " + sbParam.toString() + ");" + "\n\n");
        }
        bw.write("}" + "\n");
        //刷新缓冲区
        bw.flush();
    }

}
