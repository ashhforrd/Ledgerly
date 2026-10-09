package dev.atqiya.ledgerly.identity.application;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.identity.domain.AuthSession;
import dev.atqiya.ledgerly.identity.infrastructure.AppUserRepository;
import dev.atqiya.ledgerly.identity.infrastructure.AuthSessionRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private final AppUserRepository userRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionTokenService tokenService;
    private final Duration sessionDuration;

    public AuthenticationService(
            AppUserRepository userRepository,
            AuthSessionRepository sessionRepository,
            PasswordEncoder passwordEncoder,
            SessionTokenService tokenService,
            @Value("${ledgerly.auth.session-duration}") Duration sessionDuration
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.sessionDuration = sessionDuration;
    }

    @Transactional
    public AuthResult register(
            String email,
            String displayName,
            String password
    ) {
        String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException();
        }

        Instant now = Instant.now();

        AppUser user = new AppUser(
                UUID.randomUUID(),
                normalizedEmail,
                displayName.trim(),
                passwordEncoder.encode(password),
                now
        );

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyRegisteredException();
        }

        return createSession(user, now);
    }

    @Transactional
    public AuthResult login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);

        AppUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return createSession(user, Instant.now());
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser authenticate(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new InvalidSessionException();
        }

        String tokenHash = tokenService.hash(rawToken);
        Instant now = Instant.now();

        AuthSession session = sessionRepository.findByTokenHash(tokenHash)
                .filter(candidate -> candidate.isActiveAt(now))
                .orElseThrow(InvalidSessionException::new);

        return toAuthenticatedUser(session.getUser());
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        String tokenHash = tokenService.hash(rawToken);

        sessionRepository.findByTokenHash(tokenHash)
                .ifPresent(session -> session.revoke(Instant.now()));
    }

    private AuthResult createSession(AppUser user, Instant now) {
        String rawToken = tokenService.generate();
        Instant expiresAt = now.plus(sessionDuration);

        AuthSession session = new AuthSession(
                UUID.randomUUID(),
                user,
                tokenService.hash(rawToken),
                now,
                expiresAt
        );

        sessionRepository.save(session);

        return new AuthResult(
                rawToken,
                expiresAt,
                toAuthenticatedUser(user)
        );
    }

    private AuthenticatedUser toAuthenticatedUser(AppUser user) {
        return new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getDisplayName()
        );
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}