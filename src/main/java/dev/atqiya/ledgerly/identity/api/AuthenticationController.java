package dev.atqiya.ledgerly.identity.api;

import dev.atqiya.ledgerly.identity.application.AuthResult;
import dev.atqiya.ledgerly.identity.application.AuthenticationService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationService authenticationService
    ) {
        this.authenticationService = authenticationService;
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
}