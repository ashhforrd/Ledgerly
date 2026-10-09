package dev.atqiya.ledgerly.invoicing.domain;

import dev.atqiya.ledgerly.identity.domain.AppUser;
import dev.atqiya.ledgerly.workspace.domain.Workspace;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "customers")
public class Customer {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "workspace_id") private Workspace workspace;
    @Column(nullable = false, length = 160) private String name;
    @Column(length = 320) private String email;
    @Column(length = 40) private String phone;
    @Column(name = "billing_address", length = 500) private String billingAddress;
    @Column(nullable = false) private boolean active;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by") private AppUser createdBy;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "updated_by") private AppUser updatedBy;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version private long version;
    protected Customer() {}
    public Customer(UUID id, Workspace workspace, String name, String email, String phone,
            String billingAddress, AppUser actor, Instant now) {
        this.id=id; this.workspace=workspace; this.name=name; this.email=email; this.phone=phone;
        this.billingAddress=billingAddress; this.active=true; this.createdBy=actor; this.updatedBy=actor;
        this.createdAt=now; this.updatedAt=now;
    }
    public void update(String name,String email,String phone,String address,AppUser actor,Instant now){
        this.name=name;this.email=email;this.phone=phone;this.billingAddress=address;this.updatedBy=actor;this.updatedAt=now;
    }
    public void archive(AppUser actor,Instant now){active=false;updatedBy=actor;updatedAt=now;}
    public UUID getId(){return id;} public UUID getWorkspaceId(){return workspace.getId();}
    public String getName(){return name;} public String getEmail(){return email;} public String getPhone(){return phone;}
    public String getBillingAddress(){return billingAddress;} public boolean isActive(){return active;}
    public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public long getVersion(){return version;}
}
