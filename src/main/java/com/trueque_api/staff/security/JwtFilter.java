package com.trueque_api.staff.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String ip          = resolveClientIp(httpRequest);
        String method      = httpRequest.getMethod();
        String uri         = httpRequest.getRequestURI();
        String userAgent   = httpRequest.getHeader("User-Agent");
        String authHeader  = httpRequest.getHeader("Authorization");

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {

                    String email      = jwtUtil.extractEmail(token);
                    List<String> roles = jwtUtil.extractRoles(token);
                    if (roles == null) {
                        roles = List.of();
                    }

                    List<GrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    authorities
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("[AUTH] VALID_TOKEN | ip={} | method={} | uri={} | user={} | roles={} | ua={}",
                            ip, method, uri, email, roles, userAgent);

                } else {
                    log.warn("[AUTH] INVALID_TOKEN | ip={} | method={} | uri={} | ua={}",
                            ip, method, uri, userAgent);
                }

            } else if (authHeader != null && !authHeader.startsWith("Bearer ")) {
                log.warn("[AUTH] MALFORMED_AUTH_HEADER | ip={} | method={} | uri={} | header={} | ua={}",
                        ip, method, uri, sanitize(authHeader), userAgent);

            } else {
                log.debug("[AUTH] NO_TOKEN | ip={} | method={} | uri={} | ua={}",
                        ip, method, uri, userAgent);
            }

        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            log.error("[AUTH] TOKEN_PROCESSING_ERROR | ip={} | method={} | uri={} | ua={} | error={}",
                    ip, method, uri, userAgent, e.getMessage());
        }

        chain.doFilter(request, response);
    }

    /**
     * Resolve o IP real do cliente respeitando proxies e load balancers (ex: AWS ALB).
     * X-Forwarded-For pode conter múltiplos IPs — o primeiro é o cliente original.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Trunca strings potencialmente longas ou maliciosas antes de logar.
     * Evita log injection e poluição dos logs com payloads enormes.
     */
    private String sanitize(String value) {
        if (value == null) return "null";
        String clean = value.replaceAll("[\r\n\t]", "_");
        return clean.length() > 80 ? clean.substring(0, 80) + "..." : clean;
    }
}