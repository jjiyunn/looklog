package com.looklog.repository;

import com.looklog.entity.Board;
import com.looklog.entity.Likes;
import com.looklog.entity.LikesId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, LikesId> {
    long countByBoard(Board board);
    boolean existsByMemberIdAndBoardId(Long memberId, Long boardId);
}