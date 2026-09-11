package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "board_img", nullable = false, length = 255)
    private String img;

    @Column(length = 500)
    private String content;

    @Column(name = "reg_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    @UpdateTimestamp
    private LocalDateTime modDate;

    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
        this.modDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modDate = LocalDateTime.now();
    }
}