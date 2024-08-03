package com.investment_management.portfolio_service.service.portfolio;

import com.investment_management.portfolio_service.api.BankApi;
import com.investment_management.portfolio_service.dto.PortfolioDto;
import com.investment_management.portfolio_service.entity.Portfolio;
import com.investment_management.portfolio_service.exception.ResourceAlreadyFoundException;
import com.investment_management.portfolio_service.exception.ResourceNotFoundException;
import com.investment_management.portfolio_service.mapper.MapperIf;
import com.investment_management.portfolio_service.repository.PortfolioRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DefaultPortfolioService implements PortfolioService{

    @Autowired
    private PortfolioRepo portfolioRepo;

    @Autowired
    private BankApi userApiConfig;

    @Autowired
    private MapperIf mapperIf;





    @Override
    public PortfolioDto addPortfolio(PortfolioDto portfolioDto, String userId) {
        Portfolio portfolio = mapperIf.PortfolioDtoToPortFolio(portfolioDto);
        portfolio.setUserId(userId);
        Portfolio response;
        checkBankAccountPresent();
        try {
            if(portfolioRepo.findByUserId(userId).isPresent()){
                throw new Exception("Cant Add more than one portfolio");
            }
            response = portfolioRepo.save(portfolio);
        }
        catch (Exception e){
            throw new ResourceAlreadyFoundException(e.getMessage());
        }
        return mapperIf.PortfolioToPortFolioDto(response);
    }

    @Override
    @Transactional
    public PortfolioDto getPortfolio(String userId) {
        checkBankAccountPresent();
        Portfolio portfolio = portfolioRepo.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("No Portfolio for this user is added"));
        return mapperIf.PortfolioToPortFolioDto(portfolio);
    }

    @Override
    public PortfolioDto updatePortfolio(PortfolioDto portfolioDto, String userId) {
        checkBankAccountPresent();
        Portfolio portfolio = portfolioRepo.findByUserId(userId).orElseThrow(()->new ResourceNotFoundException("No Portfolio found"));
        portfolio.setName(portfolioDto.getName());
        PortfolioDto response = mapperIf.PortfolioToPortFolioDto(portfolioRepo.save(portfolio));
        return response;
    }

    public void checkBankAccountPresent(){
        try {
            userApiConfig.getBank().execute();
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
}
