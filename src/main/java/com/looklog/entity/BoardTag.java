package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board_tag")
@Getter
@Setter
@NoArgsConstructor
public class BoardTag {

    @EmbeddedId
    private BoardTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("boardId")           // 복합키의 boardId 부분과 매핑
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")             // 복합키의 tagId 부분과 매핑
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public BoardTag(Board board, Tag tag) {
        this.board = board;
        this.tag = tag;
        this.id = new BoardTagId(board.getId(), tag.getId());
    }
}