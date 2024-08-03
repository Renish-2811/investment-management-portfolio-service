package com.investment_management.portfolio_service.api;

import com.investment_management.portfolio_service.dto.BalanceDto;
import com.investment_management.portfolio_service.dto.BankDto;
import retrofit2.Call;
import retrofit2.http.*;

public interface BankApi {
    @GET("user-service/api/v1/bank")
    @Headers({ "Content-Type: application/json" })
    public Call<BankDto> getBank();

    @GET("user-service/api/v1/balance/{type}")
    @Headers({ "Content-Type: application/json" })
    public Call<BalanceDto> getBalance(@Path("type") String type);

    @POST("user-service/api/v1/add-fund")
    @Headers({"Content-Type: application/json"})
    public Call<BalanceDto> deductBalance(@Body BalanceDto balanceDto);

    @POST("user-service/api/v1/add-trades")
    @Headers({"Content-Type: application/json"})
    public Call<BalanceDto> addTradeBalance(@Body BalanceDto balanceDto);
}
