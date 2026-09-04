package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
            HttpSession session   // 추가
    ) {
        try {
            Member member = memberService.signUp(email, password, name, userName);

            // 회원가입 성공 -> 바로 세션에 로그인 정보 저장 (자동 로그인)
            session.setAttribute("loginMemberId", member.getId());
            session.setAttribute("loginMemberName", member.getName());
            session.setAttribute("loginUserName", member.getUserName());

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

            // 로그인 성공 -> 세션에 회원 정보 저장
            session.setAttribute("loginMemberId", member.getId());
            session.setAttribute("loginMemberName", member.getName());
            session.setAttribute("loginUserName", member.getUserName());

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
}