package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String userName;

    @Column(name = "profile_img", length = 255)
    private String profileImg;

    @Column(length = 150)
    private String bio;

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @Column(name = "mod_date", updatable = false)
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