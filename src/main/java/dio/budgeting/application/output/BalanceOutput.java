package dio.budgeting.application.output;

import dio.budgeting.domain.Category;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record BalanceOutput(String category, double totalBalance) {
    public static BalanceOutput from(Category category, long totalAmountInCents) {
        double valueInReais = BigDecimal.valueOf(totalAmountInCents)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .doubleValue();
        return new BalanceOutput(category.name(), valueInReais);
    }
}