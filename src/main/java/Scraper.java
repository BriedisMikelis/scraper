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
        System.out.println("STARTING RUN -----------------------------------------");
        restClient = new RestClient();
        db = new SqLiteDb();

        List<Coins> listOfTrendingCoins;
        try {
            listOfTrendingCoins = webScraper.scrapeBittrexFrontpage();
        }catch (TimeoutException e){
            System.out.println("A timeout exception happened in scrape scrapeBittrexFrontpage() stoping execution of loop");
            return;
        }
//        webScraper.driver.quit();

        List<Coins> listOfCoinsThatAppearedWithinTwoDays = db.getCoinsThatApearedWithinTwoDays();

        List<Coins> newCoinsToAdd = new ArrayList<>();
        List<Coins> coinsToUpdateForLastTimeSeen = new ArrayList<>();
        listOfTrendingCoins.stream().forEach(trendingCoin -> {
            Optional<Coins> result = listOfCoinsThatAppearedWithinTwoDays.stream().filter(o -> o.equals(trendingCoin)).findFirst();
            if (result.isPresent()) {
                coinsToUpdateForLastTimeSeen.add(result.get());
            } else {
                newCoinsToAdd.add(trendingCoin);
            }
        });

        addNewCoins(newCoinsToAdd);
        updateLastTimeSeen(coinsToUpdateForLastTimeSeen);
        updatePrices(listOfCoinsThatAppearedWithinTwoDays);
        db.closeConnection();
        System.out.println("done");
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
                MarketTicker marketTicker = restClient.getTicker(coin.getMarketName());
                if (marketTicker.getResult() == null ){
                    System.out.println("Coin " + coin.getMarketName() +"was not added duet to ticker results not present");
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
            System.out.println("Updating asked price and %");
            for (Coins coin : listOfCoinsThatAppearedWithinTwoDays) {
                BigDecimal lastPrice = restClient.getTicker(coin.getMarketName()).getResult().getLast();

                BigDecimal percentChange = Utils.calculatePercentChange(coin.getBuyPrice(), lastPrice);
                if (coin.getMaxPrice().compareTo(lastPrice) < 0) {
                    if(coin.getMinutesToPositive5Prct() == 0 && percentChange.compareTo(new BigDecimal(5)) >= 0){
                        db.updateMinutesToPositive5Prct(coin.getId(), calculateMinutesPassed(coin.getFirstTimeAppearing()));
                    }
                    if(coin.getMinutesToPositive10Prct() == 0 && percentChange.compareTo(new BigDecimal(10)) >= 0){
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
