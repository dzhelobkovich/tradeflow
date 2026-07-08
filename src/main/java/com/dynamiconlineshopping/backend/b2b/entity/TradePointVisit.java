package com.dynamiconlineshopping.backend.b2b.entity;

import com.dynamiconlineshopping.backend.b2b.enumtype.TradePointVisitStatus;
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
@Table(name = "trade_point_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradePointVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trade_point_id", nullable = false)
    private TradePoint tradePoint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TradePointVisitStatus status;

    @Column(name = "planned_at")
    private LocalDateTime plannedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "visit_date", nullable = false)
    private LocalDateTime visitDate;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "goal", columnDefinition = "text")
    private String goal;

    @Column(name = "result_summary", columnDefinition = "text")
    private String resultSummary;

    @Column(name = "manager_comment", columnDefinition = "text")
    private String managerComment;

    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY)
    @Builder.Default
    private List<B2BOrder> orders = new ArrayList<>();

    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<TradePointPhotoReport> photoReports = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = TradePointVisitStatus.PLANNED;
        }

        if (this.visitDate == null) {
            this.visitDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}