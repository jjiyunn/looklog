package com.looklog.controller;

import com.looklog.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  @PostMapping("/report")
  public ResponseEntity<?> report(
          @RequestParam String targetType,
          @RequestParam Long targetId,
          @RequestParam String reason,
          @RequestParam(required = false) String detail,
          HttpSession session
  ) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }

    try {
      reportService.createReport(loginMemberId, targetType, targetId, reason, detail);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}