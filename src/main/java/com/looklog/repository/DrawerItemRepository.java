package com.looklog.repository;

import com.looklog.entity.DrawerItem;
import com.looklog.entity.DrawerItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.looklog.entity.Board;

public interface DrawerItemRepository extends JpaRepository<DrawerItem, DrawerItemId> {

  long countByDrawer_Id(Long drawerId);

  List<DrawerItem> findByDrawer_IdOrderByRegDateDesc(Long drawerId);

  boolean existsByDrawer_IdAndBoard_Id(Long drawerId, Long boardId);

  void deleteByDrawer_IdAndBoard_Id(Long drawerId, Long boardId);

  // 어떤 서랍에든 저장돼있는지 체크용
  boolean existsByBoard_IdAndDrawer_Member_Id(Long boardId, Long memberId);

  // 특정 유저의 모든 서랍에 든 게시글 중복 없이 조회
  @Query("select distinct di.board from DrawerItem di where di.drawer.member.id = :memberId")
  List<Board> findDistinctBoardsByMemberId(@Param("memberId") Long memberId);

  void deleteByBoard_IdAndDrawer_Member_Id(Long boardId, Long memberId);

  Optional<DrawerItem> findByBoard_IdAndDrawer_Member_Id(Long boardId, Long memberId);
}