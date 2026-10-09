package dev.atqiya.ledgerly.invoicing.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name = "invoice_lines")
public class InvoiceLine {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "invoice_id") private Invoice invoice;
    private int position;
    @Column(nullable = false, length = 200) private String description;
    @Column(nullable = false, precision = 15, scale = 4) private BigDecimal quantity;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4) private BigDecimal unitPrice;
    @Column(name = "line_total", nullable = false, precision = 19, scale = 4) private BigDecimal lineTotal;
    protected InvoiceLine() {}
    public InvoiceLine(UUID id,Invoice invoice,int position,String description,BigDecimal quantity,BigDecimal unitPrice){
        this.id=id;this.invoice=invoice;this.position=position;this.description=description;
        this.quantity=quantity;this.unitPrice=unitPrice;this.lineTotal=quantity.multiply(unitPrice).setScale(4,java.math.RoundingMode.HALF_UP);
    }
    public int getPosition(){return position;} public String getDescription(){return description;}
    public BigDecimal getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;} public BigDecimal getLineTotal(){return lineTotal;}
}
