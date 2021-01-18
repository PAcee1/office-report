package com.tansun.risk.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tansun.risk.service.ConvertService;
import com.tansun.risk.service.ProcessService;
import com.tansun.risk.utils.FileUtil;
import com.tansun.risk.utils.JsonResult;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.core.DocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>文件格式转换</p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 16:27
 **/
@Service
public class ProcessServiceImpl implements ProcessService {

    private final Logger log = LoggerFactory.getLogger(ConvertService.class);

    @Override
    public String wideDeal(String reportPath,HttpServletResponse response) {
        // 1.根据report名称查库
        // 直接用本地的
        //String path = "D:\\temp\\a.xlsx";
        String orgName = reportPath.substring(reportPath.lastIndexOf("\\") + 1);
        // 2.查数据 模拟数据
        createDataMap();

        // 3.模板填充，导出Excel
        TemplateExportParams params = new TemplateExportParams(reportPath);
        Workbook workbook = ExcelExportUtil.exportExcel(params,createDataMap());
        String filePath = FileUtil.saveFile(workbook, orgName);
        return filePath;
    }

    @Override
    public String narrowDeal(String reportName,HttpServletResponse response) {
        return null;
    }

    private Map createDataMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("C3","111");
        map.put("C4","111");
        map.put("C6","111");
        map.put("C11","111");
        map.put("C12","111");
        map.put("C13","111");
        map.put("C14","111");
        map.put("D3","111");
        map.put("D4","111");
        map.put("D6","111");
        map.put("D11","111");
        map.put("D12","111");
        map.put("D13","111");
        map.put("D14","111");
        map.put("E3","111");
        map.put("E4","111");
        map.put("E6","111");
        map.put("E11","111");
        map.put("E12","111");
        map.put("E13","111");
        map.put("E14","111");

        map.put("C8","111");

        map.put("B16","111");
        map.put("B17","111");
        map.put("B18","111");
        map.put("B19","111");
        map.put("B20","111");
        map.put("B21","111");
        map.put("B22","111");
        map.put("C16","111");
        map.put("C17","111");
        map.put("C18","111");
        map.put("C19","111");
        map.put("C20","111");
        map.put("C21","111");
        map.put("C22","111");
        map.put("dataDt","qqq");
        map.put("tenantId","qqq");
        return map;
    }
}
