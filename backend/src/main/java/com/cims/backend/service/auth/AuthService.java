package com.cims.backend.service.auth;

import com.cims.backend.domain.UserAccount;
import com.cims.backend.dto.LoginResponse;
import com.cims.backend.service.system.SystemService;
import com.cims.backend.repository.UserRepository;
import com.cims.backend.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证业务：校验用户名密码并签发 JWT，登录体中的资源码由用户角色在库中的授权聚合得到。
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final SystemService systemService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserRepository userRepository,
        JwtService jwtService,
        SystemService systemService,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.systemService = systemService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户名密码登录；成功则返回用户标识、展示名、JWT 与聚合后的资源码列表。
     *
     * @param username 登录名（明文不落日志）
     * @param password 密码（不落日志）
     * @return 登录成功后的契约对象
     */
    public LoginResponse login(String username, String password) {
        UserAccount account = userRepository.findByUsername(username)
            .filter(user -> passwordEncoder.matches(password, user.getPassword()) || user.getPassword().equals(password))
            .orElseThrow(() -> {
                log.warn("Login failed: user not found or bad credentials, username={}", username);
                return new IllegalArgumentException("Invalid username or password");
            });

        List<String> resourceCodes = systemService.resourceCodesByRoleCodes(account.getRoles());
        String token = jwtService.generateToken(account.getUsername(), resourceCodes);
        log.info("Login success: userId={}, username={}, resourceCodeCount={}",
            account.getUserId(), account.getUsername(), resourceCodes.size());
        return new LoginResponse(
            account.getUserId(),
            account.getUsername(),
            account.getDisplayName(),
            account.getOrgId(),
            token,
            resourceCodes
        );
    }
}
