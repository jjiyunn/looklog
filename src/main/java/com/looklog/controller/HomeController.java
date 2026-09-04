package com.looklog.controller;

import com.looklog.repository.TagRepository;
import com.looklog.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;import com.looklog.entity.Tag;
import java.util.ArrayList;
import java.util.List;
import com.looklog.entity.Tag;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;
    private final TagRepository tagRepository;

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");
        String loginMemberName = (String) session.getAttribute("loginMemberName");

        model.addAttribute("isLoggedIn", loginMemberId != null);
        model.addAttribute("loginMemberName", loginMemberName);

        return "index";
    }

    @GetMapping("/looklog")
    public String looklog(
            @RequestParam(required = false) String tag,
            HttpSession session,
            Model model
    ) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");

        if (loginMemberId == null) {
            return "redirect:/";
        }

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loginMemberName", session.getAttribute("loginMemberName"));

        model.addAttribute("styleTags", tagRepository.findByType("style"));
        model.addAttribute("seasonTags", tagRepository.findByType("season"));
        model.addAttribute("colorTags", tagRepository.findByType("color"));

        // 상단 필터용: style + season + color 순서로 합쳐서 하나의 리스트로
        List<Tag> filterTags = new ArrayList<>();
        filterTags.addAll(tagRepository.findByType("style"));
        filterTags.addAll(tagRepository.findByType("season"));
        filterTags.addAll(tagRepository.findByType("color"));
        model.addAttribute("allTags", filterTags);

        model.addAttribute("boards", boardService.getBoardListForView(loginMemberId, tag));
        model.addAttribute("selectedTag", tag);

        return "looklog";
    }

    @GetMapping("/profile")
    public String myProfile(HttpSession session) {
        String loginMemberName = (String) session.getAttribute("loginMemberName");

        if (loginMemberName == null) {
            return "redirect:/";
        }

        // 세션엔 userName이 아니라 name(표시이름)이 저장되어 있을 수 있으니 주의!
        return "redirect:/profile/" + loginMemberName;
    }


}