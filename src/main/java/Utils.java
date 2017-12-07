import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

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

    public static BigDecimal calculateProffit(List<Integer> integers, int initialMoney, int percentGain, int percentLose, boolean reinvest) {
        BigDecimal startMoney = new BigDecimal(initialMoney);
        BigDecimal proffit = new BigDecimal(initialMoney);
        for (int i : integers) {
            if (i == 1) {
                if (reinvest) {
                    proffit = proffit.add(calculatePercentMoney(proffit, percentGain));
                } else {
                    proffit = proffit.add(calculatePercentMoney(startMoney, percentGain));
                }
            } else {
                if (reinvest) {
                    proffit = proffit.subtract(calculatePercentMoney(proffit, percentLose));
                } else {
                    proffit = proffit.subtract(calculatePercentMoney(startMoney, percentLose));
                }
            }
        }
        return proffit;
    }

    private static BigDecimal calculatePercentMoney(BigDecimal money, int percent) {
        return new BigDecimal(percent).multiply(money).divide(new BigDecimal(100));
    }
}
