package com.tansun.risk.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传下载工具类
 * Created by wgs on 2018-12-14 08:38
 */
public class FileUtil {
    // 文件上传的目录
    private static final String UPLOAD_FILE_DIR = Constants.UPLOAD_DIR ;
    // 文件上传的目录
    private static final String DOWNLOAD_FILE_DIR = Constants.DOWNLOAD_DIR ;
    // 缩略图存放的目录
    private static final String UPLOAD_SM_DIR = Constants.UPLOAD_DIR + "thumbnail/";

    /**
     * 上传文件
     *
     * @param file MultipartFile
     * @return 示例：{"code": 0, "msg": "", "url": "", "fileName": ""}
     */
    public static ResponseResult upload(MultipartFile file) {
        String path;  // 文件保存路径
        // 文件原始名称
        String orgName = file.getOriginalFilename();
        if (orgName == null) return ResponseResult.errorMsg("上传失败");
        File outFile;
        String suffix = orgName.substring(orgName.lastIndexOf(".") + 1);  // 获取文件后缀
        // 使用原名称_0，存在相同着加1
        String prefix = orgName.substring(0, orgName.lastIndexOf("."));  // 获取文件名称
        path = getDateDir() + prefix + "_0." + suffix;
        path = UPLOAD_FILE_DIR + path;
        outFile = new File(path);
        int sameSize = 1;
        while (outFile.exists()) {
            path = getDateDir() + prefix + "_" + sameSize + "." + suffix;
            path = UPLOAD_FILE_DIR + path;
            outFile = new File(path);
            sameSize++;
        }
        try {
            if (!outFile.getParentFile().exists()) {
                if (!outFile.getParentFile().mkdirs()) return ResponseResult.errorMsg("上传失败");
            }
            file.transferTo(outFile);
            return ResponseResult.ok(path);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorMsg("上传失败");
        }
    }

    /**
     * 保存处理过的Excel
     * @param workbook
     * @return
     */
    public static String saveFile(Workbook workbook,String orgName) {
        // 文件原始名称
        //String orgName = file.getOriginalFilename();
        if (orgName == null) return "error";
        String path; // 文件保存路径
        File outFile;
        String suffix = orgName.substring(orgName.lastIndexOf(".") + 1);  // 获取文件后缀
        // 使用原名称_0，存在相同着加1
        String prefix = orgName.substring(0, orgName.lastIndexOf("."));  // 获取文件名称
        path = getDateDir() + prefix + "_0." + suffix;
        path = UPLOAD_FILE_DIR + path;
        outFile = new File(path);
        int sameSize = 1;
        while (outFile.exists()) {
            path = getDateDir() + prefix + "_" + sameSize + "." + suffix;
            path = UPLOAD_FILE_DIR + path;
            outFile = new File(path);
            sameSize++;
        }
        try {
            if (!outFile.getParentFile().exists()) {
                if (!outFile.getParentFile().mkdirs())
                    return "error";
            }
            workbook.write(new FileOutputStream(outFile));
            //file.transferTo(outFile);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static String getLastFilePath(String orgName){
        String path;
        String suffix = orgName.substring(orgName.lastIndexOf(".") + 1);  // 获取文件后缀
        // 使用原名称_0，存在相同着加1
        String prefix = orgName.substring(0, orgName.lastIndexOf("."));  // 获取文件名称
        path = getDateDir() + prefix + "_0." + suffix;
        path = UPLOAD_FILE_DIR + path;
        File outFile = new File(path);
        int sameSize = 1;
        String existPath = path;
        while (outFile.exists()) {
            existPath = path;
            path = getDateDir() + prefix + "_" + sameSize + "." + suffix;
            path = UPLOAD_FILE_DIR + path;
            outFile = new File(path);
            sameSize++;
        }
        return existPath;
    }

    /**
     * 按照日期分存放目录
     */
    public static String getDateDir() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd/");
        return sdf.format(new Date());
    }


    /**
     * 下载源文件
     */
    public static void download(String path, HttpServletResponse response) {
        File file = new File(path);
        if (!file.exists()) {
            outNotFund(response);
            return;
        }
        setDownloadHeader(response, file.getName());
        try {
            output(file, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出文件流
     */
    public static void output(File file, OutputStream os) {
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = new byte[1024 * 256];
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     */
    public static boolean delete(String path) {
        File file = new File(UPLOAD_FILE_DIR, path);
        if (file.delete()) {
            return new File(UPLOAD_SM_DIR, path).delete();
        }
        return false;
    }

    /**
     * 获取文件列表
     */
    public static List<Map<String, Object>> list(String dir) {
        dir = dir == null ? "" : dir;
        List<Map<String, Object>> list = new ArrayList<>();
        File file = new File(UPLOAD_FILE_DIR + dir);
        File[] files = file.listFiles();
        if (files == null) return list;
        for (File f : files) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", f.getName());
            map.put("size", f.length());
            map.put("isDir", f.isDirectory());
            if (!f.isDirectory()) {
                map.put("url", dir + "/" + f.getName());
                map.put("smUrl", "thumbnail" + dir + "/" + f.getName());
            }
            map.put("updateTime", f.lastModified());
            list.add(map);
        }
        return list;
    }

    /**
     * 设置下载文件的header
     */
    public static void setDownloadHeader(HttpServletResponse response, String fileName) {
        response.setContentType("application/force-download");
        try {
            fileName = URLEncoder.encode(fileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
    }

    /**
     * 输出文件不存在
     */
    public static void outNotFund(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        outMessage("404 Not Found", response);
    }

    /**
     * 输出错误信息
     */
    public static void outMessage(String message, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write("<!doctype html>");
            writer.write("<title>" + message + "</title>");
            writer.write("<h1 style=\"text-align: center\">" + message + "</h1>");
            writer.write("<hr/><p style=\"text-align: center\">tansun File Server</p>");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
