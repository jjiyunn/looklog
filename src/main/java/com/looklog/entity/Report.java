package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id", nullable = false)
  private Member reporter;

  @Column(name = "target_type", nullable = false, length = 20)
  private String targetType;   // "BOARD" or "MEMBER"

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Column(nullable = false, length = 50)
  private String reason;

  @Column(length = 500)
  private String detail;

  @Column(nullable = false, length = 20)
  private String status = "PENDING";

  @Column(name = "reg_date", updatable = false)
  private LocalDateTime regDate;

  @PrePersist
  protected void onCreate() {
    this.regDate = LocalDateTime.now();
  }

  public Report(Member reporter, String targetType, Long targetId, String reason, String detail) {
    this.reporter = reporter;
    this.targetType = targetType;
    this.targetId = targetId;
    this.reason = reason;
    this.detail = detail;
  }
}