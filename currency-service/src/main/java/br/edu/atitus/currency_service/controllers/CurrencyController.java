package br.edu.atitus.currency_service.controllers;

import br.edu.atitus.currency_service.clients.CurrencyBcClient;
import br.edu.atitus.currency_service.clients.CurrencyBcResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.currency_service.entities.CurrencyEntity;
import br.edu.atitus.currency_service.repositories.CurrencyRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("currency")
public class CurrencyController {

	private final CurrencyRepository repository;
	private final CurrencyBcClient currencyBcClient;

	@Value("${server.port}")
	private int serverPort;

	public CurrencyController(CurrencyRepository repository, CurrencyBcClient currencyBcClient) {
		super();
		this.repository = repository;
		this.currencyBcClient = currencyBcClient;
	}

	@GetMapping("/{value}/{source}/{target}")
	public ResponseEntity<CurrencyEntity> getConversion(
			@PathVariable double value,
			@PathVariable String source,
			@PathVariable String target) throws Exception{

//		CurrencyEntity currency = repository.
//				findBySourceAndTarget(source, target)
//				.orElseThrow(() -> new Exception("Currency not found"));

		source = source.toUpperCase();
		target = target.toUpperCase();
		String dataSource = "None";

		CurrencyEntity currency = new CurrencyEntity();
		currency.setSource(source);
		currency.setTarget(target);

		if(source.equals(target)){
			currency.setConversionRate(1);
		}
		else {
			try {
				LocalDate lastBusinessDay = LocalDate.now();

				if (lastBusinessDay.getDayOfWeek() == DayOfWeek.SATURDAY) {
					lastBusinessDay = lastBusinessDay.minusDays(1);
				} else if (lastBusinessDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
					lastBusinessDay = lastBusinessDay.minusDays(2);
				}

				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
				String quoteDate = lastBusinessDay.format(formatter);

				double currencySource = 1;
				double currencyTarget = 1;

				if(!source.equals("BRL")){
					CurrencyBcResponse response = currencyBcClient.getCurrency(source, quoteDate);
					if(response.getValue().isEmpty()) throw new Exception("Currency not found " + source + " for date " + quoteDate);
					currencySource = response.getValue().get(0).getCotacaoVenda();
				}
				if(!target.equals("BRL")){
					CurrencyBcResponse response = currencyBcClient.getCurrency(target, quoteDate);
					if(response.getValue().isEmpty()) throw new Exception("Currency not found " + target + " for date " + quoteDate);
					currencyTarget = response.getValue().get(0).getCotacaoVenda();
				}
				currency.setConversionRate(currencySource / currencyTarget);
				dataSource = "API BCB";
			}
			catch (Exception e){
				currency = repository.findBySourceAndTarget(source,target).orElseThrow(() -> new Exception("Currency Unsupported"));
				dataSource = "Local Database";
			}
		}

		currency.setConvertedValue(value * currency.getConversionRate());
		currency.setEnviroment("Currency running in port: " + serverPort + "- DataSource: " + dataSource);

		return ResponseEntity.ok(currency);
	}
}
