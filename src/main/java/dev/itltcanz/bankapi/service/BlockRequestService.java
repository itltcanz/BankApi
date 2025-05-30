package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.RequestAlreadyProcessedException;
import dev.itltcanz.bankapi.repository.BlockRequestRepo;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockRequestService {
    private final BlockRequestRepo requestRepo;
    private final CardService cardService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public BlockRequestDtoResponse getRequest(String requestId) {
        var blockingRequest = findByIdValid(requestId);
        return modelMapper.map(blockingRequest, BlockRequestDtoResponse.class);
    }

    public BlockRequestDtoResponse createRequest(BlockRequestDtoCreate dto) {
        Card card = cardService.findByIdValid(dto.getCardId());
        if (card.getStatus().equals(CardStatus.BLOCKED)) {
            throw new IllegalStateException("The card has already been blocked");
        }
        BlockRequest request = new BlockRequest();
        request.setCardId(dto.getCardId());
        request.setUserId(userService.getCurrentUser().getId().toString());
        var savedRequest = requestRepo.save(request);
        return modelMapper.map(savedRequest, BlockRequestDtoResponse.class);
    }

    public BlockRequestDtoResponse approveRequest(String requestId) {
        BlockRequest request = findByIdValid(requestId);
        checkRequestStatus(request.getStatus());
        Card card = cardService.findByIdValid(request.getCardId());
        card.setStatus(CardStatus.BLOCKED);
        request.setStatus(RequestStatus.APPROVED);
        request.setAdminId(userService.getCurrentUser().getId().toString());
        request.setUpdatedAt(LocalDateTime.now());
        cardService.save(card);
        var savedRequest = requestRepo.save(request);
        return modelMapper.map(savedRequest, BlockRequestDtoResponse.class);
    }

    public BlockRequestDtoResponse rejectRequest(@NotNull String requestId) {
        var request = findByIdValid(requestId);
        checkRequestStatus(request.getStatus());
        request.setStatus(RequestStatus.REJECTED);
        request.setAdminId(userService.getCurrentUser().getId().toString());
        request.setUpdatedAt(LocalDateTime.now());
        var savedRequest = requestRepo.save(request);
        return modelMapper.map(savedRequest, BlockRequestDtoResponse.class);
    }

    public BlockRequest findByIdValid(String requestId) {
        var request = requestRepo.findById(UUID.fromString(requestId))
            .orElseThrow(() -> new NotFoundException("A block request with id " + requestId + " was not found"));
        userService.hasRights(request.getUserId());
        return request;
    }

    public void checkRequestStatus(RequestStatus requestStatus) {
        if (requestStatus != RequestStatus.PENDING) {
            throw new RequestAlreadyProcessedException("The request has already been processed");
        }
    }
}