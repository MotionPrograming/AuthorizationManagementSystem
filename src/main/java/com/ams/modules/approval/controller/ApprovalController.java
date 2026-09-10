package com.ams.modules.approval.controller;

import java.io.IOException;
import com.ams.common.util.JsonUtil;
import com.ams.modules.approval.dto.CreateApprovalRequest;
import com.ams.modules.approval.repository.impl.ApprovalRepositoryImpl;
import com.ams.modules.approval.service.*;
import com.ams.modules.approval.service.impl.ApprovalServiceImpl;
import com.ams.modules.approval.validator.ApprovalValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/v1/approvals/*")
public class ApprovalController extends HttpServlet {
    private static final long serialVersionUID=1L; private ApprovalService service;
    @Override public void init()throws ServletException{service=new ApprovalServiceImpl(new ApprovalRepositoryImpl(),new ApprovalValidator());}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{String p=req.getPathInfo();if(p==null||"/".equals(p))write(resp,service.getAllApprovals(),200);else if(p.startsWith("/request/"))write(resp,service.getApprovalByRequestId(Long.valueOf(p.substring(9))),200);else if(p.startsWith("/approver/"))write(resp,service.getApprovalsByApproverId(Long.valueOf(p.substring(10))),200);else write(resp,service.getApprovalById(Long.valueOf(p.substring(1))),200);}catch(Exception e){error(resp,e,404);}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{CreateApprovalRequest r=new CreateApprovalRequest();r.setRequestId(Long.valueOf(req.getParameter("requestId")));r.setApproverId(Long.valueOf(req.getParameter("approverId")));r.setDecision(req.getParameter("decision"));r.setComments(req.getParameter("comments"));write(resp,service.processApproval(r),201);}catch(Exception e){error(resp,e,400);}}
    private void write(HttpServletResponse r,Object o,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.toJson(o));}
    private void error(HttpServletResponse r,Exception e,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.response("ERROR",e.getMessage()));}
}
