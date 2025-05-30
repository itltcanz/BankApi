package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.card.CardDtoCreate;
import dev.itltcanz.bankapi.dto.card.CardDtoPatch;
import dev.itltcanz.bankapi.dto.card.CardDtoPut;
import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.Role;

import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.repository.CardRepo;
import dev.itltcanz.bankapi.util.CardNumberGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @Mock
    private CardRepo cardRepo;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CardService cardService;

    private Card card;
    private User user;
    private CardDtoResponse response;
    private MockedStatic<CardNumberGenerator> cardNumberGenerator;

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

        cardNumberGenerator = mockStatic(CardNumberGenerator.class);
        cardNumberGenerator.when(CardNumberGenerator::generateCardNumber).thenReturn(card.getNumber());
    }

    @AfterEach
    void tearDown() {
        cardNumberGenerator.close();
    }

    // Тесты для createCard
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createCard_success() {
        CardDtoCreate dto = new CardDtoCreate();
        doNothing().when(modelMapper).map(any(CardDtoCreate.class), any(Card.class));
        when(cardRepo.save(any(Card.class))).thenReturn(card);
        when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

        CardDtoResponse result = cardService.createCard(dto);

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
        assertEquals(CardStatus.ACTIVE.toString(), result.getStatus());
        verify(modelMapper).map(any(CardDtoCreate.class), any(Card.class));
        verify(cardRepo).save(any(Card.class));
        verify(modelMapper).map(card, CardDtoResponse.class);
    }

    // Тесты для getCards
    @Test
    @WithMockUser
    void getCards_success() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<Card> cardPage = new PageImpl<>(List.of(card), pageRequest, 1);
        when(cardRepo.findAll(pageRequest)).thenReturn(cardPage);
        when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

        Page<CardDtoResponse> result = cardService.getCards(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(card.getNumber(), result.getContent().get(0).getNumber());
        verify(cardRepo).findAll(pageRequest);
        verify(modelMapper).map(card, CardDtoResponse.class);
    }

    // Тесты для deleteCard
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteCard_success() {
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);

        cardService.deleteCard(card.getNumber());

        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
        verify(cardRepo).delete(card);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteCard_notFound_throwsNotFoundException() {
        String cardId = "1234567890123456";
        when(cardRepo.findById(cardId)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(NotFoundException.class, () -> cardService.deleteCard(cardId));
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
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(AccessDeniedException.class, () -> cardService.deleteCard(card.getNumber()));
        verify(cardRepo).findById(card.getNumber());
        verify(cardRepo, never()).delete(any(Card.class));
    }

    // Тесты для getCardById
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getCardById_success() {
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);
        when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

        CardDtoResponse result = cardService.getCardById(card.getNumber());

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
        verify(userService).hasRights(card.getNumber());
        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
        verify(modelMapper).map(card, CardDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getCardById_notFound_throwsNotFoundException() {
        String cardId = "1234567890123456";
        when(cardRepo.findById(cardId)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(NotFoundException.class, () -> cardService.getCardById(cardId));
        verify(userService).hasRights(cardId);
        verify(cardRepo).findById(cardId);
        verify(modelMapper, never()).map(any(), eq(CardDtoResponse.class));
    }

    // Тесты для putCard
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void putCard_success() {
        CardDtoPut dto = new CardDtoPut();
        doNothing().when(modelMapper).map(dto, card);
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cardRepo.save(any(Card.class))).thenReturn(card);
        when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

        CardDtoResponse result = cardService.putCard(card.getNumber(), dto);

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
        verify(modelMapper).map(dto, card);
        verify(cardRepo).save(card);
        verify(modelMapper).map(card, CardDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void putCard_notFound_throwsNotFoundException() {
        String cardId = "1234567890123456";
        CardDtoPut dto = new CardDtoPut();
        when(cardRepo.findById(cardId)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(NotFoundException.class, () -> cardService.putCard(cardId, dto));
        verify(cardRepo).findById(cardId);
        verify(cardRepo, never()).save(any(Card.class));
        verify(modelMapper, never()).map(any(), any());
    }

    // Тесты для patchCard
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void patchCard_success() {
        CardDtoPatch dto = new CardDtoPatch();
        doNothing().when(modelMapper).map(dto, card);
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);
        when(cardRepo.save(any(Card.class))).thenReturn(card);
        when(modelMapper.map(card, CardDtoResponse.class)).thenReturn(response);

        CardDtoResponse result = cardService.patchCard(card.getNumber(), dto);

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
        verify(modelMapper).map(dto, card);
        verify(cardRepo).save(card);
        verify(modelMapper).map(card, CardDtoResponse.class);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void patchCard_notFound_throwsNotFoundException() {
        String cardId = "1234567890123456";
        CardDtoPatch dto = new CardDtoPatch();
        when(cardRepo.findById(cardId)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(NotFoundException.class, () -> cardService.patchCard(cardId, dto));
        verify(cardRepo).findById(cardId);
        verify(cardRepo, never()).save(any(Card.class));
        verify(modelMapper, never()).map(any(), any());
    }

    // Тесты для findByIdValid
    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void findByIdValid_Role_success_userOwner() {
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);

        Card result = cardService.findByIdValidRole(card.getNumber());

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void findByIdValid_Role_success_admin() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setRole(Role.ROLE_USER);
        card.setOwner(otherUser);
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setRole(Role.ROLE_ADMIN);
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(admin);

        Card result = cardService.findByIdValidRole(card.getNumber());

        assertNotNull(result);
        assertEquals(card.getNumber(), result.getNumber());
        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void findByIdValid_Role_notFound_throwsNotFoundException() {
        String cardId = "1234567890123456";
        when(cardRepo.findById(cardId)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(NotFoundException.class, () -> cardService.findByIdValidRole(cardId));
        verify(cardRepo).findById(cardId);
        verify(userService).getCurrentUser();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void findByIdValid_Role_accessDenied_throwsAccessDeniedException() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        otherUser.setRole(Role.ROLE_USER);
        card.setOwner(otherUser);
        when(cardRepo.findById(card.getNumber())).thenReturn(Optional.of(card));
        when(userService.getCurrentUser()).thenReturn(user);

        assertThrows(AccessDeniedException.class, () -> cardService.findByIdValidRole(card.getNumber()));
        verify(cardRepo).findById(card.getNumber());
        verify(userService).getCurrentUser();
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
