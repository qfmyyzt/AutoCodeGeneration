package com.qfmy.builder;

import ch.qos.logback.classic.Logger;
import com.qfmy.consts.CommonPropertiesConst;
import com.qfmy.consts.PathAndPackageConst;
import com.qfmy.consts.TestConst;
import com.qfmy.utils.CommentUtils;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author: 廖志伟
 * @date: 2024-11-14
 * 创建登入
 */
@SuppressWarnings("all")
public class BuilderLogin {
    //定义一个日志变量
    private static final Logger log = (Logger) LoggerFactory.getLogger(BuilderLogin.class);

    public static void execute() {
        //创建登入Service接口
        try {
            BuilderLoginService();
        } catch (Exception e) {
            log.error("创建登入Service接口失败");
        }
        //创建登入Service实现类
        try {
            BuilderLoginServiceImpl();
        } catch (Exception e) {
            log.error("创建登入Service实现类失败");
        }
        //创建登入Controller
        try {
            BuilderLoginController();
        } catch (Exception e) {
            log.error("创建登入Controller失败");
        }
    }

    /**
     * 创建登入的Controller
     */
    private static void BuilderLoginController() throws Exception {
        //判断文件是否存在
        File folder = new File(PathAndPackageConst.PATH_CONTROLLER);
        if (!folder.exists()){
            //创建文件
            folder.mkdirs();
        }
        //创建文件
        File file = new File(folder, "LoginController.java");
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        os = new FileOutputStream(file);
        ow = new OutputStreamWriter(os);
        bw = new BufferedWriter(ow);
        bw.write("package " + PathAndPackageConst.PACKAGE_CONTROLLER + ";\n");
        bw.write("import org.springframework.web.bind.annotation.RestController;\n");
        bw.write("import org.springframework.web.bind.annotation.PostMapping;\n");
        bw.write("import org.springframework.web.bind.annotation.RequestMapping;\n");
        bw.write("import " + CommonPropertiesConst.CONST_PACKAGE + ".Captcha;\n");
        bw.write("import " + CommonPropertiesConst.RESULT_PACKAGE + ".Result;\n");
        bw.write("import org.springframework.web.bind.annotation.RequestParam;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_SERVICE + ".LoginService;\n");
        if(TestConst.IS_TEST)
        {
            bw.write("import io.swagger.v3.oas.annotations.Operation;\n" +
                    "import io.swagger.v3.oas.annotations.tags.Tag;\n");
        }
        bw.write("import jakarta.annotation.Resource;\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,"登入Controller");
        if(TestConst.IS_TEST)
        {
            bw.write("@Tag(name = \"登入接口\")\n");
        }
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("@RestController\n");
        bw.write("@RequestMapping(\"/app\")\n");
        bw.write("public class LoginController {\n");
        //生成注释
        CommentUtils.CreateFieldComment(bw,"注入登入服务层");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate LoginService loginService;\n");
        //生成注释
        CommentUtils.CreateMethodComment("获取图形验证码",bw);
        if(TestConst.IS_TEST)
        {
            bw.write("\t\t@Operation(summary = \"获取图形验证码\")\n");
        }
        bw.write("\t\t@PostMapping(\"/login\")\n");
        bw.write("\t\tpublic Result<Captcha> getCaptcha() {\n" +
                "        Captcha captcha = loginService.getCaptcha();\n" +
                "        return Result.ok(captcha);\n" +
                "\t}\n");
        CommentUtils.CreateMethodComment("获取短信验证码",bw);
        bw.write("\t\t@PostMapping(\"/login/getCode\")\n");
        if(TestConst.IS_TEST)
        {
            bw.write("\t\t@Operation(summary = \"获取短信验证码\")\n");
        }
        bw.write("\t\tpublic Result getCode(@RequestParam String phone) {\n" +
                "        loginService.getCode(phone);\n" +
                "        return Result.ok();\n" +
                "    }\n");
        bw.write("}");
        //刷新
        bw.flush();
        //关闭流
        if(bw != null){
            bw.close();
        }
        if(ow != null){
            ow.close();
        }
        if(os != null){
            os.close();
        }
    }

    /**
     * 创建登入的Service实现类
     */
    private static void BuilderLoginServiceImpl() throws Exception {
        //判断文件是否存在
        File folder = new File(PathAndPackageConst.PATH_SERVICE_IMPL);
        if (!folder.exists()){
            //创建文件
            folder.mkdirs();
        }
        //创建文件
        File file = new File(folder, "LoginServiceImpl.java");
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        os = new FileOutputStream(file);
        ow = new OutputStreamWriter(os);
        bw = new BufferedWriter(ow);
        bw.write("package " + PathAndPackageConst.PACKAGE_SERVICE_IMPL + ";\n");
        bw.write("import jakarta.annotation.Resource;\n");
        bw.write("import org.springframework.stereotype.Service;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_SERVICE + ".LoginService;\n");
        bw.write("import " + CommonPropertiesConst.CONST_PACKAGE + ".RedisConstant;\n");
        bw.write("import " + CommonPropertiesConst.CONST_PACKAGE + ".Captcha;\n");
        bw.write("import " +CommonPropertiesConst.GLOBAL_EXCEPTION_PACKAGE + ".GlobalException;\n");
        bw.write("import com.wf.captcha.SpecCaptcha;\n");
        bw.write("import org.springframework.data.redis.core.StringRedisTemplate;\n");
        bw.write("import java.util.UUID;\n");
        bw.write("import java.util.concurrent.TimeUnit;\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,"登入Service实现类");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("@Service\n");
        bw.write("public class LoginServiceImpl implements LoginService {\n");
        //生成注释
        CommentUtils.CreateFieldComment(bw,"redis模板");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate StringRedisTemplate redisTemplate;\n");
        //生成注释
        CommentUtils.CreateMethodComment("获取图形验证码",bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\t public Captcha getCaptcha() {\n" +
                "        // 创建一个指定宽度为 130、高度为 48、字符数量为 4 的特定类型的验证码对象。\n" +
                "        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);\n" +
                "        // 获取生成的验证码文本，并转换为小写字母形式。\n" +
                "        String code = specCaptcha.text().toLowerCase();\n" +
                "        // 使用 Redis 常量中的管理员登录前缀和随机生成的 UUID 组成验证码在 Redis 中的键。\n" +
                "        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();\n" +
                "        // 将验证码值存储在 Redis 中，设置超时时间为 APP_LOGIN_CODE_RESEND_TIME_SEC 秒。\n" +
                "        redisTemplate.opsForValue().set(key, code, RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC, TimeUnit.SECONDS);\n" +
                "        // 创建并返回一个包含验证码图片数据（以 Base64 编码形式）和验证码 key 的 CaptchaVo 对象。\n" +
                "        return new Captcha(specCaptcha.toBase64(), key);\n" +
                "\t}\n" );
        CommentUtils.CreateMethodComment("获取短信验证码,并且把验证码保存到redis",bw);
        bw.write("\t\t@Override\n" +
                "    public void getCode(String phone) {\n" +
                "        //获取短信验证码\n" +
                "        String code = CodeUtils.getCode(6);\n" +
                "        //将验证码存入redis\n" +
                "        String key = RedisConstant.APP_LOGIN_PREFIX + phone;\n" +
                "        //限制验证码发送频率\n" +
                "        Boolean b = redisTemplate.hasKey(key);\n" +
                "        if(b){\n" +
                "            //获取验证码发送时间\n" +
                "            Long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);\n" +
                "            //计算时间差\n" +
                "            Long diff = RedisConstant.APP_LOGIN_CODE_TTL_SEC - time;\n" +
                "            //如果时间差小于60秒，则抛出异常\n" +
                "            if(diff < 60)\n" +
                "            {\n" +
                "                throw new BaseException(ResultCodeEnum.APP_SEND_SMS_TOO_OFTEN);\n" +
                "            }\n" +
                "        }\n" +
                "        //通过短信发送验证码\n" +
                "       smsService.sendCode(phone, code);\n" +
                "       //将验证码存入redis\n" +
                "        redisTemplate.opsForValue().set(key,code,RedisConstant.APP_LOGIN_CODE_TTL_SEC, TimeUnit.SECONDS);\n" +
                "    }");
        //刷新
        bw.flush();
        //关闭流
        if(bw != null){
            bw.close();
        }
        if(ow != null){
            ow.close();
        }
        if(os != null){
            os.close();
        }
    }

    /**
     * 创建登入Service接口
     */
    private static void BuilderLoginService() throws Exception {
        //判断文件是否存在
        File folder = new File(PathAndPackageConst.PATH_SERVICE);
        if (!folder.exists()){
            //创建文件
            folder.mkdirs();
        }
        //创建文件
        File file = new File(folder, "LoginService.java");
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        os = new FileOutputStream(file);
        ow = new OutputStreamWriter(os);
        bw = new BufferedWriter(ow);
       //导包
        bw.write("package " + PathAndPackageConst.PACKAGE_SERVICE + ";\n");
        bw.write("import " + CommonPropertiesConst.CONST_PACKAGE + ".Captcha;\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,"登入Service接口");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("public interface LoginService {\n");
        //生成注释
        CommentUtils.CreateMethodComment("获取图形验证码",bw);
        bw.write("\t\t Captcha getCaptcha();\n");
        CommentUtils.CreateMethodComment("获取短信验证码",bw);
        bw.write("\t\t   void getCode(String phone);\n");
        bw.write("}\n");
        //刷新
        bw.flush();
        //关闭流
        if(bw != null){
            bw.close();
        }
        if(ow != null){
            ow.close();
        }
        if(os != null){
            os.close();
        }
    }

}
