package kz.app.utils;

import java.math.BigDecimal;

public interface RateCalculator {
    BigDecimal calc(int scale, BigDecimal amount, BigDecimal rate);
}
