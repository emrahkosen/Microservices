package com.currency_exchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.currency_exchange.sevice.CurrencyExchange;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long>{
	CurrencyExchange findByFromAndTo(String from, String to);
}
