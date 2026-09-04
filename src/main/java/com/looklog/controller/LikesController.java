package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.service.LikesService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;
    private final MemberRepository memberRepository;

    @PostMapping("/like/{boardId}")
    public ResponseEntity<?> toggleLike(@PathVariable Long boardId, HttpSession session) {

        Long loginMemberId = (Long) session.getAttribute("loginMemberId");
        if (loginMemberId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));

        boolean liked = likesService.toggleLike(member, boardId);

        return ResponseEntity.ok(liked);
    }
}