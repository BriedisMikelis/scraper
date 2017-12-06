import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Mikelis on 2017.12.01..
 */
public class Utils {
    public static BigDecimal calculatePercentChange(BigDecimal buyPrice, BigDecimal lastPrice) {
        BigDecimal result = lastPrice.subtract(buyPrice);
        result = result.divide(buyPrice, 8, RoundingMode.HALF_UP);
        result = result.multiply(new BigDecimal(100));
        return result;
    }
}
