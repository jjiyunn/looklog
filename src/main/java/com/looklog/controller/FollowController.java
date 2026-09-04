package com.looklog.controller;

import com.looklog.service.FollowService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

  private final FollowService followService;

  @PostMapping("/follow/{targetId}")
  public ResponseEntity<?> toggleFollow(@PathVariable Long targetId, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }

    try {
      boolean following = followService.toggleFollow(loginMemberId, targetId);
      return ResponseEntity.ok(following);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}