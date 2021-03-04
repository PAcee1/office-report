package com.tansun.risk.controller;

import com.tansun.risk.service.ConvertService;
import com.tansun.risk.service.ProcessService;
import com.tansun.risk.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * <p>数据处理Controller
 *      1.读取模板
 *      2.处理数据
 *      3.模板数据组合输出最终Excel
 * </p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 16:34
 **/
@RestController
@RequestMapping("/download")
public class DownloadController {

    private final Logger log = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    private ProcessService processService;
    @Autowired
    private ConvertService service;

    @GetMapping("/excel")
    public void excelDownload(HttpServletResponse response){
        // 判断已处理文件是否存在
        String orgName = "a.xlsx";
        String filePath = FileUtil.getLastFilePath(orgName);
        log.info("下载Excel地址：" + filePath);
        FileUtil.download(filePath,response);
    }

    @GetMapping("/pdf")
    public void pdfDownload(HttpServletResponse response){
        // 判断已处理文件是否存在
        String orgName = "a.xlsx";
        String filePath = FileUtil.getLastFilePath(orgName);
        File file = new File(filePath);
        // 转换成pdf
        String pdfPath = filePath.substring(0,filePath.lastIndexOf(".") + 1) + "pdf";
        log.info("下载Pdf地址：" + pdfPath);
        service.convertExcelToPDFByFitColumn(file,new File(pdfPath));
        // 输出pdfPath
        FileUtil.download(pdfPath,response);

        //processService.wideDeal("Test",response);
    }

}
