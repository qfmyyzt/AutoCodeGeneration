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
 * 创建文件上传
 */
@SuppressWarnings("all")
public class BuilderFileUpload {
    //定义一个日志变量
    private static final ch.qos.logback.classic.Logger log = (Logger) LoggerFactory.getLogger(BuilderFileUpload.class);

    public static void execute(){
      //生成文件上传Service对象
        try {
            builderFileUploadService();
        } catch (Exception e) {
            log.error("生成文件上传Service对象失败");
        }
        //生成文件上传ServiceImpl对象
        try {
            builderFileUploadServiceImpl();
        } catch (Exception e) {
            log.error("生成文件上传ServiceImpl对象失败");
        }
        //生成文件上传Controller对象对象
        try {
            builderFileUploadController();
        } catch (Exception e) {
            log.error("生成文件上传Controller对象失败");
        }
    }

    /**
     * 生成文件生成的Controller对象
     */
    private static void builderFileUploadController() throws Exception {
        //判断文件是否存在
        File folder = new File(PathAndPackageConst.PATH_CONTROLLER);
        if (!folder.exists()){
            folder.mkdirs();
        }
        //创建文件
        File file = new File(folder, "FileUploadController.java");
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        os = new FileOutputStream(file);
        ow = new OutputStreamWriter(os);
        bw = new BufferedWriter(ow);
        bw.write("package " + PathAndPackageConst.PACKAGE_CONTROLLER + ";\n\n");
        bw.write("import org.springframework.web.bind.annotation.*;\n");
        bw.write("import org.springframework.web.multipart.MultipartFile;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_SERVICE + ".FileUploadService;\n");
        bw.write("import jakarta.annotation.Resource;\n");
        //判断是否使用了单元测试
        if(TestConst.IS_TEST){
          bw.write("import io.swagger.v3.oas.annotations.Operation;\n");
          bw.write("import io.swagger.v3.oas.annotations.tags.Tag;\n");
        }
        bw.write("import " + CommonPropertiesConst.RESULT_PACKAGE + ".Result;\n\n");
        if(TestConst.IS_TEST){
            bw.write("@Tag(name = \"文件管理\")\n");
        }
        bw.write("@RequestMapping(\"/admin/file\")");
        bw.write("@RestController\n");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("public class FileUploadController {\n\n");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate FileUploadService fileUploadService;\n\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,"文件上传Controller");
        if(TestConst.IS_TEST){
            bw.write("\t\t@Operation(summary = \"文件上传\")\n");
        }
        bw.write("\t\t@PostMapping(\"/upload\")\n");
        bw.write("\t\tpublic Result<String> upload(@RequestParam MultipartFile file) throws Exception {\n");
        bw.write("\t\t\t// 上传文件\n");
        bw.write("\t\t\tString url = fileUploadService.upload(file);\n");
        bw.write("\t\t\treturn Result.ok(url);\n");
        bw.write("\t}\n");
        bw.write("\t}\n");
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
     * 生成文件上传Servicimpl对象
     */
    private static void builderFileUploadServiceImpl() throws Exception {
        //判断文件是否存在
        File folder = new File(PathAndPackageConst.PATH_SERVICE_IMPL);
        if (!folder.exists()){
            folder.mkdirs();
        }
        //创建文件
        File file = new File(folder, "FileUploadServiceImpl.java");
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        os = new FileOutputStream(file);
        ow = new OutputStreamWriter(os);
        bw = new BufferedWriter(ow);
        bw.write("package " + PathAndPackageConst.PACKAGE_SERVICE_IMPL + ";\n\n");
        bw.write("import jakarta.annotation.Resource;\n");
        bw.write("import java.util.Date;\n");
        bw.write("import java.util.UUID;\n");
        bw.write("import org.springframework.web.multipart.MultipartFile;\n");
        bw.write("import io.minio.*;\n");
        bw.write("import " + PathAndPackageConst.PACKAGE_SERVICE + ".FileUploadService;\n");
        bw.write("import " + CommonPropertiesConst.MINIO_PACKAGE + ".MinioProperties;\n");
        bw.write("import java.text.SimpleDateFormat;\n");
        bw.write("import org.springframework.stereotype.Service;\n\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,"文件上传ServiceImpl");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("@Service\n");
        bw.write("public class  FileUploadServiceImpl implements FileUploadService {\n\n");
        //生成注释
        CommentUtils.CreateFieldComment(bw,"注入Minio客户端");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate MinioClient mc;\n\n");
        //生成注释
        CommentUtils.CreateFieldComment(bw,"注入Minio配置文件");
        bw.write("\t\t@Resource\n");
        bw.write("\t\tprivate MinioProperties mp;\n\n");
        //进行文件上传
        CommentUtils.CreateMethodComment("生成文件上传",bw);
        bw.write("\t\t@Override\n");
        bw.write("\t\tpublic String upload(MultipartFile file) throws Exception {\n");
        bw.write("\t\t\t  String url = null;\n" +
                "        //判断桶是否存在，不存在则创建\n" +
                "            boolean b = mc.bucketExists(BucketExistsArgs.builder()\n" +
                "                    .bucket(mp.getBucketName()).build());\n" +
                "            if (!b) {\n" +
                "                //创建桶\n" +
                "                mc.makeBucket(MakeBucketArgs.builder()\n" +
                "                        .bucket(mp.getBucketName()).build());\n" +
                "                //设置桶策略\n" +
                "                mc.setBucketPolicy(SetBucketPolicyArgs.builder()\n" +
                "                        .bucket(mp.getBucketName())\n" +
                "                        .config(BuckerName(mp.getBucketName()))\n" +
                "                        .build());\n" +
                "\n" +
                "            }\n" +
                "            //使用UUID生成文件名\n" +
                "            String filename = new SimpleDateFormat(\"yyyyMMdd\").format(new Date())\n" +
                "                    + \"/\" + UUID.randomUUID() + \"-\"\n" +
                "                    + file.getOriginalFilename();\n" +
                "            //上传文件\n" +
                "            mc.putObject(PutObjectArgs.builder()\n" +
                "                    .bucket(mp.getBucketName())\n" +
                "                    .stream(file.getInputStream(),file.getSize(),-1).object(filename)\n" +
                "                    //设置文件类型->为了让浏览器不下载直接打开所以要设置文件类型\n" +
                "                    .contentType(file.getContentType())\n" +
                "                    .build());\n" +
                "            //拼接url\n" +
                "             url = String.join(\"/\",mp.getEndpoint(),mp.getBucketName(),filename);\n" +
                "        return url;\n" );
        bw.write("\t}\n\n");
        //生成注释
        CommentUtils.CreateMethodComment("生成桶的名称",bw);
        //生成桶的名称
        bw.write("\t\tpublic String BuckerName(String bucketName)\n" +
                "    {\n" +
                "        \treturn \"\"\"\n" +
                "                  {\n" +
                "                    \"Statement\" : [ {\n" +
                "                      \"Action\" : \"s3:GetObject\",\n" +
                "                      \"Effect\" : \"Allow\",\n" +
                "                      \"Principal\" : \"*\",\n" +
                "                      \"Resource\" : \"arn:aws:s3:::%s/*\"\n" +
                "                    } ],\n" +
                "                    \"Version\" : \"2012-10-17\"\n" +
                "                  }\n" +
                "                  \"\"\".formatted(bucketName);\n" +
                "    }\n");
        bw.write("\t}");
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
     *生成文件上传Service对象
     */
    private static void builderFileUploadService() throws Exception{
        //判断文件是否存在
        File folder = new File(PathAndPackageConst.PATH_SERVICE);
        if (!folder.exists()){
            //创建文件
            folder.mkdirs();
        }
        //生成文件上传Service对象
        File file = new File(folder,"FileUploadService.java");
        //创建输出流
        OutputStream os = null;
        OutputStreamWriter ow = null;
        BufferedWriter bw = null;
        os = new FileOutputStream(file);
        ow = new OutputStreamWriter(os);
        bw = new BufferedWriter(ow);
        //导包
        bw.write("package " + PathAndPackageConst.PACKAGE_SERVICE + ";\n\n");
        bw.write("import org.springframework.web.multipart.MultipartFile;\n\n");
        //生成注释
        CommentUtils.CreateClassComment(bw,"文件上传Service");
        bw.write("@SuppressWarnings(\"all\")\n");
        bw.write("public interface FileUploadService {\n\n");
        //生成注释
        CommentUtils.CreateMethodComment("文件上传",bw);
        bw.write("\t\tString upload(MultipartFile file) throws Exception;\n\n");
        bw.write("\t}");
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