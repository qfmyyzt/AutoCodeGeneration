package com.qfmy;

import com.qfmy.bean.TableInfo;
import com.qfmy.builder.*;
import com.qfmy.consts.CommonPropertiesConst;
import com.qfmy.consts.PathAndPackageConst;

import java.util.List;

/**
 * @author: 廖志伟
 * @date: 2024-11-10
 * 启动类
 */
@SuppressWarnings("all")
public class RunApplication {
    public static void main(String[] args) {
        //创建表
        List<TableInfo> tables = BuilderTable.getTables();
        //循环遍历表
        tables.stream().forEach(table ->
        {
            //创建pojo对象
            BuilderPo.execute(table);
            //创建Query类
            BuilderQuery.execute(table);
            //创建Mapper接口
            BuilderMapper.execute(table);
            //判断是生成Mapper.xml还是基于注解的方式
            if(PathAndPackageConst.SQL_TYPE)
            {
                //创建Mapper.xml
                BuilderMapperXml.execute(table);
            }
            else
            {

            }
            //创建Service接口
            BuilderService.execute(table);
            //创建ServiceImpl类
            BuilderServiceImpl.execute(table);
            //创建Controller类
            BuilderController.execute(table);
        });
        //创建基础类
        BuilderBase.execute();
        //判断是否有minio配置
        if(CommonPropertiesConst.IS_MINIO)
        {
            //创建文件上传类
            BuilderFileUpload.execute();
        }
        if(CommonPropertiesConst.IS_SMS)
        {
            //创建登入接口
            BuilderLogin.execute();
        }
    }
}
