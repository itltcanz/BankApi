package dev.itltcanz.bankapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoPut;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.filter.CardFilter;
import dev.itltcanz.bankapi.filter.CardSpecification;
import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.service.impl.AuthServiceImpl;
import dev.itltcanz.bankapi.service.impl.CardNumberGeneratorServiceImpl;
import dev.itltcanz.bankapi.service.impl.CardServiceImpl;
import dev.itltcanz.bankapi.service.impl.PermissionServiceImpl;
import dev.itltcanz.bankapi.service.impl.UserServiceImpl;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

  @Mock
  private CardRepo cardRepo;
  @Mock
  private CardSpecification specification;
  @Mock
  private ModelMapper modelMapper;

  @Mock
  private AuthServiceImpl authService;
  @Mock
  private UserServiceImpl userService;
  @Mock
  private PermissionServiceImpl permissionService;
  @Mock
  private CardNumberGeneratorServiceImpl cardNumberGeneratorService;
  @InjectMocks
  private CardServiceImpl cardService;

  private Card card;
  private User user;
  private CardDtoResponse response;

  private CardFilter filter;
  private PageRequest pageRequest;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setRole(Role.ROLE_USER);

    card = new Card();
    card.setNumber("4561261212345467");
    card.setStatus(CardStatus.ACTIVE);
    card.setOwner(user);
    card.setBalance(BigDecimal.ZERO);

    response = new CardDtoResponse();
    response.setNumber(card.getNumber());
    response.setStatus(card.getStatus().toString());
    response.setOwnerId(user.getId().toString());
    response.setBalance(card.getBalance());

    filter = new CardFilter(card.getStatus(), card.getNumber(), null);
    pageRequest = PageRequest.of(0, 1);
  }

  // Тесты для createCard
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void createCard_success() {
    var dtoCreate = new CardDtoCreate();
    dtoCreate.setOwnerId(user.getId().toString());
    dtoCreate.setValidityPeriod(YearMonth.of(2030, 6));
    dtoCreate.setBalance(BigDecimal.valueOf(10000));

    when(userService.findUserById(dtoCreate.getOwnerId())).thenReturn(user);
    when(cardNumberGeneratorService.generateCardNumber()).thenReturn(card.getNumber());
    when(cardRepo.save(any(Card.class))).thenReturn(card);
    when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

    var result = cardService.createCard(dtoCreate);

    assertNotNull(result);
    assertEquals(card.getNumber(), result.getNumber());
    assertEquals(CardStatus.ACTIVE.toString(), result.getStatus());
    verify(cardRepo).save(any(Card.class));
    verify(modelMapper).map(card, CardDtoResponse.class);
  }

  // Тесты для getCards
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void getUserCards_success() {
    // Arrange
    var cardPage = new PageImpl<>(List.of(card), pageRequest, 1);

    when(authService.getCurrentUser()).thenReturn(user);
    when(specification.withFilter(filter)).thenReturn(
        null); // Заменили Specification.where(null) на null
    when(cardRepo.findAll((Specification<Card>) null, pageRequest)).thenReturn(cardPage);
    when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

    // Act
    var userCards = cardService.getUserCards(filter, pageRequest);

    // Assert
    assertNotNull(userCards);
    assertEquals(1, userCards.getTotalElements());
    assertEquals(card.getNumber(), userCards.getContent().get(0).getNumber());
    assertEquals(CardStatus.ACTIVE.toString(), userCards.getContent().get(0).getStatus());

    // Verify
    verify(authService).getCurrentUser();
    verify(specification).withFilter(filter);
    verify(cardRepo).findAll((Specification<Card>) null, pageRequest);
    verify(modelMapper).map(card, CardDtoResponse.class);
  }

  @Test
  @WithMockUser
  void getAdminCards_success() {
    // Arrange
    Page<Card> cardPage = new PageImpl<>(List.of(card), pageRequest, 1);

    when(specification.withFilter(filter)).thenReturn(
        null); // Заменили Specification.where(null) на null
    when(cardRepo.findAll((Specification<Card>) null, pageRequest)).thenReturn(cardPage);
    when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

    // Act
    Page<CardDtoResponse> result = cardService.getAdminCards(filter, pageRequest);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(card.getNumber(), result.getContent().get(0).getNumber());
    assertEquals(CardStatus.ACTIVE.toString(), result.getContent().get(0).getStatus());

    // Verify
    verify(specification).withFilter(filter);
    verify(cardRepo).findAll((Specification<Card>) null, pageRequest);
    verify(modelMapper).map(card, CardDtoResponse.class);
  }

  // Тесты для deleteCard
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void deleteCard_success() {
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
    doNothing().when(permissionService).hasRights(card.getOwner().getId().toString());

    cardService.deleteCard(card.getNumber());

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());
    verify(cardRepo).delete(card);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void deleteCard_notFound_throwsNotFoundException() {
    String cardId = "1234567890123456";
    when(cardRepo.findById(cardId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cardService.deleteCard(cardId));

    verify(permissionService, never()).hasRights(any(String.class));
    verify(cardRepo).findById(cardId);
    verify(cardRepo, never()).delete(any(Card.class));
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void deleteCard_accessDenied_throwsAccessDeniedException() {
    User otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setRole(Role.ROLE_USER);
    card.setOwner(otherUser);

    doThrow(new AccessDeniedException("Access denied"))
        .when(permissionService).hasRights(otherUser.getId().toString());
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));

    assertThrows(AccessDeniedException.class, () -> cardService.deleteCard(card.getNumber()));

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());
    verify(cardRepo, never()).delete(any(Card.class));
  }

  // Тесты для getCardById
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void getCardById_success() {
    doNothing().when(permissionService).hasRights(card.getOwner().getId().toString());
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
    when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

    var result = cardService.getCardById(card.getNumber());

    assertNotNull(result);
    assertEquals(card.getNumber(), result.getNumber());

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());
    verify(modelMapper).map(card, CardDtoResponse.class);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void getCardById_notFound_throwsNotFoundException() {
    String cardId = "1234567890123456";

    when(cardRepo.findById(cardId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cardService.getCardById(cardId));

    verify(cardRepo).findById(cardId);
    verify(modelMapper, never()).map(any(), eq(CardDtoResponse.class));

  }

  // Тесты для putCard
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void updateCard_success() {
    CardDtoPut dto = new CardDtoPut();
    dto.setOwnerId(user.getId().toString());

    doNothing().when(permissionService).hasRights(card.getOwner().getId().toString());
    doNothing().when(modelMapper).map(dto, card);
    when(userService.findUserById(user.getId().toString())).thenReturn(user);
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
    when(cardRepo.save(any(Card.class))).thenReturn(card);
    when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

    CardDtoResponse result = cardService.updateCard(card.getNumber(), dto);

    assertNotNull(result);
    assertEquals(card.getNumber(), result.getNumber());

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());
    verify(modelMapper).map(dto, card);
    verify(cardRepo).save(card);
    verify(modelMapper).map(card, CardDtoResponse.class);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void updateCard_notFound_throwsNotFoundException() {
    String cardId = "1234567890123456";
    CardDtoPut dto = new CardDtoPut();

    when(cardRepo.findById(cardId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cardService.updateCard(cardId, dto));

    verify(cardRepo).findById(cardId);
    verify(cardRepo, never()).save(any(Card.class));
    verify(modelMapper, never()).map(any(), any());
  }

  // Тесты для findByIdWithPermissionCheck
  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void findByIdWithPermissionCheck_success_userOwner() {
    doNothing().when(permissionService).hasRights(card.getOwner().getId().toString());
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));

    Card result = cardService.findByIdWithPermissionCheck(card.getNumber());

    assertNotNull(result);
    assertEquals(card.getNumber(), result.getNumber());

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());

  }

  @Test
  @WithMockUser(username = "testadmin", roles = "ADMIN")
  void findByIdWithPermissionCheck_success_admin() {
    User otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setRole(Role.ROLE_USER);
    card.setOwner(otherUser);
    User admin = new User();
    admin.setId(UUID.randomUUID());
    admin.setRole(Role.ROLE_ADMIN);

    doNothing().when(permissionService).hasRights(card.getOwner().getId().toString());
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));

    Card result = cardService.findByIdWithPermissionCheck(card.getNumber());

    assertNotNull(result);
    assertEquals(card.getNumber(), result.getNumber());

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());

  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void findByIdWithPermissionCheck_notFound_throwsNotFoundException() {
    String cardId = "1234567890123456";

    when(cardRepo.findById(cardId)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> cardService.findByIdWithPermissionCheck(cardId));

    verify(permissionService, never()).hasRights(any(String.class));
    verify(cardRepo).findById(cardId);
  }

  @Test
  @WithMockUser(username = "testuser", roles = "USER")
  void findByIdWithPermissionCheck_accessDenied_throwsAccessDeniedException() {
    User otherUser = new User();
    otherUser.setId(UUID.randomUUID());
    otherUser.setRole(Role.ROLE_USER);
    card.setOwner(otherUser);

    doThrow(new AccessDeniedException("Access denied"))
        .when(permissionService).hasRights(otherUser.getId().toString());
    when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));

    assertThrows(AccessDeniedException.class,
        () -> cardService.findByIdWithPermissionCheck(card.getNumber()));

    verify(permissionService).hasRights(card.getOwner().getId().toString());
    verify(cardRepo).findById(card.getNumber());
  }

  // Тесты для save
  @Test
  void save_success() {
    when(cardRepo.save(card)).thenReturn(card);

    Card result = cardService.save(card);

    assertNotNull(result);
    assertEquals(card.getNumber(), result.getNumber());

    verify(cardRepo).save(card);
  }
}
