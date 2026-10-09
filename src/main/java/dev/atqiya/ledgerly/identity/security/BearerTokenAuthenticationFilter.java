package dev.atqiya.ledgerly.identity.security;

import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import dev.atqiya.ledgerly.identity.application.AuthenticationService;
import dev.atqiya.ledgerly.identity.application.InvalidSessionException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final BearerTokenResolver tokenResolver;
    private final AuthenticationService authenticationService;

    public BearerTokenAuthenticationFilter(
            BearerTokenResolver tokenResolver,
            AuthenticationService authenticationService
    ) {
        this.tokenResolver = tokenResolver;
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        tokenResolver.resolve(request).ifPresent(this::authenticate);
        filterChain.doFilter(request, response);
    }

    private void authenticate(String rawToken) {
        try {
            AuthenticatedUser user = authenticationService.authenticate(rawToken);
            var authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (InvalidSessionException exception) {
            SecurityContextHolder.clearContext();
        }
    }
}
