package dev.atqiya.ledgerly.invoicing.application;
import dev.atqiya.ledgerly.invoicing.domain.Customer; import java.time.Instant; import java.util.UUID;
public record CustomerView(UUID id,String name,String email,String phone,String billingAddress,boolean active,Instant createdAt,Instant updatedAt,long version){public static CustomerView from(Customer c){return new CustomerView(c.getId(),c.getName(),c.getEmail(),c.getPhone(),c.getBillingAddress(),c.isActive(),c.getCreatedAt(),c.getUpdatedAt(),c.getVersion());}}
