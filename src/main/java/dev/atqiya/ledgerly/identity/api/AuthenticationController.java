package dev.atqiya.ledgerly.identity.api;

import dev.atqiya.ledgerly.identity.application.AuthResult;
import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import dev.atqiya.ledgerly.identity.application.AuthenticationService;
import dev.atqiya.ledgerly.identity.security.BearerTokenResolver;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final BearerTokenResolver tokenResolver;

    public AuthenticationController(
            AuthenticationService authenticationService,
            BearerTokenResolver tokenResolver
    ) {
        this.authenticationService = authenticationService;
        this.tokenResolver = tokenResolver;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResult result = authenticationService.register(
                request.email(),
                request.displayName(),
                request.password()
        );

        return ResponseEntity
                .created(URI.create("/api/v1/users/" + result.user().id()))
                .body(AuthResponse.from(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResult result = authenticationService.login(
                request.email(),
                request.password()
        );

        return ResponseEntity.ok(AuthResponse.from(result));
    }

    @GetMapping("/me")
    public UserResponse me(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return UserResponse.from(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        tokenResolver.resolve(authorizationHeader)
                .ifPresent(authenticationService::logout);
        return ResponseEntity.noContent().build();
    }
}
