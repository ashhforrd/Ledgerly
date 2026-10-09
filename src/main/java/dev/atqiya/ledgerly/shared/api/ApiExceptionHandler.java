package dev.atqiya.ledgerly.shared.api;

import dev.atqiya.ledgerly.identity.application.EmailAlreadyRegisteredException;
import dev.atqiya.ledgerly.identity.application.InvalidCredentialsException;
import dev.atqiya.ledgerly.identity.application.InvalidSessionException;
import dev.atqiya.ledgerly.invoicing.application.InvoicingException;
import dev.atqiya.ledgerly.ledger.application.CategoryNotFoundException;
import dev.atqiya.ledgerly.ledger.application.DuplicateCategoryException;
import dev.atqiya.ledgerly.ledger.application.InvalidCategoryException;
import dev.atqiya.ledgerly.ledger.application.InvalidTransactionException;
import dev.atqiya.ledgerly.ledger.application.TransactionNotFoundException;
import dev.atqiya.ledgerly.workspace.application.InvalidWorkspaceException;
import dev.atqiya.ledgerly.workspace.application.WorkspaceNotFoundException;
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

    @ExceptionHandler(InvoicingException.class)
    ProblemDetail handleInvoicing(InvoicingException exception, HttpServletRequest request) {
        return createProblem(HttpStatus.BAD_REQUEST, "Invalid invoicing operation", exception.getMessage(),
                "invalid-invoicing-operation", request);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    ProblemDetail handleTransactionNotFound(TransactionNotFoundException exception, HttpServletRequest request) {
        return createProblem(HttpStatus.NOT_FOUND, "Transaction not found", exception.getMessage(),
                "transaction-not-found", request);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    ProblemDetail handleInvalidTransaction(InvalidTransactionException exception, HttpServletRequest request) {
        return createProblem(HttpStatus.BAD_REQUEST, "Invalid transaction", exception.getMessage(),
                "invalid-transaction", request);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ProblemDetail handleCategoryNotFound(CategoryNotFoundException exception, HttpServletRequest request) {
        return createProblem(HttpStatus.NOT_FOUND, "Category not found", exception.getMessage(),
                "category-not-found", request);
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    ProblemDetail handleDuplicateCategory(DuplicateCategoryException exception, HttpServletRequest request) {
        return createProblem(HttpStatus.CONFLICT, "Category already exists", exception.getMessage(),
                "duplicate-category", request);
    }

    @ExceptionHandler(InvalidCategoryException.class)
    ProblemDetail handleInvalidCategory(InvalidCategoryException exception, HttpServletRequest request) {
        return createProblem(HttpStatus.BAD_REQUEST, "Invalid category", exception.getMessage(),
                "invalid-category", request);
    }

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

    @ExceptionHandler(WorkspaceNotFoundException.class)
    ProblemDetail handleWorkspaceNotFound(
            WorkspaceNotFoundException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.NOT_FOUND,
                "Workspace not found",
                exception.getMessage(),
                "workspace-not-found",
                request
        );
    }

    @ExceptionHandler(InvalidWorkspaceException.class)
    ProblemDetail handleInvalidWorkspace(
            InvalidWorkspaceException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid workspace",
                exception.getMessage(),
                "invalid-workspace",
                request
        );
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
