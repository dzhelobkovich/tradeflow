package com.dynamiconlineshopping.backend.b2b.entity;

import com.dynamiconlineshopping.backend.b2b.enumtype.InspectionResult;
import com.dynamiconlineshopping.backend.b2b.enumtype.TradePointPhotoReportType;
import com.dynamiconlineshopping.backend.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trade_point_photo_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradePointPhotoReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trade_point_id", nullable = false)
    private TradePoint tradePoint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_b2b_order_id")
    private B2BOrder relatedOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 40)
    private TradePointPhotoReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_result", nullable = false, length = 30)
    private InspectionResult inspectionResult;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String comment;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder asc, id asc")
    @Builder.Default
    private List<TradePointPhoto> photos = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private TradePointVisit visit;

    public void addPhoto(TradePointPhoto photo) {
        photos.add(photo);
        photo.setReport(this);
    }

    public void clearPhotos() {
        for (TradePointPhoto photo : photos) {
            photo.setReport(null);
        }
        photos.clear();
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.inspectionResult == null) {
            this.inspectionResult = InspectionResult.OK;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.inspectionResult == null) {
            this.inspectionResult = InspectionResult.OK;
        }
    }
}