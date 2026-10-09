package dev.atqiya.ledgerly.imports.domain;
import dev.atqiya.ledgerly.ledger.domain.*; import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
@Entity @Table(name="statement_import_rows") public class StatementImportRow {
 @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="import_id") private StatementImport statementImport;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="workspace_id") private Workspace workspace; @Column(name="row_number") private int rowNumber;
 @Column(name="occurred_on") private LocalDate occurredOn; private String description; private BigDecimal amount; @Enumerated(EnumType.STRING) private TransactionType type;
 private String fingerprint; @Column(name="validation_error") private String validationError; private boolean duplicate;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="transaction_id") private LedgerTransaction transaction;
 protected StatementImportRow(){} public StatementImportRow(UUID id,StatementImport i,Workspace w,int n,LocalDate d,String s,BigDecimal a,TransactionType t,String fp,String error,boolean dup){this.id=id;statementImport=i;workspace=w;rowNumber=n;occurredOn=d;description=s;amount=a;type=t;fingerprint=fp;validationError=error;duplicate=dup;}
 public boolean importable(){return validationError==null&&!duplicate&&transaction==null;} public void link(LedgerTransaction t){transaction=t;}
 public UUID getId(){return id;} public int getRowNumber(){return rowNumber;} public LocalDate getOccurredOn(){return occurredOn;} public String getDescription(){return description;} public BigDecimal getAmount(){return amount;} public TransactionType getType(){return type;} public String getValidationError(){return validationError;} public boolean isDuplicate(){return duplicate;}
}
