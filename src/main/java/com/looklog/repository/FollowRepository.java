// FollowRepository.java
package com.looklog.repository;

import com.looklog.entity.Follow;
import com.looklog.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
  long countByFollowingId(Long followingId);
  long countByFollowerId(Long followerId);
  List<Follow> findByFollowingId(Long followingId);
  List<Follow> findByFollowerId(Long followerId);
}