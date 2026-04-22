package com.tujuhsembilan.smartedutelu.common.security;

import com.tujuhsembilan.smartedutelu.domain.identity.repository.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserSessionRepository userSessionRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Cek keberadaan token pada header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Ekstraksi token
        jwt = authHeader.substring(7);

        try {
            username = jwtUtil.extractUsername(jwt);

            // Validasi dan set Security Context jika belum diatur
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    // Reject tokens for disabled or suspended accounts
                    if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                        logger.debug("Token valid tapi akun non-aktif/terkunci: " + username);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Daftarkan user sebagai "authenticated"
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // B4: Update session lastActive asynchronously
                    try {
                        UUID sessionId = jwtUtil.extractSessionId(jwt);
                        if (sessionId != null) {
                            updateSessionLastActive(sessionId);
                        }
                    } catch (Exception e) {
                        logger.debug("Failed to update session lastActive: " + e.getMessage());
                    }
                }
            }
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            logger.debug("JWT token expired for request: " + request.getRequestURI());
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            logger.warn("Invalid JWT signature from IP " + request.getRemoteAddr() + ": " + ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.UnsupportedJwtException ex) {
            logger.warn("Malformed/unsupported JWT: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("JWT processing error: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    protected void updateSessionLastActive(UUID sessionId) {
        userSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setLastActive(LocalDateTime.now());
            userSessionRepository.save(session);
        });
    }
}