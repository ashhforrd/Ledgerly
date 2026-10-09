package dev.atqiya.ledgerly.identity.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class BearerTokenResolver {

    private static final String PREFIX = "Bearer ";

    public Optional<String> resolve(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.regionMatches(
                        true, 0, PREFIX, 0, PREFIX.length())) {
            return Optional.empty();
        }

        String token = authorizationHeader.substring(PREFIX.length()).trim();
        return token.isEmpty() ? Optional.empty() : Optional.of(token);
    }

    public Optional<String> resolve(HttpServletRequest request) {
        return resolve(request.getHeader(HttpHeaders.AUTHORIZATION));
    }
}
