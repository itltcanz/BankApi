package dev.itltcanz.bankapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlockRequestDtoCreate {
    @NotBlank
    private String cardId;
}
