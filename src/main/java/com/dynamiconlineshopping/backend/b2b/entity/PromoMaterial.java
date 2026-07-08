package com.dynamiconlineshopping.backend.b2b.entity;

import com.dynamiconlineshopping.backend.b2b.enumtype.PromoMaterialType;
import com.dynamiconlineshopping.backend.entity.product.ProductCategory;
import com.dynamiconlineshopping.backend.entity.promotion.Promotion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promo_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PromoMaterialType type;

    @Column(name = "preview_image_url", columnDefinition = "text")
    private String previewImageUrl;

    @Column(name = "file_url", columnDefinition = "text")
    private String fileUrl;

    @Column(name = "object_key", length = 1000)
    private String objectKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @ManyToMany
    @JoinTable(
            name = "promo_material_trade_points",
            joinColumns = @JoinColumn(name = "promo_material_id"),
            inverseJoinColumns = @JoinColumn(name = "trade_point_id")
    )
    @Builder.Default
    private Set<TradePoint> tradePoints = new HashSet<>();

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "version_number", nullable = false)
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
