// FollowId.java
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
public class FollowId implements Serializable {

    private Long followerId;
    private Long followingId;

    public FollowId(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}