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
public class DrawerItemId implements Serializable {

  private Long drawerId;
  private Long boardId;

  public DrawerItemId(Long drawerId, Long boardId) {
    this.drawerId = drawerId;
    this.boardId = boardId;
  }
}