package dev.atqiya.ledgerly.imports.application;
import dev.atqiya.ledgerly.imports.domain.*; import java.math.BigDecimal; import java.time.*; import java.util.*;
public record ImportView(UUID id,String fileName,ImportStatus status,int totalRows,int validRows,int duplicateRows,Instant createdAt,Instant confirmedAt,List<Row> rows){
 public record Row(int rowNumber,LocalDate occurredOn,String description,BigDecimal amount,String type,String error,boolean duplicate){}
 public static ImportView from(StatementImport i,List<StatementImportRow> rows){return new ImportView(i.getId(),i.getFileName(),i.getStatus(),i.getTotalRows(),i.getValidRows(),i.getDuplicateRows(),i.getCreatedAt(),i.getConfirmedAt(),rows.stream().map(r->new Row(r.getRowNumber(),r.getOccurredOn(),r.getDescription(),r.getAmount(),r.getType()==null?null:r.getType().name(),r.getValidationError(),r.isDuplicate())).toList());}
}
