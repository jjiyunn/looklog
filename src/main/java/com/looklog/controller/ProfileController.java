package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.entity.MemberStatus;
import com.looklog.repository.MemberRepository;
import com.looklog.service.BoardService;
import com.looklog.service.DrawerService;
import com.looklog.service.FollowService;
import com.looklog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class ProfileController {

  private final MemberRepository memberRepository;
  private final BoardService boardService;
  private final FollowService followService;
  private final DrawerService drawerService;
  private final MemberService memberService;

  @GetMapping("/profile/{userName}")
  public String profile(@PathVariable String userName, HttpSession session, Model model) {

    Long loginMemberId = (Long) session.getAttribute("loginMemberId");

    if (loginMemberId == null) {
      return "redirect:/";
    }

    model.addAttribute("isLoggedIn", true);
    model.addAttribute("loginMemberName", session.getAttribute("loginMemberName"));

    Member profileMember = memberRepository.findByUserName(userName)
            .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

    boolean isOwner = profileMember.getId().equals(loginMemberId);
    boolean isWithdrawn = profileMember.getStatus() == MemberStatus.WITHDRAWN;

    boolean isFollowingThem = followService.isFollowing(loginMemberId, profileMember.getId());
    boolean isFollowedByThem = followService.isFollowing(profileMember.getId(), loginMemberId);
    boolean isMutualFollow = isFollowingThem && isFollowedByThem;

    boolean isPrivateBlocked = !profileMember.isProfilePublic() && !isOwner && !isMutualFollow;

    model.addAttribute("profileMember", profileMember);
    model.addAttribute("isOwner", isOwner);
    model.addAttribute("isWithdrawn", isWithdrawn);
    model.addAttribute("isPrivateBlocked", isPrivateBlocked);
    model.addAttribute("isFollowing", isFollowingThem);
    model.addAttribute("followerCount", followService.countFollowers(profileMember.getId()));
    model.addAttribute("followingCount", followService.countFollowings(profileMember.getId()));


    // 프로필 비공개
    if (isPrivateBlocked) {
      model.addAttribute("boards", java.util.List.of());
      model.addAttribute("drawers", java.util.List.of());
    } else {
      model.addAttribute("boards", boardService.getBoardsByMember(profileMember.getId(), loginMemberId));
      model.addAttribute("drawers", drawerService.getDrawerFolders(profileMember.getId(), loginMemberId));
    }

    return "profile";
  }

  // 프로필 수정
  @GetMapping("/profile/edit")
  public String editForm(HttpSession session, Model model,
                         @RequestParam(required = false) Boolean welcome) {
    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) return "redirect:/";

    Member member = memberRepository.findById(loginMemberId).orElseThrow();
    model.addAttribute("member", member);
    model.addAttribute("welcome", welcome != null && welcome);
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