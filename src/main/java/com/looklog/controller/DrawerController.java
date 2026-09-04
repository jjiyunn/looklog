package com.looklog.controller;

import com.looklog.entity.Drawer;
import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.service.DrawerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DrawerController {

  private final DrawerService drawerService;
  private final MemberRepository memberRepository;

  // 모달에 보여줄 서랍 목록
  @GetMapping("/drawer/list")
  public ResponseEntity<?> list(@RequestParam Long boardId, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }
    return ResponseEntity.ok(drawerService.getDrawersForModal(loginMemberId, boardId));
  }

  @GetMapping("/drawer/saved")
  public ResponseEntity<?> getSavedDrawerId(@RequestParam Long boardId, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }
    Long drawerId = drawerService.findSavedDrawerId(boardId, loginMemberId);
    return ResponseEntity.ok(drawerId);
  }

  // 새 서랍 만들기
  @PostMapping("/drawer")
  public ResponseEntity<?> create(@RequestBody Map<String, String> body, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }

    Member member = memberRepository.findById(loginMemberId)
            .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

    Drawer drawer = drawerService.createDrawer(member, body.get("name"));
    return ResponseEntity.ok(drawer.getId());
  }

  // 서랍 수정
  @PatchMapping("/drawer/{drawerId}")
  public ResponseEntity<?> rename(@PathVariable Long drawerId, @RequestBody Map<String, String> body, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }

    try {
      drawerService.renameDrawer(drawerId, loginMemberId, body.get("name"));
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // 서랍 삭제
  @DeleteMapping("/drawer/{drawerId}")
  public ResponseEntity<?> delete(@PathVariable Long drawerId, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }

    try {
      drawerService.deleteDrawer(drawerId, loginMemberId);
      return ResponseEntity.ok().build();
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  // 서랍에 게시글 저장/해제 토글
  @PostMapping("/drawer/{drawerId}/board/{boardId}")
  public ResponseEntity<?> toggle(@PathVariable Long drawerId, @PathVariable Long boardId, HttpSession session) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return ResponseEntity.status(401).body("로그인이 필요합니다.");
    }

    try {
      boolean saved = drawerService.toggleItem(drawerId, boardId, loginMemberId);
      return ResponseEntity.ok(saved);
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}