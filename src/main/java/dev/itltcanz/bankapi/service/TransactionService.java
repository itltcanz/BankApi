package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.transaction.TransactionDtoCreate;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.entity.enumeration.CardStatus;
import dev.itltcanz.bankapi.entity.enumeration.TransactionStatus;
import dev.itltcanz.bankapi.exception.InactiveCardException;
import dev.itltcanz.bankapi.exception.InsufficientFundsException;
import dev.itltcanz.bankapi.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepo transactionRepo;
    private final CardService cardService;
    private final ModelMapper modelMapper;

    @Transactional
    public TransactionDtoResponse createTransaction(TransactionDtoCreate transactionDto) {
        var transaction = modelMapper.map(transactionDto, Transaction.class);

        var senderCard = cardService.findByIdValid(transaction.getSenderCardId());
        var receiverCard = cardService.findByIdValid(transaction.getReceiverCardId());

        if (senderCard.getStatus() != CardStatus.ACTIVE || receiverCard.getStatus() != CardStatus.ACTIVE) {
            throw new InactiveCardException("One of the cards is inactive");
        }

        if (senderCard.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InsufficientFundsException("There are insufficient funds on the " + senderCard.getNumber() + " card");
        }

        senderCard.setBalance(senderCard.getBalance().subtract(transaction.getAmount()));
        receiverCard.setBalance(receiverCard.getBalance().add(transaction.getAmount()));
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCreatedAt(LocalDateTime.now());
        cardService.save(senderCard);
        cardService.save(receiverCard);
        var savedTransaction = transactionRepo.save(transaction);
        return modelMapper.map(savedTransaction, TransactionDtoResponse.class);
    }
}