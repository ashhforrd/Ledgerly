package dev.atqiya.ledgerly.reporting.application;

import dev.atqiya.ledgerly.ledger.domain.*;
import dev.atqiya.ledgerly.ledger.infrastructure.LedgerTransactionRepository;
import dev.atqiya.ledgerly.workspace.application.WorkspaceAccessService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialReportService {
 private final LedgerTransactionRepository transactions; private final WorkspaceAccessService access;
 public FinancialReportService(LedgerTransactionRepository t,WorkspaceAccessService a){transactions=t;access=a;}
 @Transactional(readOnly=true) public FinancialReport report(UUID userId,UUID workspaceId,LocalDate from,LocalDate to){
  var membership=access.requireMembership(userId,workspaceId);if(from==null||to==null||from.isAfter(to))throw new IllegalArgumentException("A valid report period is required.");
  var values=transactions.findAllByWorkspace_IdAndStatusAndOccurredOnBetweenOrderByOccurredOnAsc(workspaceId,TransactionStatus.POSTED,from,to);
  BigDecimal income=sum(values,TransactionType.INCOME),expense=sum(values,TransactionType.EXPENSE);
  Map<String,BigDecimal> categoryTotals=new TreeMap<>();for(var t:values){String key=t.getCategoryName()==null?"Uncategorized":t.getCategoryName();categoryTotals.merge(t.getType()+":"+key,t.getAmount(),BigDecimal::add);}
  var categories=categoryTotals.entrySet().stream().map(e->new CategoryTotal(e.getKey().split(":",2)[0],e.getKey().split(":",2)[1],e.getValue())).toList();
  return new FinancialReport(from,to,membership.getWorkspace().getDefaultCurrency(),income,expense,income.subtract(expense),values.size(),categories);
 }
 private BigDecimal sum(List<LedgerTransaction> values,TransactionType type){return values.stream().filter(t->t.getType()==type).map(LedgerTransaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);}
 public record FinancialReport(LocalDate from,LocalDate to,String currency,BigDecimal income,BigDecimal expenses,BigDecimal netCashFlow,int transactionCount,List<CategoryTotal> categories){}
 public record CategoryTotal(String type,String category,BigDecimal amount){}
}
