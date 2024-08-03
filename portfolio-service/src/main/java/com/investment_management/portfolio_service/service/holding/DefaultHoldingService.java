package com.investment_management.portfolio_service.service.holding;

import com.investment_management.portfolio_service.api.BankApi;
import com.investment_management.portfolio_service.dto.BalanceDto;
import com.investment_management.portfolio_service.dto.HoldingDto;
import com.investment_management.portfolio_service.entity.Holdings;
import com.investment_management.portfolio_service.entity.Portfolio;
import com.investment_management.portfolio_service.exception.InvalidCallException;
import com.investment_management.portfolio_service.exception.ResourceNotFoundException;
import com.investment_management.portfolio_service.mapper.MapperIf;
import com.investment_management.portfolio_service.repository.HoldingRepo;
import com.investment_management.portfolio_service.repository.PortfolioRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

//import static com.investment_management.portfolio_service.mapper.Mapper.HoldingToHoldingDto;

@Service
@Transactional
public class DefaultHoldingService implements HoldingService{

    @Autowired
    private HoldingRepo holdingRepo;

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private BankApi userApiConfig;

    @Autowired
    private MapperIf mapperIf;

    @Override
    @Transactional
    public HoldingDto addHolding(HoldingDto holdingDto, String userId) {
        Portfolio portfolio = portfolioRepo.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("No Portfolio exist for this ID"));
        Holdings holdings = mapperIf.HoldingDtoToHolding(holdingDto);
        holdings.setPortfolio(portfolio);
        if(holdings.getRealizedPL()==null){
            holdings.setRealizedPL(BigDecimal.valueOf(0));
        }
        // update holding wisely, fetch current holdings first then average the values and then only save the holdings
        if(holdingRepo.findByStockCode(holdingDto.getStockCode()).isPresent()){
            Holdings existingHolding  = holdingRepo.findByStockCode(holdingDto.getStockCode()).get();
            BigDecimal averageRate = ((existingHolding.getQuantity().multiply(existingHolding.getRate())).add(holdings.getQuantity().multiply(holdings.getRate()))).divide(existingHolding.getQuantity().add(holdings.getQuantity()),2, RoundingMode.HALF_UP);
            holdings.setRate(averageRate);
            holdings.setQuantity(existingHolding.getQuantity().add(holdings.getQuantity()));
            holdings.setId(holdingRepo.findByStockCode(holdingDto.getStockCode()).get().getId());
        }
        holdings.setUserId(userId);
        holdings.setPurchasedMarketValue(holdings.getRate().multiply(holdings.getQuantity()));
        return mapperIf.HoldingToHoldingDto(holdingRepo.save(holdings));
    }

    @Override
    public HoldingDto deductHoldings(HoldingDto holdingDto, String userId) {
        Holdings holdings = holdingRepo.findByStockCode(holdingDto.getStockCode()).orElseThrow(()->new ResourceNotFoundException("Invalid Holding"));
        //update realized PL
        BigDecimal pl = holdings.getRealizedPL().add(holdingDto.getQuantity().multiply((holdingDto.getRate().subtract(holdings.getRate()))));
        holdings.setRealizedPL(pl);

        //update wallet
        long amount = holdingDto.getRate().multiply(holdingDto.getQuantity()).longValue();
        Call<BalanceDto> balanceDtoCall = userApiConfig.addTradeBalance(new BalanceDto(holdingDto.getType(),amount,"ADD"));
        try {
            Response<BalanceDto> response = balanceDtoCall.execute();
            if(response.message().equals("Not Found")){
                throw new ResourceNotFoundException("Call Failed");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        //save transaction
        holdings.setPurchasedMarketValue(holdings.getRate().multiply(holdings.getQuantity()));
        return mapperIf.HoldingToHoldingDto(holdingRepo.save(holdings));
    }


    @Override
    @Transactional
    public List<HoldingDto> getHolding(String userId) {
        Portfolio portfolio = portfolioRepo.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("No Portfolio exist for this ID"));
        List<Holdings> holdings = holdingRepo.findByPortfolioId(portfolio.getId()).orElseThrow(()->new ResourceNotFoundException("No Holdings found"));
        return holdings.stream().map(
                mapperIf::HoldingToHoldingDto
        ).toList();
    }

    @Override
    public List<HoldingDto> getHoldingByType(String type, String userId) {
        List<Holdings> holdings = holdingRepo.findByType(type).orElseThrow(()->new InvalidCallException("No holding of such type found"));
        return holdings.stream().map(mapperIf::HoldingToHoldingDto).toList();
    }
}
