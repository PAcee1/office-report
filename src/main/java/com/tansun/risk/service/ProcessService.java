package com.tansun.risk.service;

import com.tansun.risk.utils.JsonResult;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * <p></p>
 *
 * @author : Pacee1
 * @date : 2021-01-13 15:49
 **/
public interface ProcessService {

    String wideDeal(String reportName, HttpServletResponse response);

    String narrowDeal(String reportName,HttpServletResponse response);

}
