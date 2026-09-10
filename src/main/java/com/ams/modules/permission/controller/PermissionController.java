package com.ams.modules.permission.controller;

import java.io.IOException;
import com.ams.common.util.JsonUtil;
import com.ams.modules.permission.dto.*;
import com.ams.modules.permission.repository.impl.PermissionRepositoryImpl;
import com.ams.modules.permission.service.*;
import com.ams.modules.permission.service.impl.PermissionServiceImpl;
import com.ams.modules.permission.validator.PermissionValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/v1/permissions/*")
public class PermissionController extends HttpServlet {
    private static final long serialVersionUID=1L; private PermissionService service;
    @Override public void init()throws ServletException{service=new PermissionServiceImpl(new PermissionRepositoryImpl(),new PermissionValidator());}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{String p=req.getPathInfo();write(resp,p==null||"/".equals(p)?service.getAllPermissions():service.getPermissionById(Long.valueOf(p.substring(1))),200);}catch(Exception e){error(resp,e,404);}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{CreatePermissionRequest r=new CreatePermissionRequest();r.setPermissionName(req.getParameter("permissionName"));r.setDescription(req.getParameter("description"));write(resp,service.createPermission(r),201);}catch(Exception e){error(resp,e,400);}}
    @Override protected void doPut(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{UpdatePermissionRequest r=new UpdatePermissionRequest();r.setPermissionName(req.getParameter("permissionName"));r.setDescription(req.getParameter("description"));if(!service.updatePermission(requiredId(req),r)){error(resp,new RuntimeException("Permission not found"),404);return;}write(resp,JsonUtil.response("SUCCESS","Permission updated successfully"),200);}catch(Exception e){error(resp,e,400);}}
    @Override protected void doDelete(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{if(!service.deletePermission(requiredId(req))){error(resp,new RuntimeException("Permission not found"),404);return;}write(resp,JsonUtil.response("SUCCESS","Permission deleted successfully"),200);}catch(Exception e){error(resp,e,400);}}
    private Long requiredId(HttpServletRequest req){String p=req.getPathInfo();try{return Long.valueOf(p.substring(1));}catch(Exception e){throw new IllegalArgumentException("Permission id is required");}}
    private void write(HttpServletResponse r,Object o,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.toJson(o));}
    private void error(HttpServletResponse r,Exception e,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.response("ERROR",e.getMessage()));}
}
