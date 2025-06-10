package dev.itltcanz.bankapi.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import dev.itltcanz.bankapi.service.impl.AuthServiceImpl;
import dev.itltcanz.bankapi.service.impl.BlockRequestServiceImpl;
import dev.itltcanz.bankapi.service.impl.CardServiceImpl;
import dev.itltcanz.bankapi.service.impl.PermissionServiceImpl;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class BlockRequestServiceTest {

  @Mock
  private BlockRequestRepo requestRepo;
  @Mock
  private CardServiceImpl cardService;
  @Mock
  private AuthServiceImpl authService;
  @Mock
  private PermissionServiceImpl permissionService;
  @Mock
  private ModelMapper modelMapper;
  @InjectMocks
  private BlockRequestServiceImpl blockRequestService;

  private Card card;
  private User user;
  private BlockRequest blockRequest;
  private BlockRequestDtoResponse blockResponse;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    card = new Card();
    card.setNumber("4561261212345467");
    card.setStatus(CardStatus.ACTIVE);
    blockRequest = new BlockRequest();
    blockRequest.setId(UUID.randomUUID());
    blockRequest.setCard(card);
    blockRequest.setUser(user);
    blockRequest.setStatus(RequestStatus.PENDING);

    blockResponse = new BlockRequestDtoResponse();
    blockResponse.setId(blockRequest.getId().toString());
    blockResponse.setCardId(blockRequest.getCard().getNumber());
    blockResponse.setUserId(blockRequest.getUser().getId().toString());
    blockResponse.setStatus(blockRequest.getStatus());
  }

  // Тесты для getRequest
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void getRequest_success() {
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(blockRequest));
    when(modelMapper.map(blockRequest, BlockRequestDtoResponse.class)).thenReturn(blockResponse);

    var result = blockRequestService.getRequest(blockRequest.getId().toString());

    assertNotNull(result);
    assertEquals(blockResponse.getId(), result.getId());
    assertEquals(blockResponse.getCardId(), result.getCardId());
    verify(requestRepo).findById(any(UUID.class));
    verify(permissionService).hasRights(blockRequest.getUser().getId().toString());
    verify(modelMapper).map(blockRequest, BlockRequestDtoResponse.class);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void getRequest_notFound_throwsNotFoundException() {
    String requestId = UUID.randomUUID().toString();
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> blockRequestService.getRequest(requestId));
    verify(requestRepo).findById(any(UUID.class));
    verify(permissionService, never()).hasRights(any(String.class));
    verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
  }

  // Тесты для createRequest
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void createRequest_success() {
    BlockRequestDtoCreate dto = new BlockRequestDtoCreate(card.getNumber());
    when(cardService.findByIdWithPermissionCheck(card.getNumber())).thenReturn(card);
    when(authService.getCurrentUser()).thenReturn(user);
    when(requestRepo.save(any(BlockRequest.class))).thenReturn(blockRequest);
    when(modelMapper.map(blockRequest, BlockRequestDtoResponse.class)).thenReturn(blockResponse);

    BlockRequestDtoResponse result = blockRequestService.createRequest(dto);

    assertNotNull(result);
    assertEquals(blockResponse.getId(), result.getId());
    assertEquals(blockResponse.getCardId(), result.getCardId());
    verify(cardService).findByIdWithPermissionCheck(card.getNumber());
    verify(authService).getCurrentUser();
    verify(requestRepo).save(any(BlockRequest.class));
    verify(modelMapper).map(blockRequest, BlockRequestDtoResponse.class);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void createRequest_cardBlocked_throwsIllegalStateException() {
    BlockRequestDtoCreate dto = new BlockRequestDtoCreate(card.getNumber());
    card.setStatus(CardStatus.BLOCKED);
    when(cardService.findByIdWithPermissionCheck(card.getNumber())).thenReturn(card);

    assertThrows(IllegalStateException.class, () -> blockRequestService.createRequest(dto));
    verify(cardService).findByIdWithPermissionCheck(card.getNumber());
    verify(requestRepo, never()).save(any(BlockRequest.class));
    verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
  }

  // Тесты для approveRequest
  @Test
  @WithMockUser(username = "testadmin", roles = "ADMIN")
  void approveRequest_success() {
    BlockRequest updatedRequest = new BlockRequest();
    updatedRequest.setId(blockRequest.getId());
    updatedRequest.setCard(blockRequest.getCard());
    updatedRequest.setUser(blockRequest.getUser());
    updatedRequest.setStatus(RequestStatus.APPROVED);
    var updatedResponse = new BlockRequestDtoResponse();
    updatedResponse.setId(blockRequest.getId().toString());
    updatedResponse.setCardId(blockRequest.getCard().getNumber());
    updatedResponse.setStatus(RequestStatus.APPROVED);

    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(blockRequest));
    when(cardService.findByIdWithPermissionCheck(card.getNumber())).thenReturn(card);
    when(authService.getCurrentUser()).thenReturn(user);
    when(cardService.save(any(Card.class))).thenReturn(card);
    when(requestRepo.save(any(BlockRequest.class))).thenReturn(updatedRequest);
    when(modelMapper.map(updatedRequest, BlockRequestDtoResponse.class)).thenReturn(
        updatedResponse);

    var result = blockRequestService.approveRequest(blockRequest.getId().toString());

    assertNotNull(result);
    assertEquals(RequestStatus.APPROVED, result.getStatus());
    assertEquals(card.getNumber(), result.getCardId());
    verify(requestRepo).findById(any(UUID.class));
    verify(cardService).findByIdWithPermissionCheck(card.getNumber());
    verify(cardService).save(any(Card.class));
    verify(requestRepo).save(any(BlockRequest.class));
    verify(permissionService).hasRights(blockRequest.getUser().getId().toString());
    verify(authService).getCurrentUser();
    verify(modelMapper).map(updatedRequest, BlockRequestDtoResponse.class);
  }

  @Test
  @WithMockUser(username = "testadmin", roles = "ADMIN")
  void approveRequest_notFound_throwsNotFoundException() {
    String requestId = UUID.randomUUID().toString();
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> blockRequestService.approveRequest(requestId));
    verify(requestRepo).findById(any(UUID.class));
    verify(cardService, never()).findByIdWithPermissionCheck(any(String.class));
    verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
  }

  @Test
  @WithMockUser(username = "testadmin", roles = "ADMIN")
  void approveRequest_alreadyProcessed_throwsRequestAlreadyProcessedException() {
    blockRequest.setStatus(RequestStatus.APPROVED);
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(blockRequest));

    assertThrows(RequestAlreadyProcessedException.class,
        () -> blockRequestService.approveRequest(blockRequest.getId().toString()));
    verify(requestRepo).findById(any(UUID.class));
    verify(cardService, never()).findByIdWithPermissionCheck(any(String.class));
    verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
  }

  // Тесты для rejectRequest
  @Test
  @WithMockUser(username = "testadmin", roles = "ADMIN")
  void rejectRequest_success() {
    BlockRequest updatedRequest = new BlockRequest();
    updatedRequest.setId(blockRequest.getId());
    updatedRequest.setCard(blockRequest.getCard());
    updatedRequest.setUser(blockRequest.getUser());
    updatedRequest.setStatus(RequestStatus.REJECTED);
    BlockRequestDtoResponse updatedResponse = new BlockRequestDtoResponse();
    updatedResponse.setId(blockRequest.getId().toString());
    updatedResponse.setCardId(blockRequest.getCard().getNumber());
    updatedResponse.setStatus(RequestStatus.REJECTED);

    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(blockRequest));
    when(authService.getCurrentUser()).thenReturn(user);
    when(requestRepo.save(any(BlockRequest.class))).thenReturn(updatedRequest);
    when(modelMapper.map(updatedRequest, BlockRequestDtoResponse.class)).thenReturn(
        updatedResponse);

    var result = blockRequestService.rejectRequest(blockRequest.getId().toString());

    assertNotNull(result);
    assertEquals(RequestStatus.REJECTED, result.getStatus());
    assertEquals(blockRequest.getCard().getNumber(), result.getCardId());
    verify(requestRepo).findById(any(UUID.class));
    verify(requestRepo).save(any(BlockRequest.class));
    verify(permissionService).hasRights(blockRequest.getUser().getId().toString());
    verify(authService).getCurrentUser();
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
    blockRequest.setStatus(RequestStatus.REJECTED);
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(blockRequest));

    assertThrows(RequestAlreadyProcessedException.class,
        () -> blockRequestService.rejectRequest(blockRequest.getId().toString()));
    verify(requestRepo).findById(any(UUID.class));
    verify(requestRepo, never()).save(any(BlockRequest.class));
    verify(modelMapper, never()).map(any(), eq(BlockRequestDtoResponse.class));
  }

  // Тесты для findByIdValid
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void findByIdWithPermissionCheck_success() {
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.of(blockRequest));

    var result = blockRequestService.findByIdWithPermissionCheck(blockRequest.getId().toString());

    assertNotNull(result);
    assertEquals(blockRequest.getId(), result.getId());
    verify(requestRepo).findById(any(UUID.class));
    verify(permissionService).hasRights(blockRequest.getUser().getId().toString());
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void findByIdWithPermissionCheck_notFound_throwsNotFoundException() {
    String requestId = UUID.randomUUID().toString();
    when(requestRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class,
        () -> blockRequestService.findByIdWithPermissionCheck(requestId));
    verify(requestRepo).findById(any(UUID.class));
    verify(permissionService, never()).hasRights(any(String.class));
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