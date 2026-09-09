package com.looklog.service;

import com.looklog.entity.Member;
import com.looklog.entity.Report;
import com.looklog.repository.MemberRepository;
import com.looklog.repository.ReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

  private final ReportRepository reportRepository;
  private final MemberRepository memberRepository;

  public void createReport(Long reporterId, String targetType, Long targetId, String reason, String detail) {
    Member reporter = memberRepository.findById(reporterId)
            .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

    if (!"BOARD".equals(targetType) && !"MEMBER".equals(targetType)) {
      throw new IllegalStateException("잘못된 신고 대상입니다.");
    }

    Report report = new Report(reporter, targetType, targetId, reason, detail);
    reportRepository.save(report);
  }

  // 대기중인 신고 목록
  public List<Report> getPendingReports() {
    return reportRepository.findByStatusOrderByRegDateDesc("PENDING");
  }

  // 처리완료로 상태 변경
  public void resolveReport(Long reportId) {
    Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalStateException("신고 내역을 찾을 수 없습니다."));
    report.setStatus("RESOLVED");
  }
}