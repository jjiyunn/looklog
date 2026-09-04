package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
public class Likes {

    @EmbeddedId
    private LikesId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("boardId")
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
    }

    public Likes(Member member, Board board) {
        this.member = member;
        this.board = board;
        this.id = new LikesId(member.getId(), board.getId());
    }
}