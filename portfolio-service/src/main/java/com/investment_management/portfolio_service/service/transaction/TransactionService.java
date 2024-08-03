package com.investment_management.portfolio_service.service.transaction;

import com.investment_management.portfolio_service.dto.TransactionDto;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface TransactionService {

    TransactionDto addTransaction(TransactionDto transactionDto, String userId) throws ExecutionException, InterruptedException;

    TransactionDto executeTransaction(TransactionDto transactionDto, UUID transactionId, String userId, SecurityContext securityContext);

    List<TransactionDto> getTransactionByStatus(String status,String userId);

    List<TransactionDto> getTransactionByDate(String userId);

    List<TransactionDto> getAllTransaction(String userId);

    void deleteTransaction(UUID id,String userId);
}
