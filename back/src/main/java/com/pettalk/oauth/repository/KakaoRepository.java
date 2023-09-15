package com.pettalk.oauth.repository;

import com.pettalk.member.entity.RefreshToken;
import com.pettalk.oauth.entity.KakaoRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoRepository extends JpaRepository<KakaoRefreshToken, Long> {
    Optional<KakaoRefreshToken> findByRefreshToken(String refreshToken);
}
