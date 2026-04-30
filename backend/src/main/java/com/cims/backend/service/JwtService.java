package com.cims.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * 简易 JWT：载荷为 Base64URL( username | resourceCodes | expireAt ) + HMAC-SHA256 签名。
 * 密钥与过期时间由配置项注入；载荷中不含密码与 token 副本。
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private static final String HMAC_SHA256 = "HmacSHA256";

    @Value("${cims.security.jwt-secret:cims-secret-key}")
    private String secret;

    @Value("${cims.security.jwt-expire-seconds:28800}")
    private long jwtExpireSeconds;

    /**
     * 签发访问令牌，仅含用户名与资源码列表。
     *
     * @param username      登录名
     * @param resourceCodes 当前用户可用的资源编码集合
     * @return 两段式 token 字符串
     */
    public String generateToken(String username, List<String> resourceCodes) {
        long expireAt = System.currentTimeMillis() + (jwtExpireSeconds * 1000L);
        String payload = username + "|" + String.join(",", resourceCodes) + "|" + expireAt;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    /**
     * 校验签名与过期时间并解析主体；兼容旧版四段载荷（含已废弃的角色段）。
     *
     * @param token 客户端提交的完整 token
     * @return 用户名与资源码
     */
    public TokenInfo parseToken(String token) {
        if (token == null || !token.contains(".")) {
            throw new IllegalArgumentException("Invalid token");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid token");
        }
        String payload = parts[0];
        String signature = parts[1];
        if (!sign(payload).equals(signature)) {
            log.warn("JWT signature mismatch");
            throw new IllegalArgumentException("Invalid token signature");
        }
        String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
        String[] values = decoded.split("\\|");
        String username;
        List<String> resourceCodes;
        long expireAt;
        if (values.length == 3) {
            username = values[0];
            resourceCodes = values[1].trim().isEmpty()
                ? Collections.<String>emptyList()
                : Arrays.asList(values[1].split(","));
            expireAt = Long.parseLong(values[2]);
        } else if (values.length == 4) {
            username = values[0];
            resourceCodes = values[2].trim().isEmpty()
                ? Collections.<String>emptyList()
                : Arrays.asList(values[2].split(","));
            expireAt = Long.parseLong(values[3]);
        } else {
            throw new IllegalArgumentException("Invalid token payload");
        }
        if (System.currentTimeMillis() > expireAt) {
            log.debug("JWT expired for user={}", username);
            throw new IllegalArgumentException("Token expired");
        }
        return new TokenInfo(username, resourceCodes);
    }

    /**
     * 使用配置的 secret 对载荷做 HMAC-SHA256 签名。
     */
    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (GeneralSecurityException ex) {
            log.error("JWT sign failure", ex);
            throw new IllegalStateException("Failed to sign token", ex);
        }
    }

    /**
     * JWT 解析后的主体信息。
     */
    public static class TokenInfo {
        private final String username;
        private final List<String> resourceCodes;

        public TokenInfo(String username, List<String> resourceCodes) {
            this.username = username;
            this.resourceCodes = resourceCodes;
        }

        public String getUsername() {
            return username;
        }

        public List<String> getResourceCodes() {
            return resourceCodes;
        }
    }
}
