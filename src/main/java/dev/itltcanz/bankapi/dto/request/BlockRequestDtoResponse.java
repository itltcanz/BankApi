package dev.itltcanz.bankapi.dto.request;

import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) for representing card block request details in responses.
 */
@Getter
@Setter
@ToString
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