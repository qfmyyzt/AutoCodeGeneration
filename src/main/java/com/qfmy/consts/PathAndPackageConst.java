package com.qfmy.consts;

import com.qfmy.utils.PropertiesUtils;

/**
 * @author: 廖志伟
 * @date: 2024-11-12
 * @description: 路径和包名常量类
 */
@
SuppressWarnings("all")
public class PathAndPackageConst {
    //定义一个Resource名称
    public static final String RESOURCE_NAME = "resources";
    //定义一个java名称
    public static final String JAVA_NAME = "java";
    //定义一个生成代码的路径常量
    public static final String PATH_BASE =  PropertiesUtils.getProperty("path.base");
    //定义一个生成文件包名常量
    public static final String PACKAGE_NAME =  PropertiesUtils.getProperty("package.name");
    //定义一个生成Pojo包名常量
    public static final String PACKAGE_POJO =  PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.pojo");
    //定义一个生成参数包名常量->query
    public static final String PACKAGE_PARAM =   PACKAGE_NAME + "." +  PropertiesUtils.getProperty("package.query");
    //定义一个生成VO包名常量->vo
    public static final String PACKAGE_VO =   PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.vo");
    //定义一个po的路径常量
    public static final String PATH_PO =  PATH_BASE + JAVA_NAME + "/" + PACKAGE_POJO.replace(".", "/");
    //定义一个query的路径常量
    public static final String PATH_PARAM =  PATH_BASE +  JAVA_NAME + "/"  + PACKAGE_PARAM.replace(".", "/");
    //定义一个vo的路径常量
    public static final String PATH_VO =  PATH_BASE + JAVA_NAME + "/"  + PACKAGE_VO.replace(".", "/");
    //定义一个父类包名常量
    public static final String PACKAGE_SUPER = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.super");
    //定义一个父类路径常量
    public static final String PATH_SUPER =  PATH_BASE + JAVA_NAME + "/"  + PACKAGE_SUPER.replace(".", "/");
    //定义一个DateUtils类包名
    public static final String PACKAGE_DATE_UTILS = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.DateUtil");
    //定义一个DateUtils类路径
    public static final String PATH_DATE_UTILS = PATH_BASE + JAVA_NAME + "/" + PACKAGE_DATE_UTILS.replace(".", "/");
    //定义一个DateTimePattern类包名
    public static final String PACKAGE_DATETIME_PATTERN = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.DateTimePattern");
    //定义一个DateTimePattern类路径
    public static final String PATH_DATETIME_PATTERN = PATH_BASE + JAVA_NAME + "/" + PACKAGE_DATETIME_PATTERN.replace(".", "/");
    //生成一个Mapper接口包名
    public static final String PACKAGE_MAPPER = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.mapper");
    //生成一个Mapper接口路径
    public static final String PATH_MAPPER = PATH_BASE + JAVA_NAME + "/" + PACKAGE_MAPPER.replace(".", "/");
    //基于xml还是注解生成sql语句
    public static final boolean SQL_TYPE = Boolean.valueOf(PropertiesUtils.getProperty("mapper.IsXmlOrAnnotation"));
    //定义一个xml文件路劲
    public static final String PATH_XML = PATH_BASE + RESOURCE_NAME + "/" + "mapper";
    //定义一个分页查询的包名
    public static final String PACKAGE_PAGE = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.page");
    //定义一个分页查询的路径
    public static final String PATH_PAGE = PATH_BASE + JAVA_NAME + "/" + PACKAGE_PAGE.replace(".", "/");
    //定义一个每页显示的记录数常量枚举类包名
    public static final String PACKAGE_PAGE_SIZE = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.pageSize");
    //定义一个每页显示的记录数常量枚举类路径
    public static final String PATH_PAGE_SIZE = PATH_BASE + JAVA_NAME + "/" + PACKAGE_PAGE_SIZE.replace(".", "/");
    //定义一个Service接口包名
    public static final String PACKAGE_SERVICE = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.service");
    //定义一个Service接口路径
    public static final String PATH_SERVICE = PATH_BASE + JAVA_NAME + "/" + PACKAGE_SERVICE.replace(".", "/");
    //定义一个Service实现类包名
    public static final String PACKAGE_SERVICE_IMPL = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.service.impl");
    //定义一个Service实现类路径
    public static final String PATH_SERVICE_IMPL = PATH_BASE + JAVA_NAME + "/" + PACKAGE_SERVICE_IMPL.replace(".", "/");
    //定义一个Controller包名
    public static final String PACKAGE_CONTROLLER = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.controller");
    //定义一个Controller路径
    public static final String PATH_CONTROLLER = PATH_BASE + JAVA_NAME + "/" + PACKAGE_CONTROLLER.replace(".", "/");
    //配置类所在的包名
    public static final String PACKAGE_CONFIG = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.config");
    //配置类所在的路径
    public static final String PATH_CONFIG = PATH_BASE + JAVA_NAME + "/" + PACKAGE_CONFIG.replace(".", "/");
    //定义一个拦截器包名
    public static final String PACKAGE_INTERCEPTOR = PACKAGE_NAME + "." + PropertiesUtils.getProperty("package.interceptor");
    //定义一个拦截器路径
    public static final String PATH_INTERCEPTOR = PATH_BASE + JAVA_NAME + "/" + PACKAGE_INTERCEPTOR.replace(".", "/");
    //是否开启缓存
    public static final boolean CACHE_OPEN = Boolean.parseBoolean(PropertiesUtils.getProperty("is.cache"));
}
