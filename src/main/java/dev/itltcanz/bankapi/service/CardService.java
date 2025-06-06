package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.card.CardDto;
import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.filter.CardFilter;
import dev.itltcanz.bankapi.filter.CardSpecification;
import dev.itltcanz.bankapi.repository.CardRepo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Service for managing card entities and operations.
 */
@Service
@RequiredArgsConstructor
public class CardService {

  private final CardRepo cardRepo;
  private final CardSpecification cardSpecification;
  private final ModelMapper modelMapper;
  private final AuthenticationService authService;
  private final UserService userService;
  private final PermissionService permissionService;
  private final CardNumberGeneratorService cardNumberGeneratorService;

  /**
   * Creates a new card with the provided details.
   *
   * @param cardDto The card creation details.
   * @return The created card as a DTO.
   * @throws NotFoundException if the card owner is not found.
   */
  public CardDtoResponse createCard(CardDtoCreate cardDto) {
    var owner = userService.findUserById(cardDto.getOwnerId());
    var number = cardNumberGeneratorService.generateCardNumber();
    var card = new Card(number, owner, cardDto.getValidityPeriod(), CardStatus.ACTIVE,
        cardDto.getBalance());
    var savedCard = cardRepo.save(card);
    return modelMapper.map(savedCard, CardDtoResponse.class);
  }

  /**
   * Retrieves a paginated list of cards for the authenticated user with filtering.
   *
   * @param filter   The card filter parameters.
   * @param pageable Pagination parameters.
   * @return A page of card details.
   */
  public Page<CardDtoResponse> getUserCards(CardFilter filter, PageRequest pageable) {
    var currentUser = authService.getCurrentUser();
    filter.setOwnerId(currentUser.getId().toString());
    var spec = cardSpecification.withFilter(filter);
    var userCards = cardRepo.findAll(spec, pageable);
    return userCards.map(card -> modelMapper.map(card, CardDtoResponse.class));
  }

  /**
   * Retrieves a paginated list of all cards with filtering for admin users.
   *
   * @param filter   The card filter parameters.
   * @param pageable Pagination parameters.
   * @return A page of card details.
   */
  public Page<CardDtoResponse> getAdminCards(CardFilter filter, PageRequest pageable) {
    var spec = cardSpecification.withFilter(filter);
    var cards = cardRepo.findAll(spec, pageable);
    return cards.map(card -> modelMapper.map(card, CardDtoResponse.class));
  }

  /**
   * Deletes a card by its ID.
   *
   * @param cardId The ID of the card.
   * @throws NotFoundException if the card is not found.
   */
  public void deleteCard(String cardId) {
    var card = findByIdWithPermissionCheck(cardId);
    cardRepo.delete(card);
  }

  /**
   * Retrieves a card by its ID.
   *
   * @param cardId The ID of the card.
   * @return The card details as a DTO.
   * @throws NotFoundException if the card is not found.
   */
  public CardDtoResponse getCardById(@NotNull String cardId) {
    var card = findByIdWithPermissionCheck(cardId);
    return modelMapper.map(card, CardDtoResponse.class);
  }

  /**
   * Updates a card with the provided details.
   *
   * @param cardId  The ID of the card.
   * @param cardDto The updated card details.
   * @return The updated card as a DTO.
   * @throws NotFoundException if the card or owner is not found.
   */
  public CardDtoResponse updateCard(@NotNull String cardId, @NotNull CardDto cardDto) {
    var cardEntity = findByIdWithPermissionCheck(cardId);
    var user = userService.findUserById(cardDto.getOwnerId());
    modelMapper.map(cardDto, cardEntity);
    cardEntity.setOwner(user);
    var savedCard = cardRepo.save(cardEntity);
    return modelMapper.map(savedCard, CardDtoResponse.class);
  }

  /**
   * Retrieves a card by its ID without permission checks.
   *
   * @param cardId The ID of the card.
   * @return The card entity.
   * @throws NotFoundException if the card is not found.
   */
  public Card findById(String cardId) {
    return cardRepo.findById(cardId).orElseThrow(
        () -> new NotFoundException("A card with the number " + cardId + " has not been found."));
  }

  /**
   * Retrieves a card by its ID with permission checks.
   *
   * @param cardId The ID of the card.
   * @return The card entity.
   * @throws NotFoundException if the card is not found.
   */
  public Card findByIdWithPermissionCheck(String cardId) {
    var card = findById(cardId);
    permissionService.hasRights(card.getOwner().getId().toString());
    return card;
  }

  /**
   * Saves a card entity to the repository.
   *
   * @param card The card to save.
   * @return The saved card entity.
   */
  @SuppressWarnings("UnusedReturnValue")
  public Card save(Card card) {
    return cardRepo.save(card);
  }
}