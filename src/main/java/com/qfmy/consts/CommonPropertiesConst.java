package com.qfmy.consts;

import com.qfmy.utils.CommonPropertiesUtils;

import java.sql.SQLOutput;

/**
 * @author: 廖志伟
 * @date: 2024-11-14
 * @Description: common.properties里面的固定的配置
 */
@SuppressWarnings("all")
public class CommonPropertiesConst {
    //定义一个java
    public static final String JAVA = "java/";
    //定义基础的路径
    public static final String BASE_PATH = CommonPropertiesUtils.getProperty("path.common");
    //定义基础的包
    public static final String BASE_PACKAGE = CommonPropertiesUtils.getProperty("package.common");
    //定义响应结果所在的包
    public static final String RESULT_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.result");
    //定义响应结果所在的路径
    public static final String RESULT_PATH = BASE_PATH + JAVA + RESULT_PACKAGE.replace(".", "/");
    //定义响应结果枚举所在的包
    public static final String RESULT_ENUM_PACKAGE = RESULT_PACKAGE;
    //定义响应结果枚举所在的路径
    public static final String RESULT_ENUM_PATH = RESULT_PATH;
    //定义常量所在的包
    public static final String CONST_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.const");
    //定义常量所在的路径
    public static final String CONST_PATH = BASE_PATH + JAVA + CONST_PACKAGE.replace(".", "/");
    //定义jwt工具类所在的包
    public static final String JWT_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.jwt");
    //定义jwt工具类所在的路径
    public static final String JWT_PATH = BASE_PATH + JAVA + JWT_PACKAGE.replace(".", "/");
    //定义一个全局异常处理器的包
    public static final String GLOBAL_EXCEPTION_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.exception");
    //定义一个全局异常处理器的路径
    public static final String GLOBAL_EXCEPTION_PATH = BASE_PATH + JAVA + GLOBAL_EXCEPTION_PACKAGE.replace(".", "/");
    //判断是否要生成minio配置
    public static final boolean IS_MINIO = Boolean.parseBoolean(CommonPropertiesUtils.getProperty("minio.generate"));
    //minio配置所在的包
    public static final String MINIO_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.minio");
    //minio配置所在的路径
    public static final String MINIO_PATH = BASE_PATH + JAVA + MINIO_PACKAGE.replace(".", "/");
    //判断是否需要进行短信验证
    public static final boolean IS_SMS = Boolean.parseBoolean(CommonPropertiesUtils.getProperty("login.sms"));
    //短信验证的包
    public static final String SMS_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.sms");
    //短信验证的路径
    public static final String SMS_PATH = BASE_PATH + JAVA + SMS_PACKAGE.replace(".", "/");
    //验证码生成器所在的包
    public static final String CAPTCHA_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.captcha");
    //验证码生成器所在的路径
    public static final String CAPTCHA_PATH = BASE_PATH + JAVA + CAPTCHA_PACKAGE.replace(".", "/");
    //文件上传的Service所在的包
    public static final String FILE_UPLOAD_SERVICE_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.upload.service");
    //文件上传的Service所在的路径
    public static final String FILE_UPLOAD_SERVICE_PATH = BASE_PATH + JAVA + FILE_UPLOAD_SERVICE_PACKAGE.replace(".", "/");
    //文件上传Controller所在的包
    public static final String FILE_UPLOAD_CONTROLLER_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.upload.controller");
    //文件上传Controller所在的路径
    public static final String FILE_UPLOAD_CONTROLLER_PATH = BASE_PATH + JAVA + FILE_UPLOAD_CONTROLLER_PACKAGE.replace(".", "/");
    //生成Redis常量类所在的包
    public static final String REDIS_CONSTANT_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.redis.const");
    //生成Redis常量类所在的路径
    public static final String REDIS_CONSTANT_PATH = BASE_PATH + JAVA + REDIS_CONSTANT_PACKAGE.replace(".", "/");
    //生成图型验证码的包
    public static final String CODE_PACKAGE = BASE_PACKAGE + "." + CommonPropertiesUtils.getProperty("package.captcha.code");
    //生成图型验证码的路径
    public static final String CODE_PATH = BASE_PATH + JAVA +CODE_PACKAGE.replace(".", "/");

}
