package dev.itltcanz.bankapi.controller.impl;

import dev.itltcanz.bankapi.controller.CardController;
import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoPatch;
import dev.itltcanz.bankapi.dto.card.CardDtoPut;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.filter.CardFilter;
import dev.itltcanz.bankapi.service.impl.CardServiceImpl;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardControllerImpl implements CardController {

  private final CardServiceImpl cardService;

  public ResponseEntity<CardDtoResponse> createCard(CardDtoCreate cardDto) {
    var cardResponse = cardService.createCard(cardDto);
    return new ResponseEntity<>(cardResponse, HttpStatus.CREATED);
  }

  public ResponseEntity<CardDtoResponse> getCardById(String cardId) {
    var cardResponse = cardService.getCardById(cardId);
    return ResponseEntity.ok(cardResponse);
  }

  public ResponseEntity<Page<CardDtoResponse>> getUserCards(int page, int size, String sortBy,
      String direction, CardStatus status, String number) {
    var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    var pageable = PageRequest.of(page, size, sort);
    var filter = new CardFilter(status, number, null);
    var cardsResponse = cardService.getUserCards(filter, pageable);
    return ResponseEntity.ok(cardsResponse);
  }

  public ResponseEntity<Page<CardDtoResponse>> getAdminCards(int page, int size, String sortBy,
      String direction, CardStatus status, String number, UUID ownerId) {
    var sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
    var pageable = PageRequest.of(page, size, sort);
    var filter = new CardFilter(status, number, ownerId != null ? ownerId.toString() : null);
    var cardsResponse = cardService.getAdminCards(filter, pageable);
    return ResponseEntity.ok(cardsResponse);
  }

  public ResponseEntity<CardDtoResponse> updateCard(String cardId, CardDtoPut cardDto) {
    var cardResponse = cardService.updateCard(cardId, cardDto);
    return ResponseEntity.ok(cardResponse);
  }

  public ResponseEntity<CardDtoResponse> patchCard(String cardId, CardDtoPatch cardDto) {
    var cardResponse = cardService.updateCard(cardId, cardDto);
    return ResponseEntity.ok(cardResponse);
  }


  public ResponseEntity<Void> deleteCard(String cardId) {
    cardService.deleteCard(cardId);
    return ResponseEntity.noContent().build();
  }
}