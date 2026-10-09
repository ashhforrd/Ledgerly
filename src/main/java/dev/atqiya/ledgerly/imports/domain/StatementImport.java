package dev.atqiya.ledgerly.imports.domain;
import dev.atqiya.ledgerly.identity.domain.AppUser; import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="statement_imports") public class StatementImport {
 @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="workspace_id") private Workspace workspace;
 @Column(name="file_name") private String fileName; @Enumerated(EnumType.STRING) private ImportStatus status;
 @Column(name="total_rows") private int totalRows; @Column(name="valid_rows") private int validRows;
 @Column(name="duplicate_rows") private int duplicateRows; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
 @Column(name="created_at") private Instant createdAt; @Column(name="confirmed_at") private Instant confirmedAt;
 protected StatementImport(){} public StatementImport(UUID id,Workspace w,String f,AppUser u,Instant now){this.id=id;workspace=w;fileName=f;createdBy=u;createdAt=now;status=ImportStatus.PREVIEW;}
 public void totals(int total,int valid,int duplicates){totalRows=total;validRows=valid;duplicateRows=duplicates;}
 public void confirm(Instant now){if(status!=ImportStatus.PREVIEW)throw new IllegalStateException("Import is already confirmed.");status=ImportStatus.CONFIRMED;confirmedAt=now;}
 public UUID getId(){return id;} public UUID getWorkspaceId(){return workspace.getId();} public String getFileName(){return fileName;} public ImportStatus getStatus(){return status;}
 public int getTotalRows(){return totalRows;} public int getValidRows(){return validRows;} public int getDuplicateRows(){return duplicateRows;} public Instant getCreatedAt(){return createdAt;} public Instant getConfirmedAt(){return confirmedAt;}
}
