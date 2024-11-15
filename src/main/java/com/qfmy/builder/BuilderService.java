package com.qfmy.builder;


import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
import com.qfmy.consts.JavaTypeConst;
import com.qfmy.consts.PathAndPackageConst;
import com.qfmy.consts.TableConstInfo;
import com.qfmy.utils.CommentUtils;
import com.qfmy.utils.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: 廖志伟
 * @date: 2024-11-13
 * 创建pojo对象
 */
@SuppressWarnings("all")
public class BuilderService {

    //定义一个log日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderService.class);

    /**
     * 创建service对象
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_SERVICE);
        //判断文件夹是否存在，不存在则创建
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        //创建query文件对象
        File file = new File(folder, tableInfo.getBeanName() + "Service." + PathAndPackageConst.JAVA_NAME);
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
            log.error("创建service文件失败！", e);
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
        bw.write("package " + PathAndPackageConst.PACKAGE_SERVICE + ";" + "\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_POJO + "." + tableInfo.getBeanName() + ";\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PARAM + "." +tableInfo.getBeanParamName() + ";\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PAGE + ".PaginationResultVo;\n");
        bw.write("import java.util.List;\n\n");
        CommentUtils.CreateClassComment(bw, tableInfo.getComment() + "Service");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("public interface " + tableInfo.getBeanName() + "Service {" + "\n");
        CommentUtils.CreateMethodComment("根据条件查询列表", bw);
        bw.write("\t\tpublic List<" + tableInfo.getBeanName() + "> findByParam( " + tableInfo.getBeanParamName() + " param);" + "\n");
        CommentUtils.CreateMethodComment("根据条件查询总数", bw);
        bw.write("\t\tpublic Integer findCountByParam( " + tableInfo.getBeanParamName() + " param);" + "\n");
        CommentUtils.CreateMethodComment("根据条件分页查询", bw);
        bw.write("\t\tpublic PaginationResultVo<" + tableInfo.getBeanName() + "> findListByPage( " + tableInfo.getBeanParamName() + " param);" + "\n");
        CommentUtils.CreateMethodComment("新增", bw);
        bw.write("\t\tpublic Integer add(" + tableInfo.getBeanName() + " param);" + "\n");
        CommentUtils.CreateMethodComment("批量新增", bw);
        bw.write("\t\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> param);" + "\n");
        CommentUtils.CreateMethodComment("批量新增或者修改", bw);
        bw.write("\t\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> param);" + "\n");
        //遍历所有的索引->生成Mapper方法
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        for(var key : keyIndexMap.entrySet())
        {
            //定义一个StringBuilder对象
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
                sbParam.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                if(index <value.size())
                {
                    sbParam.append(", ");
                }
            }
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "查询数据",bw);
            bw.write("\t\tpublic " + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString()  +");" + "\n");
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "删除数据",bw);
            bw.write("\t\tpublic Integer delete" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString() + ") ;" + "\n");
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "更新数据",bw);
            bw.write("\t\tpublic Integer Update"+ tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" + tableInfo.getBeanName()  + " param, " + sbParam.toString() + ");" + "\n");
        }
        bw.write("}" + "\n");
        //刷新缓冲区
        bw.flush();
    }


}
