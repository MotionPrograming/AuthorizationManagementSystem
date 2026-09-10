package com.ams.modules.role.controller;

import java.io.IOException;
import com.ams.common.util.JsonUtil;
import com.ams.modules.role.dto.*;
import com.ams.modules.role.repository.impl.RoleRepositoryImpl;
import com.ams.modules.role.service.*;
import com.ams.modules.role.service.impl.RoleServiceImpl;
import com.ams.modules.role.validator.RoleValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/api/v1/roles/*")
public class RoleController extends HttpServlet {
    private static final long serialVersionUID=1L; private RoleService service;
    @Override public void init() throws ServletException{service=new RoleServiceImpl(new RoleRepositoryImpl(),new RoleValidator());}
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{String p=req.getPathInfo();write(resp,p==null||"/".equals(p)?service.getAllRoles():service.getRoleById(Long.valueOf(p.substring(1))),200);}catch(Exception e){error(resp,e,404);}}
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{if("/assign-permission".equals(req.getPathInfo())){service.assignPermissionToRole(Long.valueOf(req.getParameter("roleId")),Long.valueOf(req.getParameter("permissionId")));write(resp,JsonUtil.response("SUCCESS","Permission assigned successfully"),200);return;}CreateRoleRequest r=new CreateRoleRequest();r.setRoleName(req.getParameter("roleName"));r.setDescription(req.getParameter("description"));write(resp,service.createRole(r),201);}catch(Exception e){error(resp,e,400);}}
    @Override protected void doPut(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{UpdateRoleRequest r=new UpdateRoleRequest();r.setRoleName(req.getParameter("roleName"));r.setDescription(req.getParameter("description"));if(!service.updateRole(requiredId(req),r)){error(resp,new RuntimeException("Role not found"),404);return;}write(resp,JsonUtil.response("SUCCESS","Role updated successfully"),200);}catch(Exception e){error(resp,e,400);}}
    @Override protected void doDelete(HttpServletRequest req,HttpServletResponse resp)throws IOException{try{if(!service.deleteRole(requiredId(req))){error(resp,new RuntimeException("Role not found"),404);return;}write(resp,JsonUtil.response("SUCCESS","Role deleted successfully"),200);}catch(Exception e){error(resp,e,400);}}
    private Long requiredId(HttpServletRequest req){String p=req.getPathInfo();try{return Long.valueOf(p.substring(1));}catch(Exception e){throw new IllegalArgumentException("Role id is required");}}
    private void write(HttpServletResponse r,Object o,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.toJson(o));}
    private void error(HttpServletResponse r,Exception e,int s)throws IOException{r.setContentType("application/json");r.setCharacterEncoding("UTF-8");r.setStatus(s);r.getWriter().write(JsonUtil.response("ERROR",e.getMessage()));}
}
