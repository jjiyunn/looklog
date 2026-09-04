// FollowRepository.java
package com.looklog.repository;

import com.looklog.entity.Follow;
import com.looklog.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
  long countByFollowingId(Long followingId);
  long countByFollowerId(Long followerId);
}