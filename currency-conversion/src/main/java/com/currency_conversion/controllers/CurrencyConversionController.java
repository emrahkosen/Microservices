package com.currency_conversion.controllers;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.currency_conversion.conversion.CurrencyConversion;
import com.currency_conversion.exception.ResourceNotFoundException;
import com.currency_conversion.feign_proxy.CurrentExchangeProxy;

@RestController
@RequestMapping("/currency_conversion")
public class CurrencyConversionController {
	
	@GetMapping
	public String getInfo() {
		return 	"http://localhost:8100/currency_conversion/from/USD/to/TRY/quantity/10";
	}
	
	@GetMapping("/from/{from}/to/{to}/quantity/{quantity}")
	public ResponseEntity<?> convert(@PathVariable String from,
										@PathVariable String to,
										@PathVariable BigDecimal quantity) {
		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from",from);
		uriVariables.put("to",to);
		
		try {
			ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity
					("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
							CurrencyConversion.class, uriVariables);
			
			CurrencyConversion currencyConversion = responseEntity.getBody();
			currencyConversion.setQuantity(quantity);
			currencyConversion.setTotalCalculatedAmount(quantity.multiply(currencyConversion.getConversionMultiple()));
			
			return ResponseEntity.ok(currencyConversion);
			
		} catch (Exception e) {
			throw new ResourceNotFoundException("Error: There Is No Resource " + from + " - " + to);
		}
	}
	
	
	
	@Autowired
	CurrentExchangeProxy currentExchangeProxy;
	
	
	@GetMapping("/feign/from/{from}/to/{to}/quantity/{quantity}")
	public ResponseEntity<?> convertFeign(@PathVariable String from,
										@PathVariable String to,
										@PathVariable BigDecimal quantity) {
		HashMap<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from",from);
		uriVariables.put("to",to);
		
		
		try {
			CurrencyConversion exchangeValue = currentExchangeProxy.retrieveExchangeValue(from, to);
			exchangeValue.setQuantity(quantity);
			exchangeValue.setTotalCalculatedAmount(quantity.multiply(exchangeValue.getConversionMultiple()));
			return ResponseEntity.ok(exchangeValue);
		} catch (Exception e) {
			throw new ResourceNotFoundException("Error: There Is No Resource " + from + " - " + to + " \n" + e.getMessage());
		}
		
		
	}
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
