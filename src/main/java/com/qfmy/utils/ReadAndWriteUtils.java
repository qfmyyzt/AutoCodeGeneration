package com.qfmy.utils;

import ch.qos.logback.classic.Logger;
import lombok.Data;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author: 廖志伟
 * @date: 2024-11-13
 * 生成一个读写类
 */
@SuppressWarnings("all")
@Data
public class ReadAndWriteUtils {
    //定义一个日志对象
    private  final Logger log = (Logger) LoggerFactory.getLogger(ReadAndWriteUtils.class);
    // 定义一个输入流
    public  InputStream in = null;
    //定义一个输入流读取器
    public  InputStreamReader inr = null;
    //定义一个缓冲区读取器
    public  BufferedReader br = null;
    //定义一个输出流
    public  OutputStream out = null;
    //定义一个输出流写入器
    public  OutputStreamWriter osw = null;
    //定义一个缓冲区写入器
    public  BufferedWriter bw = null;

    /**
     * 读取文件
     * @param filePath
     */
    public  void read(String filePath)
    {
        try {
            in = new FileInputStream(filePath);
            inr = new InputStreamReader(in);
            br = new BufferedReader(inr);
        } catch (Exception e) {
            log.error("文件不存在");
        }
    }

    /**
     * 写入文件
     * @param filePath
     */
    public  void write(File file)
    {
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw = new BufferedWriter(osw);
        } catch (Exception e) {
            log.error("文件不存在");
        }
    }

    /**
     * 关闭流
     */
    public  void close() {
        if(bw != null)
        {
            try {
                bw.close();
            } catch (IOException e) {
                log.error("关闭缓冲区写入器失败");
            }
        }
        if(osw!= null)
        {
            try {
                osw.close();
            } catch (IOException e) {
                log.error("关闭输出流写入器失败");
            }
        }
        if(out != null)
        {
            try {
                out.close();
            } catch (IOException e) {
                log.error("关闭输出流失败");
            }
        }
        if(br != null)
        {
            try {
                br.close();
            } catch (IOException e) {
                log.error("关闭缓冲区读取器失败");
            }
        }
        if(inr != null)
        {
            try {
                inr.close();
            } catch (IOException e) {
                log.error("关闭输入流读取器失败");
            }
        }
        if(in != null)
        {
            try {
                in.close();
            } catch (IOException e) {
                log.error("关闭输入流失败");
            }
        }
    }


}
