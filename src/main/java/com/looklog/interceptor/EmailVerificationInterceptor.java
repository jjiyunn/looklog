package com.looklog.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class EmailVerificationInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    HttpSession session = request.getSession(false);

    if (session == null) {
      return true; // 비로그인 상태는 다른 필터/로직에서 처리
    }

    Object emailVerified = session.getAttribute("emailVerified");

    if (Boolean.FALSE.equals(emailVerified)) {
      response.sendRedirect("/email-verify");
      return false;
    }

    return true;
  }
}