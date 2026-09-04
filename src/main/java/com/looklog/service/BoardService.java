package com.looklog.service;

import com.looklog.entity.*;
import com.looklog.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final LikesRepository likesRepository;
    private final TagRepository tagRepository;
    private final BoardTagRepository boardTagRepository;
    private final MemberRepository memberRepository;
    private final DrawerItemRepository drawerItemRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    // feed upload
    public Board createBoard(Member member, MultipartFile imageFile, String content) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + extension;

        File dest = new File(uploadDir + savedFilename);
        dest.getParentFile().mkdirs();
        imageFile.transferTo(dest);

        Board board = new Board();
        board.setMember(member);
        board.setImg("/uploads/" + savedFilename);
        board.setContent(content);

        return boardRepository.save(board);
    }

    // 게시글 업로드 시 태그
    public Board createBoard(Member member, MultipartFile imageFile, String content, String tagsInput) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + extension;

        File dest = new File(uploadDir + savedFilename);
        dest.getParentFile().mkdirs();
        imageFile.transferTo(dest);

        Board board = new Board();
        board.setMember(member);
        board.setImg("/uploads/" + savedFilename);
        board.setContent(content);
        boardRepository.save(board);

        // 태그 처리
        if (tagsInput != null && !tagsInput.isBlank()) {
            String[] tagNames = tagsInput.split(",");   // "스트릿,데님,빈티지" 형태로 받는다고 가정

            for (String rawName : tagNames) {
                String name = rawName.trim();
                if (name.isEmpty()) continue;

                Tag tag = tagRepository.findByName(name).orElse(null);
                if (tag == null) {
                    tag = new Tag(name);
                    tagRepository.save(tag);
                }

                BoardTag boardTag = new BoardTag(board, tag);
                boardTagRepository.save(boardTag);
            }
        }

        return board;
    }


    // 화면에 보여줄 게시글 목록 (좋아요, 옷장, 태그)
    public List<BoardViewDto> getBoardListForView(Long loginMemberId, String tagName) {

        List<Board> boards;

        if (tagName != null && !tagName.isBlank()) {
            boards = boardRepository.findByTagName(tagName);
        } else {
            boards = boardRepository.findAllByOrderByRegDateDesc();
        }

        return boards.stream().map(board -> toDto(board, loginMemberId)).toList();
    }



    // 게시글 하나 상세 조회
    public BoardViewDto getBoardDetail(Long boardId, Long loginMemberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

        return toDto(board, loginMemberId);
    }

    // 관련 게시글: 같은 태그를 가진 다른 게시글들
    public List<BoardViewDto> getRelatedBoards(Long boardId, Long loginMemberId) {
        Board target = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

        List<String> tagNames = boardTagRepository.findByBoard(target).stream()
                .map(bt -> bt.getTag().getName())
                .toList();

        if (tagNames.isEmpty()) {
            return boardRepository.findAllByOrderByRegDateDesc().stream()
                    .filter(b -> !b.getId().equals(boardId))
                    .limit(6)
                    .map(b -> toDto(b, loginMemberId))
                    .toList();
        }

        return tagNames.stream()
                .flatMap(tagName -> boardRepository.findByTagName(tagName).stream())
                .filter(b -> !b.getId().equals(boardId))
                .distinct()
                .limit(6)
                .map(b -> toDto(b, loginMemberId))
                .toList();
    }

    // 공통 변환 로직
    private BoardViewDto toDto(Board board, Long loginMemberId) {
        long likeCount = likesRepository.countByBoard(board);
        boolean likedByMe = loginMemberId != null
                && likesRepository.existsByMemberIdAndBoardId(loginMemberId, board.getId());

        boolean savedByMe = loginMemberId != null
                && drawerItemRepository.existsByBoard_IdAndDrawer_Member_Id(board.getId(), loginMemberId);

        List<String> tagNames = boardTagRepository.findByBoard(board).stream()
                .map(bt -> bt.getTag().getName())
                .limit(6)
                .toList();

        boolean isOwner = loginMemberId != null
                && board.getMember().getId().equals(loginMemberId);

        return new BoardViewDto(
                board.getId(),
                board.getMember().getId(),
                board.getImg(),
                board.getContent(),
                board.getMember().getUserName(),
                board.getMember().getProfileImg(),
                likeCount,
                likedByMe,
                savedByMe,
                tagNames,
                isOwner
        );
    }

    public BoardViewDto toDtoPublic(Board board, Long loginMemberId) {
        return toDto(board, loginMemberId);
    }

    // 피드 삭제
    public void deleteBoard(Long boardId, Long loginMemberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException("게시글을 찾을 수 없습니다."));

        deleteImageFile(board.getImg());
        boardRepository.delete(board);
    }

    private void deleteImageFile(String imgPath) {
        try {
            // /uploads/파일명.png
            String fileName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
            File file = new File(uploadDir + fileName);

            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BoardViewDto> getBoardsByMember(Long memberId, Long loginMemberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        List<Board> boards = boardRepository.findByMemberOrderByRegDateDesc(member);
        return boards.stream().map(board -> toDto(board, loginMemberId)).toList();
    }

    public List<BoardViewDto> getClosetBoardsByMember(Long memberId, Long loginMemberId) {
        List<Board> boards = drawerItemRepository.findDistinctBoardsByMemberId(memberId);
        return boards.stream()
                .map(board -> toDto(board, loginMemberId))
                .toList();
    }



    @Getter
    public static class BoardViewDto {
        private final Long id;
        private final Long memberId;
        private final String img;
        private final String content;
        private final String userName;
        private final String profileImg;
        private final long likeCount;
        private final boolean likedByMe;
        private final boolean savedByMe;
        private final List<String> tags;
        private final boolean isOwner;

        public BoardViewDto(Long id, Long memberId, String img, String content, String userName, String profileImg,
                            long likeCount, boolean likedByMe, boolean savedByMe, List<String> tags,
                            boolean isOwner) {
            this.id = id;
            this.memberId = memberId;
            this.img = img;
            this.content = content;
            this.userName = userName;
            this.profileImg = profileImg;
            this.likeCount = likeCount;
            this.likedByMe = likedByMe;
            this.savedByMe = savedByMe;
            this.tags = tags;
            this.isOwner = isOwner;
        }
    }
}


