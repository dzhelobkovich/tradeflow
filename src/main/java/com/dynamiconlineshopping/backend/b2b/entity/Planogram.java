package com.dynamiconlineshopping.backend.b2b.entity;

import com.dynamiconlineshopping.backend.entity.product.ProductCategory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "planograms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planogram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "planogram_trade_points",
            joinColumns = @JoinColumn(name = "planogram_id"),
            inverseJoinColumns = @JoinColumn(name = "trade_point_id")
    )
    private Set<TradePoint> tradePoints = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "planogram",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<PlanogramImage> images = new LinkedHashSet<>();

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.active == null) {
            this.active = true;
        }

        if (this.versionNumber == null) {
            this.versionNumber = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
