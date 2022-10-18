package org.zerock.shop.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        if("XMLHttpRequest".equals(request.getHeader("x-requested=with"))){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"unauthorized");
            //인증되지 않은 사용자가 ajax로 리소스를 요청할 경우 unauthorized 에러 발생
        } else {
            response.sendRedirect("/members/login");
            //나머지인 경우 로그인 페이지로 리다이렉트
        }

    }
}
