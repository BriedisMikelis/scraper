import Model.Coins;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mikelis on 2017.12.03..
 */
public class WebScraper {
    WebDriver driver;

    public WebScraper() {
        DesiredCapabilities DesireCaps = new DesiredCapabilities();
        DesireCaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:/Users/Mikelis/PhantomJs/phantomjs-2.1.1-windows/bin/phantomjs.exe");
        driver = new PhantomJSDriver(DesireCaps);
        driver.manage().window().setSize(new Dimension(1270, 768));
    }

    public List<Coins> scrapeBittrexFrontpage() {
        driver.get("https://bittrex.com/Home/Markets");
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return d.findElement(By.className("item")).getText().length() != 0;
            }
        });
        Utils.captureScreenshot(driver, "TestImage");
        List<WebElement> itemsFromCarousel = driver.findElements(By.className("item"));
        List<Coins> listOfTrendingCoins = itemsFromCarousel.stream().map(i -> {
            Coins trendingCoin = new Coins();
            String changeString = i.findElement(By.className("changed")).getText();
            trendingCoin.setPercentChange(new BigDecimal(changeString.substring(0, changeString.length() - 1)));
            trendingCoin.setName(i.findElement(By.className("name")).getText());
            trendingCoin.setMarketName(i.findElement(By.className("marketName")).getText());
            trendingCoin.setTitle(i.findElement(By.className("title")).getText());
            String volume = i.findElement(By.className("volume")).getText();
            trendingCoin.setVolumeInBTC(new BigDecimal(volume.split(" ")[0]));
            trendingCoin.setFirstTimeAppearing(LocalDateTime.now());
            trendingCoin.setLastTimeAppeared(LocalDateTime.now());
            return trendingCoin;
        }).collect(Collectors.toList());
        return listOfTrendingCoins;
    }
}
