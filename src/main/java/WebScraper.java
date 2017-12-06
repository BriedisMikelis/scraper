import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Model.Coins;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by Mikelis on 2017.12.03..
 */
public class WebScraper {
    WebDriver driver;

    public WebScraper(boolean isLinux) {
        ChromeOptions options = new ChromeOptions();
        if (isLinux) options.setBinary("/usr/bin/chromium-browser");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1200x600");
        driver = new ChromeDriver(options);
    }

    public List<Coins> scrapeBittrexFrontpage() {
        List<WebElement> itemsFromCarousel;
        int count = 0;
        int maxTries = 3;
        while (true) {
            try {
                driver.get("https://bittrex.com/Home/Markets");
                System.out.println("Getting items from carousell");
//              http://docs.seleniumhq.org/docs/04_webdriver_advanced.jsp#explicit-and-implicit-waits
                itemsFromCarousel = (new WebDriverWait(driver, 15))
                        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));
                break;
            } catch (TimeoutException e) {
                System.out.println("a timeout exception for bittrex trendig page scrape");
                if (++count == maxTries) throw e;
            }
        }

        System.out.println("Mapping items to object");
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

        System.out.println("\nTrending coins:");
        listOfTrendingCoins.forEach(coin -> System.out.println(coin.getName() + " : " + coin.getTitle() + " : " + coin.getPercentChange()));
        System.out.println("");
        return listOfTrendingCoins;
    }
}
