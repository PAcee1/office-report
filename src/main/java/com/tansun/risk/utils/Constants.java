package com.tansun.risk.utils;

/**
 * 系统常量
 * Created by Gosse on 2020-09-18 15:55
 */
public class Constants {
    /* 文件服务器配置 */
    public static final String UPLOAD_DIR = System.getProperty("user.dir") + "/upload/";  // 上传的目录
    public static final String DOWNLOAD_DIR = System.getProperty("user.dir") + "/upload/";  // 下载的目录
    public static final boolean UPLOAD_UUID_NAME = false;  // 文件上传是否用uuid命名

    /* 返回结果统一 */
    public static final int RESULT_OK_CODE = 0;  // 默认成功码
    public static final int RESULT_ERROR_CODE = 1;  // 默认失败码

}
