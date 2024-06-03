package com.currency_exchange.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/resillience4j")
public class CircuitBreakerController {
	Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);
	
	@GetMapping
	public ResponseEntity<?> sampleApi(){
		return ResponseEntity.ok("SampleApi for Resiliance4j");
	}
	
	@GetMapping("/malfucntioned")
	@Retry(name = "malfunctionedapi", fallbackMethod = "fallbackMalfunctionedApi")//app.prop da bununla ilgili max-attempts atadım
									 //eğer hata atarsa max-attempts kadar tekrar 
									 //response gönderecek
	public ResponseEntity<?> malfunctionedApi(){
		logger.info("malfunctionedApi called");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity(
				"http://localhost:8080/some-malfuctioned-url", String.class);
		
		return ResponseEntity.ok(forEntity.getBody());
	}
	
	public ResponseEntity<?> fallbackMalfunctionedApi(Throwable t){
		logger.error("Fallback method called due to: {}", t.getMessage());
		return ResponseEntity.internalServerError().body("Default response due to error: " + t.getMessage());
	}
}
