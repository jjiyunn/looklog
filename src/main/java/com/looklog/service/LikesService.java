package com.looklog.service;

import com.looklog.entity.Board;
import com.looklog.entity.Likes;
import com.looklog.entity.LikesId;
import com.looklog.entity.Member;
import com.looklog.repository.BoardRepository;
import com.looklog.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final BoardRepository boardRepository;

    public boolean toggleLike(Member member, Long boardId) {

        LikesId id = new LikesId(member.getId(), boardId);

        if (likesRepository.existsById(id)) {
            likesRepository.deleteById(id);
            return false;
        } else {
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

            Likes likes = new Likes(member, board);
            likesRepository.save(likes);
            return true;
        }
    }
}