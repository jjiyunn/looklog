package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.repository.TagRepository;
import com.looklog.service.BoardService;
import com.looklog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;import com.looklog.entity.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
            @RequestParam(required = false, defaultValue = "latest") String sort,
            HttpSession session,
            Model model
    ) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");

        if (loginMemberId == null) {
            return "redirect:/";
        }

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("loginMemberName", session.getAttribute("loginMemberName"));


        List<Tag> filterTags = new ArrayList<>();
        filterTags.addAll(tagRepository.findByType("style"));
        filterTags.addAll(tagRepository.findByType("season"));
        filterTags.addAll(tagRepository.findByType("color"));
        model.addAttribute("allTags", filterTags);

        model.addAttribute("boards", boardService.getBoardListForView(loginMemberId, tag, sort));
        model.addAttribute("selectedTag", tag);
        model.addAttribute("selectedSort", sort);

        return "looklog";
    }


    //검색
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return "redirect:/looklog";
        }
        String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
        return "redirect:/looklog?tag=" + encoded;
    }


    // 프로필
    @GetMapping("/profile")
    public String myProfile(HttpSession session) {
        String loginMemberName = (String) session.getAttribute("loginMemberName");

        if (loginMemberName == null) {
            return "redirect:/";
        }

        return "redirect:/profile/" + loginMemberName;
    }



}