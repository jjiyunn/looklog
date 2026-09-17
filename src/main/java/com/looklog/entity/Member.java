package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(length = 255)
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
    @CreationTimestamp
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    @UpdateTimestamp
    private LocalDateTime modDate;

    @Column(nullable = false, length = 20)
    private String role = "USER";

    @Column(nullable = false, length = 20)
    private String provider = "LOCAL";   // LOCAL/ GOOGLE

    @Column(name = "profile_public", nullable = false)
    private boolean profilePublic = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

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