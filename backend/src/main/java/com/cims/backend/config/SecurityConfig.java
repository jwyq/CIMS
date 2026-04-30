package com.cims.backend.config;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description Spring Security 安全配置，定义鉴权规则与异常响应
 */

import com.cims.backend.dto.ApiErrorCode;
import com.cims.backend.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private static final int HTTP_STATUS_UNAUTHORIZED = 401;
    private static final int HTTP_STATUS_FORBIDDEN = 403;
    private static final String MESSAGE_AUTH_REQUIRED = "Authentication required";
    private static final String MESSAGE_NO_PERMISSION = "No permission";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Configuring HTTP security");
        http
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint((request, response, authException) ->
                writeErrorResponse(response, HTTP_STATUS_UNAUTHORIZED, ApiErrorCode.UNAUTHORIZED, MESSAGE_AUTH_REQUIRED))
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                log.warn("Access denied at gateway: {}", accessDeniedException.getMessage());
                writeErrorResponse(response, HTTP_STATUS_FORBIDDEN, ApiErrorCode.FORBIDDEN, MESSAGE_NO_PERMISSION);
            })
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 整个认证模块公开（含 /login，避免因路径尾斜杠或规则顺序导致误拦为 403）
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/health").permitAll()
            .antMatchers(HttpMethod.GET, "/api/reference/**").authenticated()
            .antMatchers(HttpMethod.GET, "/api/customers/*/basic-info-history").hasAuthority("api:customer:history")
            .antMatchers(HttpMethod.GET, "/api/customers/*").hasAuthority("api:customer:detail")
            .antMatchers(HttpMethod.GET, "/api/customers").hasAuthority("api:customer:list")
            .antMatchers(HttpMethod.POST, "/api/customers/register").hasAuthority("api:customer:create")
            .antMatchers(HttpMethod.PUT, "/api/customers/**").hasAuthority("api:customer:update")
            .antMatchers(HttpMethod.POST, "/api/loans/**").hasAuthority("api:loan:create")
            .antMatchers(HttpMethod.POST, "/api/approvals/approve").hasAuthority("api:approval:approve")
            .antMatchers("/api/system/**").authenticated()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        // 认证接口不经过 Security 过滤器链，避免携带旧 JWT 时与 permitAll 评估顺序产生 403
        web.ignoring().antMatchers("/", "/api/auth/**", "/health");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeErrorResponse(javax.servlet.http.HttpServletResponse response, int httpStatus, int code, String message) throws IOException {
        response.setStatus(httpStatus);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), ApiResponse.fail(code, message));
    }
}
