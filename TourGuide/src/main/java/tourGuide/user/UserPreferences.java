package tourGuide.user;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jsoniter.annotation.JsonMissingProperties;
import com.jsoniter.annotation.JsonWrapper;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javamoney.moneta.Money;


@Getter
@Setter
@NoArgsConstructor
public class UserPreferences {
	private int attractionProximity = Integer.MAX_VALUE;
	private CurrencyUnit currency = Monetary.getCurrency("USD");
	@JsonMissingProperties
	private Money lowerPricePoint = Money.of(0, currency);
	@JsonMissingProperties
	private Money highPricePoint = Money.of(Integer.MAX_VALUE, currency);

	private int tripDuration = 1;
	private int ticketQuantity = 1;
	private int numberOfAdults = 1;
	private int numberOfChildren = 0;
}
