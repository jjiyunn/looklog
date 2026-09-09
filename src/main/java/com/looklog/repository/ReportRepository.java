package com.looklog.repository;

import com.looklog.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
  List<Report> findByStatusOrderByRegDateDesc(String status);
}