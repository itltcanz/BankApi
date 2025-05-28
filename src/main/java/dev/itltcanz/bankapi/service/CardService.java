package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoPatch;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.dto.card.CardDtoPut;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.util.CardNumberGenerator;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepo cardRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public CardDtoResponse createCard(CardDtoCreate cardDto) {
        var card = new Card();
        card.setNumber(CardNumberGenerator.generateCardNumber());
        card.setStatus(CardStatus.ACTIVE);
        modelMapper.map(cardDto, card);
        var savedCard = cardRepo.save(card);
        return modelMapper.map(savedCard, CardDtoResponse.class);
    }

    public Page<CardDtoResponse> getCards(PageRequest pageable) {
        return cardRepo.findAll(pageable)
            .map(card -> modelMapper.map(card, CardDtoResponse.class));
    }

    public void deleteCard(String cardId) {
        var card = findByIdValid(cardId);
        cardRepo.delete(card);
    }

    public CardDtoResponse getCardById(@NotNull String cardId) {
        userService.hasRights(cardId);
        var card = findByIdValid(cardId);
        return modelMapper.map(card, CardDtoResponse.class);
    }

    public CardDtoResponse putCard(@NotNull String cardId, @NotNull CardDtoPut cardDto) {
        var cardEntity = findByIdValid(cardId);
        modelMapper.map(cardDto, cardEntity);
        var savedCard = cardRepo.save(cardEntity);
        return modelMapper.map(savedCard, CardDtoResponse.class);
    }

    public CardDtoResponse patchCard(@NotNull String cardId, @NotNull CardDtoPatch cardDto) {
        var cardEntity = findByIdValid(cardId);
        modelMapper.map(cardDto, cardEntity);
        var savedCard = cardRepo.save(cardEntity);
        return modelMapper.map(savedCard, CardDtoResponse.class);
    }

    public Card findByIdValid(String cardId) {
        var currentUser = userService.getCurrentUser();
        var card = cardRepo.findById(cardId)
            .orElseThrow(() -> new NotFoundException("A card with the number " + cardId + " has not been found."));
        if (!card.getOwner().getId().equals(currentUser.getId()) && !currentUser.getRole().equals(Role.ROLE_ADMIN.toString())) {
            throw new AccessDeniedException("The card parameters are specified incorrectly");
        }
        return card;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Card save(Card card) {
        return cardRepo.save(card);
    }
}