package dev.atqiya.ledgerly.reporting.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.ledger.domain.Category;
import dev.atqiya.ledgerly.ledger.domain.TransactionType;
import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="categorization_rules")
public class CategorizationRule {
 @Id private UUID id;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="workspace_id") private Workspace workspace;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id") private Category category;
 private String name;
 @Enumerated(EnumType.STRING) @Column(name="transaction_type") private TransactionType transactionType;
 @Column(name="description_contains") private String descriptionContains;
 private int priority; private boolean active;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
 @Column(name="created_at") private Instant createdAt;
 protected CategorizationRule(){}
 public CategorizationRule(UUID id,Workspace w,Category c,String name,TransactionType type,String contains,int priority,AppUser actor,Instant now){this.id=id;workspace=w;category=c;this.name=name;transactionType=type;descriptionContains=contains;this.priority=priority;active=true;createdBy=actor;createdAt=now;}
 public boolean matches(String description,TransactionType type){return active&&transactionType==type&&description.toLowerCase(java.util.Locale.ROOT).contains(descriptionContains.toLowerCase(java.util.Locale.ROOT));}
 public void disable(){active=false;}
 public UUID getId(){return id;} public Category getCategory(){return category;} public String getName(){return name;} public TransactionType getTransactionType(){return transactionType;} public String getDescriptionContains(){return descriptionContains;} public int getPriority(){return priority;} public boolean isActive(){return active;}
}
