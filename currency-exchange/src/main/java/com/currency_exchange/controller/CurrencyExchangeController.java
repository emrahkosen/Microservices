package com.currency_exchange.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.currency_exchange.exception.ResourceNotFoundException;
import com.currency_exchange.repository.CurrencyExchangeRepository;
import com.currency_exchange.sevice.CurrencyExchange;

@RestController
public class CurrencyExchangeController {
	
	@Autowired
	CurrencyExchangeRepository currencyExchangeRepository;
	
	@Autowired
	private Environment environment;
	

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public ResponseEntity<?> retrieveExchangeValue(
            @PathVariable String from,
            @PathVariable String to) {

        CurrencyExchange currencyExchange = currencyExchangeRepository.findByFromAndTo(from, to);

        if (currencyExchange == null) {
            // Return a custom response with HTTP status 404 (Not Found)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body("Unable to find exchange rate for " + from + " to " + to);
        	throw new ResourceNotFoundException("Unable to find data for " + from + " to " + to);
        }

        String port = environment.getProperty("local.server.port");
        currencyExchange.setEnvironment(port);

        // Return the currency exchange information with HTTP status 200 (OK)
        return ResponseEntity.ok(currencyExchange);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
