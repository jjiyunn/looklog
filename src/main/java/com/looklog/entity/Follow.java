// Follow.java
package com.looklog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow")
@Getter
@Setter
@NoArgsConstructor
public class Follow {

    @EmbeddedId
    private FollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    private Member follower;      // 팔로우 하는 사람

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")
    @JoinColumn(name = "following_id")
    private Member following;     // 팔로우 당하는 사람

    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
    }

    public Follow(Member follower, Member following) {
        this.follower = follower;
        this.following = following;
        this.id = new FollowId(follower.getId(), following.getId());
    }
}