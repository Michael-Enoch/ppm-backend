package com.company.ppm.security;

import com.company.ppm.config.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class KpiIngestionRateLimitFilter extends OncePerRequestFilter {

    private static final DateTimeFormatter MINUTE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
            .withZone(ZoneOffset.UTC);

    private final StringRedisTemplate redisTemplate;
    private final AppProperties appProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public KpiIngestionRateLimitFilter(StringRedisTemplate redisTemplate, AppProperties appProperties) {
        this.redisTemplate = redisTemplate;
        this.appProperties = appProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"POST".equalsIgnoreCase(request.getMethod())
                || !pathMatcher.match("/api/kpis/*/readings", request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String identity = resolveIdentity();
        String minuteBucket = MINUTE_FORMATTER.format(Instant.now());
        String key = "ratelimit:kpi:" + identity + ":" + minuteBucket;

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1L) {
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }
            if (count != null && count > appProperties.getIngestion().getRateLimitPerMinute()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"KPI ingestion rate limit exceeded\"}");
                return;
            }
        } catch (Exception ignored) {
            // If Redis is unavailable, fail open to avoid blocking API writes.
        }

        filterChain.doFilter(request, response);
    }

    private String resolveIdentity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
            return authentication.getName();
        }
        return "anonymous";
    }
}
