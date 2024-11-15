package com.qfmy.utils;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: 廖志伟
 * @date: 2024-11-12
 * 日期工具类->解析日期字符串、格式化日期字符串、日期加减操作类
 */
@SuppressWarnings("all")
public class DateUtils {
    //定义一个Loger对象，用于打印日志
    public static final Logger log = (Logger) LoggerFactory.getLogger(DateUtils.class);
    //定义日期格式
    public static final String YYYY_MM_DD_HH_MM_SS_PATTERN = "yyyy-MM-dd HH:mm:ss";
    //定义日期格式
    public static final String YYYY_MM_DD_PATTERN = "yyyy-MM-dd";
    //定义日期格式
    public static final String YYYYMMDD_PATTERN = "yyyyMMdd";

    /**
     * 解析日期字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String parseDate(Date date, String pattern)
    {
        //日期对象格式化成日期字符串
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化日期字符串
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String date, String pattern)
    {
        try {
            //日期字符串解析成日期对象
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            log.error("日期字符串解析异常", e);
            return null;
        }
    }
}
