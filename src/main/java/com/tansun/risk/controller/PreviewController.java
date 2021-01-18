package com.tansun.risk.controller;

import com.tansun.risk.service.ConvertService;
import com.tansun.risk.service.ProcessService;
import com.tansun.risk.utils.CodeUtils;
import com.tansun.risk.utils.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * <p></p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 13:49
 **/
@RestController
@RequestMapping("/preview")
public class PreviewController {

    private final Logger log = LoggerFactory.getLogger(PreviewController.class);

    @Autowired
    private ConvertService service;
    @Autowired
    private ProcessService processService;


    /**
     * 预览Excel
     * 1.读取Excel
     * 2.处理Excel
     * 3.保存Excel
     * 4.返回Excel路径
     * @return
     * @throws Exception
     */
    @GetMapping("getHtml")
    public void getHtml(HttpServletResponse response){
        // 先用测试Excel，TODO 从数据库中查xlsx
        String reportName = "D:\\temp\\a.xlsx";
        // 处理Excel，获取保存路径
        String filePath = processService.wideDeal(reportName, response);
        if(filePath.equals("error")){
            log.error("处理Excel失败");
            throw new RuntimeException("处理Excel失败");
            //return ResponseResult.errorMsg();
        }
        // 转换Excel为Html
        // 存放路径
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        String htmlPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        htmlPath = htmlPath + fileName + ".html";

        // 转换，获取转换后的html代码
        String resultHtml = service.convert(new File(filePath),new File(htmlPath),true);
        // 打印html代码到页面
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(resultHtml);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Excel转Html失败");
            throw new RuntimeException("Excel转Html失败");
            //return ResponseResult.errorMsg("Excel转Html失败");
        }
    }

}
