package com.qfmy.builder;

import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
import com.qfmy.consts.CommonPropertiesConst;
import com.qfmy.consts.PathAndPackageConst;
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
 * @date: 2024-11-15
 * 创建COntroller
 */
@SuppressWarnings("all")
public class BuilderController {
    //定义一个log日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderController.class);

    /**
     * 创建pojo对象 -> 构建表创建对象
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_CONTROLLER);
        //判断文件夹是否存在，不存在则创建
        if(!folder.exists())
        {
            //创建文件夹
            folder.mkdirs();
        }
        //创建query文件对象
        File file = new File(folder, tableInfo.getBeanName() + "Controller." + PathAndPackageConst.JAVA_NAME);
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
            log.error("创建controller文件失败！", e);
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
        bw.write("package " + PathAndPackageConst.PACKAGE_CONTROLLER + ";" + "\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_POJO + "." + tableInfo.getBeanName() + ";\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PARAM + "." +tableInfo.getBeanParamName() + ";\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_SERVICE + "." + tableInfo.getBeanName() + "Service;\n");
        bw.write("import jakarta.annotation.Resource;\n");
        bw.write("import org.springframework.web.bind.annotation.RestController;\n");
        bw.write("import org.springframework.web.bind.annotation.GetMapping;\n");
        bw.write("import org.springframework.web.bind.annotation.RequestBody;\n");
        bw.write("import org.springframework.web.bind.annotation.RequestMapping;\n");
        bw.write("import org.springframework.web.bind.annotation.PostMapping;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PAGE + ".PaginationResultVo;\n");
        bw.write("import org.springframework.web.bind.annotation.*;\n");
        bw.write("import " + CommonPropertiesConst.RESULT_PACKAGE + ".Result;\n");
        bw.write("import java.util.List;\n\n");
        CommentUtils.CreateClassComment(bw, tableInfo.getComment() + "controller");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("@RestController\n");
        bw.write("@RequestMapping(\""+ tableInfo.getBeanName() + "/\")\n");
        bw.write("public class " + tableInfo.getBeanName() + "Controller  {" + "\n");
        //定义一个mapper的名称
        String mapperName = tableInfo.getBeanName().toLowerCase() + "Service";
        //注入mapper
        bw.write("\t\t/** 注入" + tableInfo.getComment() + "service */\n");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate " + tableInfo.getBeanName() + "Service " + mapperName + ";\n\n");
        bw.write("\t\t@GetMapping(\"loadDataList\")\n");
        bw.write("\t\tpublic Result loadDataList(@RequestParam " + tableInfo.getBeanParamName() + " param){\n");
        bw.write("\t\t\tPaginationResultVo<"+tableInfo.getBeanName()+">page = this." + mapperName + ".findListByPage(param);\n");
        bw.write("\t\t\treturn Result.ok(page);\n");
        bw.write("\t}\n");
        CommentUtils.CreateMethodComment("新增", bw);
        bw.write("\t\t@PostMapping(\"add\")\n");
        bw.write("\t\tpublic Result add(@RequestBody " + tableInfo.getBeanName() + " param){" + "\n");
        bw.write("\t\t\tthis." + mapperName + ".add(param);" + "\n");
        bw.write("\t\t\treturn Result.ok();\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("批量新增", bw);
        bw.write("\t\t@PostMapping(\"addBatch\")\n");
        bw.write("\t\tpublic Result addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> params){" + "\n");
        bw.write("\t\t\tthis." + mapperName + ".addBatch(params);" + "\n");
        bw.write("\t\t\treturn Result.ok();\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("批量新增或者修改", bw);
        bw.write("\t\t@PostMapping(\"addOrUpdateBatch\")\n");
        bw.write("\t\tpublic Result addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> params){" + "\n");
        bw.write("\t\t\tthis." + mapperName + ".addOrUpdateBatch(params);" + "\n");
        bw.write("\t\t\treturn Result.ok();\n");
        bw.write("\t\t}" + "\n");
        //遍历所有的索引->生成Mapper方法
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        for(var key : keyIndexMap.entrySet())
        {
            //定义一个StringBuilder对象
            StringBuilder sb = new StringBuilder();
            //定义一个StringBuilder对象->用来拼接Mapper方法参数
            StringBuilder sbParam = new StringBuilder();
            //定义一个StringBuilder对象->用来拼接Mapper方法参数
            StringBuilder param = new StringBuilder();
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
                sbParam.append("@RequestParam ");
                sbParam.append(fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName());
                if(index <value.size())
                {
                    sbParam.append(", ");
                }
                param.append(fieldInfo.getPropertyName());
                if(index <value.size())
                {
                    param.append(",");
                }
            }
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "查询数据",bw);
            bw.write("\t\t@GetMapping(\"get" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "\")" + "\n");
            bw.write("\t\tpublic Result<" + tableInfo.getBeanName() + "> get" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString()  +"){" + "\n");
            bw.write("\t\t\t"+tableInfo.getBeanName() +"  " + tableInfo.getBeanName().toLowerCase() + " = this." + mapperName +".get"+tableInfo.getBeanName()+"By" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" +param.toString() + ");" + "\n");
            bw.write("\t\t\treturn Result.ok(" + tableInfo.getBeanName().toLowerCase() + ");" + "\n");
            bw.write("\t\t}" + "\n");
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "删除数据",bw);
            bw.write("\t\t@DeleteMapping(\"delete" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "\")" + "\n");
            bw.write("\t\tpublic Result delete" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString() + "){" + "\n");
            bw.write("\t\t\tthis." + mapperName +".delete"+tableInfo.getBeanName()+"By" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" +param.toString() + ");" + "\n");
            bw.write("\t\t\treturn Result.ok();" + "\n");
            bw.write("\t\t}" + "\n");
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "更新数据",bw);
            bw.write("\t\t@PostMapping(\"update" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "\")" + "\n");
            bw.write("\t\tpublic Result Update"+ tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" + tableInfo.getBeanName()  + " param, " + sbParam.toString() + "){" + "\n");
            bw.write("\t\t\tthis." + mapperName +".Update"+tableInfo.getBeanName()+"By" + StringUtils.firstCharToUpperCase(sb.toString()) + "(param" + "," +param.toString() + ");" + "\n");
            bw.write("\t\t\treturn Result.ok();" + "\n");
            bw.write("\t\t}" + "\n");
        }
        bw.write("}" + "\n");
        //刷新缓冲区
        bw.flush();
    }
}
