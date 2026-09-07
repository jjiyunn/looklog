package com.looklog.repository;

import com.looklog.entity.Board;
import com.looklog.entity.BoardTag;
import com.looklog.entity.BoardTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardTagRepository extends JpaRepository<BoardTag, BoardTagId> {
    List<BoardTag> findByBoard(Board board);
    void deleteByBoard(Board board);
}