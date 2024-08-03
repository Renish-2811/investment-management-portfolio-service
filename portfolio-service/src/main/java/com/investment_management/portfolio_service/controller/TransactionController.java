package com.investment_management.portfolio_service.controller;

import com.investment_management.portfolio_service.dto.TransactionDto;
import com.investment_management.portfolio_service.service.transaction.TransactionService;
import com.investment_management.portfolio_service.service.user.DefaultUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RequestMapping("/api/v1")
@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private DefaultUserService defaultUserService;

    @PostMapping(path = "/transaction")
    public ResponseEntity<TransactionDto> addTransaction(@Valid @RequestBody TransactionDto transactionDto) throws ExecutionException, InterruptedException {
        TransactionDto response = transactionService.addTransaction(transactionDto,defaultUserService.getUserInfo("id"));
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/transaction/{status}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionByStatus(@PathVariable("status") String status){
        return ResponseEntity.ok(transactionService.getTransactionByStatus(status,defaultUserService.getUserInfo("id")));
    }

    @DeleteMapping(path = "/transaction/{id}")
    public ResponseEntity deleteTransaction(@PathVariable("id")UUID id){
        transactionService.deleteTransaction(id,defaultUserService.getUserInfo("id"));
        return ResponseEntity.ok("Transaction Deleted Successfully");
    }
}
