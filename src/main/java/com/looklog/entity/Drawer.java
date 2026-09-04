package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "drawer")
@Getter
@Setter
@NoArgsConstructor
public class Drawer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "drawer_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(name = "drawer_name", nullable = false)
  private String name;

  @Column(name = "is_default", nullable = false)
  private boolean isDefault;

  @Column(name = "reg_date", updatable = false)
  private LocalDateTime regDate;

  @PrePersist
  protected void onCreate() {
    this.regDate = LocalDateTime.now();
  }

  public Drawer(Member member, String name, boolean isDefault) {
    this.member = member;
    this.name = name;
    this.isDefault = isDefault;
  }
}