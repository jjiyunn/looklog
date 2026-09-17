package com.looklog.controller;

import com.looklog.entity.Drawer;
import com.looklog.entity.DrawerItem;
import com.looklog.repository.DrawerRepository;
import com.looklog.service.BoardService;
import com.looklog.service.DrawerService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DrawerViewController {

  private final DrawerRepository drawerRepository;
  private final DrawerService drawerService;
  private final BoardService boardService;

  @GetMapping("/drawer/{id}")
  public String drawerDetail(@PathVariable Long id, HttpSession session, Model model) {

    Long loginMemberId = (Long) session.getAttribute("loginMemberId");
    if (loginMemberId == null) {
      return "redirect:/";
    }

    Drawer drawer = drawerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("서랍을 찾을 수 없습니다."));

    boolean isOwner = drawer.getMember().getId().equals(loginMemberId);

    // 서랍 비공개면 안보이게
    if (!drawer.isDrawerPublic() && !isOwner) {
      return "redirect:/";
    }

    model.addAttribute("isLoggedIn", true);
    model.addAttribute("loginMemberName", session.getAttribute("loginMemberName"));
    model.addAttribute("drawer", drawer);
    model.addAttribute("isDrawerOwner", isOwner);

    List<DrawerItem> items = drawerService.getDrawerItems(id);
    List<BoardService.BoardViewDto> boards = items.stream()
            .map(di -> boardService.toDtoPublic(di.getBoard(), loginMemberId))
            .toList();

    model.addAttribute("boards", boards);

    return "drawer-detail";
  }
}