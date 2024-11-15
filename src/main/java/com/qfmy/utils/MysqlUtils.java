package com.qfmy.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 获取数据库连接
 */
@SuppressWarnings("all")
public class MysqlUtils {
    //定义一个日志对象
    private static final Logger logger = LoggerFactory.getLogger(MysqlUtils.class);
    //定义一个连接对象
    private static Connection conn = null;

    //定义一个静态代码块，在类被加载时执行
    static {
        //获取数据库连接
        String url = PropertiesUtils.getProperty("db.url");
        //获取用户名
        String userName = PropertiesUtils.getProperty("db.username");
        //获取密码
        String password = PropertiesUtils.getProperty("db.password");
        //获取驱动类
        String driver = PropertiesUtils.getProperty("db.driver.name");
        try {
            //加载驱动
            Class.forName(driver);
            //获取数据库连接
            conn = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            //打印日志
            logger.error("加载驱动失败！", e);
        }
    }

    /**
     * 获取数据库连接
     * @return  conn -> 数据库连接对象
     */
    public static Connection getConn() {
        //如果连接对象为空，则获取连接
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     * @param pstmt
     * @param rs
     */
    public static void close( PreparedStatement pstmt, ResultSet rs)
    {
        if(rs!=null)
        {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("关闭ResultSet失败！", e);
            }
        }
        if(pstmt!=null)
        {
            try {
                pstmt.close();
            } catch (SQLException e) {
                logger.error("关闭PreparedStatement失败！", e);
            }
        }
    }

}
