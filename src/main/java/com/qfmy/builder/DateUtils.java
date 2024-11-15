package com.qfmy.builder;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 格式化日期工具类
 */
@SuppressWarnings("all")
public class DateUtils {
    //定义一个日志对象
    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
    //定义一个集合，存放SimpleDateFormat对象->由于SimpleDateFormat不是线程安全的，所以不能直接在多线程环境下使用，所以需要定义一个集合，存放SimpleDateFormat对象
    private static final Map<String,ThreadLocal<SimpleDateFormat>> sdf = new ConcurrentHashMap<>();

    /**
     * 根据pattern获取SimpleDateFormat对象
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getSdf(final String pattern)
    {
        //从集合中获取SimpleDateFormat对象的线程
        ThreadLocal<SimpleDateFormat> sd = sdf.get(pattern);
        //判断是否存在SimpleDateFormat对象的线程
        if(sd == null)
        {
           synchronized (log)
           {
            //在次判断是否存在SimpleDateFormat对象的线程
               sd = sdf.get(pattern);
               if(sd == null)
               {
                   //不存在     创建SimpleDateFormat对象并放入集合
                   sd = new ThreadLocal<SimpleDateFormat>(){
                       @Override
                       protected SimpleDateFormat initialValue() {
                           return new SimpleDateFormat(pattern);
                       }
                   };
                  //放入集合
                   sdf.put(pattern, sd);
               }
           }
        }
        //返回SimpleDateFormat对象
        return sd.get();
    }

    /**
     * 格式化日期 ->转为字符串类型
     * @param date
     * @param pattern
     * @return
     */
    public static @Nullable String format(Date date, String pattern) {
        try {
            return getSdf(pattern).format(date);
        } catch (Exception e) {
            log.error("日期格式化失败", e);
        }
        return null;
    }

}
