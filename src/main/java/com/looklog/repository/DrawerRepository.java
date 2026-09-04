package com.looklog.repository;

import com.looklog.entity.Drawer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawerRepository extends JpaRepository<Drawer, Long> {

  // 기본서랍이 항상 맨 위에 오도록 정렬
  List<Drawer> findByMember_IdOrderByIsDefaultDescRegDateAsc(Long memberId);
}