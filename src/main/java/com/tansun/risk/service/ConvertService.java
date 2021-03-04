package com.tansun.risk.service;

import java.io.File;

/**
 * <p></p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 15:49
 **/
public interface ConvertService {

    String convert(File sourceFile, File targetFile,Boolean htmlCode);

    String convertExcelToPDFByFitColumn(File sourceFile, File targetFile);

}
