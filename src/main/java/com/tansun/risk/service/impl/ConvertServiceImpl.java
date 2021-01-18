package com.tansun.risk.service.impl;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tansun.risk.service.ConvertService;
import com.tansun.risk.utils.CodeUtils;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.core.DocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>文件格式转换</p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 16:27
 **/
@Service
public class ConvertServiceImpl implements ConvertService {
    private final Logger log = LoggerFactory.getLogger(ConvertService.class);
    /**
     * 转换器注入
     */
    @Autowired
    private DocumentConverter converter;

    @Override
    public String convert(File sourceFile, File targetFile,Boolean htmlCode) {
        try {
            //convertExcelToPDFByFitColumn(sourceFile,targetFile);
            converter.convert(sourceFile).to(targetFile).execute();
            // 转换编码格式，针对Html
            //String isHtml = targetFile.getName().substring(targetFile.getName().lastIndexOf(".") + 1);
            String result = null;
            if(htmlCode){
                result = CodeUtils.doActionConvertedFile(targetFile.getPath());
            }else {
                result = targetFile.getPath();
            }
            return result;
        } catch (Exception e) {
            log.error("转换office文档失败", e);
        }
        return "error";
    }

    @Override
    public String convertExcelToPDFByFitColumn(File sourceFile, File targetFile) {
        String uuid = UUID.randomUUID().toString();
        File tempExcel = new File(sourceFile.getParentFile(), uuid.concat("_").concat(sourceFile.getName()));
        try {
            setExcelPrintParameter(sourceFile, tempExcel);
            String result = this.convert(tempExcel, targetFile,false);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tempExcel.exists() && !tempExcel.delete())
                log.warn("删除临时文件失败 file={}", tempExcel.getPath());
        }
        return "error";
    }

    /**
     * 设置Excel打印参数
     *
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     * @throws InvalidFormatException
     */
    private void setExcelPrintParameter(File sourceFile, File targetFile) throws Exception {
        Workbook workbook = new XSSFWorkbook(sourceFile);
        for (int i = 0, j = workbook.getNumberOfSheets(); i < j; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
            // FitHeight=1, 将所有行都缩放显示在一页上（设置1表示一页显示完，如果设置2表示分2页显示完）
            // FitWidth=1, 将所有列都缩放显示在一页上
            // 两个都等于1时，如果行数太多则会挤压列，一般来说只设置一个FitWidth=1，让行数自动换页
            // 要使这两个参数有效，则需要设置FitToPage=true
            sheet.setFitToPage(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
//          sheet.getPrintSetup().setFitHeight((short)1);
            // 是否显示自动换页符
            sheet.setAutobreaks(true);
        }
        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            workbook.write(out);
            workbook.close();
        }
    }
}
