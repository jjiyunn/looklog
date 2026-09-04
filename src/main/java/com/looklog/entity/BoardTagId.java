package com.looklog.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class BoardTagId implements Serializable {

    private Long boardId;
    private Long tagId;

    public BoardTagId(Long boardId, Long tagId) {
        this.boardId = boardId;
        this.tagId = tagId;
    }
}