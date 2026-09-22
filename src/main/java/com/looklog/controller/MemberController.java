package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //회원가입
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> signUp(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam String userName,
            HttpSession session
    ) {
        try {
            Member member = memberService.signUp(email, password, name, userName);

            // 회원가입 성공 -> 바로 세션에 로그인 정보 저장 (자동 로그인)
            session.setAttribute("loginMemberId", member.getId());
            session.setAttribute("loginMemberName", member.getUserName());
            session.setAttribute("loginMemberRole", member.getRole());
            session.setAttribute("emailVerified", member.isEmailVerified());

            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session
    ) {
        try {
            Member member = memberService.login(email, password);

            session.setAttribute("loginMemberId", member.getId());
            session.setAttribute("loginMemberName", member.getUserName());
            session.setAttribute("loginMemberRole", member.getRole());
            session.setAttribute("emailVerified", member.isEmailVerified());

            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //로그아웃
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();   // 세션 통째로 무효화(삭제)
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @PostMapping("/profile/withdraw")
    @ResponseBody
    public ResponseEntity<?> withdraw(@RequestParam String password, HttpSession session) {
        try {
            Long memberId = (Long) session.getAttribute("loginMemberId");
            memberService.withdraw(memberId, password);
            session.invalidate(); // 강제 로그아웃
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    // 프로필 비공개
    @PatchMapping("/member/profile-visibility")
    @ResponseBody
    public ResponseEntity<?> toggleProfileVisibility(@RequestBody Map<String, Boolean> body, HttpSession session) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");
        if (loginMemberId == null) return ResponseEntity.status(401).build();

        memberService.updateProfileVisibility(loginMemberId, body.get("profilePublic"));
        return ResponseEntity.ok().build();
    }

}