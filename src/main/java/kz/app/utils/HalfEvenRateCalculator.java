package kz.app.utils;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Singleton
public class HalfEvenRateCalculator implements RateCalculator {
    @Override
    public BigDecimal calc(int scale, BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(scale, RoundingMode.HALF_EVEN);
    }
}
