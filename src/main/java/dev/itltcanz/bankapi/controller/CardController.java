package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoPatch;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.dto.card.CardDtoPut;
import dev.itltcanz.bankapi.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cards")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "BearerAuth")
public class CardController {

    private final CardService cardService;

    @PostMapping
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Create a new card",
        description = "Creates a new card with the provided details. Requires admin privileges"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Card created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid card data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<CardDtoResponse> createCard(
        @Parameter(description = "Card creation details", required = true) @RequestBody @Valid CardDtoCreate cardDto) {
        var cardResponse = cardService.createCard(cardDto);
        return new ResponseEntity<>(cardResponse, HttpStatus.CREATED);
    }

    @GetMapping()
    @Operation(
        summary = "Get a list of cards",
        description = "Sends a list of the user's cards or all the cards if the method is called by the administrator"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The cards have been sent successfully")
    })
    public ResponseEntity<Page<CardDtoResponse>> getCards(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size);
        var cardsResponse = cardService.getCards(pageable);
        return ResponseEntity.ok(cardsResponse);
    }

    @GetMapping("/{cardId}")
    @Operation(
        summary = "Retrieve a card by ID",
        description = "Fetches details of a specific card by its ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Card found"),
        @ApiResponse(responseCode = "404", description = "Card not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<CardDtoResponse> getCardById(
        @Parameter(description = "Card ID", required = true)
        @PathVariable @NotNull String cardId) {
        var cardResponse = cardService.getCardById(cardId);
        return ResponseEntity.ok(cardResponse);
    }

    @PutMapping("/{cardId}")
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Update a card",
        description = "Fully updates a card's details by its ID. Requires admin privileges"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Card updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid card data"),
        @ApiResponse(responseCode = "404", description = "Card not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<CardDtoResponse> updateCard(
        @Parameter(description = "Card ID", required = true)
        @PathVariable @NotNull String cardId,
        @Parameter(description = "Updated card details", required = true)
        @RequestBody @Valid CardDtoPut cardDto) {
        var cardResponse = cardService.putCard(cardId, cardDto);
        return ResponseEntity.ok(cardResponse);
    }

    @PatchMapping("/{cardId}")
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Partially update a card",
        description = "Partially updates a card's details by its ID. Requires admin privileges"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Card updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid card data"),
        @ApiResponse(responseCode = "404", description = "Card not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<CardDtoResponse> patchCard(
        @Parameter(description = "Card ID", required = true)
        @PathVariable @NotNull String cardId,
        @Parameter(description = "Partial card updates", required = true)
        @RequestBody @Valid CardDtoPatch cardDto) {
        var cardResponse = cardService.patchCard(cardId, cardDto);
        return ResponseEntity.ok(cardResponse);
    }

    @DeleteMapping("/{cardId}")
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Delete a card",
        description = "Deletes a card by its ID. Requires admin privileges"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Card not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<Void> deleteCard(
        @Parameter(description = "Card ID", required = true)
        @PathVariable @NotNull String cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}