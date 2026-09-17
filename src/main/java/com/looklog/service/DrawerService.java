package com.looklog.service;

import com.looklog.entity.Board;
import com.looklog.entity.Drawer;
import com.looklog.entity.DrawerItem;
import com.looklog.entity.Member;
import com.looklog.repository.BoardRepository;
import com.looklog.repository.DrawerItemRepository;
import com.looklog.repository.DrawerRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DrawerService {

  private final DrawerRepository drawerRepository;
  private final DrawerItemRepository drawerItemRepository;
  private final BoardRepository boardRepository;

  // 모달에 보여줄 서랍 목록 (게시글 저장 여부 포함)
  public List<DrawerModalDto> getDrawersForModal(Long memberId, Long boardId) {
    List<Drawer> drawers = drawerRepository.findByMember_IdOrderByIsDefaultDescRegDateAsc(memberId);

    return drawers.stream()
            .map(d -> new DrawerModalDto(
                    d.getId(),
                    d.getName(),
                    d.isDefault(),
                    drawerItemRepository.countByDrawer_Id(d.getId()),
                    drawerItemRepository.existsByDrawer_IdAndBoard_Id(d.getId(), boardId)
            ))
            .collect(Collectors.toList());
  }


  // 새 서랍 만들기
  public Drawer createDrawer(Member member, String name) {
    Drawer drawer = new Drawer(member, name, false);
    return drawerRepository.save(drawer);
  }

  // 서랍 취소 시 위치 찾기
  public Long findSavedDrawerId(Long boardId, Long memberId) {
    return drawerItemRepository.findByBoard_IdAndDrawer_Member_Id(boardId, memberId)
            .map(item -> item.getDrawer().getId())
            .orElse(null);
  }

  // 서랍 수정
  public void renameDrawer(Long drawerId, Long memberId, String newName) {
    Drawer drawer = drawerRepository.findById(drawerId)
            .orElseThrow(() -> new IllegalStateException("서랍을 찾을 수 없습니다."));

    if (!drawer.getMember().getId().equals(memberId)) {
      throw new IllegalStateException("권한이 없습니다.");
    }
    if (drawer.isDefault()) {
      throw new IllegalStateException("기본서랍은 이름을 바꿀 수 없습니다.");
    }

    drawer.setName(newName);
    // @Transactional 안에서 dirty checking으로 자동 UPDATE 됨, save() 호출 불필요
  }

  // 서랍 삭제 (기본서랍은 삭제 불가)
  public void deleteDrawer(Long drawerId, Long memberId) {
    Drawer drawer = drawerRepository.findById(drawerId)
            .orElseThrow(() -> new IllegalStateException("서랍을 찾을 수 없습니다."));

    if (!drawer.getMember().getId().equals(memberId)) {
      throw new IllegalStateException("권한이 없습니다.");
    }
    if (drawer.isDefault()) {
      throw new IllegalStateException("기본서랍은 삭제할 수 없습니다.");
    }

    drawerRepository.delete(drawer);
  }

  // 특정 서랍에 게시글 저장/해제 토글
  public boolean toggleItem(Long drawerId, Long boardId, Long memberId) {
    Drawer drawer = drawerRepository.findById(drawerId)
            .orElseThrow(() -> new IllegalStateException("서랍을 찾을 수 없습니다."));

    if (!drawer.getMember().getId().equals(memberId)) {
      throw new IllegalStateException("권한이 없습니다.");
    }

    boolean exists = drawerItemRepository.existsByDrawer_IdAndBoard_Id(drawerId, boardId);

    if (exists) {
      drawerItemRepository.deleteByDrawer_IdAndBoard_Id(drawerId, boardId);
      return false;
    } else {
      drawerItemRepository.deleteByBoard_IdAndDrawer_Member_Id(boardId, memberId);

      Board board = boardRepository.findById(boardId)
              .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));
      drawerItemRepository.save(new DrawerItem(drawer, board));
      return true;
    }
  }


  // 프로필 옷장탭 - 서랍 폴더 목록
  public List<DrawerFolderDto> getDrawerFolders(Long memberId, Long viewerId) {
    boolean isOwner = memberId.equals(viewerId);

    List<Drawer> drawers = drawerRepository.findByMember_IdOrderByIsDefaultDescRegDateAsc(memberId);

    return drawers.stream()
            .filter(d -> isOwner || d.isDrawerPublic()) // 본인 아니면 비공개 서랍 제외
            .map(d -> {
              List<DrawerItem> items = drawerItemRepository.findByDrawer_IdOrderByRegDateDesc(d.getId());
              List<String> thumbnails = items.stream()
                      .limit(4)
                      .map(di -> di.getBoard().getImg())
                      .toList();

              return new DrawerFolderDto(
                      d.getId(),
                      d.getName(),
                      items.size(),
                      thumbnails,
                      d.isDefault(),
                      d.isDrawerPublic()
              );
            })
            .toList();
  }

  // 폴더 카드용 DTO
  @Getter
  public static class DrawerFolderDto {
    private final Long id;
    private final String name;
    private final int itemCount;
    private final List<String> thumbnails;
    private final boolean isDefault;
    private final boolean drawerPublic;

    public DrawerFolderDto(Long id, String name, int itemCount, List<String> thumbnails, boolean isDefault, boolean drawerPublic) {
      this.id = id;
      this.name = name;
      this.itemCount = itemCount;
      this.thumbnails = thumbnails;
      this.isDefault = isDefault;
      this.drawerPublic = drawerPublic;
    }
  }


  // 서랍 상세보기 (안에 든 아이템들)
  public List<DrawerItem> getDrawerItems(Long drawerId) {
    return drawerItemRepository.findByDrawer_IdOrderByRegDateDesc(drawerId);
  }

  // 모달용 DTO
  @Getter
  public static class DrawerModalDto {
    private final Long id;
    private final String name;
    private final boolean isDefault;
    private final long itemCount;
    private final boolean saved;

    public DrawerModalDto(Long id, String name, boolean isDefault, long itemCount, boolean saved) {
      this.id = id;
      this.name = name;
      this.isDefault = isDefault;
      this.itemCount = itemCount;
      this.saved = saved;
    }
  }

  // 서랍 비공개
  @Transactional
  public void updateVisibility(Long drawerId, Long memberId, boolean drawerPublic) {
    Drawer drawer = drawerRepository.findById(drawerId)
            .orElseThrow(() -> new IllegalArgumentException("서랍이 존재하지 않습니다."));
    if (!drawer.getMember().getId().equals(memberId)) {
      throw new IllegalStateException("권한이 없습니다.");
    }
    drawer.setDrawerPublic(drawerPublic);
  }
}