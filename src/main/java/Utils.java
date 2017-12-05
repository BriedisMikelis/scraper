import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Mikelis on 2017.12.01..
 */
public class Utils {
    public static BigDecimal calculatePercentIncrease(BigDecimal buyPrice, BigDecimal lastPrice) {
        System.out.println("calculatePercentIncrease 1 = " + lastPrice.toString() + " buy price " + buyPrice.toString());
        BigDecimal result = lastPrice.subtract(buyPrice);
        System.out.println("calculatePercentIncrease 2");
        result = result.divide(buyPrice, 8, RoundingMode.HALF_UP);
        System.out.println("calculatePercentIncrease 3");
        result = result.multiply(new BigDecimal(100));
        System.out.println("calculatePercentIncrease 4");
        return result;
    }
}
