import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Model.Coins;
import Rest.RestClient;
import Rest.model.MarketTicker;
import org.openqa.selenium.TimeoutException;

/**
 * Created by Mikelis on 2017.12.04..
 */
public class Scraper {
    RestClient restClient;
    WebScraper webScraper;
    SqLiteDb db;

    public Scraper(boolean isLinux) {
        webScraper = new WebScraper(isLinux);
    }

    public void run() {
        System.out.println("STARTING RUN");
        restClient = new RestClient();
        db = new SqLiteDb();

        List<Coins> listOfTrendingCoins;
        try {
            listOfTrendingCoins = webScraper.scrapeBittrexFrontpage();
        } catch (TimeoutException e) {
            System.out.println("A timeout exception happened in scrape scrapeBittrexFrontpage() stoping execution of loop");
            return;
        }
//        webScraper.driver.quit();

        List<Coins> coinsThatAreStillActive = db.getCoinsThatAreStillActive();

        List<Coins> newCoinsToAdd = new ArrayList<>();
        List<Coins> coinsToUpdateForLastTimeSeen = new ArrayList<>();
        listOfTrendingCoins.stream().forEach(trendingCoin -> {
            Optional<Coins> result = coinsThatAreStillActive.stream().filter(o -> o.equals(trendingCoin)).findFirst();
            if (result.isPresent()) {
                coinsToUpdateForLastTimeSeen.add(result.get());
            } else {
                newCoinsToAdd.add(trendingCoin);
            }
        });

        addNewCoins(newCoinsToAdd);
        updateLastTimeSeen(coinsToUpdateForLastTimeSeen);
        updatePrices(coinsThatAreStillActive);
        db.closeConnection();
        System.out.println("DONE\n");
    }

    private void updateLastTimeSeen(List<Coins> coinsToUpdateForLastTimeSeen) {
        System.out.println("Updating last time seen");
        coinsToUpdateForLastTimeSeen.forEach(coin -> {
            db.updateLastTimeSeen(coin.getId());
        });
    }

    private void addNewCoins(List<Coins> newCoinsToAdd) {
        if (!newCoinsToAdd.isEmpty()) {
            System.out.println("Adding new coins");
            for (Coins coin : newCoinsToAdd) {
                MarketTicker marketTicker;
                try {
                    marketTicker = restClient.getTicker(coin.getMarketName());
                    System.out.println("Get ticker finished");
                } catch (Exception e) {
                    System.out.println("Exception in getTicker(), skipping adding new coin" + coin.getMarketName());
                    continue;
                }
                if (marketTicker.getResult() == null || marketTicker.getResult().getAsk() == null) {
                    System.out.println("Coin " + coin.getMarketName() + "was not added duet to ticker results or ask price not present. Skipping adding new coin" + coin.getMarketName());
                    continue;
                }
                BigDecimal askPrice = marketTicker.getResult().getAsk();
                coin.setBuyPrice(askPrice);
                coin.setMaxPrice(askPrice);
                db.insertCoin(coin);
            }
        }
    }

    private void updatePrices(List<Coins> listOfCoinsThatAppearedWithinTwoDays) {
        if (!listOfCoinsThatAppearedWithinTwoDays.isEmpty()) {
            System.out.println("Updating coins");
            for (Coins coin : listOfCoinsThatAppearedWithinTwoDays) {
                BigDecimal lastPrice;
                try {
                    lastPrice = restClient.getTicker(coin.getMarketName()).getResult().getLast();
                    System.out.println("Get ticker finished");
                } catch (Exception e) {
                    System.out.println("Exception in getTicker(), skipping updating coin" + coin.getMarketName());
                    continue;
                }

                BigDecimal percentChange = Utils.calculatePercentChange(coin.getBuyPrice(), lastPrice);
                if (coin.getMaxPrice().compareTo(lastPrice) < 0) {
                    if (coin.getMinutesToPositive5Prct() == 0 && percentChange.compareTo(new BigDecimal(5)) >= 0) {
                        db.updateMinutesToPositive5Prct(coin.getId(), calculateMinutesPassed(coin.getFirstTimeAppearing()));
                    }
                    if (coin.getMinutesToPositive10Prct() == 0 && percentChange.compareTo(new BigDecimal(10)) >= 0) {
                        db.updateMinutesToPositive10Prct(coin.getId(), calculateMinutesPassed(coin.getFirstTimeAppearing()));
                    }
                    db.updateCoin(coin.getId(), lastPrice, percentChange, calculateMinutesPassed(coin.getFirstTimeAppearing()));
                } else {
                    if (coin.getMinutesToNegative5Prct() == 0 && percentChange.compareTo(new BigDecimal(-5)) <= 0) {
                        db.updateMinutesToNegative5Prct(coin.getId(), calculateMinutesPassed(coin.getFirstTimeAppearing()));
                    }
                    if (coin.getMinutesToNegative10Prct() == 0 && percentChange.compareTo(new BigDecimal(-10)) <= 0) {
                        db.updateMinutesToNegative10Perc(coin.getId(), calculateMinutesPassed(coin.getFirstTimeAppearing()));
                    }
                    db.updateCurrentPercentage(coin.getId(), percentChange);
                }

            }
        }
    }

    private int calculateMinutesPassed(LocalDateTime firstTimeAppearing) {
        return (int) Duration.between(firstTimeAppearing, LocalDateTime.now()).toMinutes();
    }
}
