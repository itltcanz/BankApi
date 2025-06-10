package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.dto.card.CardDto;
import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.filter.CardFilter;
import dev.itltcanz.bankapi.filter.CardSpecification;
import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.service.AuthService;
import dev.itltcanz.bankapi.service.CardNumberGeneratorService;
import dev.itltcanz.bankapi.service.CardService;
import dev.itltcanz.bankapi.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service("cardService")
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final CardRepo cardRepo;
  private final CardSpecification cardSpecification;
  private final ModelMapper modelMapper;
  private final AuthService authService;
  private final UserService userService;
  private final PermissionServiceImpl permissionService;
  private final CardNumberGeneratorService cardNumberGeneratorService;

  @Override
  @CacheEvict(value = "{userCards, adminCards}", allEntries = true)
  public CardDtoResponse createCard(CardDtoCreate cardDto) {
    var owner = userService.findUserById(cardDto.getOwnerId());
    var number = cardNumberGeneratorService.generateCardNumber();
    var card = new Card(number, owner, cardDto.getValidityPeriod(), CardStatus.ACTIVE,
        cardDto.getBalance());
    var savedCard = cardRepo.save(card);
    return modelMapper.map(savedCard, CardDtoResponse.class);
  }

  @Override
  @Cacheable(
      value = "userCards",
      key = "@authService.getCurrentUser().id + '-' + #filter.hashCode() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
  )
  public Page<CardDtoResponse> getUserCards(CardFilter filter, PageRequest pageable) {
    var currentUser = authService.getCurrentUser();
    filter.setOwnerId(currentUser.getId().toString());
    var spec = cardSpecification.withFilter(filter);
    var userCards = cardRepo.findAll(spec, pageable);
    return userCards.map(card -> modelMapper.map(card, CardDtoResponse.class));
  }

  @Override
  @Cacheable(
      value = "adminCards",
      key = "#filter.hashCode() + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
  )
  public Page<CardDtoResponse> getAdminCards(CardFilter filter, PageRequest pageable) {
    var spec = cardSpecification.withFilter(filter);
    var cards = cardRepo.findAll(spec, pageable);
    return cards.map(card -> modelMapper.map(card, CardDtoResponse.class));
  }

  @Override
  @Cacheable(value = "cards", key = "#cardId")
  public CardDtoResponse getCardById(@NotNull String cardId) {
    var card = findByIdWithPermissionCheck(cardId);
    return modelMapper.map(card, CardDtoResponse.class);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(cacheNames = "cards", key = "#cardId"),
      @CacheEvict(cacheNames = {"userCards", "adminCards"}, allEntries = true)
  })
  public void deleteCard(String cardId) {
    var card = findByIdWithPermissionCheck(cardId);
    cardRepo.delete(card);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(cacheNames = "cards", key = "#cardId"),
      @CacheEvict(cacheNames = {"userCards", "adminCards"}, allEntries = true)
  })
  public CardDtoResponse updateCard(@NotNull String cardId, @NotNull CardDto cardDto) {
    var cardEntity = findByIdWithPermissionCheck(cardId);
    var user = userService.findUserById(cardDto.getOwnerId());
    modelMapper.map(cardDto, cardEntity);
    cardEntity.setOwner(user);
    var savedCard = cardRepo.save(cardEntity);
    return modelMapper.map(savedCard, CardDtoResponse.class);
  }

  @Override
  public Card findById(String cardId) {
    return cardRepo.findById(cardId).orElseThrow(
        () -> new NotFoundException("A card with the number " + cardId + " has not been found."));
  }

  @Override
  public Card findByIdWithPermissionCheck(String cardId) {
    var card = findById(cardId);
    permissionService.hasRights(card.getOwner().getId().toString());
    return card;
  }

  @Override
  @SuppressWarnings("UnusedReturnValue")
  public Card save(Card card) {
    return cardRepo.save(card);
  }
}