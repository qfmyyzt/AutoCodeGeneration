package com.qfmy.builder;

import com.qfmy.bean.FieldInfo;
import com.qfmy.bean.TableInfo;
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
 * @date: 2024-11-14
 * 创建pojo对象
 */
@SuppressWarnings("all")
public class BuilderServiceImpl {

    //定义一个log日志对象
    private static final Logger log = LoggerFactory.getLogger(BuilderServiceImpl.class);

    /**
     * 创建serviceImpl文件
     * @param tableInfo
     */
    public static void execute(TableInfo tableInfo) {
        //创建一个文件夹
        File folder = new File(PathAndPackageConst.PATH_SERVICE_IMPL);
        //判断文件夹是否存在，不存在则创建
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        //创建query文件对象
        File file = new File(folder, tableInfo.getBeanName() + "ServiceImpl." + PathAndPackageConst.JAVA_NAME);
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
        bw.write("package " + PathAndPackageConst.PACKAGE_SERVICE_IMPL + ";" + "\n");
        bw.write("import org.springframework.stereotype.Service;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_POJO + "." + tableInfo.getBeanName() + ";\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PARAM + "." +tableInfo.getBeanParamName() + ";\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PAGE + ".PaginationResultVo;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_SERVICE + "." + tableInfo.getBeanName() + "Service;\n");
        bw.write("import jakarta.annotation.Resource;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PAGE + ".SimplePage;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_PAGE_SIZE + ".PageSize;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_MAPPER + "." + tableInfo.getBeanName() +"Mapper;\n");
        bw.write("import java.util.List;\n\n");
        CommentUtils.CreateClassComment(bw, tableInfo.getComment() + "Service服务实现类");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("@Service" + "\n");
        bw.write("public class " + tableInfo.getBeanName() + "ServiceImpl implements " + tableInfo.getBeanName() + "Service {" + "\n");
        //定义一个mapper的名称
        String mapperName = tableInfo.getBeanName().toLowerCase() + "Mapper";
        //注入mapper
        bw.write("\t\t/** 注入" + tableInfo.getComment() + "Mapper */\n");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate " + tableInfo.getBeanName() + "Mapper<" + tableInfo.getBeanName() +","
                + tableInfo.getBeanParamName() + "> " + mapperName + ";\n");
        CommentUtils.CreateMethodComment("根据条件查询列表", bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic List<" + tableInfo.getBeanName() + "> findByParam( " + tableInfo.getBeanParamName() + " param){" + "\n");
        bw.write("\t\t\treturn " + mapperName + ".selectList(param);" + "\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("根据条件查询总数", bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic Integer findCountByParam( " + tableInfo.getBeanParamName() + " param){" + "\n");
        bw.write("\t\t\treturn " + mapperName + ".selectCount(param);" + "\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("根据条件分页查询", bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic PaginationResultVo<" + tableInfo.getBeanName() + "> findListByPage( " + tableInfo.getBeanParamName() + " param){" + "\n");
        bw.write("\t\t\t\t// 计算满足条件的物品总数\n");
        bw.write("\t\t\tint count =  this.findCountByParam(param);" + "\n");
        bw.write("\t\t\t// 确定每页显示的商品数量\n" +
                "\t\t// 如果param中的pageSize为null，则使用PageSize.SIZE15.getSize()的默认值，否则使用param中的pageSize值\n");
        bw.write("\t\t\tint pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();" + "\n");
        bw.write("\t\t\t// 创建一个SimplePage对象，用于分页查询\n");// 创建一个SimplePage对象，用于描述分页信息");
        bw.write("\t\t\tSimplePage page = new SimplePage(param.getPageNo(), count,pageSize);" + "\n");
        bw.write("\t\t\t// 设置分页参数到param中\n");
        bw.write("\t\t\tparam.setSimplePage(page);" + "\n");
        bw.write("\t\t\t// 查询分页数据\n");
        bw.write("\t\t\tList<" + tableInfo.getBeanName() + "> list = this.findByParam(param);" + "\n");
        bw.write("\t\t\t// 创建一个PaginationResultVo对象，用于封装分页结果 ->这里同一用Result进行结果的返回\n");
        bw.write("\t\t\tPaginationResultVo<" + tableInfo.getBeanName() + "> result = new PaginationResultVo<>" +
                "(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),list);" + "\n");
        bw.write("\t\t\treturn result;" + "\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("新增", bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic Integer add(" + tableInfo.getBeanName() + " param){" + "\n");
        bw.write("\t\t\treturn " + mapperName + ".insert(param);" + "\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("批量新增", bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> params){" + "\n");
        bw.write("\t\t\t//进行非空判断\n");
        bw.write("\t\t\tif(params == null || params.size() == 0){" + "\n");
        bw.write("\t\t\t\treturn 0;" + "\n");
        bw.write("\t\t\t}" + "\n");
        bw.write("\t\t\treturn " + mapperName + ".insertBatch(params);" + "\n");
        bw.write("\t\t}" + "\n");
        CommentUtils.CreateMethodComment("批量新增或者修改", bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> params){" + "\n");
        bw.write("\t\t\t//进行非空判断\n");
        bw.write("\t\t\tif(params == null || params.size() == 0){" + "\n");
        bw.write("\t\t\t\treturn 0;" + "\n");
        bw.write("\t\t\t}" + "\n");
        bw.write("\t\t\treturn " + mapperName + ".insertOrUpdateBatch(params);" + "\n");
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
            bw.write("\t\t@Override\n");
            bw.write("\t\tpublic " + tableInfo.getBeanName() + " get" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString()  +"){" + "\n");
            bw.write("\t\t\treturn this." + mapperName +".selectBy" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" +param.toString() + ");" + "\n");
            bw.write("\t\t}" + "\n");
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "删除数据",bw);
            bw.write("\t\t@Override\n");
            bw.write("\t\tpublic Integer delete" + tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "("+ sbParam.toString() + "){" + "\n");
            bw.write("\t\t\treturn this." + mapperName +".deleteBy" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" +param.toString() + ");" + "\n");
            bw.write("\t\t}" + "\n");
            //生成注释
            CommentUtils.CreateMethodComment( "根据" + sb.toString() + "更新数据",bw);
            bw.write("\t\t@Override\n");
            bw.write("\t\tpublic Integer Update"+ tableInfo.getBeanName() + "By" + StringUtils.firstCharToUpperCase(sb.toString()) + "(" + tableInfo.getBeanName()  + " param, " + sbParam.toString() + "){" + "\n");
            bw.write("\t\t\treturn this." + mapperName +".UpdateBy" + StringUtils.firstCharToUpperCase(sb.toString()) + "(param" + "," +param.toString() + ");" + "\n");
            bw.write("\t\t}" + "\n");
        }
        bw.write("}" + "\n");
        //刷新缓冲区
        bw.flush();
    }
}
