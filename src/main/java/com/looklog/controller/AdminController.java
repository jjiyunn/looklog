package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

  private final ReportService reportService;
  private final MemberRepository memberRepository;

  // 관리자 권한 체크 공통 메서드
  private boolean isAdmin(HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) return false;

    Member member = memberRepository.findById(loginMemberId).orElse(null);
    return member != null && "ADMIN".equals(member.getRole());
  }

  @GetMapping("/admin/reports")
  public String reportList(HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/";
    }

    model.addAttribute("reports", reportService.getPendingReports());
    return "admin-reports";
  }

  @PostMapping("/admin/reports/{id}/resolve")
  public String resolveReport(@PathVariable Long id, HttpSession session) {
    if (!isAdmin(session)) {
      return "redirect:/";
    }

    reportService.resolveReport(id);
    return "redirect:/admin/reports";
  }
}