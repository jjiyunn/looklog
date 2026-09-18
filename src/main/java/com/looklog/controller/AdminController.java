package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.service.BoardService;
import com.looklog.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

  private final ReportService reportService;
  private final MemberRepository memberRepository;
  private final BoardService boardService;

  // 관리자 권한 체크 공통 메서드
  private boolean isAdmin(HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) return false;

    Member member = memberRepository.findById(loginMemberId).orElse(null);
    return member != null && "ADMIN".equals(member.getRole());
  }

  @GetMapping("/admin/reports")
  public String reportList(@RequestParam(required = false) String targetType,
                           @RequestParam(required = false) String sort,
                           @RequestParam(required = false) String rTargetType,
                           @RequestParam(required = false) String rSort,
                           @RequestParam(required = false, defaultValue = "0") int p,
                           @RequestParam(required = false, defaultValue = "0") int rp,
                           HttpSession session, Model model) {
    if (!isAdmin(session)) {
      return "redirect:/";
    }

    model.addAttribute("reports", reportService.getReports("PENDING", targetType, sort, p));
    model.addAttribute("targetType", targetType == null ? "ALL" : targetType);
    model.addAttribute("sort", sort == null ? "desc" : sort);
    model.addAttribute("pendingCounts", reportService.getCounts("PENDING"));
    model.addAttribute("p", p);

    model.addAttribute("resolvedReports", reportService.getReports("RESOLVED", rTargetType, rSort, rp));
    model.addAttribute("rTargetType", rTargetType == null ? "ALL" : rTargetType);
    model.addAttribute("rSort", rSort == null ? "desc" : rSort);
    model.addAttribute("resolvedCounts", reportService.getCounts("RESOLVED"));
    model.addAttribute("rp", rp);

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


  // 프로필 보러가기
  @GetMapping("/admin/member/{id}/profile")
  public String goToMemberProfile(@PathVariable Long id, HttpSession session) {
    if (!isAdmin(session)) {
      return "redirect:/";
    }

    Member member = memberRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("회원을 찾을 수 없습니다."));

    return "redirect:/profile/" + member.getUserName();
  }

  // 피드 삭제
  @PostMapping("/admin/reports/{id}/delete-board")
  public String deleteReportedBoard(@PathVariable Long id, HttpSession session) {
    if (!isAdmin(session)) {
      return "redirect:/";
    }

    reportService.deleteReportedBoard(id);
    return "redirect:/admin/reports";
  }
}