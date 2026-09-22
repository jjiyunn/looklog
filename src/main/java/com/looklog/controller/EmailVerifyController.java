package com.looklog.controller;

import com.looklog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class EmailVerifyController {

  private final MemberService memberService;

  @GetMapping("/email-verify")
  public String emailVerifyPage(HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return "redirect:/login";
    }
    Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
    if (Boolean.TRUE.equals(emailVerified)) {
      return "redirect:/looklog";
    }
    return "emailVerify";
  }

  @PostMapping("/email-verify")
  @ResponseBody
  public ResponseEntity<?> verify(@RequestParam String code, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.badRequest().body("로그인이 필요합니다.");
    }
    try {
      memberService.verifyEmail(loginMemberId, code);
      session.setAttribute("emailVerified", true);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/email-verify/resend")
  @ResponseBody
  public ResponseEntity<?> resend(HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.badRequest().body("로그인이 필요합니다.");
    }
    try {
      memberService.resendVerificationCode(loginMemberId);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}