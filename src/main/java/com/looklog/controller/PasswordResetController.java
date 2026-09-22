package com.looklog.controller;

import com.looklog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

  private final MemberService memberService;

  @GetMapping("/password-reset")
  public String passwordResetPage() {
    return "passwordReset";
  }

  @PostMapping("/password-reset/request")
  @ResponseBody
  public ResponseEntity<?> requestReset(@RequestParam String email) {
    try {
      memberService.requestPasswordReset(email);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/password-reset/verify")
  @ResponseBody
  public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code) {
    try {
      memberService.verifyPasswordResetCode(email, code);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/password-reset/reset")
  @ResponseBody
  public ResponseEntity<?> resetPassword(
          @RequestParam String email,
          @RequestParam String code,
          @RequestParam String newPassword
  ) {
    try {
      memberService.resetPassword(email, code, newPassword);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}