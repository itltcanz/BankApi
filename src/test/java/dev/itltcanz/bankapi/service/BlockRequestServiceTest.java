package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.request.BlockRequestDtoCreate;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.RequestStatus;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.RequestAlreadyProcessedException;
import dev.itltcanz.bankapi.repository.BlockRequestRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockRequestServiceTest {
    @Mock
    private BlockRequestRepo requestRepo;
    @Mock
    private CardService cardService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private BlockRequestService blockRequestService;

    private Card card;
    private User user;
    private BlockRequest request;
    private BlockRequestDtoResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        card = new Card();
        card.setNumber("4561261212345467");
        card.setStatus(CardStatus.ACTIVE);
        request = new BlockRequest();
        request.setId(UUID.randomUUID().toString());
        request.setCardId(card.getNumber());
        request.setUserId(user.getId().toString());
        request.setStatus(RequestStatus.PENDING);
        response = new BlockRequestDtoResponse();
        response.setId(request.getId());
        response.setCardId(request.getCardId());
        response.setUserId(request.getUserId());
        response.setStatus(request.getStatus());
    }

    // Тесты для getRequest
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getRequest_success() {
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(request));
        when(modelMapper.map(request, BlockRequestDtoResponse.class)).thenReturn(response);

        BlockRequestDtoResponse result = blockRequestService.getRequest(request.getId());

        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getCardId(), result.getCardId());
        verify(requestRepo).findById(any(UUID.class));
        verify(userService).hasRights(request.getUserId());
        verify(modelMapper).map(request, BlockRequestDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getRequest_notFound_throwsNotFoundException() {
        String requestId = UUID.randomUUID().toString();
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> blockRequestService.getRequest(requestId));
        verify(requestRepo).findById(any(UUID.class));
        verify(userService, never()).hasRights(any(String.class));
        verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
    }

    // Тесты для createRequest
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createRequest_success() {
        BlockRequestDtoCreate dto = new BlockRequestDtoCreate(card.getNumber());
        when(cardService.findByIdValid(card.getNumber())).thenReturn(card);
        when(userService.getCurrentUser()).thenReturn(user);
        when(requestRepo.save(any(BlockRequest.class))).thenReturn(request);
        when(modelMapper.map(request, BlockRequestDtoResponse.class)).thenReturn(response);

        BlockRequestDtoResponse result = blockRequestService.createRequest(dto);

        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getCardId(), result.getCardId());
        verify(cardService).findByIdValid(card.getNumber());
        verify(userService).getCurrentUser();
        verify(requestRepo).save(any(BlockRequest.class));
        verify(modelMapper).map(request, BlockRequestDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createRequest_cardBlocked_throwsIllegalStateException() {
        BlockRequestDtoCreate dto = new BlockRequestDtoCreate(card.getNumber());
        card.setStatus(CardStatus.BLOCKED);
        when(cardService.findByIdValid(card.getNumber())).thenReturn(card);

        assertThrows(IllegalStateException.class, () -> blockRequestService.createRequest(dto));
        verify(cardService).findByIdValid(card.getNumber());
        verify(requestRepo, never()).save(any(BlockRequest.class));
        verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
    }

    // Тесты для approveRequest
    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void approveRequest_success() {
        BlockRequest updatedRequest = new BlockRequest();
        updatedRequest.setId(request.getId());
        updatedRequest.setCardId(request.getCardId());
        updatedRequest.setUserId(request.getUserId());
        updatedRequest.setStatus(RequestStatus.APPROVED);
        BlockRequestDtoResponse updatedResponse = new BlockRequestDtoResponse();
        updatedResponse.setId(request.getId());
        updatedResponse.setCardId(request.getCardId());
        updatedResponse.setStatus(RequestStatus.APPROVED);

        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(request));
        when(cardService.findByIdValid(card.getNumber())).thenReturn(card);
        when(userService.getCurrentUser()).thenReturn(user);
        when(cardService.save(any(Card.class))).thenReturn(card);
        when(requestRepo.save(any(BlockRequest.class))).thenReturn(updatedRequest);
        when(modelMapper.map(updatedRequest, BlockRequestDtoResponse.class)).thenReturn(updatedResponse);

        BlockRequestDtoResponse result = blockRequestService.approveRequest(request.getId());

        assertNotNull(result);
        assertEquals(RequestStatus.APPROVED, result.getStatus());
        assertEquals(card.getNumber(), result.getCardId());
        verify(requestRepo).findById(any(UUID.class));
        verify(cardService).findByIdValid(card.getNumber());
        verify(cardService).save(any(Card.class));
        verify(requestRepo).save(any(BlockRequest.class));
        verify(userService).hasRights(request.getUserId());
        verify(userService).getCurrentUser();
        verify(modelMapper).map(updatedRequest, BlockRequestDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void approveRequest_notFound_throwsNotFoundException() {
        String requestId = UUID.randomUUID().toString();
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> blockRequestService.approveRequest(requestId));
        verify(requestRepo).findById(any(UUID.class));
        verify(cardService, never()).findByIdValid(any(String.class));
        verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void approveRequest_alreadyProcessed_throwsRequestAlreadyProcessedException() {
        request.setStatus(RequestStatus.APPROVED);
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(request));

        assertThrows(RequestAlreadyProcessedException.class, () -> blockRequestService.approveRequest(request.getId()));
        verify(requestRepo).findById(any(UUID.class));
        verify(cardService, never()).findByIdValid(any(String.class));
        verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
    }

    // Тесты для rejectRequest
    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void rejectRequest_success() {
        BlockRequest updatedRequest = new BlockRequest();
        updatedRequest.setId(request.getId());
        updatedRequest.setCardId(request.getCardId());
        updatedRequest.setUserId(request.getUserId());
        updatedRequest.setStatus(RequestStatus.REJECTED);
        BlockRequestDtoResponse updatedResponse = new BlockRequestDtoResponse();
        updatedResponse.setId(request.getId());
        updatedResponse.setCardId(request.getCardId());
        updatedResponse.setStatus(RequestStatus.REJECTED);

        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(request));
        when(userService.getCurrentUser()).thenReturn(user);
        when(requestRepo.save(any(BlockRequest.class))).thenReturn(updatedRequest);
        when(modelMapper.map(updatedRequest, BlockRequestDtoResponse.class)).thenReturn(updatedResponse);

        BlockRequestDtoResponse result = blockRequestService.rejectRequest(request.getId());

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals(request.getCardId(), result.getCardId());
        verify(requestRepo).findById(any(UUID.class));
        verify(requestRepo).save(any(BlockRequest.class));
        verify(userService).hasRights(request.getUserId());
        verify(userService).getCurrentUser();
        verify(modelMapper).map(updatedRequest, BlockRequestDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void rejectRequest_notFound_throwsNotFoundException() {
        String requestId = UUID.randomUUID().toString();
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> blockRequestService.rejectRequest(requestId));
        verify(requestRepo).findById(any(UUID.class));
        verify(requestRepo, never()).save(any(BlockRequest.class));
        verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void rejectRequest_alreadyProcessed_throwsRequestAlreadyProcessedException() {
        request.setStatus(RequestStatus.REJECTED);
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(request));

        assertThrows(RequestAlreadyProcessedException.class, () -> blockRequestService.rejectRequest(request.getId()));
        verify(requestRepo).findById(any(UUID.class));
        verify(requestRepo, never()).save(any(BlockRequest.class));
        verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
    }

    // Тесты для findByIdValid
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void findByIdValid_success() {
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(request));

        BlockRequest result = blockRequestService.findByIdValid(request.getId());

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        verify(requestRepo).findById(any(UUID.class));
        verify(userService).hasRights(request.getUserId());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void findByIdValid_notFound_throwsNotFoundException() {
        String requestId = UUID.randomUUID().toString();
        when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> blockRequestService.findByIdValid(requestId));
        verify(requestRepo).findById(any(UUID.class));
        verify(userService, never()).hasRights(any(String.class));
    }

    // Тесты для checkRequestStatus
    @Test
    void checkRequestStatus_pending_success() {
        assertDoesNotThrow(() -> blockRequestService.checkRequestStatus(RequestStatus.PENDING));
    }

    @Test
    void checkRequestStatus_nonPending_throwsRequestAlreadyProcessedException() {
        assertThrows(RequestAlreadyProcessedException.class,
            () -> blockRequestService.checkRequestStatus(RequestStatus.APPROVED));
        assertThrows(RequestAlreadyProcessedException.class,
            () -> blockRequestService.checkRequestStatus(RequestStatus.REJECTED));
    }
}