package com.investment_management.portfolio_service.controller;


import com.investment_management.portfolio_service.dto.HoldingDto;
import com.investment_management.portfolio_service.service.holding.DefaultHoldingService;
import com.investment_management.portfolio_service.service.user.DefaultUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class HoldingController {

    @Autowired
    private DefaultHoldingService defaultHoldingService;

    @Autowired
    private DefaultUserService defaultUserService;

    @PostMapping(path = "/holding")
    public ResponseEntity<HoldingDto> addHolding(@Valid @RequestBody HoldingDto holdingDto){
        HoldingDto response = defaultHoldingService.addHolding(holdingDto,defaultUserService.getUserInfo("id"));
        return  new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/holding")
    public ResponseEntity<List<HoldingDto>> getHolding(){
        List<HoldingDto> response = defaultHoldingService.getHolding(defaultUserService.getUserInfo("id"));
        return  new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("/holding/{type}")
    public ResponseEntity<List<HoldingDto>> getHoldingByType(@PathVariable("type") String type){
        List<HoldingDto> response = defaultHoldingService.getHoldingByType(type,defaultUserService.getUserInfo("id"));
        return  new ResponseEntity(response,HttpStatus.OK);
    }

}
