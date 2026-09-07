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
import com.looklog.entity.Tag;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;
    private final TagRepository tagRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;


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


        List<Tag> filterTags = new ArrayList<>();
        filterTags.addAll(tagRepository.findByType("style"));
        filterTags.addAll(tagRepository.findByType("season"));
        filterTags.addAll(tagRepository.findByType("color"));
        model.addAttribute("allTags", filterTags);

        model.addAttribute("boards", boardService.getBoardListForView(loginMemberId, tag));
        model.addAttribute("selectedTag", tag);

        return "looklog";
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

    // 프로필 수정
    @GetMapping("/profile/edit")
    public String editForm(HttpSession session, Model model) {
        Long loginMemberId = (Long) session.getAttribute("loginMemberId");
        if (loginMemberId == null) return "redirect:/";

        Member member = memberRepository.findById(loginMemberId).orElseThrow();
        model.addAttribute("member", member);
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String edit(@RequestParam String name,
                       @RequestParam String userName,
                       @RequestParam(required = false) String bio,
                       @RequestParam(required = false) MultipartFile profileImg,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {

        Long loginMemberId = (Long) session.getAttribute("loginMemberId");
        if (loginMemberId == null) return "redirect:/";

        try {
            memberService.updateProfile(loginMemberId, name, userName, bio, profileImg);
        } catch (IllegalStateException | IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/profile/edit";
        }

        Member member = memberRepository.findById(loginMemberId).orElseThrow();
        session.setAttribute("loginMemberName", member.getUserName());

        return "redirect:/profile/" + member.getUserName();
    }

}