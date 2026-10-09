package dev.atqiya.ledgerly.imports.api;
import dev.atqiya.ledgerly.identity.application.AuthenticatedUser; import dev.atqiya.ledgerly.imports.application.*; import java.io.IOException; import java.util.List; import java.util.UUID; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*; import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api/v1/workspaces/{workspaceId}/imports") public class StatementImportController{
 private final StatementImportService service; public StatementImportController(StatementImportService s){service=s;}
 @GetMapping public List<ImportView> list(@AuthenticationPrincipal AuthenticatedUser u,@PathVariable UUID workspaceId){return service.list(u.id(),workspaceId);}
 @PostMapping(value="/preview",consumes="multipart/form-data") public ImportView preview(@AuthenticationPrincipal AuthenticatedUser u,@PathVariable UUID workspaceId,@RequestPart("file") MultipartFile file)throws IOException{return service.preview(u.id(),workspaceId,file);}
 @PostMapping("/{importId}/confirm") public ImportView confirm(@AuthenticationPrincipal AuthenticatedUser u,@PathVariable UUID workspaceId,@PathVariable UUID importId){return service.confirm(u.id(),workspaceId,importId);}
}
