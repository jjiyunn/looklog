package com.looklog.repository;

import com.looklog.entity.Board;
import com.looklog.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderByRegDateDesc();
    List<Board> findByMemberOrderByRegDateDesc(Member member);

    @Query("SELECT bt.board FROM BoardTag bt WHERE bt.tag.name = :tagName ORDER BY bt.board.regDate DESC")
    List<Board> findByTagName(@Param("tagName") String tagName);
    
    @Query("SELECT b FROM Board b LEFT JOIN Likes l ON l.board = b " +
            "GROUP BY b ORDER BY COUNT(l) DESC, b.regDate DESC")
    List<Board> findAllOrderByLikeCountDesc();

}