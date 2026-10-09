package dev.atqiya.ledgerly.shared.api;

import dev.atqiya.ledgerly.identity.application.EmailAlreadyRegisteredException;
import dev.atqiya.ledgerly.identity.application.InvalidCredentialsException;
import dev.atqiya.ledgerly.identity.application.InvalidSessionException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    ProblemDetail handleEmailAlreadyRegistered(
            EmailAlreadyRegisteredException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.CONFLICT,
                "Email already registered",
                exception.getMessage(),
                "email-already-registered",
                request
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials",
                exception.getMessage(),
                "invalid-credentials",
                request
        );
    }

    @ExceptionHandler(InvalidSessionException.class)
    ProblemDetail handleInvalidSession(
            InvalidSessionException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.UNAUTHORIZED,
                "Invalid session",
                exception.getMessage(),
                "invalid-session",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more request fields are invalid.",
                "validation-failed",
                request
        );

        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        problem.setProperty("errors", errors);
        return problem;
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            String problemCode,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://ledgerly.dev/problems/" + problemCode));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
