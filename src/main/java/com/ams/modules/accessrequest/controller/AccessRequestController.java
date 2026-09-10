package com.ams.modules.accessrequest.controller;

import java.io.IOException;
import com.ams.common.util.JsonUtil;
import com.ams.modules.accessrequest.dto.AccessRequestRequest;
import com.ams.modules.accessrequest.repository.impl.AccessRequestRepositoryImpl;
import com.ams.modules.accessrequest.service.*;
import com.ams.modules.accessrequest.service.impl.AccessRequestServiceImpl;
import com.ams.modules.accessrequest.validator.AccessRequestValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/v1/access-requests/*")
public class AccessRequestController extends HttpServlet {
    private static final long serialVersionUID=1L; private AccessRequestService service;
    @Override public void init()throws ServletException{service=new AccessRequestServiceImpl(new AccessRequestRepositoryImpl(),new AccessRequestValidator());}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{String p=req.getPathInfo();if(p==null||"/".equals(p))write(resp,service.getAllRequests(),200);else if(p.startsWith("/user/"))write(resp,service.getRequestsByUserId(Long.valueOf(p.substring(6))),200);else write(resp,service.getRequestById(Long.valueOf(p.substring(1))),200);}catch(Exception e){error(resp,e,404);}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{AccessRequestRequest r=new AccessRequestRequest();r.setUserId(Long.valueOf(req.getParameter("userId")));r.setRequestType(req.getParameter("requestType"));r.setRequestReason(req.getParameter("requestReason"));write(resp,service.createRequest(r),201);}catch(Exception e){error(resp,e,400);}}
    @Override protected void doPut(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{Long id=requiredId(req);String status=req.getParameter("status");if(!service.updateRequestStatus(id,status)){error(resp,new RuntimeException("Access request not found"),404);return;}write(resp,JsonUtil.response("SUCCESS","Access request status updated"),200);}catch(Exception e){error(resp,e,400);}}
    private Long requiredId(HttpServletRequest req){String p=req.getPathInfo();try{return Long.valueOf(req.getParameter("id")!=null?req.getParameter("id"):p.substring(1));}catch(Exception e){throw new IllegalArgumentException("Request id is required");}}
    private void write(HttpServletResponse r,Object o,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.toJson(o));}
    private void error(HttpServletResponse r,Exception e,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.response("ERROR",e.getMessage()));}
}
