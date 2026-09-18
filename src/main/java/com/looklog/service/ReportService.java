package com.looklog.service;

import com.looklog.entity.Member;
import com.looklog.entity.Report;
import com.looklog.repository.MemberRepository;
import com.looklog.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;
  private final BoardService boardService;

  public void createReport(Long reporterId, String targetType, Long targetId, String reason, String detail) {
    Member reporter = memberRepository.findById(reporterId)
            .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

    if (!"BOARD".equals(targetType) && !"MEMBER".equals(targetType)) {
      throw new IllegalStateException("잘못된 신고 대상입니다.");
    }

    Report report = new Report(reporter, targetType, targetId, reason, detail);
    reportRepository.save(report);
  }

  // 대기중 신고 목록
  private static final int PAGE_SIZE = 5;

  public Page<Report> getReports(String status, String targetType, String sort, int page) {
    boolean asc = "asc".equals(sort);
    Pageable pageable = PageRequest.of(page, PAGE_SIZE);

    if (targetType == null || targetType.isBlank() || "ALL".equals(targetType)) {
      return asc
              ? reportRepository.findByStatusOrderByRegDateAsc(status, pageable)
              : reportRepository.findByStatusOrderByRegDateDesc(status, pageable);
    }

    return asc
            ? reportRepository.findByStatusAndTargetTypeOrderByRegDateAsc(status, targetType, pageable)
            : reportRepository.findByStatusAndTargetTypeOrderByRegDateDesc(status, targetType, pageable);
  }

  // 상태별 대상 개수
  public Map<String, Long> getCounts(String status) {
    Map<String, Long> counts = new HashMap<>();
    counts.put("ALL", reportRepository.countByStatus(status));
    counts.put("MEMBER", reportRepository.countByStatusAndTargetType(status, "MEMBER"));
    counts.put("BOARD", reportRepository.countByStatusAndTargetType(status, "BOARD"));
    return counts;
  }


  // 처리완료로 상태 변경
  public void resolveReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));
    report.setStatus("RESOLVED");
  }


  // 신고된 게시물 삭제
  public void deleteReportedBoard(Long reportId) {
    Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));

    if (!"BOARD".equals(report.getTargetType())) {
      throw new IllegalStateException("게시물 신고가 아닙니다.");
    }

    boardService.deleteBoardByAdmin(report.getTargetId());
    report.setStatus("RESOLVED");
  }

}