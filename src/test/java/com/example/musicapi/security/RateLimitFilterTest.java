package com.example.musicapi.security;

import com.example.musicapi.config.properties.RateLimitProperties;
import javax.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturn429AfterExceedingLimit() throws Exception {
        RateLimitProperties props = new RateLimitProperties();
        props.setRequestsPerMinute(10);

        RateLimitService service = new RateLimitService();
        RateLimitFilter filter = new RateLimitFilter(props, service);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/albums");
        FilterChain chain = mock(FilterChain.class);

        // usuário autenticado
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, null)
        );

        // 10 ok
        for (int i = 1; i <= 10; i++) {
            MockHttpServletResponse r = new MockHttpServletResponse();
            filter.doFilter(req, r, chain);
            assertNotEquals(429, r.getStatus(), "não deveria bloquear na tentativa " + i);
        }

        // 11 -> 429
        MockHttpServletResponse blocked = new MockHttpServletResponse();
        filter.doFilter(req, blocked, chain);

        assertEquals(429, blocked.getStatus());
        assertTrue(blocked.getContentAsString().contains("too_many_requests"));
    }

    @Test
    void shouldNotApplyToAuthEndpoints() throws Exception {
        RateLimitProperties props = new RateLimitProperties();
        props.setRequestsPerMinute(1);

        RateLimitService service = new RateLimitService();
        RateLimitFilter filter = new RateLimitFilter(props, service);

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null, null)
        );

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        assertNotEquals(429, res.getStatus());
    }

    @Test
    void shouldNotApplyWhenNotAuthenticated() throws Exception {
        RateLimitProperties props = new RateLimitProperties();
        props.setRequestsPerMinute(1);

        RateLimitService service = new RateLimitService();
        RateLimitFilter filter = new RateLimitFilter(props, service);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/albums");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();

        filter.doFilter(req, res, chain);

        verify(chain, times(1)).doFilter(req, res);
        assertNotEquals(429, res.getStatus());
    }
}