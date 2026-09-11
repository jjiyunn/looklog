package com.looklog.config;

import com.looklog.entity.Drawer;
import com.looklog.entity.Member;
import com.looklog.entity.MemberStatus;
import com.looklog.repository.DrawerRepository;
import com.looklog.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.UUID;

@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
    http
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService)
                    )
                    .successHandler((request, response, authentication) -> {
                      Boolean needsSetup = (Boolean) request.getSession().getAttribute("needsUsernameSetup");
                      if (Boolean.TRUE.equals(needsSetup)) {
                        request.getSession().removeAttribute("needsUsernameSetup");
                        response.sendRedirect("/profile/edit?welcome=true");
                      } else {
                        response.sendRedirect("/looklog");
                      }
                    })
            );

    return http.build();
  }

  // 구글 로그인 관련
  @Service
  @RequiredArgsConstructor
  public static class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final DrawerRepository drawerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
      OAuth2User oAuth2User = super.loadUser(userRequest);

      Map<String, Object> attributes = oAuth2User.getAttributes();
      String email = (String) attributes.get("email");
      String name = (String) attributes.get("name");

      Member member = memberRepository.findByEmail(email).orElse(null);

      if (member == null) {
        member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setUserName(generateUniqueUserName());
        member.setProvider("GOOGLE");
        member.setPassword(null);
        member = memberRepository.save(member);

        Drawer defaultDrawer = new Drawer(member, "기본서랍", true);
        drawerRepository.save(defaultDrawer);


        ServletRequestAttributes attrForNew = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        attrForNew.getRequest().getSession().setAttribute("needsUsernameSetup", true);

      } else if (!"GOOGLE".equals(member.getProvider())) {
        throw new OAuth2AuthenticationException("이미 가입된 이메일입니다.");
      } else if (member.getStatus() == MemberStatus.WITHDRAWN) {
        throw new OAuth2AuthenticationException("탈퇴한 계정입니다.");
      }

      ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      HttpServletRequest request = attr.getRequest();
      request.getSession().setAttribute("loginMemberId", member.getId());
      request.getSession().setAttribute("loginMemberName", member.getUserName());
      request.getSession().setAttribute("loginMemberRole", member.getRole());

      return oAuth2User;
    }

    private String generateUniqueUserName() {
      String candidate;
      do {
        candidate = "user" + UUID.randomUUID().toString().substring(0, 8);
      } while (memberRepository.existsByUserName(candidate));
      return candidate;
    }
  }
}