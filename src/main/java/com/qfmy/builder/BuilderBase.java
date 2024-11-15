package com.qfmy.builder;

import ch.qos.logback.classic.Logger;
import com.qfmy.consts.CommonPropertiesConst;
import com.qfmy.consts.PathAndPackageConst;
import com.qfmy.utils.ReadAndWriteUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: 廖志伟
 * @date: 2024-11-13
 * 创建一些基础的类
 */
@SuppressWarnings("all")
public class BuilderBase {
    //定义一个日志变量
    private static final Logger log = (Logger) LoggerFactory.getLogger(BuilderBase.class);

    public static void execute() {
        //定义一个集合,用来存放要生成的package包
        List<String> packages =  new ArrayList<>();
        packages.add("package " + PathAndPackageConst.PACKAGE_DATETIME_PATTERN);
        //创建格式式化字符工具类
        build(packages,PathAndPackageConst.PATH_DATETIME_PATTERN,"DateTimePatternEnum");
        //清理集合
        packages.clear();
        packages.add("package " + PathAndPackageConst.PACKAGE_DATE_UTILS);
        //创建DateUtil类
        build(packages,PathAndPackageConst.PATH_DATE_UTILS, "DateUtil");
        //清理集合
        packages.clear();
        packages.add("package " + PathAndPackageConst.PACKAGE_MAPPER);
        //创建Mapper基类
        build(packages,PathAndPackageConst.PATH_MAPPER, "BaseMapper");
        //清理集合
        packages.clear();
        //生成PageSizeEnum类
        packages.add("package " + PathAndPackageConst.PACKAGE_PAGE_SIZE);
        build(packages,PathAndPackageConst.PATH_PAGE_SIZE, "PageSize");
        //清理集合
        packages.clear();
        //生成分页查询类
        packages.add("package " + PathAndPackageConst.PACKAGE_PAGE );
        packages.add("import " + PathAndPackageConst.PACKAGE_PAGE_SIZE + "." + "PageSize");
        build(packages,PathAndPackageConst.PATH_PAGE, "SimplePage");
        //清理集合
        packages.clear();
        //生成BaseQuery的基类
        packages.add("package " + PathAndPackageConst.PACKAGE_PARAM );
        packages.add("import " + PathAndPackageConst.PACKAGE_PAGE + "." + "SimplePage");
        build(packages,PathAndPackageConst.PATH_PARAM, "BaseQuery");
        //清理集合
        packages.clear();
        //生成PaginationResultVo
        packages.add("package " + PathAndPackageConst.PACKAGE_PAGE);
        build(packages,PathAndPackageConst.PATH_PAGE, "PaginationResultVo");
        //清理集合
        packages.clear();
        //生成ResultCodeEnum类
        packages.add("package " + CommonPropertiesConst.RESULT_ENUM_PACKAGE);
        build(packages,CommonPropertiesConst.RESULT_ENUM_PATH, "ResultCodeEnum");
        //清理集合
        packages.clear();
        //生成Result结果类
        packages.add("package " + CommonPropertiesConst.RESULT_PACKAGE );
        build(packages,CommonPropertiesConst.RESULT_PATH, "Result");
        //清理集合
        //生成Const常量类
        packages.clear();
        packages.add("package " + CommonPropertiesConst.CONST_PACKAGE );
        build(packages,CommonPropertiesConst.CONST_PATH, "LoginMessageConst");
        //清理集合
        packages.clear();
        //生成图形验证码
        packages.add("package " + CommonPropertiesConst.CODE_PACKAGE );
        build(packages,CommonPropertiesConst.CODE_PATH, "Captcha");
        //清理集合
        packages.clear();
        //生成全局异常处理器
        packages.add("package " + CommonPropertiesConst.GLOBAL_EXCEPTION_PACKAGE );
        //导入包
        packages.add("import " + CommonPropertiesConst.RESULT_ENUM_PACKAGE  + ".ResultCodeEnum");
        //构建类
        build(packages,CommonPropertiesConst.GLOBAL_EXCEPTION_PATH, "BaseException");
        //清理集合
        packages.clear();
        //生成全局异常处理器
        packages.add("package " + CommonPropertiesConst.GLOBAL_EXCEPTION_PACKAGE );
        //导入包
        packages.add("import " + CommonPropertiesConst.RESULT_PACKAGE  + ".Result");
        build(packages,CommonPropertiesConst.GLOBAL_EXCEPTION_PATH, "GlobalExceptionHandler");
        //清理集合
        packages.clear();
        //生成JwtUtil类
        packages.add("package " + CommonPropertiesConst.JWT_PACKAGE );
        //导入包
        packages.add("import " + CommonPropertiesConst.RESULT_ENUM_PACKAGE  + ".ResultCodeEnum");
        packages.add("import " + CommonPropertiesConst.GLOBAL_EXCEPTION_PACKAGE + ".BaseException");
        build(packages,CommonPropertiesConst.JWT_PATH, "JwtUtil");
        //清理集合
        packages.clear();
        //生成标识登入用户信息的类
        packages.add("package " + CommonPropertiesConst.CONST_PACKAGE);
        build(packages,CommonPropertiesConst.CONST_PATH, "LoginUser");
        //清理集合
        packages.clear();
        //生成redis常量类
        packages.add("package " + CommonPropertiesConst.CONST_PACKAGE);
        build(packages,CommonPropertiesConst.CONST_PATH, "RedisConstant");
        //清理集合
        packages.clear();
        //生成LoginUserHolder类
        packages.add("package " + CommonPropertiesConst.CONST_PACKAGE);
        build(packages,CommonPropertiesConst.CONST_PATH, "LoginUserHolder");
        //判断一下是否要生成minio
        if(CommonPropertiesConst.IS_MINIO){
            //清理集合
            packages.clear();
            //生成MinioPropertis类
            packages.add("package " + CommonPropertiesConst.MINIO_PACKAGE);
            build(packages,CommonPropertiesConst.MINIO_PATH, "MinioProperties");
            //清理集合
            packages.clear();
            //生成MinioConfig配置类
            packages.add("package " + CommonPropertiesConst.MINIO_PACKAGE);
            build(packages,CommonPropertiesConst.MINIO_PATH, "MinioConfig");
        }
        //判断是否进行短信验证
        if(CommonPropertiesConst.IS_SMS){
            //清理集合
            packages.clear();
            //生成SmsProperties类
            packages.add("package " + CommonPropertiesConst.SMS_PACKAGE);
            build(packages,CommonPropertiesConst.SMS_PATH, "AliyunSMSProperties");
            //清里集合
            packages.clear();
            //生成SmsConfig类
            packages.add("package " + CommonPropertiesConst.SMS_PACKAGE);
            build(packages,CommonPropertiesConst.SMS_PATH, "AliyunSMSConfig");
            //清理集合
            packages.clear();
            //生成验证码生成器
            packages.add("package " + CommonPropertiesConst.SMS_PACKAGE);
            build(packages,CommonPropertiesConst.CAPTCHA_PATH, "CodeUtils");
            //清理集合
            packages.clear();
            //生成使用手机号登入的实体类
            packages.add("package " + PathAndPackageConst.PACKAGE_VO);
            build(packages,PathAndPackageConst.PATH_VO, "LoginVo");
            //清理集合
            packages.clear();
            //生成正常登入的实体类
            packages.add("package " + PathAndPackageConst.PACKAGE_VO);
            build(packages,PathAndPackageConst.PATH_VO, "Login");
        }

        //定义拦截器
        packages.clear();
        packages.add("package " + PathAndPackageConst.PACKAGE_INTERCEPTOR);
        packages.add("import " + CommonPropertiesConst.CONST_PACKAGE + ".LoginUserHolder");
        packages.add("import " + CommonPropertiesConst.CONST_PACKAGE + ".LoginUser");
        packages.add("import " + CommonPropertiesConst.GLOBAL_EXCEPTION_PACKAGE + ".BaseException");
        packages.add("import " + CommonPropertiesConst.RESULT_ENUM_PACKAGE + ".ResultCodeEnum");
        packages.add("import " + CommonPropertiesConst.JWT_PACKAGE + ".JwtUtil");
        build(packages,PathAndPackageConst.PATH_INTERCEPTOR, "AuthenticationInterceptor");
        //清理集合
        packages.clear();
        //定义一个配置类将拦截器注册到容器中
        packages.add("package " + PathAndPackageConst.PACKAGE_CONFIG);
        packages.add("import " + PathAndPackageConst.PACKAGE_INTERCEPTOR + ".AuthenticationInterceptor");
        build(packages,PathAndPackageConst.PATH_CONFIG, "WebMvcConfig");




    }

    /**
     * 创建DateUtil类
     * @param packages
     * @param filePath
     * @param filename
     */
    public static void build(List<String> packages, String filePath, String filename) {
            //创建文件对象
            File fileds = new File(filePath);
            //创建读写工具类
            ReadAndWriteUtils read = new ReadAndWriteUtils();
            //判断文件是否存在
            if (!fileds.exists()) {
                //创建文件夹
                fileds.mkdirs();
            }
            File javafile = new File(filePath + "/" + filename + ".java");
             BufferedWriter bw = null;
             BufferedReader br = null;
            try {
                read.write(javafile);
                bw = read.getBw();
                String path = "template/" + filename + ".txt";
                //获取模板路径
                String templatePath = BuilderBase.class.getClassLoader().getResource(path).getPath();
                //读取模板内容
                read.read(templatePath);
                 br = read.getBr();
                //在写类之前进行包的导入
                for (var p : packages)
                {
                    bw.write( p + ";" + "\n");
                }
                String line = null;
                //边读边写
                while ((line = br.readLine()) != null) {
                    bw.write(line + "\n");
                }
                //刷新缓冲区
                bw.flush();
            }catch (Exception e)
            {
                log.error("创建文件失败",e);
            }
            finally{
                //关闭流
                read.close();
                try {
                    if (bw != null)
                    bw.close();
                    if (br != null)
                    br.close();
                } catch (Exception e) {
                    log.error("关闭流失败",e);
                }
            }
        }




}
