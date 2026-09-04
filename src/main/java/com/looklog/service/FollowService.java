package com.looklog.service;

import com.looklog.entity.Follow;
import com.looklog.entity.FollowId;
import com.looklog.entity.Member;
import com.looklog.repository.FollowRepository;
import com.looklog.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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


  // 팔로워 목록 (나를 팔로우하는 사람들)
  public List<FollowUserDto> getFollowers(Long targetMemberId, Long loginMemberId) {
    List<Follow> follows = followRepository.findByFollowingId(targetMemberId);
    return follows.stream()
            .map(f -> toDto(f.getFollower(), loginMemberId))
            .toList();
  }

  // 팔로잉 목록 (내가 팔로우하는 사람들)
  public List<FollowUserDto> getFollowings(Long targetMemberId, Long loginMemberId) {
    List<Follow> follows = followRepository.findByFollowerId(targetMemberId);
    return follows.stream()
            .map(f -> toDto(f.getFollowing(), loginMemberId))
            .toList();
  }

  private FollowUserDto toDto(Member member, Long loginMemberId) {
    boolean following = loginMemberId != null
            && isFollowing(loginMemberId, member.getId());
    boolean me = loginMemberId != null && loginMemberId.equals(member.getId());
    return new FollowUserDto(member.getId(), member.getUserName(), member.getProfileImg(), following, me);
  }

  @Getter
  public static class FollowUserDto {
    private final Long id;
    private final String userName;
    private final String profileImg;
    private final boolean following;
    private final boolean me;

    public FollowUserDto(Long id, String userName, String profileImg, boolean following, boolean me) {
      this.id = id;
      this.userName = userName;
      this.profileImg = profileImg;
      this.following = following;
      this.me = me;
    }
  }

}