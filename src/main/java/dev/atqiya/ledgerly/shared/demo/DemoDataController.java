package dev.atqiya.ledgerly.shared.demo;

import dev.atqiya.ledgerly.identity.application.AuthenticatedUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
@ConditionalOnProperty(name = "ledgerly.demo.seed-enabled", havingValue = "true")
public class DemoDataController {

    private final DemoDataService demoDataService;

    public DemoDataController(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @PostMapping("/seed")
    public DemoDataService.DemoSeedResult seed(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return demoDataService.seed(user.id());
    }
}
