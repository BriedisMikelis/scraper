import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Created by Mikelis on 2017.12.01..
 */
public class Utils {
    public static String captureScreenshot(WebDriver driver, String screenshotName) {

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            String dest = "" + screenshotName + ".png";
            File destination = new File(dest);
            FileUtils.copyFile(source, destination);
            return dest;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static BigDecimal calculatePercentIncrease(BigDecimal buyPrice, BigDecimal lastPrice) {
        return lastPrice
                .subtract(buyPrice)
                .divide(buyPrice, 8, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }
}
