package com.looklog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  public String generateCode() {
    SecureRandom random = new SecureRandom();
    int code = 100000 + random.nextInt(900000); // 6자리
    return String.valueOf(code);
  }

  public void sendVerificationEmail(String toEmail, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("[LOOKLOG] 이메일 인증 코드");
    message.setText("인증 코드: " + code + "\n\n이 코드는 10분간 유효합니다.");
    mailSender.send(message);
  }

  public void sendPasswordResetEmail(String toEmail, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("[LOOKLOG] 비밀번호 재설정 코드");
    message.setText("비밀번호 재설정 코드: " + code + "\n\n이 코드는 10분간 유효합니다.");
    mailSender.send(message);
  }
}