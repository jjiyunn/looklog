package com.looklog.service;

import com.looklog.entity.Drawer;
import com.looklog.entity.Member;
import com.looklog.repository.DrawerRepository;
import com.looklog.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final DrawerRepository drawerRepository;
    private final PasswordEncoder passwordEncoder;

    private final String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";


    // 회원가입
    public Member signUp(String email, String password, String name, String userName) {

        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        if (memberRepository.existsByUserName(userName)) {
            throw new IllegalStateException("이미 사용중인 아이디입니다.");
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

    // 로그인 (새로 추가)
    public Member login(String email, String password) {

        // 1. 이메일로 회원 찾기 (없으면 에러)
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 이메일입니다."));

        // 2. 입력한 비밀번호가 저장된(암호화된) 비밀번호랑 맞는지 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 다 맞으면 회원 정보 리턴
        return member;
    }


    // 프로필 수정
    public void updateProfile(Long memberId, String name, String userName, String bio, MultipartFile profileImg) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow();

        if (!member.getUserName().equals(userName)
                && memberRepository.existsByUserName(userName)) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
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

}