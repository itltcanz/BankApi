package dev.itltcanz.bankapi.dto.request;

import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockRequestDtoResponse {
    private String id;
    private String cardId;
    private String userId;
    private RequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String adminId;
}
