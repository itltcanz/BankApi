package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.card.CardDto;
import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.filter.CardFilter;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * Service for managing card entities and operations.
 */
public interface CardService {
  /**
   * Creates a new card with the provided details.
   *
   * @param cardDto The card creation details.
   * @return The created card as a DTO.
   * @throws NotFoundException if the card owner is not found.
   */
   CardDtoResponse createCard(CardDtoCreate cardDto);

  /**
   * Retrieves a paginated list of cards for the authenticated user with filtering.
   *
   * @param filter   The card filter parameters.
   * @param pageable Pagination parameters.
   * @return A page of card details.
   */
   Page<CardDtoResponse> getUserCards(CardFilter filter, PageRequest pageable);

  /**
   * Retrieves a paginated list of all cards with filtering for admin users.
   *
   * @param filter   The card filter parameters.
   * @param pageable Pagination parameters.
   * @return A page of card details.
   */
   Page<CardDtoResponse> getAdminCards(CardFilter filter, PageRequest pageable);

  /**
   * Deletes a card by its ID.
   *
   * @param cardId The ID of the card.
   * @throws NotFoundException if the card is not found.
   */
   void deleteCard(String cardId);

  /**
   * Retrieves a card by its ID.
   *
   * @param cardId The ID of the card.
   * @return The card details as a DTO.
   * @throws NotFoundException if the card is not found.
   */
   CardDtoResponse getCardById(@NotNull String cardId);

  /**
   * Updates a card with the provided details.
   *
   * @param cardId  The ID of the card.
   * @param cardDto The updated card details.
   * @return The updated card as a DTO.
   * @throws NotFoundException if the card or owner is not found.
   */
   CardDtoResponse updateCard(@NotNull String cardId, @NotNull CardDto cardDto);

  /**
   * Retrieves a card by its ID without permission checks.
   *
   * @param cardId The ID of the card.
   * @return The card entity.
   * @throws NotFoundException if the card is not found.
   */
   Card findById(String cardId);

  /**
   * Retrieves a card by its ID with permission checks.
   *
   * @param cardId The ID of the card.
   * @return The card entity.
   * @throws NotFoundException if the card is not found.
   */
   Card findByIdWithPermissionCheck(String cardId);

  /**
   * Saves a card entity to the repository.
   *
   * @param card The card to save.
   * @return The saved card entity.
   */
  @SuppressWarnings("UnusedReturnValue")
   Card save(Card card);
}