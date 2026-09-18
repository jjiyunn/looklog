package com.looklog.repository;

import com.looklog.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface ReportRepository extends JpaRepository<Report, Long> {

  Page<Report> findByStatusOrderByRegDateDesc(String status, Pageable pageable);
  Page<Report> findByStatusOrderByRegDateAsc(String status, Pageable pageable);
  Page<Report> findByStatusAndTargetTypeOrderByRegDateDesc(String status, String targetType, Pageable pageable);
  Page<Report> findByStatusAndTargetTypeOrderByRegDateAsc(String status, String targetType, Pageable pageable);

  long countByStatus(String status);
  long countByStatusAndTargetType(String status, String targetType);
}