package com.pettalk.oauth.controller;

import com.pettalk.member.entity.Member;
import com.pettalk.member.repository.MemberRepository;
import com.pettalk.oauth.service.KakaoLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OauthController {

    @Autowired
    private KakaoLoginService kakaoLoginService;
    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/kakao")
    public ResponseEntity<?> getKakaoAccessToken(@RequestBody Map<String, String> payload) {
        String authorizationCode = payload.get("authorizationCode");
        if (authorizationCode == null || authorizationCode.isEmpty()) {
            return ResponseEntity.badRequest().body("authorizationCode is required");
        }

        String accessToken = kakaoLoginService.getAccessTokenFromAuthorizationCode(authorizationCode);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/kakao")
    public ResponseEntity<?> loginKakao(@RequestBody Map<String, String> payload) {
        String kakaoAccessToken = payload.get("kakaoAccessToken");
        if (kakaoAccessToken == null || kakaoAccessToken.isEmpty()) {
            return ResponseEntity.badRequest().body("kakaoAccessToken is required");
        }

        String jwtToken;
        try {
            jwtToken = kakaoLoginService.login(kakaoAccessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("카카오 로그인 실패");
        }

        Map<String, Object> kakaoProfile;
        try {
            kakaoProfile = kakaoLoginService.getKakaoProfile(kakaoAccessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("프로필을 가져오지 못했습니다");
        }

        Member member = new Member();
        member.setKakaoId(String.valueOf(kakaoProfile.get("id")));
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoProfile.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) kakaoProfile.get("properties");
        if (kakaoAccount != null) {
            member.setEmail(String.valueOf(kakaoAccount.get("email")));
            member.setPhone(String.valueOf(kakaoAccount.get("phone_number")));  // 핸드폰 번호
        }

        if (properties != null) {
            member.setNickName(String.valueOf(properties.get("nickname")));
            member.setProfileImage(String.valueOf(properties.get("profile_image")));  // 프로필 이미지
        }
        memberRepository.save(member);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getEmail(), null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        Map<String, String> response = new HashMap<>();
        response.put("jwtToken", "Bearer " + jwtToken);

        return ResponseEntity.ok(response);
    }
}