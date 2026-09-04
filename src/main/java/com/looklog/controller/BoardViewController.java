package com.looklog.controller;

import com.looklog.entity.Board;
import com.looklog.entity.Member;
import com.looklog.repository.BoardRepository;
import com.looklog.repository.MemberRepository;
import com.looklog.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BoardViewController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @GetMapping("/board/{id}")
    public String boardDetail(@PathVariable Long id, HttpSession session, Model model) {

        Long loginMemberId = (Long) session.getAttribute("loginMemberId");

        if (loginMemberId == null) {
            return "redirect:/";
        }

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loginMemberName", session.getAttribute("loginMemberName"));

        // 상세보기 대상 게시글
        BoardService.BoardViewDto board = boardService.getBoardDetail(id, loginMemberId);
        model.addAttribute("board", board);

        // 관련 게시글: 같은 태그 달린 다른 게시글들 (자기 자신 제외)
        model.addAttribute("relatedBoards", boardService.getRelatedBoards(id, loginMemberId));

        return "board-detail";
    }
}