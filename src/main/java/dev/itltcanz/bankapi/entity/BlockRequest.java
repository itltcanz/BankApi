package dev.itltcanz.bankapi.entity;

import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "block_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false)
    private String id;
    @Column(nullable = false)
    private String cardId;
    @Column(nullable = false)
    private String userId;
    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private String adminId;
}