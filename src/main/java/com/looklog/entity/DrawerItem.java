package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "drawer_item")
@Getter
@Setter
@NoArgsConstructor
public class DrawerItem {

  @EmbeddedId
  private DrawerItemId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("drawerId")
  @JoinColumn(name = "drawer_id")
  private Drawer drawer;

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

  public DrawerItem(Drawer drawer, Board board) {
    this.drawer = drawer;
    this.board = board;
    this.id = new DrawerItemId(drawer.getId(), board.getId());
  }
}