package com.ams.security.filter;

import java.io.IOException;
import java.util.Optional;

import com.ams.common.util.JsonUtil;
import com.ams.modules.auth.repository.impl.AuthRepositoryImpl;
import com.ams.modules.user.entity.User;
import com.ams.modules.user.mapper.UserMapper;
import com.ams.modules.user.repository.UserRepository;
import com.ams.modules.user.repository.impl.UserRepositoryImpl;
import com.ams.security.authentication.SecurityContextHolder;
import com.ams.security.crypto.JwtTokenProvider;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/api/v1/*")
public class AuthenticationFilter implements Filter {
    private final UserRepository users = new UserRepositoryImpl();
    private final AuthRepositoryImpl sessions = new AuthRepositoryImpl();

    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest)request; HttpServletResponse resp=(HttpServletResponse)response;
        if (isPublic(req)) { chain.doFilter(request,response); return; }
        String token=extractToken(req.getHeader("Authorization"));
        if (token==null || !JwtTokenProvider.validateToken(token)) { unauthorized(resp,"Missing or invalid authentication token"); return; }
        Optional<com.ams.modules.auth.entity.Session> session=sessions.findSessionByToken(token);
        if(session.isEmpty()){unauthorized(resp,"Session is invalid, expired, or logged out");return;}
        String username=JwtTokenProvider.getUsernameFromToken(token);
        Optional<User> user=users.findByUsername(username);
        if(user.isEmpty() || !session.get().getUserId().equals(user.get().getUserId()) || !"ACTIVE".equalsIgnoreCase(user.get().getStatus())){unauthorized(resp,"Authenticated user is not active");return;}
        try { SecurityContextHolder.setContext(UserMapper.toUserResponse(user.get())); chain.doFilter(request,response); }
        finally { SecurityContextHolder.clearContext(); }
    }
    private boolean isPublic(HttpServletRequest req){String p=req.getRequestURI(); return p.endsWith("/auth/login")||p.endsWith("/auth/register")||p.endsWith("/auth/2fa/verify")||p.endsWith("/auth/2fa-login")||p.endsWith("/auth/password/forgot")||p.endsWith("/auth/password/reset");}
    private String extractToken(String h){if(h==null||!h.startsWith("Bearer "))return null;String t=h.substring(7).trim();return t.isEmpty()?null:t;}
    private void unauthorized(HttpServletResponse r,String msg)throws IOException{r.setStatus(401);r.setContentType("application/json");r.getWriter().write(JsonUtil.response("ERROR",msg));}
}
