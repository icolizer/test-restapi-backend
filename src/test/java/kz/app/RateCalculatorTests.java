package kz.app;

import io.micronaut.test.annotation.MicronautTest;
import kz.app.utils.RateCalculator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;

@MicronautTest
class RateCalculatorTests {
    @Inject RateCalculator rateCalculator;

    @Test
    void checkMinimalValuesAvailableForMethod() throws URISyntaxException {
        var amount = rateCalculator.calc(6, new BigDecimal("0.01"), new BigDecimal("0.0001"));
        assertThat(new BigDecimal("0.000001"), Matchers.comparesEqualTo(amount));
    }
}
