package com.ams.modules.report.controller;

import java.io.IOException;
import java.time.LocalDate;
import com.ams.common.util.JsonUtil;
import com.ams.modules.report.dto.ReportFilterRequest;
import com.ams.modules.report.repository.impl.ReportRepositoryImpl;
import com.ams.modules.report.service.ReportService;
import com.ams.modules.report.service.impl.ReportServiceImpl;
import com.ams.modules.report.validator.ReportValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/v1/reports/summary")
public class ReportController extends HttpServlet {
    private static final long serialVersionUID=1L; private ReportService service;
    @Override public void init()throws ServletException{service=new ReportServiceImpl(new ReportRepositoryImpl(),new ReportValidator());}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{
        try{ReportFilterRequest f=new ReportFilterRequest();String start=req.getParameter("startDate"),end=req.getParameter("endDate");if(start!=null&&!start.isBlank())f.setStartDate(LocalDate.parse(start));if(end!=null&&!end.isBlank())f.setEndDate(LocalDate.parse(end));write(resp,service.generateSummaryReport(f),200);}
        catch(Exception e){resp.setContentType("application/json");resp.setStatus(400);resp.getWriter().write(JsonUtil.response("ERROR",e.getMessage()));}
    }
    private void write(HttpServletResponse r,Object o,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.toJson(o));}
}
