package com.looklog.controller;

import com.looklog.entity.Member;
import com.looklog.repository.MemberRepository;
import com.looklog.service.BoardService;
import com.looklog.service.DrawerService;
import com.looklog.service.FollowService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProfileController {

  private final MemberRepository memberRepository;
  private final BoardService boardService;
  private final FollowService followService;
  private final DrawerService drawerService;

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

    model.addAttribute("profileMember", profileMember);
    model.addAttribute("isOwner", isOwner);
    model.addAttribute("isFollowing", followService.isFollowing(loginMemberId, profileMember.getId()));
    model.addAttribute("followerCount", followService.countFollowers(profileMember.getId()));
    model.addAttribute("followingCount", followService.countFollowings(profileMember.getId()));

    // 피드/옷장
    model.addAttribute("boards", boardService.getBoardsByMember(profileMember.getId(), loginMemberId));
    model.addAttribute("drawers", drawerService.getDrawerFolders(profileMember.getId()));

    return "profile";
  }

}