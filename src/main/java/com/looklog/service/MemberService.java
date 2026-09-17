package com.looklog.service;

import com.looklog.entity.Drawer;
import com.looklog.entity.Member;
import com.looklog.entity.MemberStatus;
import com.looklog.repository.DrawerRepository;
import com.looklog.repository.FollowRepository;
import com.looklog.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final DrawerRepository drawerRepository;
    private final PasswordEncoder passwordEncoder;
    private final FollowRepository followRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";


    // 회원가입
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_.]{6,12}$");

    public Member signUp(String email, String password, String name, String userName) {

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalStateException("이메일 형식이 올바르지 않습니다.");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalStateException("비밀번호는 8~20자이며 영문, 숫자, 특수문자를 포함해야 합니다.");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalStateException("이름은 6~12자의 영문, 숫자, '_', '.'만 사용 가능합니다.");
        }
        if (!NAME_PATTERN.matcher(userName).matches()) {
            throw new IllegalStateException("사용자이름은 6~12자의 영문, 숫자, '_', '.'만 사용 가능합니다.");
        }

        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        if (memberRepository.existsByUserName(userName)) {
            throw new IllegalStateException("이미 사용중인 사용자이름입니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(encodedPassword);
        member.setName(name);
        member.setUserName(userName);

        Member savedMember = memberRepository.save(member);

        Drawer defaultDrawer = new Drawer(savedMember, "기본서랍", true);
        drawerRepository.save(defaultDrawer);

        return savedMember;
    }


    // 로그인
    public Member login(String email, String password) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 이메일입니다."));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalStateException("탈퇴한 계정입니다.");
        }

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }


    // 프로필 수정
    public void updateProfile(Long memberId, String name, String userName, String bio, MultipartFile profileImg) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow();

        if (!member.getUserName().equals(userName)
                && memberRepository.existsByUserName(userName)) {
            throw new IllegalStateException("이미 사용 중인 사용자이름입니다.");
        }
        if (bio != null && bio.length() > 70) {
            throw new IllegalStateException("한줄소개는 70자 이하여야 합니다.");
        }

        member.setName(name);
        member.setUserName(userName);
        member.setBio(bio);

        if (profileImg != null && !profileImg.isEmpty()) {
            String savedPath = saveImage(profileImg);
            member.setProfileImg(savedPath);
        }
    }

    // 이미지 저장 (BoardService와 동일한 방식)
    private String saveImage(MultipartFile imageFile) throws IOException {
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + extension;

        File dest = new File(uploadDir + savedFilename);
        dest.getParentFile().mkdirs();
        imageFile.transferTo(dest);

        return "/uploads/" + savedFilename;
    }

    // 회원탈퇴
    @Transactional
    public void withdraw(Long memberId, String rawPassword) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호 불일치");
        }

        member.setStatus(MemberStatus.WITHDRAWN);
        member.setEmail("withdrawn_" + memberId + "@deleted.local");
        member.setUserName("withdrawn_" + memberId);
        member.setName("탈퇴한 사용자");
        member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        member.setProfileImg(null);
        member.setBio(null);

        followRepository.deleteByFollower_Id(memberId);   // 내가 팔로우하던 관계 삭제
        followRepository.deleteByFollowing_Id(memberId);  // 나를 팔로우하던 관계 삭제
    }


    // 프로필 비공개
    @Transactional
    public void updateProfileVisibility(Long memberId, boolean profilePublic) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member.setProfilePublic(profilePublic);
    }
}

