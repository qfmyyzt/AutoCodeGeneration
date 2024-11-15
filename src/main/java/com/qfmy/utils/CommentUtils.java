package com.qfmy.utils;


import com.qfmy.consts.TableConstInfo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;


/**
 * @author: 廖志伟
 * @date: 2024-11-11
 * 生成代码注释工具类，用于生成代码注释
 */
@SuppressWarnings("all")
public class CommentUtils {

    /**
     * 生成类注释
     */
    public static void CreateClassComment(BufferedWriter bw, String comment) throws Exception {
        bw.write("/**\n");
        bw.write(" * @author: "+ TableConstInfo.AUTHOR_NAME +"\n");
        bw.write(" * @date: " + DateUtils.parseDate(new Date() ,DateUtils.YYYY_MM_DD_HH_MM_SS_PATTERN) + "\n");
        bw.write(" * " + comment +"\n");
        bw.write(" */\n");
    }

    /**
     * 生成方法注释
     */
    public static void CreateMethodComment(String comment,BufferedWriter bw) throws IOException {
        bw.write("\t\t/**\n");
        bw.write("\t\t*" + comment +"\n");
        bw.write("\t\t*/\n");
    }

    /**
     * 生成字段注释
     */
    public static void CreateFieldComment(BufferedWriter bw,String comment) throws Exception
    {
        bw.write("\t\t/**\n");
        bw.write("\t\t*" + comment +"\n");
        bw.write("\t\t*/\n");
    }
}
