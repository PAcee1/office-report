package com.tansun.risk.service;

import javax.servlet.http.HttpServletResponse;

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
