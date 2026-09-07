package com.looklog.controller;

import com.looklog.entity.Board;
import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;

    @PostMapping("/board/create")
    public ResponseEntity<?> createBoard(
            @RequestParam MultipartFile image,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String tags,   // 추가
            HttpSession session
    ) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");

        if (loginMemberId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            Member member = memberRepository.findById(loginMemberId)
                    .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

            boardService.createBoard(member, image, content, tags);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("게시글 등록에 실패했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/board/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Long id, HttpSession session) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");
        if (loginMemberId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            boardService.deleteBoard(id, loginMemberId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/board/{id}/edit")
    public ResponseEntity<?> updateBoard(
            @PathVariable Long id,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String tags,
            HttpSession session
    ) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");

        if (loginMemberId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        try {
            boardService.updateBoard(id, loginMemberId, content, tags, image);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("게시글 수정에 실패했습니다: " + e.getMessage());
        }
    }
}