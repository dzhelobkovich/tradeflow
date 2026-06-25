package com.dynamiconlineshopping.backend.entity.product;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_products_sku", columnNames = "sku")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "b2b_price")
    private Double b2bPrice;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock;

    @Column(length = 255, unique = true)
    private String sku;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.active == null) {
            this.active = true;
        }

        if (this.stock == null) {
            this.stock = 0;
        }

        if (this.reservedStock == null) {
            this.reservedStock = 0;
        }

        if (this.b2bPrice == null) {
            this.b2bPrice = this.price;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.b2bPrice == null) {
            this.b2bPrice = this.price;
        }
    }

    @Transient
    public Integer getAvailableStock() {
        return stock - reservedStock;
    }
}
