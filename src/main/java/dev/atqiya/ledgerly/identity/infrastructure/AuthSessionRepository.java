package dev.atqiya.ledgerly.identity.infrastructure;

import dev.atqiya.ledgerly.identity.domain.AuthSession;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {

	@EntityGraph(attributePaths = "user")
	Optional<AuthSession> findByTokenHash(String tokenHash);
}
