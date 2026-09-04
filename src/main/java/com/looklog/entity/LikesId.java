// LikeId.java
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
public class LikesId implements Serializable {

    private Long memberId;
    private Long boardId;

    public LikesId(Long memberId, Long boardId) {
        this.memberId = memberId;
        this.boardId = boardId;
    }
}