package com.investment_management.portfolio_service.service.transaction;

import com.investment_management.portfolio_service.api.BankApi;
import com.investment_management.portfolio_service.config.websocket.MyWebSocketClient;
import com.investment_management.portfolio_service.dto.BalanceDto;
import com.investment_management.portfolio_service.dto.HoldingDto;
import com.investment_management.portfolio_service.dto.TransactionDto;
import com.investment_management.portfolio_service.entity.Holdings;
import com.investment_management.portfolio_service.entity.Transaction;
import com.investment_management.portfolio_service.exception.InsufficientResourceException;
import com.investment_management.portfolio_service.exception.InvalidCallException;
import com.investment_management.portfolio_service.exception.ResourceNotFoundException;
import com.investment_management.portfolio_service.mapper.MapperIf;
import com.investment_management.portfolio_service.repository.HoldingRepo;
import com.investment_management.portfolio_service.repository.PortfolioRepo;
import com.investment_management.portfolio_service.repository.TransactionRepo;
import com.investment_management.portfolio_service.service.holding.HoldingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import okhttp3.Headers;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import retrofit2.Call;
import retrofit2.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@AllArgsConstructor
public class DefaultTransactionService implements TransactionService{

    public TransactionRepo transactionRepo;
    public PortfolioRepo portfolioRepo;
    private BankApi userApiConfig;
    private MyWebSocketClient myWebSocketClient;
    private StandardWebSocketClient webSocketClient;
    private HoldingService holdingService;
    private HoldingRepo holdingRepo;
    private HashMap<UUID,WebSocketSession> sessionHolders;
    private RabbitTemplate rabbitTemplate;
    private MapperIf mapperIf;


    @Override
    public TransactionDto addTransaction(TransactionDto transactionDto, String userId) throws ExecutionException, InterruptedException {
        //If valid portfolio is not found then return exception
        if(portfolioRepo.findByUserId(userId).isEmpty()){
            throw new ResourceNotFoundException("Please add portfolio first before making any transaction");
        }
        //If its buy then check wallet balance first
        if(transactionDto.getDirection().equals("BUY")){
            if (Boolean.FALSE.equals(checkSufficientWalletBalancePresent(transactionDto.getType(),transactionDto.getQuantity(),transactionDto.getRate()))){
                throw new InsufficientResourceException("Please Add Sufficient " + transactionDto.getType() +" balance first before making transaction");
            }
        }
        //If Its Sell then check holdings first
        else{
            if(Boolean.FALSE.equals(checkSufficientStockBalancePresent(transactionDto.getQuantity(),transactionDto.getStockCode()))){
                throw new ResourceNotFoundException("Can't Sell more quantity than what you are holding");
            }
            blockFunds(transactionDto.getQuantity(),transactionDto.getStockCode());
        }

        //blocking balance before executing all transactions.
        if(transactionDto.getDirection().equals("BUY")){
            Call<BalanceDto> balanceDtoCall = userApiConfig.deductBalance(new BalanceDto(transactionDto.getType(), (long) (transactionDto.getRate().doubleValue()*transactionDto.getQuantity().doubleValue()),"DEDUCT"));
            try {
                Response<BalanceDto> response = balanceDtoCall.execute();
                if(response.message().equals("Not Found")){
                    throw new ResourceNotFoundException("Balance not deducted, please try again");
                }
            } catch (Exception e) {
                throw new ResourceNotFoundException(e.getMessage());
            }
        }

        //setting transaction status to INITIATED
        //saving transaction
        Transaction transaction = mapperIf.TransactionDtoToTransaction(transactionDto);
        transaction.setStatus("INITIATED");
        transaction.setUserId(userId);
        transaction.setPortfolio(portfolioRepo.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("No portfolio found")));
        Transaction savedTransaction = transactionRepo.save(transaction);
        myWebSocketClient = new MyWebSocketClient(transactionDto,userId, savedTransaction.getId(),SecurityContextHolder.getContext(),this);
        try {
            myWebSocketClient.connect(webSocketClient, "ws://localhost:6789");
        }
        catch (Exception e){
            throw new ResourceNotFoundException(e.getMessage());
        }
        sessionHolders.put(savedTransaction.getId(),myWebSocketClient.getSession());
        transactionDto.setStatus("INITIATED");
        String message = transactionDto.getDirection()+" Transaction initiated for "+transactionDto.getStockCode()+" with quantity: "+transactionDto.getQuantity()+" at Price: "+transactionDto.getRate();
        String queueName = "subscription."+userId;
        rabbitTemplate.convertAndSend(queueName,message);
        transactionDto.setId(transaction.getId());
        return transactionDto;
    }

    public TransactionDto executeTransaction(TransactionDto transactionDto, UUID transactionId, String userId, SecurityContext securityContext) {
        Transaction transaction = transactionRepo.findById(transactionId).orElseThrow(()->new ResourceNotFoundException("Transaction Rolled back"));
        transaction.setStatus("EXECUTED");
        transaction.setDate(LocalDateTime.now());
        transactionRepo.save(transaction);
        transactionDto.setId(transactionId);
        //updating context manually
        SecurityContextHolder.setContext(securityContext);
        //we can now make external API calls
        updateHolding(transactionDto,userId);
        //Send Notification
        String queueName = "subscription."+userId;
        String message = transactionDto.getDirection()+" Transaction of "+transactionDto.getStockCode()+" executed with quantity: "+transactionDto.getQuantity()+" at Price: "+transactionDto.getRate();
        rabbitTemplate.convertAndSend(queueName,message);
        return transactionDto;
    }

    @Override
    public List<TransactionDto> getTransactionByStatus(String status,String userId) {
        if(status.equals("all")){
            return transactionRepo.findAll().stream().map(mapperIf::TransactionToTransactionDto).toList();
        }
        List<Transaction> transactions = transactionRepo.findByStatus(status).orElseThrow(()->new ResourceNotFoundException("Invalid Status or Empty List"));
        return transactions.stream().map(mapperIf::TransactionToTransactionDto).toList();
    }

    @Override
    public List<TransactionDto> getTransactionByDate(String userId) {
        return null;
    }

    @Override
    public List<TransactionDto> getAllTransaction(String userId) {
        return transactionRepo.findAll().stream().map(mapperIf::TransactionToTransactionDto).toList();
    }

    @Override
    public void deleteTransaction(UUID id, String userId) {
        Transaction transaction = transactionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!transaction.getStatus().equals("EXECUTED")) {
            //If Buy is deleted then update wallet balance first;
            if(transaction.getDirection().equals("BUY")){
                BalanceDto balanceDto = new BalanceDto(transaction.getType(),transaction.getRate().multiply(transaction.getQuantity()).longValue(),"ADD");
                Call<BalanceDto> response = userApiConfig.addTradeBalance(balanceDto);
                try {
                    Response<BalanceDto> balanceDtoResponse = response.execute();
                    if(balanceDtoResponse.message().equals("Not Found")){
                        throw new InvalidCallException("Something went wrong");
                    }
                }
                catch (Exception e){
                    throw new InvalidCallException(e.getMessage());
                }
            }
            try {
                sessionHolders.get(transaction.getId()).close();
            }
            catch (Exception e){
                throw new ResourceNotFoundException(e.getMessage());
            }
            transactionRepo.delete(transaction);
        }
        else{
            throw new InvalidCallException("Cant Delete executed Transaction");
        }
    }


    public Boolean checkSufficientWalletBalancePresent(String type,BigDecimal quantity,BigDecimal amount){
        Double amountTxn = amount.doubleValue()*quantity.doubleValue();
        Call<BalanceDto> callAsync = userApiConfig.getBalance(type);
        try {
            Response<BalanceDto> balanceDtoResponse = callAsync.execute();
            if(balanceDtoResponse.message().equals("Not Found")){
                throw new ResourceNotFoundException("Add Bank Account first");
            }
            if(balanceDtoResponse.body().getAmount()<=amountTxn.longValue())
            {
                return false;
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        return true;
    }

    public Boolean checkSufficientStockBalancePresent(BigDecimal quantity, String stockCode){
        return holdingRepo.findByStockCode(stockCode).isPresent() && holdingRepo.findByStockCode(stockCode).get().getQuantity().doubleValue() >= quantity.doubleValue();
    }

    public void blockFunds(BigDecimal quantity,String stockCode){
        Holdings holdings = holdingRepo.findByStockCode(stockCode).orElseThrow(()->new ResourceNotFoundException("Invalid Call"));
        holdings.setQuantity(holdings.getQuantity().subtract(quantity));
        holdingRepo.save(holdings);
    }

    public void updateHolding(TransactionDto transactionDto,String userId){
        HoldingDto holdingDto = new HoldingDto(transactionDto.getType(),transactionDto.getStockCode(),transactionDto.getStockName(),transactionDto.getQuantity(),transactionDto.getRate());
        if(transactionDto.getDirection().equals("BUY")){
            holdingService.addHolding(holdingDto,userId);
        }
        else {
            holdingService.deductHoldings(holdingDto,userId);
        }
    }

}
