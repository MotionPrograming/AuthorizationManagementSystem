package com.ams.modules.audit.controller;

import java.io.IOException;
import com.ams.common.util.JsonUtil;
import com.ams.modules.audit.dto.CreateAuditLogRequest;
import com.ams.modules.audit.repository.impl.AuditRepositoryImpl;
import com.ams.modules.audit.service.*;
import com.ams.modules.audit.service.impl.AuditServiceImpl;
import com.ams.modules.audit.validator.AuditValidator;
import com.ams.security.authentication.SecurityContextHolder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/v1/audits/*")
public class AuditController extends HttpServlet {
    private static final long serialVersionUID=1L; private AuditService service;
    @Override public void init()throws ServletException{service=new AuditServiceImpl(new AuditRepositoryImpl(),new AuditValidator());}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{String p=req.getPathInfo();if(p==null||"/".equals(p))write(resp,service.getAllAudits(),200);else if(p.startsWith("/user/"))write(resp,service.getAuditsByUserId(Long.valueOf(p.substring(6))),200);else write(resp,service.getAuditById(Long.valueOf(p.substring(1))),200);}catch(Exception e){error(resp,e,404);}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{var user=SecurityContextHolder.getContext();if(user==null){error(resp,new RuntimeException("Unauthorized"),401);return;}CreateAuditLogRequest r=new CreateAuditLogRequest();r.setUserId(user.getUserId());r.setAction(req.getParameter("action"));r.setDescription(req.getParameter("description"));r.setIpAddress(req.getRemoteAddr());write(resp,service.logAction(r),201);}catch(Exception e){error(resp,e,400);}}
    private void write(HttpServletResponse r,Object o,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.toJson(o));}
    private void error(HttpServletResponse r,Exception e,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.response("ERROR",e.getMessage()));}
}
