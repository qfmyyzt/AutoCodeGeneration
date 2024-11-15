package com.qfmy.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 读取配置文件工具类
 */
@SuppressWarnings("all")
public class CommonPropertiesUtils {
    //创建Properties对象，用于读取配置文件
    private static Properties props = new Properties();
    //定义一个Map，用于存储配置文件中的key和value
    private static final Map<String, String> propeMap = new HashMap<>();

    //定义一个静态代码块，用于读取配置文件
    static {
        //定义InputStream对象，用于读取配置文件
        InputStream in = null;
        try{
            //加在配置文件
            in = CommonPropertiesUtils.class.getClassLoader().getResourceAsStream("common.properties");
            //加载
            props.load(in);
            //获取配置文件的key
            Iterator<Object> iterator = props.keySet().iterator();
            //遍历key，将key和value存入Map
            while(iterator.hasNext())
            {
                //获取key
                String key = (String) iterator.next();
                //获取value
                String value = props.getProperty(key);
                //存入Map
                propeMap.put(key, value);
            }
        }catch(Exception e){
            //如果读取配置文件失败，打印异常信息
            e.printStackTrace();
        }
        finally{
          //关闭InputStream对象
            if(in != null)
            {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据key获取value
     * @param key
     * @return value
     */
    public static String getProperty(String key)
    {
        //从Map中获取value
        return propeMap.get(key);
    }

}
