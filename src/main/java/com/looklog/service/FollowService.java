package com.looklog.service;

import com.looklog.entity.Follow;
import com.looklog.entity.FollowId;
import com.looklog.entity.Member;
import com.looklog.repository.FollowRepository;
import com.looklog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowService {

  private final FollowRepository followRepository;
  private final MemberRepository memberRepository;

  public boolean isFollowing(Long followerId, Long followingId) {
    return followRepository.existsById(new FollowId(followerId, followingId));
  }

  public long countFollowers(Long memberId) {
    return followRepository.countByFollowingId(memberId);
  }

  public long countFollowings(Long memberId) {
    return followRepository.countByFollowerId(memberId);
  }

  // 팔로우 토글
  public boolean toggleFollow(Long followerId, Long followingId) {
    if (followerId.equals(followingId)) {
      throw new IllegalStateException("자기 자신은 팔로우할 수 없습니다.");
    }

    FollowId id = new FollowId(followerId, followingId);

    if (followRepository.existsById(id)) {
      followRepository.deleteById(id);
      return false;
    } else {
      Member follower = memberRepository.findById(followerId).orElseThrow();
      Member following = memberRepository.findById(followingId).orElseThrow();
      followRepository.save(new Follow(follower, following));
      return true;
    }
  }
}