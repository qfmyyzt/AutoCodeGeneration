package com.qfmy.consts;

import com.qfmy.utils.PropertiesUtils;

/**
 * @author: 廖志伟
 * @date: 2024-11-11
 * 单元测试常量类
 */
@SuppressWarnings("all")
public class TestConst {
    // swagger单元测试常量
    public static final String SWAGGER_TEST = PropertiesUtils.getProperty("unit.test");
    //判断是否开启单元测试
    public static final boolean IS_TEST = Boolean.parseBoolean(PropertiesUtils.getProperty("test"));

}
